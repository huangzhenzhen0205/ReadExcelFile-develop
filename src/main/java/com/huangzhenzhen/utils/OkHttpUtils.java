package com.huangzhenzhen.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class OkHttpUtils {

    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType FORM_TYPE = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient
            .Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build();


    /**
     *
     * @param url
     * @param params
     * @param headers
     * @return
     * @throws IOException
     */
    public static ResponseBody get(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        Request.Builder builder = new Request.Builder();
        url += "?timestamp=" + System.currentTimeMillis();
        StringBuilder appendUrl = new StringBuilder();
        if (null != params) {
            params.forEach((key, value) -> {
                if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                    appendUrl.append("&").append(key).append("=").append(value);
                }
            });
            url = url + appendUrl.toString();
            log.info(String.format("调用地址为:%s", url));
            log.info(String.format("输入参数为:%s", JSONObject.toJSON(params)));
        }

        if (null != headers) {
            headers.forEach((key, value) -> {
                if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                    builder.addHeader(key, value);
                }
            });
        }


        Request request = builder
                .url(url)
                .get()
                .build();
        Response response = OK_HTTP_CLIENT
                .newCall(request)
                .execute();

        checkResponseStatus(response, url);
        return response.body();
    }

    /**
     * OkHttp 提交post请求
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public static ResponseBody postForm(String url, Map<String, String> params, Map<String, String> headers) throws IOException {

        Request.Builder requestBuilder = new Request.Builder();
        FormBody.Builder builder = new FormBody.Builder();
        if (null != params) {
            params.forEach((key, value) -> {
                if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                    builder.add(key, value);
                }
            });
        }

        if (null != headers) {
            headers.forEach((key, value) -> {
                if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                    requestBuilder.addHeader(key, value);
                }
            });
        }
        RequestBody requestBodyPost = builder.build();

        Request request = requestBuilder
                .url(url)
                .post(requestBodyPost)
                .build();

        Response response = OK_HTTP_CLIENT
                .newCall(request)
                .execute();

        checkResponseStatus(response, url);
        return response.body();
    }


    /**
     * postForm send json body
     *
     * @param url
     * @param json json 字符串
     * @return
     * @throws IOException
     */
    public static ResponseBody postJson(String url, String json, Map<String, String> headers) throws IOException {
        Request.Builder requestBuilder = new Request.Builder();
        RequestBody requestBody = RequestBody.create(JSON_TYPE, json);

        if (null != headers) {
            headers.forEach((key, value) -> {
                if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                    requestBuilder.addHeader(key, value);
                }
            });
        }
        Request request = requestBuilder.addHeader("Connection", "false")
                .url(url)
                .post(requestBody)
                .build();

        Response response = OK_HTTP_CLIENT
                .newCall(request)
                .execute();

        checkResponseStatus(response, url);
        return response.body();
    }

    /**
     * 检查http状态码是否非200 状态
     *
     * @param response
     * @param url
     */
    private static void checkResponseStatus(Response response, String url) {
        if (response.code() != HttpStatus.OK.value()) {
            try {
                log.error("okHTTP 请求失败 {} {}", response.code(), url);
            } finally {
                response.close();
            }
        }
    }
}

