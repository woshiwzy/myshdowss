package com.vm.shadowsocks;

import android.app.Application;
import android.content.SharedPreferences;

import com.vm.shadowsocks.constant.Constant;

/**
 * Created by wangzy on 2017/11/22.
 */

public class App extends Application {


    public static String tag = "ss";
    public static String test = "ss://aes-256-cfb:password1@65.49.201.127.16clouds.com/65.49.201.127:8381";

    public static App instance;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static App getInstance() {

        return instance;
    }


    public String readProxyUrl() {
        SharedPreferences preferences = getSharedPreferences("shadowsocksProxyUrl", MODE_PRIVATE);
        return preferences.getString(Constant.CONFIG_URL_KEY, "");
    }

    public void setProxyUrl(String ProxyUrl) {
        SharedPreferences preferences = getSharedPreferences("shadowsocksProxyUrl", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constant.CONFIG_URL_KEY, ProxyUrl);
        editor.apply();
    }

}
