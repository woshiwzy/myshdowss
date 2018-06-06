package com.vm.api.helper;


import com.wangzy.httpmodel.gson.ext.Result;

import rx.functions.Func1;

public class MyHttpFun<T> implements Func1<Result<T>,Result<T>> {

    @Override
    public Result<T> call(Result<T> tResult) {
        return null;
    }
}
