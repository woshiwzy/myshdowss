package com.vm.shadowsocks.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.feedback.FeedbackAgent;
import com.squareup.picasso.Picasso;
import com.vm.shadowsocks.App;
import com.vm.shadowsocks.R;
import com.vm.shadowsocks.constant.Constant;
import com.vm.shadowsocks.core.AppInfo;
import com.vm.shadowsocks.core.AppProxyManager;
import com.vm.shadowsocks.core.LocalVpnService;
import com.vm.shadowsocks.core.ProxyConfig;
import com.vm.shadowsocks.tool.Tool;

public class MainActivity extends Activity implements
        View.OnClickListener,
        OnCheckedChangeListener {

    private TextView textViewServerCountryName, textViewCurrentConnectCount;
    private ToggleButton toggleButton;
    private CheckBox checkBox;
    private TextView textViewTitle;
    private DrawerLayout drawerLayout;
    private ImageView imageViewCountry;
    private TextView textViewStatus;
    private CheckBox checkBoxLaw;
    private int INT_GO_SELECT = 100;
    public static AVObject selectDefaultServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        initView();

    }

    private void initView() {
        setContentView(R.layout.activity_main);
        imageViewCountry = findViewById(R.id.imageViewCountry);
        //draw
        drawerLayout = findViewById(R.id.drawerLayout);
        textViewStatus = findViewById(R.id.textViewStatus);
        checkBoxLaw = findViewById(R.id.checkBoxLaw);

        findViewById(R.id.imageViewMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }

            }
        });

        //menu feedback

        findViewById(R.id.feedBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FeedbackAgent agent = new FeedbackAgent(App.getInstance());
                agent.startDefaultThreadActivity();

            }
        });


        textViewTitle = findViewById(R.id.textViewTitle);

        toggleButton = findViewById(R.id.toggleButton);
        toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                startOrStopVpn(isChecked);
            }
        });

        checkBox = findViewById(R.id.checkBox);
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ProxyConfig.Instance.globalMode = !ProxyConfig.Instance.globalMode;
                if (ProxyConfig.Instance.globalMode) {
                    Log.i(App.tag, "Proxy global mode is on");
                } else {
                    Log.i(App.tag, "Proxy global mode is off");
                }
            }
        });


        findViewById(R.id.imageViewCar).setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

            }
        });

        findViewById(R.id.viewMenuExit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitApp();
            }
        });


        findViewById(R.id.ProxyUrlLayout).setOnClickListener(this);

        textViewServerCountryName = findViewById(R.id.textViewServerCountryName);
        textViewCurrentConnectCount = findViewById(R.id.textViewCurrentConnectCount);


        LocalVpnService.addOnStatusChangedListener(statusChangedListener);


        LinearLayout fastAcessLeayout = findViewById(R.id.linearLayoutFastAccess);

        int childCount = fastAcessLeayout.getChildCount();
        for (int i = 0; i < childCount; i++) {

            final ImageView fac = (ImageView) fastAcessLeayout.getChildAt(i);

            fastAcessLeayout.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String website = (String) fac.getTag();
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(website);
                    intent.setData(content_url);
                    startActivity(intent);

                }
            });

        }


        //Pre-App Proxy
        if (AppProxyManager.isLollipopOrAbove) {
            new AppProxyManager(this);
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        AVAnalytics.onPause(this);
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
            toggleButton.setChecked(false);
            return;
        }


        if (checkBoxLaw.isChecked() == false) {

            checkBoxLaw.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake));
            toggleButton.setChecked(false);
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


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (LocalVpnService.IsRunning != isChecked) {
            if (isChecked) {
                Intent intent = LocalVpnService.prepare(this);
                if (intent == null) {
                    startVPNService();
                } else {
                    startActivityForResult(intent, Constant.START_VPN_SERVICE_REQUEST_CODE);
                }
            } else {
                LocalVpnService.IsRunning = false;
            }
        }


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
        AVAnalytics.onResume(this);
        if (AppProxyManager.isLollipopOrAbove) {
            if (AppProxyManager.Instance.proxyAppInfo.size() != 0) {

                String tmpString = "";
                for (AppInfo app : AppProxyManager.Instance.proxyAppInfo) {
                    tmpString += app.getAppLabel() + ", ";
                }
            }
        }

        if (LocalVpnService.IsRunning) {
            toggleButton.setChecked(true);
        } else {
            toggleButton.setChecked(false);
        }

    }

    @Override
    protected void onDestroy() {
        LocalVpnService.removeOnStatusChangedListener(statusChangedListener);
        super.onDestroy();
    }


    LocalVpnService.onStatusChangedListener statusChangedListener = new LocalVpnService.onStatusChangedListener() {
        @Override
        public void onStatusChanged(String status, Boolean isRunning) {

            Log.i(App.tag, "status:" + status + " isrunning:" + isRunning);
            onLogReceived(status);
            textViewStatus.setText((getResources().getString(R.string.app_name)) + ":" + getResources().getString(isRunning ? R.string.connected : R.string.connected_not));
//            Toast.makeText(MainActivity.this, status, Toast.LENGTH_SHORT).show();


            if (null != selectDefaultServer) {
                selectDefaultServer.increment("client_count", isRunning ? 1 : -1);
                selectDefaultServer.setFetchWhenSave(true);
                selectDefaultServer.saveEventually(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        updateConnectInfo(selectDefaultServer);
                    }
                });
            }


        }

        @Override
        public void onLogReceived(String logString) {
            Log.e(App.tag, logString);
        }
    };


    private void updateConnectInfo(AVObject selectDefaultServer) {
        if (null != selectDefaultServer) {
            Picasso.with(this).load(selectDefaultServer.getAVFile("icon").getUrl()).placeholder(R.drawable.loading).into(imageViewCountry);
            textViewServerCountryName.setText(selectDefaultServer.getString("country") + "(" + selectDefaultServer.getString("name") + ")");
            textViewCurrentConnectCount.setText(getResources().getString(R.string.current) + String.valueOf(selectDefaultServer.get("client_count")));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == INT_GO_SELECT && resultCode == RESULT_OK) {
            if (null != selectDefaultServer) {

                updateConnectInfo(selectDefaultServer);

                String url = selectDefaultServer.getString("proxy");
//                String url = App.test;
                Log.i(App.tag, "poxy url:" + url);

                LocalVpnService.ProxyUrl = url;

            }
        }
    }
}
