package com.tfd.base.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * HTTP 请求工具类
 *
 * @author TangFD@HF 2018/8/30
 */
public class HttpUtils {
    public static final Gson GSON = new Gson();

    public static HttpClient getHttpClient() {
        HttpClient httpClient = new HttpClient();
        HttpConnectionManager connectionManager = httpClient.getHttpConnectionManager();
        HttpConnectionManagerParams connectionManagerParams = connectionManager.getParams();
        connectionManagerParams.setConnectionTimeout(60 * 1000);
        connectionManagerParams.setSoTimeout(120 * 1000);
        return httpClient;
    }

    public static InputStream doGetAsStream(String url) throws IOException {
        GetMethod getMethod = new GetMethod(url);
        HttpMethodParams methodParams = getMethod.getParams();
        methodParams.setParameter(HttpMethodParams.SO_TIMEOUT, 30 * 1000);
        getHttpClient().executeMethod(getMethod);

        return getMethod.getResponseBodyAsStream();
    }

    public static InputStream doGetAsStream(String url, Map<String, String> header) throws IOException {
        GetMethod getMethod = new GetMethod(url);
        if (MapUtils.isNotEmpty(header)) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                getMethod.addRequestHeader(entry.getKey(), entry.getValue());
            }
        }

        HttpMethodParams methodParams = getMethod.getParams();
        methodParams.setParameter(HttpMethodParams.SO_TIMEOUT, 30 * 1000);
        getHttpClient().executeMethod(getMethod);

        return getMethod.getResponseBodyAsStream();
    }

    public static String doGetAsString(String url) throws IOException {
        InputStream inputStream = doGetAsStream(url);
        return getString(inputStream);
    }

    public static String doGetBodyAsString(String url) throws IOException {
        GetMethod getMethod = new GetMethod(url);
        HttpMethodParams methodParams = getMethod.getParams();
        methodParams.setParameter(HttpMethodParams.SO_TIMEOUT, 30 * 1000);
        getHttpClient().executeMethod(getMethod);
        return getMethod.getResponseBodyAsString();
    }

    public static String doGetAsString(String url, Map<String, String> header) throws IOException {
        InputStream inputStream = doGetAsStream(url, header);
        return getString(inputStream);
    }

    public static <T> T doGetAsObject(String url, Class<T> clazz) throws IOException {
        String string = doGetAsString(url);
        return GSON.fromJson(string, clazz);
    }

    public static JsonObject doGetAsJson(String url) throws IOException {
        String string = doGetAsString(url);
        return getJsonObject(string);
    }

    public static InputStream doPostAsStream(String url, Map<String, String> params) throws IOException {
        PostMethod postMethod = new PostMethod(url);
        NameValuePair[] param = new NameValuePair[params.size()];
        int index = 0;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            param[index++] = new NameValuePair(entry.getKey(), entry.getValue());
        }

        postMethod.setRequestBody(param);
        HttpMethodParams methodParams = postMethod.getParams();
        methodParams.setParameter(HttpMethodParams.SO_TIMEOUT, 30 * 1000);
        getHttpClient().executeMethod(postMethod);

        return postMethod.getResponseBodyAsStream();
    }

    public static InputStream doPostAsStream(String url, Map<String, String> params, Map<String, String> header) throws IOException {
        PostMethod postMethod = new PostMethod(url);
        if (MapUtils.isNotEmpty(header)) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                postMethod.addRequestHeader(entry.getKey(), entry.getValue());
            }
        }

        NameValuePair[] param = new NameValuePair[params.size()];
        int index = 0;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            param[index++] = new NameValuePair(entry.getKey(), entry.getValue());
        }

        postMethod.setRequestBody(param);
        HttpMethodParams methodParams = postMethod.getParams();
        methodParams.setParameter(HttpMethodParams.SO_TIMEOUT, 30 * 1000);
        getHttpClient().executeMethod(postMethod);

        return postMethod.getResponseBodyAsStream();
    }

    public static String doPostAsString(String url, Map<String, String> params) throws IOException {
        InputStream inputStream = doPostAsStream(url, params);
        return getString(inputStream);
    }

    public static String doPostBodyAsString(String url, Map<String, String> params) throws IOException {
        PostMethod postMethod = new PostMethod(url);
        NameValuePair[] param = new NameValuePair[params.size()];
        int index = 0;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            param[index++] = new NameValuePair(entry.getKey(), entry.getValue());
        }

        postMethod.setRequestBody(param);
        HttpMethodParams methodParams = postMethod.getParams();
        methodParams.setParameter(HttpMethodParams.SO_TIMEOUT, 30 * 1000);
        getHttpClient().executeMethod(postMethod);
        return postMethod.getResponseBodyAsString();
    }

    public static String doPostAsString(String url, Map<String, String> params, Map<String, String> header) throws IOException {
        InputStream inputStream = doPostAsStream(url, params, header);
        return getString(inputStream);
    }

    public static <T> T doPostAsObject(String url, Map<String, String> params, Class<T> clazz) throws IOException {
        String string = doPostAsString(url, params);
        return GSON.fromJson(string, clazz);
    }

    public static JsonObject doPostAsJson(String url, Map<String, String> params) throws IOException {
        String string = doPostAsString(url, params);
        return getJsonObject(string);
    }

    public static InputStream doPostByBodyAsStream(String url, String bodyParam) throws IOException {
        PostMethod postMethod = new PostMethod(url);
        RequestEntity requestEntity = new StringRequestEntity(bodyParam, "application/json", "UTF-8");
        postMethod.setRequestEntity(requestEntity);
        HttpMethodParams methodParams = postMethod.getParams();
        methodParams.setParameter(HttpMethodParams.SO_TIMEOUT, 30 * 1000);
        getHttpClient().executeMethod(postMethod);

        return postMethod.getResponseBodyAsStream();
    }

    public static String doPostByBodyAsString(String url, String bodyParam) throws IOException {
        PostMethod postMethod = new PostMethod(url);
        RequestEntity requestEntity = new StringRequestEntity(bodyParam, "application/json", "UTF-8");
        postMethod.setRequestEntity(requestEntity);
        HttpMethodParams methodParams = postMethod.getParams();
        methodParams.setParameter(HttpMethodParams.SO_TIMEOUT, 30 * 1000);
        getHttpClient().executeMethod(postMethod);

        return getString(postMethod.getResponseBodyAsStream());
    }

    public static <T> T doPostByBodyAsObject(String url, String bodyParam, Class<T> clazz) throws IOException {
        String string = doPostByBodyAsString(url, bodyParam);
        return GSON.fromJson(string, clazz);
    }

    public static JsonObject doPostByBodyAsJson(String url, String bodyParam) throws IOException {
        String string = doPostByBodyAsString(url, bodyParam);
        return getJsonObject(string);
    }


    private static JsonObject getJsonObject(String json) {
        JsonElement jsonElement = new JsonParser().parse(json);
        return jsonElement.getAsJsonObject();
    }

    private static String getString(InputStream inputStream) {
        try {
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            return new String(data, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {
        try {
            String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx02c8ffda5e39b80d&secret=920d9c76b3320543efb67e4df27eddaf";
            System.out.println(doGetAsString(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
