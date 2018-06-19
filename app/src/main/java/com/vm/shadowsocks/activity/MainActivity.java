package com.vm.shadowsocks.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.feedback.FeedbackAgent;
import com.avos.avoscloud.utils.StringUtils;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.vm.api.APIManager;
import com.vm.api.RetrofitHelper;
import com.vm.api_okapi.NetInterface;
import com.vm.service.MyIntentService;
import com.vm.shadowsocks.App;
import com.vm.shadowsocks.R;
import com.vm.shadowsocks.constant.Constant;
import com.vm.shadowsocks.core.AppInfo;
import com.vm.shadowsocks.core.AppProxyManager;
import com.vm.shadowsocks.core.LocalVpnService;
import com.vm.shadowsocks.core.ProxyConfig;
import com.vm.shadowsocks.domain.EventMessage;
import com.vm.shadowsocks.domain.Server;
import com.vm.shadowsocks.domain.User;
import com.vm.shadowsocks.tool.LogUtil;
import com.vm.shadowsocks.tool.MyAnimationUtils;
import com.vm.shadowsocks.tool.SharePersistent;
import com.vm.shadowsocks.tool.Tool;
import com.wangzy.httpmodel.HttpRequester;
import com.wangzy.httpmodel.MyNetCallBackExtend;
import com.wangzy.httpmodel.gson.ext.Result;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.TimerTask;

import okhttp3.Call;

import static com.vm.shadowsocks.constant.Constant.TAG;

