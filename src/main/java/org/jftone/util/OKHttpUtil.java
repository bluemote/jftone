package org.jftone.util;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class OKHttpUtil {
	private static Logger log = LoggerFactory.getLogger(OKHttpUtil.class);
	private Proxy proxy = null;
	private int timeout = 30;			//超时时间
	private OkHttpClient okHttpClient = null;
	private static final int READ_TIMEOUT = 30;
	private static final int WRITE_TIMEOUT = 30;
	public static String CONTENT_JSON = "application/json; charset=utf-8";
	public static String CONTENT_TEXT = "text/plain; charset=utf-8";
	public static String CONTENT_HTML = "text/html; charset=utf-8";

	private OKHttpUtil(boolean enableCookie) {
		OkHttpClient.Builder clientBuilder = getClientBuilder();
		if(enableCookie){
			clientBuilder.cookieJar(new CookieJar(){
				private final Map<HttpUrl, List<Cookie>> cookieStore = new HashMap<>(); 
				
				@Override
				public void saveFromResponse(HttpUrl httpUrl, List<Cookie> cookies) {
					cookieStore.put(httpUrl, cookies);
				}

				@Override
				public List<Cookie> loadForRequest(HttpUrl httpUrl) {
					List<Cookie> cookies = cookieStore.get(httpUrl.host());
		            return  null == cookies ? new ArrayList<Cookie>() : cookies;
				}
			});
		}
		okHttpClient = clientBuilder.build();
	}
	
	private static class OKHttpUtilHolder {
		private static final OKHttpUtil INSTANCE = new OKHttpUtil(false);
		private static final OKHttpUtil COOKIE_INSTANCE = new OKHttpUtil(true);
	}

	/**
	 * 获取OkHttpClient Builder
	 * 
	 * @return
	 */
	private OkHttpClient.Builder getClientBuilder() {
		OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
		clientBuilder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS); // 读取超时
		clientBuilder.connectTimeout(timeout, TimeUnit.SECONDS); // 连接超时
		clientBuilder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS); // 写入超时
		if (null != proxy) {
			clientBuilder.proxy(proxy);
		}
		return clientBuilder;
	}

	public static OKHttpUtil getInstance() {
		return getInstance(0, false, null);
	}

	public static OKHttpUtil getInstance(int timeout) {
		return getInstance(timeout, false, null);
	}
	
	public static OKHttpUtil getCookieInstance() {
		return getInstance(0, true, null);
	}

	public static OKHttpUtil getCookieInstance(int timeout) {
		return getInstance(timeout, true, null);
	}
	
	public static OKHttpUtil getProxyInstance(String host, int port) {
		return getProxyInstance(host, port, 0);
	}

	public static OKHttpUtil getProxyInstance(String host, int port, int timeout) {
		return getInstance(timeout, false, new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port)));
	}
	
	public static OKHttpUtil getInstance(int timeout, boolean enableCookie, Proxy proxy) {
		OKHttpUtil instance = enableCookie? OKHttpUtilHolder.COOKIE_INSTANCE : OKHttpUtilHolder.INSTANCE;
		if(timeout > 0){
			instance.timeout = timeout;
		}
		if(null != proxy){
			instance.proxy = proxy;
		}
		return instance;
	}

	/**
	 * 发送Get请求
	 * 
	 * @param httpUrl
	 * @return
	 * @throws IOException
	 */
	public String sendGetRequest(String httpUrl) throws IOException {
		return sendRequest(createHttpGet(httpUrl));
	}

	public String sendGetRequest(String httpUrl, IData<String, Object> headerData) throws IOException {
		return sendRequest(createHttpGet(httpUrl, headerData));
	}

	/**
	 * 发送一般的Post数据
	 * 
	 * @param httpUrl
	 * @param postStr
	 * @return
	 * @throws IOException
	 */
	public String sendPostRequest(String httpUrl, String postStr) throws IOException {
		return sendRequest(createPostRequest(httpUrl, postStr));
	}

	public String sendPostRequest(String httpUrl, String postStr, IData<String, Object> headerData)
			throws IOException {
		return sendRequest(createPostRequest(httpUrl, postStr, headerData));
	}

	public String sendPostRequest(String httpUrl, String postStr, String contentType) throws IOException {
		return sendRequest(createPostRequest(httpUrl, postStr, contentType));
	}

	public String sendPostRequest(String httpUrl, String postStr, IData<String, Object> headerData, String contentType)
			throws IOException {
		return sendRequest(createPostRequest(httpUrl, postStr, headerData, contentType));
	}

	/**
	 * 创建HTTP POST请求
	 * 
	 * @param httpUrl
	 * @param postStr
	 *            POST字符串
	 * @return
	 * @throws IOException
	 */
	public Request createPostRequest(String httpUrl, String postStr) throws IOException {
		return createPostRequest(httpUrl, postStr, null, null);
	}

	public Request createPostRequest(String httpUrl, String postStr, IData<String, Object> headerData)
			throws IOException {
		return createPostRequest(httpUrl, postStr, headerData, null);
	}

	public Request createPostRequest(String httpUrl, String postStr, String contentType) throws IOException {
		return createPostRequest(httpUrl, postStr, null, contentType);
	}

	public Request createPostRequest(String httpUrl, String postStr, IData<String, Object> headerData,
			String contentType) throws IOException {
		if (null == contentType) {
			contentType = OKHttpUtil.CONTENT_TEXT;
		}
		RequestBody body = RequestBody.create(MediaType.parse(contentType), postStr);
		Request.Builder reqBuilder = new Request.Builder();
		reqBuilder.url(httpUrl);
		reqBuilder.post(body);
		if (null != headerData && !headerData.isEmpty()) {
			Headers headers = getHeaders(headerData);
			if (null != headers) {
				reqBuilder.headers(headers);
			}
		}
		return reqBuilder.build();
	}

	/**
	 * 创建HTTP GET请求
	 * 
	 * @param httpUrl
	 * @return
	 * @throws IOException
	 */
	public Request createHttpGet(String httpUrl) throws IOException {
		return createHttpGet(httpUrl, null);
	}

	/**
	 * 创建HTTP GET请求，同时设置header参数
	 * 
	 * @param httpUrl
	 * @param headerData
	 * @return
	 * @throws IOException
	 */
	public Request createHttpGet(String httpUrl, IData<String, Object> headerData) throws IOException {
		Request.Builder reqBuilder = new Request.Builder();
		reqBuilder.url(httpUrl);
		if (null != headerData && !headerData.isEmpty()) {
			Headers headers = getHeaders(headerData);
			if (null != headers) {
				reqBuilder.headers(headers);
			}
		}
		return reqBuilder.build();
	}

	/**
	 * 发送FORM表单提交
	 * 
	 * @param httpUrl
	 * @param paramData
	 *            表单提交参数
	 * @return
	 * @throws IOException
	 */
	public String sendFormPost(String httpUrl, IData<String, Object> paramData) throws IOException {
		return sendRequest(createFormPost(httpUrl, paramData));
	}

	/**
	 * 发送带Header参数的表单提交
	 * 
	 * @param httpUrl
	 * @param paramData
	 *            表单提交参数
	 * @param headerData
	 *            header参数
	 * @return
	 * @throws IOException
	 */
	public String sendFormPost(String httpUrl, IData<String, Object> paramData, IData<String, Object> headerData)
			throws IOException {
		return sendRequest(createFormPost(httpUrl, paramData, headerData));
	}

	/**
	 * 发送 post请求（带文件）
	 * 
	 * @param httpUrl
	 *            地址
	 * @param param
	 *            参数
	 * @param files
	 *            附件
	 * @throws IOException
	 */
	public String sendFormPost(String httpUrl, IData<String, Object> paramData, List<File> files) throws IOException {
		MediaType type = MediaType.parse("application/octet-stream");
		MultipartBody.Builder builderBody = new MultipartBody.Builder();
		builderBody.setType(MultipartBody.FORM);
		for (Map.Entry<String, Object> data : paramData.entrySet()) {
			builderBody.addFormDataPart(data.getKey(), data.getValue().toString());
		}
		if (null != files && !files.isEmpty()) {
			for (File file : files) {
				RequestBody fileBody = RequestBody.create(type, file);
				builderBody.addFormDataPart("file", file.getName(), fileBody);
			}
		}
		Request request = new Request.Builder().url(httpUrl).post(builderBody.build()).build();
		return sendRequest(request);
	}

	/**
	 * 创建一个一般FORM表单HTTP POST
	 * 
	 * @param httpUrl
	 * @param paramData
	 * @return
	 * @throws IOException
	 */
	public Request createFormPost(String httpUrl, IData<String, Object> paramData) throws IOException {
		return createFormPost(httpUrl, paramData, null);
	}

	public Request createFormPost(String httpUrl, IData<String, Object> paramData, IData<String, Object> headerData)
			throws IOException {
		Request.Builder reqBuilder = new Request.Builder();
		reqBuilder.url(httpUrl);
		FormBody.Builder fbBuilder = new FormBody.Builder();
		if (null != paramData && !paramData.isEmpty()) {
			for (Map.Entry<String, Object> data : paramData.entrySet()) {
				fbBuilder.add(data.getKey(), data.getValue().toString());
			}
			reqBuilder.post(fbBuilder.build());
		}
		if (null != headerData && !headerData.isEmpty()) {
			Headers headers = getHeaders(headerData);
			if (null != headers) {
				reqBuilder.headers(headers);
			}
		}
		return reqBuilder.build();
	}

	/**
	 * 
	 * @param headerData
	 * @return
	 */
	protected Headers getHeaders(IData<String, Object> headerData) {
		Headers headers = null;
		if (null != headerData && !headerData.isEmpty()) {
			Headers.Builder hb = new Headers.Builder();
			for (Map.Entry<String, Object> header : headerData.entrySet()) {
				hb.set(header.getKey(), header.getValue().toString());
			}
			headers = hb.build();
		}
		return headers;
	}

	/**
	 * http请求
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public String sendRequest(Request request) throws IOException {
		return sendRequest(null, request);
	}

	public String sendHttpsRequest(Request request) throws Exception {
		return sendRequest(createHttpsClient(), request);
	}

	public String sendRequest(OkHttpClient httpClient, Request request) throws IOException {
		String respStr = null;
		Response response = null;
		if (null == httpClient) {
			httpClient = okHttpClient;
		}
		try {
			response = httpClient.newCall(request).execute();
			if (!response.isSuccessful()) {
				throw new IOException("HTTP请求失败:" + response);
			}
			respStr = response.body().string();
		} catch (IOException e) {
			log.error("HTTP请求错误", e);
			throw new IOException("HTTP请求错误", e);
		} finally {
			if (null != response) {
				response.close();
			}
		}
		return respStr;
	}

	/**
	 * http请求
	 * 
	 * @param httpReq[HttpPost|HttpGet]
	 * @param httpClient[CloseableHttpAsyncClient]
	 * @return
	 * @throws IOException
	 */
	public void sendAsyncRequest(final Request request, final AsyncHandler handler) throws IOException {
		sendAsyncRequest(null, request, handler);
	}

	public void sendAsyncRequest(OkHttpClient httpClient, final Request request, final AsyncHandler handler)
			throws IOException {
		if (null == httpClient) {
			httpClient = okHttpClient;
		}
		try {
			httpClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					handler.failed(e);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					handler.onResponse(response);
				}

			});
		} catch (Exception e) {
			log.error("HTTP请求错误", e);
			throw new IOException("HTTP请求错误", e);
		}
	}

	/**
	 * 创建一个可信的带CA签名证书HTTPS连接
	 * @param sslSocketFactory
	 * @return
	 * @throws Exception
	 */
	public OkHttpClient createHttpsClient(SSLSocketFactory sslSocketFactory)
			throws Exception {
		OkHttpClient.Builder clientBuilder = getClientBuilder();
		clientBuilder.sslSocketFactory(sslSocketFactory, SSLSocketUtil.getX509TrustManager());
		clientBuilder.hostnameVerifier(SSLSocketUtil.getAllTrustHostnameVerifier());
		return clientBuilder.build();

	}

	/**
	 * 创建一个默认的信任所有证书的HTTPS连接
	 * @return
	 * @throws Exception
	 */
	public OkHttpClient createHttpsClient() throws Exception {
		return createHttpsClient(SSLSocketUtil.getSSLSocketFactory());
	}

	public static interface AsyncHandler {
		/**
		 * 处理异常时，执行该方法
		 * 
		 * @return
		 */
		public void failed(IOException e);

		/**
		 * 返回码为200，成功返回执行该方法
		 * 
		 * @return
		 */
		public void onResponse(Response response) throws IOException;
	}
}
