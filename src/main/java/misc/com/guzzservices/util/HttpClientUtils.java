/**
 * 
 */
package com.guzzservices.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.guzz.util.FileUtil;
import org.guzz.util.StringUtil;

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class HttpClientUtils {

	static final String header = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; SV1; .NET CLR 1.1.4322; CIBA; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)";

	public static final String PARAM_NAKED_URL = "__naked_url_";

	/**
	 * 解析给定URL中的请求地址和参数（queryString），存入一个Map中。请求地址存放的key为 {@value #PARAM_NAKED_URL} 。
	 * 
	 * @return 解析结果存储的Map。如果url不含有参数，返回null。
	 */
	public static Map<String, String> parseParamsFromUrl(String url) {
		if (url == null)
			return null;
		url = url.trim();
		if (url.length() == 0)
			return null;

		int queryPos = url.indexOf('?');
		if (queryPos == -1)
			return null;

		HashMap<String, String> map = new HashMap<String, String>();
		String nurl = url.substring(0, queryPos);

		int p2 = queryPos + 1;
		if (p2 < url.length()) {
			// has query params
			String paramS = url.substring(p2);
			String[] params = StringUtil.splitString(paramS, "&");

			for (String param : params) {
				if (param.length() == 0)
					continue;

				int pos = param.indexOf('=');
				if (pos == 0)
					continue;
				if (pos == -1) {
					if (param.charAt(0) != '?') {
						map.put(param, "");
					}
					continue;
				}

				String key = param.substring(0, pos);
				if (key.equals("?")) {
					continue;
				}

				if (pos + 1 >= param.length()) {
					// no value
					map.put(key, "");
				} else {
					map.put(key, param.substring(pos + 1));
				}
			}
		}

		map.put(PARAM_NAKED_URL, nurl);

		return map;
	}

	public static String post(String url, Map<String, String> params, String encoding) throws IOException {
		Map<String, String> paramsInUrl = parseParamsFromUrl(url);
		if (paramsInUrl != null) {
			url = paramsInUrl.remove(HttpClientUtils.PARAM_NAKED_URL);
			params.putAll(paramsInUrl);
		}

		DefaultHttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter("User-Agent", header);
		httpclient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);

		HttpResponse response = null;
		HttpEntity entity = null;

		// Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; CIBA; .NET CLR 1.1.4322; .NET CLR 2.0.50727)
		HttpPost httpost = new HttpPost(url);

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		if (params != null) {
			for (Map.Entry<String, String> e : params.entrySet()) {
				nvps.add(new BasicNameValuePair(e.getKey(), e.getValue()));
			}
		}

		UrlEncodedFormEntity en = new UrlEncodedFormEntity(nvps, encoding);

		httpost.setEntity(en);
		response = httpclient.execute(httpost);
		entity = response.getEntity();

		String content = null;

		if (entity != null) {
			content = FileUtil.readText(entity.getContent(), encoding);
		}

		httpost.abort();

		return content;
	}

	public static String get(String url, Map<String, String> params, String encoding) throws IOException {
		Map<String, String> paramsInUrl = parseParamsFromUrl(url);
		if (paramsInUrl != null) {
			url = paramsInUrl.remove(HttpClientUtils.PARAM_NAKED_URL);
			params.putAll(paramsInUrl);
		}

		DefaultHttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter("User-Agent", header);

		if (params != null) {
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();

			for (Map.Entry<String, String> e : params.entrySet()) {
				nvps.add(new BasicNameValuePair(e.getKey(), e.getValue()));
			}

			if (!url.endsWith("?")) {
				url += "?";
			}

			String paramString = URLEncodedUtils.format(nvps, encoding);
			url += paramString;
		}

		HttpGet g = new HttpGet(url);
		HttpResponse response = httpclient.execute(g);
		HttpEntity entity = response.getEntity();

		String content = null;

		if (entity != null) {
			content = FileUtil.readText(entity.getContent(), encoding);
		}

		g.abort();

		return content;
	}

	public static String get(String url, Map<String, String> params, String encoding, int timeoutInMills) throws IOException {
		Map<String, String> paramsInUrl = parseParamsFromUrl(url);
		if (paramsInUrl != null) {
			url = paramsInUrl.remove(HttpClientUtils.PARAM_NAKED_URL);
			params.putAll(paramsInUrl);
		}

		DefaultHttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter("User-Agent", header);
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeoutInMills);// 连接时间
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, timeoutInMills);// 数据传输时间

		if (params != null) {
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();

			for (Map.Entry<String, String> e : params.entrySet()) {
				nvps.add(new BasicNameValuePair(e.getKey(), e.getValue()));
			}

			if (!url.endsWith("?")) {
				url += "?";
			}

			String paramString = URLEncodedUtils.format(nvps, encoding);
			url += paramString;
		}

		HttpGet g = new HttpGet(url);
		HttpResponse response = httpclient.execute(g);
		HttpEntity entity = response.getEntity();

		String content = null;

		if (entity != null) {
			content = FileUtil.readText(entity.getContent(), encoding);
		}

		g.abort();

		return content;
	}
}