public class MainActivity extends BaseActivity implements
        View.OnClickListener {

    private TextView textViewServerCountryName, textViewCurrentConnectCount;
    private ImageView toggleButton;
    private ImageView imageViewCountry;
    private ImageView imageViewMenu;
    private TextView textViewStatus;
    private TextView textViewSent;
    private TextView textViewReceived;
    private TextView textViewRemain;
    private ImageView imageViewUp;
    private ImageView imageVIewDown;

    private View drawView;

    private int INT_GO_SELECT = 100;
    public static Server selectDefaultServer;
    public Animation animationRotate;
    public boolean enable = true;
    private String disableMessage = "";
    private long remain = 0;
    private AdView adView;

    private TextView textViewTotal;
    private View tipLayout;
    private APIManager apiManager = null;
    private IInAppBillingService mService;
    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            LogUtil.e(TAG, "IInAppBillingServiceon.ServiceDisconnected");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
            LogUtil.e(TAG, "IInAppBillingService.onServiceConnected");
        }
    };
    private DecimalFormat nf = new DecimalFormat("###,##0.00");
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //google 内购
//        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
//        serviceIntent.setPackage("com.android.vending");
//        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);


        //=======bind service
        animationRotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        animationRotate.setRepeatCount(Animation.INFINITE);
        animationRotate.setRepeatMode(Animation.RESTART);
        animationRotate.setInterpolator(new LinearInterpolator());

        //init all view
        initView();


        if (null == App.instance.getUser() || App.instance.getUser().isShowad()) {
            initGoogleAd();
            initFacebookAd();
        }

        apiManager = new APIManager(this);

        EventBus.getDefault().register(this);

        showTipLayout();
        startUpdateService();
    }


    /**
     * 初始化Google Ad
     */
    private void initGoogleAd() {

        //===init goole ad=======
        MobileAds.initialize(this, "ca-app-pub-9033563274040080~1800036213");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-9033563274040080/2783505895");
//        AdRequest.Builder.addTestDevice("A597024318FAD982551E3EEDA0E1D2C8") to get test ads on this device.
        mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("A597024318FAD982551E3EEDA0E1D2C8").build());
        mInterstitialAd.setAdListener(new com.google.android.gms.ads.AdListener() {

            @Override
            public void onAdLoaded() {
                LogUtil.e(Constant.TAG, "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                LogUtil.e(Constant.TAG, "onAdFailedToLoad google:" + errorCode);
            }

            @Override
            public void onAdClicked() {
                LogUtil.e(Constant.TAG, "onAdClicked:");
                getReard(MainActivity.this);
            }

            @Override
            public void onAdOpened() {
                LogUtil.e(Constant.TAG, " onAdOpened onAdFailedToLoad:");
            }
        });

    }

    /**
     * init fb ad sdk
     */
    private void initFacebookAd() {
        adView = new AdView(this, "870289033159365_870296759825259", AdSize.BANNER_HEIGHT_50);
        adView.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                LogUtil.e(TAG, " face book ad error:" + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                LogUtil.e(TAG, "Facebook loaded");
            }

            @Override
            public void onAdClicked(Ad ad) {
                LogUtil.e(TAG, "clicked fb=======");
                getReard(MainActivity.this);

            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }
        });

        LinearLayout adContainer = findViewById(R.id.banner_container);
        adContainer.addView(adView);
        adView.loadAd();
    }


    @Override
    protected void onPostResume() {
        updateDataUsed();
        super.onPostResume();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage event) {
        if (event.type == EventMessage.TYPE_MSG_REGIST) {
            updateDataUsed();
        } else {
            if (null != textViewSent && null != textViewReceived) {

                long sent = Math.abs(event.sent / 1024);
                textViewSent.setText(sent + " KB/S");
                if (sent > 0) {
                    imageViewUp.setImageResource(R.drawable.icon_up_go);
                } else {
                    imageViewUp.setImageResource(R.drawable.icon_up);
                }
                long receid = Math.abs(event.received / 1024);
                if (receid > 0) {
                    imageVIewDown.setImageResource(R.drawable.icon_down_go);
                } else {
                    imageVIewDown.setImageResource(R.drawable.icon_down);
                }
                textViewReceived.setText(receid + " KB/S");
            }
        }


    }


    private void initView() {
        setContentView(R.layout.activity_main);
        LocalVpnService.addOnStatusChangedListener(statusChangedListener);

        findViewById(R.id.imageViewCar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


        imageViewUp = findViewById(R.id.imageViewUp);
        imageVIewDown = findViewById(R.id.imageViewDown);


        textViewRemain = findViewById(R.id.textViewRemain);

        tipLayout = findViewById(R.id.tipLayout);

        imageViewCountry = findViewById(R.id.imageViewCountry);
        textViewStatus = findViewById(R.id.textViewStatus);
        textViewTotal = findViewById(R.id.textViewTotalUsed);

        textViewReceived = findViewById(R.id.textViewReceived);
        textViewSent = findViewById(R.id.textViewSent);

        toggleButton = findViewById(R.id.toggleButton);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (null != App.instance.getUser() && App.instance.getUser().isEnable()) {

                    if (isZh(MainActivity.this)) {
                        if (LocalVpnService.IsRunning == false) {

                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("你是中国的用户吗？")
                                    .setMessage("当前系统语言是中文，如果你是中国用户根据相关法律不能给你提供服务！")
                                    .setPositiveButton("我是在中国的用户", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {


                                        }
                                    })
                                    .setNegativeButton("我不是中国的用户，当前系统是中文", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            startOrStopVpn(!LocalVpnService.IsRunning);
                                        }
                                    })
                                    .show();

                        } else {
                            startOrStopVpn(!LocalVpnService.IsRunning);
                        }
                    } else {
                        startOrStopVpn(!LocalVpnService.IsRunning);
                    }
