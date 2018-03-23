package com.wangzy.httpmodel;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by wangzy on 2018/3/22.
 */

public abstract class MyNetCallBack implements Callback {

    @Override
    public final void onFailure(Call call, IOException e) {
        onFailureFinish(call, e);
    }

    @Override
    public final void onResponse(Call call, Response response) throws IOException {

        onSuccessFinish(call, response);
    }

    public void onSuccessResponseText(String responseText) {

    }


    public void onSuccessFinish(Call call, Response response) throws IOException {
    }

    public void onFailureFinish(Call call, Exception e) {
    }

    public void onStart(Call call) {
    }
}
