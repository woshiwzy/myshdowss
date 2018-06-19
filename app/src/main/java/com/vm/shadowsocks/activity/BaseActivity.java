package com.vm.shadowsocks.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVAnalytics;
import com.vm.api.APIManager;
import com.vm.shadowsocks.App;
import com.vm.shadowsocks.R;
import com.vm.shadowsocks.domain.User;
import com.wangzy.httpmodel.gson.ext.Result;

import java.util.Locale;
import java.util.Random;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.vm.shadowsocks.constant.Constant.MAX_DEFAULT_REWARD_M;

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



    public void getReard(Activity activity) {


        if (null != App.instance.getUser()) {

            User user = App.instance.getUser();

            Random random = new Random();
            int reward = random.nextInt(MAX_DEFAULT_REWARD_M);

            APIManager apiManager = new APIManager(activity);
            apiManager.rewardTraffic(user.getUuid(), String.valueOf(reward), "click ad")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Result<User>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Result<User> userResult) {

                        }
                    });


        }
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
