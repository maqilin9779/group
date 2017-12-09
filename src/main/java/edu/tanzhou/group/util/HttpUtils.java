package edu.tanzhou.group.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSONObject;

public class HttpUtils {

	private static Logger log = LoggerFactory.getLogger(HttpUtils.class);

	/**
	 * Send a get request
	 * 
	 * @param url
	 * @return response
	 * @throws IOException
	 */
	static public String get(String url) throws IOException {
		return get(url, null);
	}

	/**
	 * Send a get request
	 * 
	 * @param url
	 *            Url as string
	 * @param headers
	 *            Optional map with headers
	 * @return response Response as string
	 * @throws IOException
	 */
	static public String get(String url, Map<String, String> headers) throws IOException {
		return fetch("GET", url, null, headers);
	}

	/**
	 * Send a post request
	 * 
	 * @param url
	 *            Url as string
	 * @param body
	 *            Request body as string
	 * @param headers
	 *            Optional map with headers
	 * @return response Response as string
	 * @throws IOException
	 */
	static public String post(String url, String body, Map<String, String> headers) throws IOException {
		return fetch("POST", url, body, headers);
	}

	/**
	 * Send a post request
	 * 
	 * @param url
	 *            Url as string
	 * @param body
	 *            Request body as string
	 * @return response Response as string
	 * @throws IOException
	 */
	static public String post(String url, String body) throws IOException {
		return post(url, body, null);
	}

	/**
	 * Post a form with parameters
	 * 
	 * @param url
	 *            Url as string
	 * @param params
	 *            map with parameters/values
	 * @return response Response as string
	 * @throws IOException
	 */
	static public String postForm(String url, Map<String, String> params) throws IOException {
		return postForm(url, params, null);
	}

	/**
	 * Post a form with parameters
	 * 
	 * @param url
	 *            Url as string
	 * @param params
	 *            Map with parameters/values
	 * @param headers
	 *            Optional map with headers
	 * @return response Response as string
	 * @throws IOException
	 */
	static public String postForm(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
		// set content type
		if (headers == null) {
			headers = new HashMap<String, String>();
		}
		headers.put("Content-Type", "application/x-www-form-urlencoded");

		// parse parameters
		String body = "";
		if (params != null) {
			boolean first = true;
			for (String param : params.keySet()) {
				if (first) {
					first = false;
				} else {
					body += "&";
				}
				String value = params.get(param);
				body += URLEncoder.encode(param, "UTF-8") + "=";
				body += URLEncoder.encode(value, "UTF-8");
			}
		}

		return post(url, body, headers);
	}

	/**
	 * Send a put request
	 * 
	 * @param url
	 *            Url as string
	 * @param body
	 *            Request body as string
	 * @param headers
	 *            Optional map with headers
	 * @return response Response as string
	 * @throws IOException
	 */
	static public String put(String url, String body, Map<String, String> headers) throws IOException {
		return fetch("PUT", url, body, headers);
	}

	/**
	 * Send a put request
	 * 
	 * @param url
	 *            Url as string
	 * @return response Response as string
	 * @throws IOException
	 */
	static public String put(String url, String body) throws IOException {
		return put(url, body, null);
	}

	/**
	 * Send a delete request
	 * 
	 * @param url
	 *            Url as string
	 * @param headers
	 *            Optional map with headers
	 * @return response Response as string
	 * @throws IOException
	 */
	static public String delete(String url, Map<String, String> headers) throws IOException {
		return fetch("DELETE", url, null, headers);
	}

	/**
	 * Send a delete request
	 * 
	 * @param url
	 *            Url as string
	 * @return response Response as string
	 * @throws IOException
	 */
	static public String delete(String url) throws IOException {
		return delete(url, null);
	}

	/**
	 * Append query parameters to given url
	 * 
	 * @param url
	 *            Url as string
	 * @param params
	 *            Map with query parameters
	 * @return url Url with query parameters appended
	 * @throws IOException
	 */
	static public String appendQueryParams(String url, Map<String, String> params) throws IOException {
		String fullUrl = url;
		if (params != null) {
			boolean first = (fullUrl.indexOf('?') == -1);
			for (String param : params.keySet()) {
				if (first) {
					fullUrl += '?';
					first = false;
				} else {
					fullUrl += '&';
				}
				String value = params.get(param);
				fullUrl += URLEncoder.encode(param, "UTF-8") + '=';
				fullUrl += URLEncoder.encode(value, "UTF-8");
			}
		}

		return fullUrl;
	}

	/**
	 * Retrieve the query parameters from given url
	 * 
	 * @param url
	 *            Url containing query parameters
	 * @return params Map with query parameters
	 * @throws IOException
	 */
	static public Map<String, String> getQueryParams(String url) throws IOException {
		Map<String, String> params = new HashMap<String, String>();

		int start = url.indexOf('?');
		while (start != -1) {
			// read parameter name
			int equals = url.indexOf('=', start);
			String param = "";
			if (equals != -1) {
				param = url.substring(start + 1, equals);
			} else {
				param = url.substring(start + 1);
			}

			// read parameter value
			String value = "";
			if (equals != -1) {
				start = url.indexOf('&', equals);
				if (start != -1) {
					value = url.substring(equals + 1, start);
				} else {
					value = url.substring(equals + 1);
				}
			}

			params.put(URLDecoder.decode(param, "UTF-8"), URLDecoder.decode(value, "UTF-8"));
		}

		return params;
	}

