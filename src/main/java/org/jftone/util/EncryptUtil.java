package org.jftone.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import org.apache.commons.codec.binary.Base64;
import org.jftone.config.Const;

public final class EncryptUtil {

	/**
	 * MD5加密
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String md5(String str) throws Exception {
		return md5(str, null);
	}

	public static String md5(String str, String charset) throws Exception {
		StringBuilder buf = new StringBuilder("");
		MessageDigest md = MessageDigest.getInstance("MD5");
		if (charset == null || charset.equals("")) {
			charset = Const.CHARSET_UTF8;
		}
		md.update(str.getBytes(charset));
		byte b[] = md.digest();
		int offset;
		for (int i = 0, size = b.length; i < size; i++) {
			offset = b[i];
			if (offset < 0)
				offset += 256;
			if (offset < 16)
				buf.append("0");
			buf.append(Integer.toHexString(offset));
		}
		return buf.toString();
	}

	/**
	 * SHA-1加密
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String sha1(String str) throws Exception {
		return sha(str, "SHA-1");
	}

	public static String sha256(String str) throws Exception {
		return sha(str, "SHA-256");
	}

	public static String sha512(String str) throws Exception {
		return sha(str, "SHA-512");
	}

	/**
	 * 按照指定的SHA加密方式加密
	 * 
	 * @param str
	 * @param keyType
	 * @return
	 * @throws Exception
	 */
	public static String sha(String str, String keyType) throws Exception {
		StringBuilder buf = new StringBuilder("");
		MessageDigest md = MessageDigest.getInstance(keyType);
		byte[] digest = md.digest(str.toString().getBytes());
		for (int i = 0, size = digest.length; i < size; i++) {
			String hex = Integer.toHexString(0xff & digest[i]);
			if (hex.length() == 1) {
				buf.append('0');
			}
			buf.append(hex);
		}
		return buf.toString();
	}
	public byte[] shaToByte(String str, String keyType) throws Exception {
		MessageDigest md = MessageDigest.getInstance(keyType);
		return md.digest(str.toString().getBytes());
	}

	/**
	 * Base64加密
	 * 
	 * @param str
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String enBase64(String str) throws UnsupportedEncodingException {
		return enBase64(str, null);
	}

	public static String enBase64(String str, String charset) throws UnsupportedEncodingException {
		if (charset == null || charset.equals("")) {
			charset = Const.CHARSET_UTF8;
		}
		byte[] bytes = str.getBytes(charset);
		return enBase64(bytes);
	}
	
	public static String enBase64(byte[] bytes) throws UnsupportedEncodingException {
		return Base64.encodeBase64String(bytes);
	}

	/**
	 * Base64解密
	 * 
	 * @param keyStr
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String deBase64(String keyStr) throws UnsupportedEncodingException {
		return deBase64(keyStr, null);
	}

	public static String deBase64(String keyStr, String charset) throws UnsupportedEncodingException {
		if (charset == null || charset.equals("")) {
			charset = Const.CHARSET_UTF8;
		}
		byte[] bytes = keyStr.getBytes(charset);
		return new String(Base64.decodeBase64(bytes));
	}
	public static byte[] deBase64ToByte(String keyStr) throws UnsupportedEncodingException {
		return Base64.decodeBase64(keyStr);
	}

}
