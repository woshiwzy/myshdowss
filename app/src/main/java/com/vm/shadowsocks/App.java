package com.vm.shadowsocks;

import android.app.Application;
import android.os.Build;
import android.text.TextUtils;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SignUpCallback;
import com.vm.shadowsocks.activity.MainActivity;
import com.vm.shadowsocks.tool.SharePersistent;
import com.vm.shadowsocks.tool.Tool;

/**
 * Created by wangzy on 2017/11/22.
 */

public class App extends Application {


    public static String tag = "ss";
    //    public static String test = "ss://aes-256-cfb:password1@65.49.201.127:8381";
    public static App instance;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initLeancloud();
    }

    private void initLeancloud() {
        AVOSCloud.initialize(this, "CyBKvg5FWhBWJaLHb9e49NW6-gzGzoHsz", "B8fB7h8ehVmuPgt0RVhTmcei");
        AVInstallation.getCurrentInstallation().saveInBackground();
        PushService.setDefaultPushCallback(this, MainActivity.class);

        if (TextUtils.isEmpty(SharePersistent.getPerference(this, "uid"))) {
            regist();
        }

    }

    private void regist() {

        AVUser user = new AVUser();

        user.put("device", Tool.getImei(this));
        user.put("mac", Tool.getAdresseMAC(this));
        user.put("clientPhone", Build.MODEL);
        user.put("phoneVersion", Build.VERSION.RELEASE);
        user.put("country", getResources().getConfiguration().locale.getCountry());
        user.put("ip", Tool.getLocalIpAddress());
        user.put("os", "Android");
        user.setUsername(AVInstallation.getCurrentInstallation().getInstallationId());
        user.setPassword(AVInstallation.getCurrentInstallation().getInstallationId());

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(AVException e) {
                if (null == e) {
                    SharePersistent.savePreference(App.getInstance(), "uid", AVInstallation.getCurrentInstallation().getInstallationId());
                }
            }
        });
    }


    public static App getInstance() {

        return instance;
    }


}