//                    startOrStopVpn(!LocalVpnService.IsRunning);
                } else {
                    Tool.ToastShow(MainActivity.this, R.string.server_error);
                }
            }
        });

        findViewById(R.id.ProxyUrlLayout).setOnClickListener(this);
        textViewServerCountryName = findViewById(R.id.textViewServerCountryName);
        textViewCurrentConnectCount = findViewById(R.id.textViewCurrentConnectCount);
        drawView = findViewById(R.id.linearLayoutDraw);

        imageViewMenu = findViewById(R.id.imageViewMenu);

        imageViewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageViewMenu.setImageResource(R.drawable.selector_back);

                if (drawView.getVisibility() == View.VISIBLE) {
                    hideMenu();
                } else {
                    MyAnimationUtils.animationShowView(drawView, AnimationUtils.loadAnimation(MainActivity.this, R.anim.in_to_left));
                }

            }
        });


        findViewById(R.id.textViewNews).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMenu();
                Tool.startActivity(MainActivity.this, MessagesActivity.class);
            }
        });

        findViewById(R.id.textViewSupport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMenu();
                Tool.startActivity(MainActivity.this, AdActivity.class);
            }
        });

        findViewById(R.id.textViewRewardHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMenu();
                Tool.startActivity(MainActivity.this, RewardListActivity.class);
            }
        });

        findViewById(R.id.textViewFeedBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMenu();
                FeedbackAgent agent = new FeedbackAgent(MainActivity.this);
                agent.startDefaultThreadActivity();
            }
        });

        findViewById(R.id.textViewGetTraffic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMenu();
//                Tool.ToastShow(MainActivity.this,R.string.select_server_befor);
//                Tool.startActivity(MainActivity.this, MyGoogleAdActivity.class);
                Tool.startActivity(MainActivity.this, MyFaceBookAdActivity.class);

            }
        });

        findViewById(R.id.imageViewAsk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tool.startActivity(MainActivity.this, AskActivity.class);
            }
        });


        findViewById(R.id.viewTouchHide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMenu();
            }
        });

        ((TextView) findViewById(R.id.textViewTitle)).setText(getResources().getText(R.string.app_name) + " " + Tool.getVersionName(this));

        try {

            AVUser currentUser = AVUser.getCurrentUser();
            if (null != currentUser) {

//                enable = currentUser.getBoolean("enable");
//                disableMessage = currentUser.getString("disableMessage");
//                remain = currentUser.getLong("remaining_bytes");
//                long remainingM = (remain / 1024);
//
//                if (remainingM <= 0) {
//                    enable = false;
//                }
            }
            PushService.setDefaultPushCallback(this, MainActivity.class);

        } catch (Exception e) {
            LogUtil.e(TAG, "Set up push exception:" + e);
        }

        updateDataUsed();
        //Pre-App Proxy
        if (AppProxyManager.isLollipopOrAbove) {
            new AppProxyManager(this);
        }


    }

    private void showTipLayout() {
        MyAnimationUtils.animationShowView(tipLayout, AnimationUtils.loadAnimation(this, R.anim.fade_in));
        tipLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideTipLayout();
            }
        }, 4 * 1000);
    }


    private void hideTipLayout() {
        MyAnimationUtils.animationHideview(tipLayout, AnimationUtils.loadAnimation(this, R.anim.fade_out));
    }


    private void updateDataUsed() {

        float total = 0;
        float remain = 0;

        if (null != App.instance.getUser()) {
            total = (App.instance.getUser().getUsedByte() / 1024.0f);
            remain = (App.instance.getUser().getRemaining_bytes()) / 1024.0f;
        }

        if (null == textViewTotal) {
            return;
        }

        final float finalTotal = total;
        final float finalRemain = remain;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String ret = String.format(getResources().getString(R.string.total_used), nf.format(finalTotal) + " M");
                textViewTotal.setText(getResources().getString(R.string.remaining));

//                String ret2 = String.format(getResources().getString(R.string.remaining_traffic), nf.format(finalRemain) + " M");
                String ret2 = nf.format(finalRemain) + " M";
                textViewRemain.setText(ret2);

                User user = App.instance.getUser();
                if (null != user && user.getRemaining_bytes() <= 0) {

                    LocalVpnService.IsRunning = false;
                    if (isZh(MainActivity.this)) {
                        Tool.ToastShow(MainActivity.this, user.getDisableMessageCn());
                    } else {
                        Tool.ToastShow(MainActivity.this, user.getDisableMessage());
                    }
                }
            }
        });

    }


    private void hideMenu() {
        imageViewMenu.setImageResource(R.drawable.selector_menu);
        MyAnimationUtils.animationHideview(drawView, AnimationUtils.loadAnimation(MainActivity.this, R.anim.out_to_lef));
    }

    @Override
    public void onBackPressed() {
        if (drawView.getVisibility() == View.VISIBLE) {
            imageViewMenu.setImageResource(R.drawable.selector_menu);
            MyAnimationUtils.animationHideview(drawView, AnimationUtils.loadAnimation(MainActivity.this, R.anim.out_to_lef));
            return;
        }
        super.onBackPressed();
    }


    private void startOrStopVpn(boolean isChecked) {
        if (!enable) {
            if (!com.avos.avoscloud.utils.StringUtils.isBlankString(disableMessage)) {
                Toast.makeText(this, disableMessage, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, " Sorry, vpn service not enable for you!", Toast.LENGTH_LONG).show();
            }
            return;
        }

        if (isChecked && null == selectDefaultServer) {
            findViewById(R.id.ProxyUrlLayout).startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake));
            Toast.makeText(this, R.string.select_server, Toast.LENGTH_SHORT).show();
            return;
        }


        if (LocalVpnService.IsRunning != isChecked) {
//                    toggleButton.setEnabled(false);
            if (isChecked) {


                Intent intent = LocalVpnService.prepare(MainActivity.this);
                if (intent == null) {
                    ProxyConfig.Instance.globalMode = true;
                    startVPNService();
                } else {
                    startActivityForResult(intent, Constant.START_VPN_SERVICE_REQUEST_CODE);
                }

            } else {
                LocalVpnService.IsRunning = false;
            }
        }


    }


    @Override
    public void onClick(View v) {
        if (LocalVpnService.IsRunning) {
            Tool.toast(R.string.select_server_befor, this);
            return;
        }

        Intent intent = new Intent(MainActivity.this, HostListActivity.class);
        startActivityForResult(intent, INT_GO_SELECT);

    }

    private void startVPNService() {

        if (!Tool.isValidUrl(LocalVpnService.ProxyUrl)) {
            Tool.toast(R.string.err_invalid_url, this);
            return;
        }
        startService(new Intent(this, LocalVpnService.class));
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (AppProxyManager.isLollipopOrAbove) {
            if (AppProxyManager.Instance.proxyAppInfo.size() != 0) {
                String tmpString = "";
                for (AppInfo app : AppProxyManager.Instance.proxyAppInfo) {
                    tmpString += app.getAppLabel() + ", ";
                }
                Log.e(App.Companion.getTag(), "tempStr:" + tmpString);
            }
        }

        if (LocalVpnService.IsRunning) {
            toggleButton.setImageResource(R.drawable.icon_stop);
        } else {
            toggleButton.setImageResource(R.drawable.icon_start);
        }

        updateDataUsed();

        if (null != App.instance.getUser() && !StringUtils.isBlankString(App.instance.getUser().getPersonalMsg())) {
            ((TextView) findViewById(R.id.textViewAllTip)).setText(App.instance.getUser().getPersonalMsg());
            showTipLayout();
        }


        if (null == App.instance.getUser() || App.instance.getUser().isShowad()) {

            if (null != mInterstitialAd && mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                LogUtil.d(Constant.TAG, " face  ad The interstitial wasn't loaded yet.");
            }
            findViewById(R.id.viewAdLine).setVisibility(View.VISIBLE);
            findViewById(R.id.textViewGetTraffic).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.viewAdLine).setVisibility(View.GONE);
            findViewById(R.id.textViewGetTraffic).setVisibility(View.GONE);
        }

    }

    @Override
    protected void onDestroy() {


        if (null == App.instance.getUser() || App.instance.getUser().isShowad()) {


            if (null != mInterstitialAd && mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                LogUtil.d(Constant.TAG, "The interstitial wasn't loaded yet.");
            }

            if (null != adView && adView != null) {
                adView.destroy();
            }
        }


        LocalVpnService.removeOnStatusChangedListener(statusChangedListener);
//
//        if (mService != null) {
//            unbindService(mServiceConn);
//        }

        EventBus.getDefault().unregister(this);

        super.onDestroy();

    }

    private void updateInfo(final Server server) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                textViewServerCountryName.setText(server.getName());
                textViewCurrentConnectCount.setText(String.format(getResources().getString(R.string.online_pre), String.valueOf(server.getOnline())));

                if (StringUtils.isBlankString(server.getHost())) {
                    LocalVpnService.ProxyUrl = selectDefaultServer.getBase64str();
                } else {
                    LocalVpnService.ProxyUrl = selectDefaultServer.toString();
                }


            }
        });
    }


    LocalVpnService.onStatusChangedListener statusChangedListener = new LocalVpnService.onStatusChangedListener() {
        @Override
        public void onStatusChanged(String status, Boolean isRunning) {
            Log.i(App.Companion.getTag(), "status:" + status + " isrunning:" + isRunning);
            onLogReceived(status);
            textViewStatus.setText((getResources().getString(R.string.app_name)) + ":" + getResources().getString(isRunning ? R.string.connected : R.string.connected_not));

            if (isRunning) {

                imageViewCountry.startAnimation(animationRotate);
                toggleButton.setImageResource(R.drawable.icon_stop);

                online(selectDefaultServer.getId());


                if (null != selectDefaultServer) {
                    String methid = selectDefaultServer.getMethod();
                    int port = selectDefaultServer.getPort();
                    saveLog(port, methid);
                } else {
                    saveLog(0, "no method");
                }

                AVAnalytics.onEvent(MainActivity.this, "Start Proxy");

            } else {

                if (null != selectDefaultServer) {
                    offline(selectDefaultServer.getId());
                }
                imageViewCountry.clearAnimation();
                toggleButton.setImageResource(R.drawable.icon_start);
                AVAnalytics.onEvent(MainActivity.this, "Stop Proxy");

            }


            updateDataUsed();
        }

        @Override
        public void onLogReceived(String logString) {
            Log.e(App.Companion.getTag(), "log----->" + logString);
        }
    };


    private void startUpdateService(){
        Intent service=new Intent(MainActivity.this, MyIntentService.class);
        startService(service);
    }

    private void online(String hostId) {

        NetInterface.online(hostId, new MyNetCallBackExtend<Server>(Server.class, false) {
            @Override
            public void onResponseResult(Result<Server> result, Call call) {
                if (null != selectDefaultServer) {
                    selectDefaultServer.setOnline(result.getData().getOnline());
                    updateInfo(selectDefaultServer);
                }
            }
        });
    }

    private void offline(String hostId) {
        NetInterface.offline(hostId, new MyNetCallBackExtend<Server>(Server.class, false) {
            @Override
            public void onResponseResult(Result<Server> result, Call call) {
                if (null != selectDefaultServer) {
                    selectDefaultServer.setOnline(result.getData().getOnline());
                    updateInfo(selectDefaultServer);
                }
            }
        });

    }




    public void saveLog(int port, String method) {
        App.instance.saveLog(port, method);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INT_GO_SELECT && resultCode == RESULT_OK) {
            if (null != selectDefaultServer) {
                updateInfo(selectDefaultServer);
            }
        } else if (Constant.START_VPN_SERVICE_REQUEST_CODE == requestCode) {
            Intent intent = LocalVpnService.prepare(MainActivity.this);
            if (null == intent) {
                startVPNService();
            }
        }
    }


}
