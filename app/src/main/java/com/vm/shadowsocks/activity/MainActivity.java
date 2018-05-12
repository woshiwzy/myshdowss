package com.vm.shadowsocks.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.LogUtil;
import com.vm.shadowsocks.App;
import com.vm.shadowsocks.R;
import com.vm.shadowsocks.constant.Constant;
import com.vm.shadowsocks.core.AppInfo;
import com.vm.shadowsocks.core.AppProxyManager;
import com.vm.shadowsocks.core.LocalVpnService;
import com.vm.shadowsocks.core.ProxyConfig;
import com.vm.shadowsocks.domain.EventMessage;
import com.vm.shadowsocks.domain.Server;
import com.vm.shadowsocks.tool.SystemUtil;
import com.vm.shadowsocks.tool.Tool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends BaseActivity implements
        View.OnClickListener {

    private TextView textViewServerCountryName, textViewCurrentConnectCount;
    private ImageView toggleButton;
    private ImageView imageViewCountry;
    private TextView textViewStatus;

    private TextView textViewSent;
    private TextView textViewReceived;

    private int INT_GO_SELECT = 100;
    public static Server selectDefaultServer;

    public Animation animationRotate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        animationRotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        animationRotate.setRepeatCount(Animation.INFINITE);
        animationRotate.setRepeatMode(Animation.RESTART);
        animationRotate.setInterpolator(new LinearInterpolator());
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage event) {
        if (null != textViewSent && null != textViewReceived) {
            textViewSent.setText(event.sent / 1024 + " KB");
            textViewReceived.setText(event.received / 1024 + " KB");
//            textViewCurrentConnectCount.setText("Sent:"+event.sent / 1024 + " kb/s Received:"+event.received / 1024 + " kb/s");
        }
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        imageViewCountry = findViewById(R.id.imageViewCountry);
        //draw
        textViewStatus = findViewById(R.id.textViewStatus);

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


        LocalVpnService.addOnStatusChangedListener(statusChangedListener);


        //Pre-App Proxy
        if (AppProxyManager.isLollipopOrAbove) {
            new AppProxyManager(this);
        }

    }

    private void exitApp() {


        new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.menu_item_exit)
                .setMessage(R.string.exit_confirm_info)
                .setPositiveButton(R.string.btn_ok, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (null != LocalVpnService.Instance) {
                            LocalVpnService.IsRunning = false;
                            LocalVpnService.Instance.disconnectVPN();
                            stopService(new Intent(MainActivity.this, LocalVpnService.class));
                        }

                        System.runFinalization();
                        System.exit(0);
                    }
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();

    }

    private void startOrStopVpn(boolean isChecked) {

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

    }

    @Override
    protected void onDestroy() {
        LocalVpnService.removeOnStatusChangedListener(statusChangedListener);
        super.onDestroy();
    }

    private void updateInfo(Server server) {
        textViewServerCountryName.setText(server.getName());
        textViewCurrentConnectCount.setText(server.getMethod());
        LocalVpnService.ProxyUrl = selectDefaultServer.toString();
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
                saveLog();
                AVAnalytics.onEvent(MainActivity.this,"Start Proxy");
            } else {
                imageViewCountry.clearAnimation();
                toggleButton.setImageResource(R.drawable.icon_start);
                AVAnalytics.onEvent(MainActivity.this,"Stop Proxy");
            }
        }

        @Override
        public void onLogReceived(String logString) {
            Log.e(App.Companion.getTag(), logString);
        }
    };

    public void saveLog() {
        try {
            String address = Tool.getAdresseMAC(App.instance);
            String ip = Tool.getLocalIpAddress();
            String brand = SystemUtil.getDeviceBrand();
            String model=SystemUtil.getSystemModel();
            String imei = SystemUtil.getIMEI(MainActivity.this);

//            String ret = address + "," + ip + "," + brand + "," + imei;
            AVObject avObject = new AVObject("VPLOG");
//            avObject.put("log", ret);

            avObject.put("mac",address);
            avObject.put("ip",ip);
            avObject.put("brand",brand+","+model);
            avObject.put("imei",imei);

            avObject.put("system_version",Tool.getSystemVersion());
            avObject.put("country", Tool.getCountryCode());
            avObject.put("app_version",Tool.getVersionName(MainActivity.this));

            avObject.saveEventually();

        }catch (Exception e){
            e.printStackTrace();
        }

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
