package com.vm.shadowsocks.activity;

import android.content.ComponentName;
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
import com.avos.avoscloud.utils.StringUtils;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.vm.api.APIManager;
import com.vm.api.RetrofitHelper;
import com.vm.api_okapi.NetInterface;
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
import java.util.Timer;

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
    private Timer timer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
//        serviceIntent.setPackage("com.android.vending");
//        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        //=======bind service
        animationRotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        animationRotate.setRepeatCount(Animation.INFINITE);
        animationRotate.setRepeatMode(Animation.RESTART);
        animationRotate.setInterpolator(new LinearInterpolator());

        initView();
        initAd();
        apiManager = new APIManager(this);

        EventBus.getDefault().register(this);

        updateTraffic();
//        showTipLayout();
    }


    private void initAd() {
        adView = new AdView(this, "870289033159365_870296759825259", AdSize.BANNER_HEIGHT_50);
        adView.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                LogUtil.e(TAG, "ad error:" + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                LogUtil.e(TAG, "onAdLoaded");
            }

            @Override
            public void onAdClicked(Ad ad) {

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

    @Override
    protected void onStop() {

        super.onStop();
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
                startOrStopVpn(!LocalVpnService.IsRunning);
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
            PushService.setDefaultPushCallback(this, MessagesActivity.class);

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
        }, 5 * 1000);
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
                textViewTotal.setText(ret);
                String ret2 = String.format(getResources().getString(R.string.remaining_traffic), nf.format(finalRemain) + " M");
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

        updateTraffic();
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

        updateTraffic();

        updateDataUsed();
    }

    @Override
    protected void onDestroy() {

        updateTraffic();

        if (null != timer) {
            timer.purge();
            timer.cancel();
        }

        if (adView != null) {
            adView.destroy();
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

                offline(selectDefaultServer.getId());

                imageViewCountry.clearAnimation();
                toggleButton.setImageResource(R.drawable.icon_start);
                AVAnalytics.onEvent(MainActivity.this, "Stop Proxy");

            }
            updateTraffic();
            updateDataUsed();
        }

        @Override
        public void onLogReceived(String logString) {
            Log.e(App.Companion.getTag(), "log----->" + logString);
        }
    };


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


    private void updateTraffic() {

        long totalbyte = (long) (SharePersistent.getFloat(MainActivity.this, "adflaksdflasfjaldskj") / 1024);

        LogUtil.e(Constant.TAG, "流量======>:" + totalbyte);
        totalbyte = totalbyte > 0 ? totalbyte : 0;
        final long finalTotalbyte = totalbyte;

        if (null == App.instance.getUser()) {
            LogUtil.e(Constant.TAG, "没有用户无法更新流量");
            return;
        }

        if (finalTotalbyte <= 0) {
//                        LogUtil.e(Constant.TAG, "没有正流量:"+finalTotalbyte);
            return;
        }

        if (null != App.instance.getUser() && finalTotalbyte > 0) {

            HashMap<String, String> map = new HashMap<>();
            map.put("uuid", App.instance.getUser().getUuid());
            map.put("cost_size", String.valueOf(finalTotalbyte));
            HttpRequester.postHashMap(RetrofitHelper.BASE_URL + "cost_traffic", map, new MyNetCallBackExtend<User>(User.class, false) {

                @Override
                public void onFailureFinish(Call call, Exception e) {
                    LogUtil.e(Constant.TAG, "onFailureFinish:" + e.getLocalizedMessage());
                    call.cancel();
                    imageViewCountry.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            updateTraffic();
                        }
                    }, 1 * 1000);
                }

                @Override
                public void onResponseResult(Result<User> result, okhttp3.Call call) {
                    call.cancel();

                    User user = result.getData();
                    App.instance.setUser(user);
                    LocalVpnService.m_ReceivedBytes = 0;
                    LocalVpnService.m_SentBytes = 0;
                    updateDataUsed();

                    LocalVpnService.logDataSaved(MainActivity.this, 0, true);

                }
            });
        } else {
            LogUtil.e(Constant.TAG, "条件不足无法更新流量");
        }

//
//                }
//            }, 0, 10 * 1000);
//        }
    }

    public void saveLog(int port, String method) {
        App.instance.saveLog(port, method);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        LogUtil.e(Constant.TAG, "-----------------------------------");
        updateTraffic();
        super.onSaveInstanceState(outState);
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
