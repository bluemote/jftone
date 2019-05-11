package org.jftone.util;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public final class SSLSocketUtil {

	public static final String TLS = "TLS";
	public static final String SSL = "SSL";

	/**
	 * 获取默认协议[TLS]的SSLSocketFactory 信任所有证书
	 * @return
	 */
	public static SSLSocketFactory getSSLSocketFactory() throws Exception {
		return getSSLSocketFactory(TLS);
	}

	/**
	 * 获取指定协议的[TLS]的SSLSocketFactory 信任所有证书
	 * @param protocal TSLv1 TSLv2 TSLv3 SSL
	 * @return
	 * @throws Exception
	 */
	public static SSLSocketFactory getSSLSocketFactory(String protocal) throws Exception {
		SSLContext sslContext = SSLContext.getInstance(protocal == null ? TLS : protocal);
		sslContext.init(null, new TrustManager[] { getX509TrustManager() }, new SecureRandom());
		return sslContext.getSocketFactory();
	}

	/**
	 * 创建SSLSocketFactory
	 * @param keyManagers
	 * @param trustManagers
	 * @return
	 * @throws Exception
	 */
	public static SSLSocketFactory getSSLSocketFactory(KeyManager[] keyManagers,
			TrustManager[] trustManagers) throws Exception {
		return getSSLSocketFactory(TLS, keyManagers, trustManagers);
	}
	/**
	 * 创建SSLSocketFactory
	 * @param protocal
	 * @param keyManagers
	 * @param trustManagers
	 * @return
	 * @throws Exception
	 */
	public static SSLSocketFactory getSSLSocketFactory(String protocal, KeyManager[] keyManagers,
			TrustManager[] trustManagers) throws Exception {
		SSLContext sslContext = SSLContext.getInstance(protocal == null ? TLS : protocal);
		sslContext.init(keyManagers, trustManagers, new SecureRandom());
		return sslContext.getSocketFactory();
	}

	/**
	 * 获取HostnameVerifier
	 * @return
	 */
	public static HostnameVerifier getAllTrustHostnameVerifier() {
		HostnameVerifier hostnameVerifier = new HostnameVerifier() {
			@Override
			public boolean verify(String s, SSLSession sslSession) {
				return true;
			}
		};
		return hostnameVerifier;
	}

	/**
	 * 创建指定证书的TrustManager
	 * 
	 * @param certFile  证书文件
	 * @param password  证书密码
	 * @param keyStoreType KeyStore类型 例如PKCS12
	 * @return
	 * @throws Exception
	 */
	public static TrustManager[] createTrustManager(File certFile, String password, String keyStoreType)
			throws Exception {
		// 指定交换数字证书的加密标准
		keyStoreType = null == keyStoreType ? KeyStore.getDefaultType() : keyStoreType;
		KeyStore keyStore = KeyStore.getInstance(keyStoreType);
		char[] passwds = password.toCharArray();
		FileInputStream instream = new FileInputStream(certFile);
		try {
			keyStore.load(instream, passwds);
		} finally {
			instream.close();
		}
		TrustManagerFactory tmFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmFactory.init(keyStore);
		return tmFactory.getTrustManagers();
	}

	/**
	 * 创建指定证书的KeyManager
	 * 
	 * @param certFile 证书文件
	 * @param password 证书密码
	 * @param keyStoreType KeyStore类型 例如PKCS12
	 * @return
	 * @throws Exception
	 */
	public static KeyManager[] createKeyManager(File certFile, String password, String keyStoreType) throws Exception {
		// 指定交换数字证书的加密标准
		keyStoreType = null == keyStoreType ? KeyStore.getDefaultType() : keyStoreType;
		KeyStore keyStore = KeyStore.getInstance(keyStoreType);
		char[] passwds = password.toCharArray();
		FileInputStream instream = new FileInputStream(certFile);
		try {
			keyStore.load(instream, passwds);
		} finally {
			instream.close();
		}
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		keyManagerFactory.init(keyStore, passwds);
		return keyManagerFactory.getKeyManagers();
	}

	/**
	 * 获取所有可信任的X509TrustManager
	 * @return
	 */
	public static X509TrustManager getX509TrustManager() {
		X509TrustManager x509TrustManager = new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[] {};
			}
		};
		return x509TrustManager;
	}

}
