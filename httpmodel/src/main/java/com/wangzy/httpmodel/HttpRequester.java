package com.wangzy.httpmodel;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by wangzy on 2018/3/22.
 */

public class HttpRequester {

    public static OkHttpClient okHttpClient = null;
    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final int time_out = 30;

    static {
        okHttpClient = new OkHttpClient();
        okHttpClient.newBuilder().connectTimeout(time_out, TimeUnit.SECONDS);
        okHttpClient.newBuilder().readTimeout(time_out, TimeUnit.SECONDS);
        okHttpClient.newBuilder().writeTimeout(time_out, TimeUnit.SECONDS);
    }

    public static Call get(String path, Headers headers, final MyNetCallBack netCallBack) {

        if (null == headers) {
            headers = Headers.of(new HashMap<String, String>());
        }

        Request request = new Request.Builder().get().url(path).headers(headers).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(netCallBack);
        netCallBack.onStart(call);
        return call;
    }

    public static Call postJson(String path, String json, final MyNetCallBack netCallBack) {

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(path).post(body).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(netCallBack);
        netCallBack.onStart(call);
        return call;
    }


    public static Call putJson(String url, String jsonBody, final MyNetCallBack myNetCallBack, Headers headers) {

        RequestBody body = RequestBody.create(JSON, jsonBody);
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .headers(headers)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(myNetCallBack);
        myNetCallBack.onStart(call);
        return call;
    }


    public static Call postHashMap(String path, HashMap<String, String> hashMap, final MyNetCallBack netCallBack) {

        FormBody.Builder body = new FormBody.Builder();
        for (Map.Entry<String, String> entry : hashMap.entrySet()) {
            body.add(entry.getKey(), entry.getValue());
        }
        Request request = new Request.Builder().url(path).post(body.build()).build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(netCallBack);

        netCallBack.onStart(call);

        return call;
    }


    public static Call postUpload(String path, HashMap<String, String> param, File[] files, String[] fileKeys, Headers headers, final MyNetCallBack myNetCallBack) {

        String boundary = "xx--------------------------------------------------------------xx";
        MultipartBody.Builder mBodyBuilder = new MultipartBody.Builder(boundary).setType(MultipartBody.FORM);

        for (Map.Entry<String, String> entry : param.entrySet()) {
            mBodyBuilder.addFormDataPart(entry.getKey(), entry.getValue());
        }

        for (int i = 0, isize = files.length; i < isize; i++) {
            RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), files[i]);
            mBodyBuilder.addFormDataPart(fileKeys[i], files[i].getName(), fileBody);
        }


        Request request = new Request.Builder().url(path).post(mBodyBuilder.build()).headers(headers).build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(myNetCallBack);
        return call;
    }


    public static Response postUpload(String path, HashMap<String, String> param, File[] files, String[] fileKeys, Headers headers) throws IOException {

        String boundary = "xx--------------------------------------------------------------xx";
        MultipartBody.Builder mBodyBuilder = new MultipartBody.Builder(boundary).setType(MultipartBody.FORM);

        for (Map.Entry<String, String> entry : param.entrySet()) {
            mBodyBuilder.addFormDataPart(entry.getKey(), entry.getValue());
        }

        for (int i = 0, isize = files.length; i < isize; i++) {
            RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), files[i]);
            mBodyBuilder.addFormDataPart(fileKeys[i], files[i].getName(), fileBody);
        }

        Request request = new Request.Builder().url(path).post(mBodyBuilder.build()).headers(headers).build();

        Call call = okHttpClient.newCall(request);

        return call.execute();
    }

}
