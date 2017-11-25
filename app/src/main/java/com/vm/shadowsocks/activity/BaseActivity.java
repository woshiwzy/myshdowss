package com.vm.shadowsocks.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.vm.shadowsocks.R;

public class BaseActivity extends Activity {

    View loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadingView=View.inflate(this, R.layout.cover_loading,null);
    }


    public void showCover() {
        ViewGroup rootView = getWindow().getDecorView().findViewById(android.R.id.content);

        rootView.addView(loadingView);
    }

    public void hideCover() {
        ViewGroup rootView = getWindow().getDecorView().findViewById(android.R.id.content);

        rootView.removeView(loadingView);
    }


}
