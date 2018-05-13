package com.vm.shadowsocks

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import com.avos.avoscloud.*
import com.taobao.sophix.PatchStatus
import com.taobao.sophix.SophixManager
import com.vm.shadowsocks.core.LocalVpnService
import com.vm.shadowsocks.domain.Server
import com.vm.shadowsocks.tool.LogUtil
import com.vm.shadowsocks.tool.SystemUtil
import com.vm.shadowsocks.tool.Tool

/**
 * Created by wangzy on 2017/11/22.
 */

class App : MultiDexApplication() {


    companion object {
        var tag = "ss"
        lateinit var instance: App
        val password: String = "666666"
    }


    lateinit var hostList: MutableList<Server>
    override fun onCreate() {
        super.onCreate()
        instance = this
        SophixManager.getInstance().queryAndLoadNewPatch();
        AVOSCloud.initialize(this, "jadP41WoqD4mptx79gok48JY-gzGzoHsz", "VbupgDD0dyLX3pHuxgV8QAp7")
        AVAnalytics.enableCrashReport(this, true)
        LogUtil.DEBUG = BuildConfig.LOG

        loginDevice()
    }

    fun loginDevice() {

        AVUser.logInInBackground(getUserName(), getPassword(), object : LogInCallback<AVUser>() {
            override fun done(p0: AVUser?, p1: AVException?) {
                if (p0 == null) {
                    LogUtil.d(tag, "login fail")
                    registerDevice()
                } else {
                    LogUtil.d(tag, "login success")
                }
            }
        })
    }


    fun getUserName(): String {
        return LocalVpnService.getAppInstallID(this)
    }

    fun getPassword(): String {
        return password
    }

    fun registerDevice() {

        try {

            val address = Tool.getAdresseMAC(App.instance)
            val ip = Tool.getLocalIpAddress()
            val brand = SystemUtil.getDeviceBrand()
            val model = SystemUtil.getSystemModel()
            val imei = SystemUtil.getIMEI(this)


            var avUser = AVUser()
            avUser.username = getUserName()
            avUser.setPassword(getPassword())

            avUser.put("mac", address)
            avUser.put("ip", ip)
            avUser.put("brand", "$brand,$model")
            avUser.put("imei", imei)

            avUser.put("system_version", Tool.getSystemVersion())
            avUser.put("country", Tool.getCountryCode())
            avUser.put("app_version", Tool.getVersionName(this))

            avUser.signUpInBackground(object : SignUpCallback() {

                override fun done(p0: AVException?) {
                    if (null == p0) {
                        LogUtil.e(tag, "r-success")
                    } else {
                        LogUtil.e(tag, "r-fail:" + p0?.localizedMessage)
                    }

                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.d(tag,"register error:"+e?.localizedMessage)
        }

    }


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)

        SophixManager.getInstance().setContext(this)
                .setAppVersion(getVersionName(this))
                .setAesKey(null)
                .setEnableDebug(true)
                .setPatchLoadStatusStub { mode, code, info, handlePatchVersion ->
                    // 补丁加载回调通知
                    if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                        // 表明补丁加载成功
                        com.vm.shadowsocks.tool.LogUtil.e(App.tag, "hot fix success======")
                    } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                        // 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
                        // 建议: 用户可以监听进入后台事件, 然后调用killProcessSafely自杀，以此加快应用补丁，详见1.3.2.3
                        LogUtil.e(App.tag, "hot fix success======restart")
                        SophixManager.getInstance().killProcessSafely()
                    } else {
                        // 其它错误信息, 查看PatchStatus类说明
                    }
                }.initialize()
    }

    /**
     * get App versionName
     * @param context
     * @return
     */
    fun getVersionName(context: Context): String {
        val packageManager = context.packageManager
        val packageInfo: PackageInfo
        var versionName = ""
        try {
            packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            versionName = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return versionName
    }
}
