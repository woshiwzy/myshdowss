package com.wangzy.httpmodel;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by wangzy on 2018/3/22.
 */

public class HttpRequester {

    public static OkHttpClient okHttpClient = null;
//    http://65.49.201.127:7000/list_servers
    public static String baseHost = "http://65.49.201.127:7000/";
    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");

    public static final int time_out = 60;

    static {
        okHttpClient = new OkHttpClient();

        okHttpClient.newBuilder().connectTimeout(time_out, TimeUnit.SECONDS);
        okHttpClient.newBuilder().readTimeout(time_out, TimeUnit.SECONDS);
        okHttpClient.newBuilder().writeTimeout(time_out, TimeUnit.SECONDS);
    }

    public static Call get(String path, final MyNetCallBack netCallBack) {
        Request request = new Request.Builder().url(baseHost + path).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(netCallBack);
        netCallBack.onStart(call);
        return call;
    }

    public static Call postJson(String path, String json, final MyNetCallBack netCallBack) {

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Request request = new Request.Builder().url(baseHost + path).post(body).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(netCallBack);
        netCallBack.onStart(call);
        return call;

    }

    public static Call postHashMap(String path, HashMap<String, String> hashMap, final MyNetCallBack netCallBack) {

        FormBody.Builder body = new FormBody.Builder();
        for (Map.Entry<String, String> entry : hashMap.entrySet()) {
            body.add(entry.getKey(), entry.getValue());
        }
        Request request = new Request.Builder().url(baseHost + path).post(body.build()).build();


        Call call = okHttpClient.newCall(request);
        call.enqueue(netCallBack);

        netCallBack.onStart(call);

        return call;
    }

    public void postUpload(String path, File file) {

        Request request = new Request.Builder().url(baseHost + path).post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file)).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });

    }

}
