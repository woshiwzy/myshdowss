package com.vm.api.helper;

import com.vm.shadowsocks.constant.Constant;
import com.vm.shadowsocks.tool.LogUtil;
import com.wangzy.httpmodel.gson.ext.Result;

import rx.functions.Func1;

public class HttpResultFunc<T> implements Func1<Result<T>, Result<T>> {

    public Result<T> call(Result<T> httpResult) {

        LogUtil.e(Constant.TAG, httpResult.getData().toString() + "");

        return httpResult;
    }
}
