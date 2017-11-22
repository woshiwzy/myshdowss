package com.vm.shadowsocks.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
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


    private Switch switchProxy;
    private TextView textViewProxyUrl, textViewProxyApp;
    private ToggleButton toggleButton;
    private CheckBox checkBox;
    private TextView textViewTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题

        //getWindow().setFlags(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT);  //设置全屏

        setContentView(R.layout.activity_main);

        textViewTitle = findViewById(R.id.textViewTitle);

        toggleButton = findViewById(R.id.toggleButton);
        toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

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


        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.menu_item_exit)
                        .setMessage(R.string.exit_confirm_info)
                        .setPositiveButton(R.string.btn_ok, new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LocalVpnService.IsRunning = false;
                                LocalVpnService.Instance.disconnectVPN();
                                stopService(new Intent(MainActivity.this, LocalVpnService.class));
                                System.runFinalization();
                                System.exit(0);
                            }
                        })
                        .setNegativeButton(R.string.btn_cancel, null)
                        .show();
            }
        });


        findViewById(R.id.imageViewCar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });


        findViewById(R.id.ProxyUrlLayout).setOnClickListener(this);
        findViewById(R.id.AppSelectLayout).setOnClickListener(this);

        textViewProxyUrl = (TextView) findViewById(R.id.textViewProxyUrl);
        String ProxyUrl = App.getInstance().readProxyUrl();
        if (TextUtils.isEmpty(ProxyUrl)) {
            textViewProxyUrl.setText(R.string.config_not_set_value);
        } else {
            textViewProxyUrl.setText(ProxyUrl);
        }

        LocalVpnService.addOnStatusChangedListener(statusChangedListener);

        //Pre-App Proxy
        if (AppProxyManager.isLollipopOrAbove) {
            new AppProxyManager(this);
            textViewProxyApp = (TextView) findViewById(R.id.textViewAppSelectDetail);
        } else {

            ((ViewGroup) findViewById(R.id.AppSelectLayout).getParent()).removeView(findViewById(R.id.AppSelectLayout));

            ViewParent gp = (ViewParent) (findViewById(R.id.viewAppSelectLine).getParent());

            ((ViewGroup) findViewById(R.id.viewAppSelectLine).getParent()).removeView(findViewById(R.id.viewAppSelectLine));

        }
    }


    @Override
    public void onClick(View v) {
        if (LocalVpnService.IsRunning) {
            return;
        }

        if (v.getTag().toString().equals("ProxyUrl")) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.config_url)
                    .setItems(new CharSequence[]{
                            getString(R.string.config_url_scan),
                            getString(R.string.config_url_manual)
                    }, new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i) {
                                case 0:
                                    scanForProxyUrl();
                                    break;
                                case 1:
                                    showProxyUrlInputDialog();
                                    break;
                            }
                        }
                    })
                    .show();
        } else if (v.getTag().toString().equals("AppSelect")) {
            System.out.println("abc");
            startActivity(new Intent(this, AppManager.class));
        }
    }

    private void scanForProxyUrl() {
        new IntentIntegrator(this)
                .setPrompt(getString(R.string.config_url_scan_hint))
                .initiateScan(IntentIntegrator.QR_CODE_TYPES);
    }

    private void showProxyUrlInputDialog() {
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        editText.setHint(getString(R.string.config_url_hint));
        editText.setText(App.getInstance().readProxyUrl());

        new AlertDialog.Builder(this)
                .setTitle(R.string.config_url)
                .setView(editText)
                .setPositiveButton(R.string.btn_ok, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText() == null) {
                            return;
                        }

                        String ProxyUrl = editText.getText().toString().trim();
                        if (Tool.isValidUrl(ProxyUrl)) {
                            App.getInstance().setProxyUrl(ProxyUrl);
                            textViewProxyUrl.setText(ProxyUrl);
                        } else {
                            Toast.makeText(MainActivity.this, R.string.err_invalid_url, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (LocalVpnService.IsRunning != isChecked) {
            switchProxy.setEnabled(false);
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
        String ProxyUrl = App.getInstance().readProxyUrl();
        if (!Tool.isValidUrl(ProxyUrl)) {
            Toast.makeText(this, R.string.err_invalid_url, Toast.LENGTH_SHORT).show();
            switchProxy.post(new Runnable() {
                @Override
                public void run() {
                    switchProxy.setChecked(false);
                    switchProxy.setEnabled(true);
                }
            });
            return;
        }

        Constant.GL_HISTORY_LOGS = null;
        Log.i(App.tag, "starting...");
        LocalVpnService.ProxyUrl = ProxyUrl;
        startService(new Intent(this, LocalVpnService.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == Constant.START_VPN_SERVICE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                startVPNService();
            } else {
                switchProxy.setChecked(false);
                switchProxy.setEnabled(true);
                Log.i(App.tag, "canceled...");
            }
            return;
        }

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String ProxyUrl = scanResult.getContents();
            if (Tool.isValidUrl(ProxyUrl)) {
                App.getInstance().setProxyUrl(ProxyUrl);
                textViewProxyUrl.setText(ProxyUrl);
            } else {
                Toast.makeText(MainActivity.this, R.string.err_invalid_url, Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
//
//        MenuItem menuItem = menu.findItem(R.id.menu_item_switch);
//        if (menuItem == null) {
//            return false;
//        }
//
//        switchProxy = (Switch) menuItem.getActionView();
//        if (switchProxy == null) {
//            return false;
//        }
//
//        switchProxy.setChecked(LocalVpnService.IsRunning);
//        switchProxy.setOnCheckedChangeListener(this);
//        return true;

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.menu_item_exit:
                if (!LocalVpnService.IsRunning) {
                    finish();
                    return true;
                }

                new AlertDialog.Builder(this)
                        .setTitle(R.string.menu_item_exit)
                        .setMessage(R.string.exit_confirm_info)
                        .setPositiveButton(R.string.btn_ok, new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LocalVpnService.IsRunning = false;
                                LocalVpnService.Instance.disconnectVPN();
                                stopService(new Intent(MainActivity.this, LocalVpnService.class));
                                System.runFinalization();
                                System.exit(0);
                            }
                        })
                        .setNegativeButton(R.string.btn_cancel, null)
                        .show();

                return true;

            case R.id.menu_item_toggle_global:
                ProxyConfig.Instance.globalMode = !ProxyConfig.Instance.globalMode;
                if (ProxyConfig.Instance.globalMode) {
                    Log.i(App.tag, "Proxy global mode is on");
                } else {
                    Log.i(App.tag, "Proxy global mode is off");
                }
            default:
                return super.onOptionsItemSelected(item);


        }
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
                textViewProxyApp.setText(tmpString);
            }
        }

        if(LocalVpnService.IsRunning){
            toggleButton.setChecked(true);
        }else {
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

//            switchProxy.setEnabled(true);
//            switchProxy.setChecked(isRunning);

            onLogReceived(status);
            textViewTitle.setText(isRunning ? R.string.connected : R.string.connected_not);

            Toast.makeText(MainActivity.this, status, Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onLogReceived(String logString) {
            Log.e(App.tag, logString);
        }
    };


}
