package com.wangzy.httpmodel;

import android.util.Log;

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

        String ret = response.body().string();
        onSuccessFinish(call, ret);
        onSuccessResponseText(ret, call);
    }

    public void onSuccessResponseText(String responseText, Call call) {
    }


    public void onSuccessFinish(Call call, String ret) {
    }

    public void onFailureFinish(Call call, Exception e) {
        Log.i("", " " + e.getMessage());
    }

    public void onStart(Call call) {
    }
}
