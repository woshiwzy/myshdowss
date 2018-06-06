package com.vm.shadowsocks.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVAnalytics;
import com.vm.shadowsocks.R;

import java.util.Locale;

public class BaseActivity extends Activity {

    View loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingView=View.inflate(this, R.layout.cover_loading,null);
    }


    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }
    public void showCover() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewGroup rootView = getWindow().getDecorView().findViewById(android.R.id.content);
                rootView.addView(loadingView);
            }
        });

    }

    public void hideCover() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewGroup rootView = getWindow().getDecorView().findViewById(android.R.id.content);
                rootView.removeView(loadingView);
            }
        });

    }

    @Override
    protected void onPause() {
        AVAnalytics.onPause(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        AVAnalytics.onResume(this);
        super.onResume();
    }
}
