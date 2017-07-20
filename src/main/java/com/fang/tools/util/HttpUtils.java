package com.fang.tools.util;

import com.google.common.base.Strings;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 2016/5/4.
 */
public class HttpUtils {
    private static final String CHARSET = "UTF-8";
    //HTTP请求静态常量
    private final static Integer CONNECTION_TIMEOUT = 15000;
    private final static Integer SO_TIMEOUT = 15000;
    private final static Integer REQUEST_TRY_NUM = 15000;
    static {
        RequestConfig config = RequestConfig.custom().setConnectTimeout(CONNECTION_TIMEOUT).setSocketTimeout(SO_TIMEOUT).build();
        httpClient = HttpClientBuilder.create().setRetryHandler(new DefaultHttpRequestRetryHandler(REQUEST_TRY_NUM, true)).setDefaultRequestConfig(config).build();
    }
    private static final CloseableHttpClient httpClient;
    private static Logger logger = Logger.getLogger(HttpUtils.class);
    public static String doGet(String url, Map<String, String> params) {
        return doGet(url, params, CHARSET);
    }
    public static String doPost(String url, Map<String, String> params) {
        return doPost(url, params, CHARSET);
    }

    /**
     * HTTP Get 获取内容
     *
     * @param url     请求的url地址 ?之前的地址
     * @param params  请求的参数
     * @param charset 编码格式
     * @return 页面内容
     */
    public static String doGet(String url, Map<String, String> params, String charset) {
        if (Strings.isNullOrEmpty(url)) {
            return null;
        }
        try {
            if (params != null && !params.isEmpty()) {
                List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
                if (url.endsWith("&"))
                {
                    url +=   EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
                }else if (url.endsWith("?"))
                {
                    url +=  EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
                }else
                {
                    url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
                }

            }
            logger.info(" get url " + url);
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpGet.abort();
                return httpGet.toString();
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, charset);
            }
            EntityUtils.consume(entity);
            response.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * HTTP Post 获取内容
     *
     * @param url     请求的url地址 ?之前的地址
     * @param params  请求的参数
     * @param charset 编码格式
     * @return 页面内容
     */
    public static String doPost(String url, Map<String, String> params, String charset) {
        if (Strings.isNullOrEmpty(url)) {
            return null;
        }
        try {
            List<NameValuePair> pairs = null;
            if (params != null && !params.isEmpty()) {
                pairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                        logger.debug(entry.getKey()+"=" + value);
                    }
                }
            }
            HttpPost httpPost = new HttpPost(url);
            if (pairs != null && pairs.size() > 0) {
                httpPost.setEntity(new UrlEncodedFormEntity(pairs, charset));
            }
            CloseableHttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                return httpPost.toString();
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, charset);
            }
            EntityUtils.consume(entity);
            response.close();
            logger.debug(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