	/**
	 * Returns the url without query parameters
	 * 
	 * @param url
	 *            Url containing query parameters
	 * @return url Url without query parameters
	 * @throws IOException
	 */
	static public String removeQueryParams(String url) throws IOException {
		int q = url.indexOf('?');
		if (q != -1) {
			return url.substring(0, q);
		} else {
			return url;
		}
	}

	/**
	 * Send a request
	 * 
	 * @param method
	 *            HTTP method, for example "GET" or "POST"
	 * @param url
	 *            Url as string
	 * @param body
	 *            Request body as string
	 * @param headers
	 *            Optional map with headers
	 * @return response Response as string
	 * @throws IOException
	 */
	static public String fetch(String method, String url, String body, Map<String, String> headers) throws IOException {
		// connection
		URL u = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		conn.setConnectTimeout(10000);
		conn.setReadTimeout(10000);

		// method
		if (method != null) {
			conn.setRequestMethod(method);
		}

		// headers
		if (headers != null) {
			for (String key : headers.keySet()) {
				conn.addRequestProperty(key, headers.get(key));
			}
		}

		// body
		if (body != null) {
			conn.setDoOutput(true);
			OutputStream os = conn.getOutputStream();
			os.write(body.getBytes());
			os.flush();
			os.close();
		}

		// response
		InputStream is = conn.getInputStream();
		String response = streamToString(is);
		is.close();

		// handle redirects
		if (conn.getResponseCode() == 301) {
			String location = conn.getHeaderField("Location");
			return fetch(method, location, body, headers);
		}

		return response;
	}

	/**
	 * Read an input stream into a string
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	static public String streamToString(InputStream in) throws IOException {
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		for (int n; (n = in.read(b)) != -1;) {
			out.append(new String(b, 0, n));
		}
		return out.toString();
	}

	/**
	 * 证书信任管理器（用于https请求）
	 * 
	 */
	static class MyX509TrustManager implements X509TrustManager {

		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

	/**
	 * 发起https请求并获取结果
	 * 
	 * @param requestUrl
	 *            请求地址
	 * @param requestMethod
	 *            请求方式（GET、POST）
	 * @param data
	 *            提交的数据
	 * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
	 */
	public static JSONObject httpsRequest(String requestUrl, String requestMethod, String data) {
		JSONObject jsonObject = null;
		StringBuffer buffer = new StringBuffer();
		try {
			// 创建SSLContext对象，并使用我们指定的信任管理器初始化
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			URL url = new URL(requestUrl);
			HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();
			httpUrlConn.setSSLSocketFactory(ssf);

			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);
			// 设置请求方式（GET/POST）
			httpUrlConn.setRequestMethod(requestMethod);

			if ("GET".equalsIgnoreCase(requestMethod))
				httpUrlConn.connect();

			// 当有数据需要提交时
			if (null != data) {
				OutputStream outputStream = httpUrlConn.getOutputStream();
				// 注意编码格式，防止中文乱码
				outputStream.write(data.getBytes("UTF-8"));
				outputStream.close();
			}

			// 将返回的输入流转换成字符串
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			// 释放资源
			inputStream.close();
			inputStream = null;
			httpUrlConn.disconnect();
			jsonObject = JSONObject.parseObject(buffer.toString());
		} catch (ConnectException ce) {
			log.error("Weixin server connection timed out.");
		} catch (Exception e) {
			log.error("https request error:{}", e);
		}
		return jsonObject;
	}
	
	/**
	 * 发起http请求并获取结果
	 * 
	 * @param requestUrl
	 *            请求地址
	 * @param requestMethod
	 *            请求方式（GET、POST）
	 * @param data
	 *            提交的数据
	 * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
	 */
	public static JSONObject httpRequest(String requestUrl, String requestMethod, String data) {
		JSONObject jsonObject = null;
		StringBuffer buffer = new StringBuffer();
		try {
			

			URL url = new URL(requestUrl);
			HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();			

			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);
			// 设置请求方式（GET/POST）
			httpUrlConn.setRequestMethod(requestMethod);

			if ("GET".equalsIgnoreCase(requestMethod))
				httpUrlConn.connect();

			// 当有数据需要提交时
			if (null != data) {
				OutputStream outputStream = httpUrlConn.getOutputStream();
				// 注意编码格式，防止中文乱码
				outputStream.write(data.getBytes("UTF-8"));
				outputStream.close();
			}

			// 将返回的输入流转换成字符串
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			// 释放资源
			inputStream.close();
			inputStream = null;
			httpUrlConn.disconnect();
			jsonObject = JSONObject.parseObject(buffer.toString());
		} catch (ConnectException ce) {
			log.error("Weixin server connection timed out.");
		} catch (Exception e) {
			log.error("https request error:{}", e);
		}
		return jsonObject;
	}
	
	public static void main(String[] args) {

	}
}
