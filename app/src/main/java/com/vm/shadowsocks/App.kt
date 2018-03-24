package com.vm.shadowsocks

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import android.text.TextUtils
import android.util.Log
import com.orhanobut.logger.Logger
import com.taobao.sophix.PatchStatus
import com.taobao.sophix.SophixManager
import com.taobao.sophix.listener.PatchLoadStatusListener
import com.vm.shadowsocks.domain.Server
import java.io.InputStreamReader


/**
 * Created by wangzy on 2017/11/22.
 */

class App : MultiDexApplication() {

    lateinit var hostList: MutableList<Server>
    override fun onCreate() {
        super.onCreate()
        instance = this
        SophixManager.getInstance().queryAndLoadNewPatch();
    }

    companion object {
        var tag = "ss"
        lateinit var instance: App
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
                        Log.e(App.tag,"hot fix success======")
                    } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                        // 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
                        // 建议: 用户可以监听进入后台事件, 然后调用killProcessSafely自杀，以此加快应用补丁，详见1.3.2.3
                        Log.e(App.tag,"hot fix success======restart")
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
