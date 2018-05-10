package com.huangzhenzhen.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.squareup.okhttp.*;

/**
 * Created by huangzhenzhen on 2018/1/29.
 */
public class HttpUtils {

    /**
     * okhttp发送get请求
     *
     * @throws Exception
     */
    public static JSONObject getJsonResponse(String url) throws Exception {
        OkHttpClient client = new OkHttpClient();
        Request getReq = new Request.Builder().url(url).build();
        Response getResp = client.newCall(getReq).execute();
//        System.out.println(getResp.body().string());
        JSONObject jsonObject = JSON.parseObject(getResp.body().string());
//        getResp.newBuilder().build();
        return jsonObject;
    }



    /**
     * post请求方式
     *
     * @param url
     * @param jsonData
     * @return
     * @throws Exception
     */
    public static JSONObject postJsonResponse(String url, String jsonData) throws Exception {
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, jsonData);
        Request request = new Request.Builder().
                url(url).
                post(body).
                build();
        Response response = client.newCall(request).execute();
        JSONObject jsonObject = JSONObject.parseObject(response.body().string());
        System.out.println(jsonObject);
        return jsonObject;
    }
}
