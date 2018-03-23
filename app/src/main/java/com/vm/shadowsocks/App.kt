package com.vm.shadowsocks

import android.content.Context
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import android.text.TextUtils
import android.util.Log
import com.orhanobut.logger.Logger
import com.vm.shadowsocks.domain.Server
import java.io.InputStreamReader


/**
 * Created by wangzy on 2017/11/22.
 */

class App : MultiDexApplication() {

    lateinit var hostList: MutableList<Server>
    val tag = "ss"
    override fun onCreate() {
        super.onCreate()
        instance = this

        var ret = loadServerList()
    }


    fun loadServerList(): String {
        var reader = InputStreamReader(assets.open("proxy.json"))
        var sbf = StringBuffer()
        while (true) {
            var line = reader.readText()
            if (!TextUtils.isEmpty(line)) {
                sbf.append(line)
            } else {
                reader.close()
                break
            }
        }
        return sbf.toString()
    }


    companion object {
        var tag = "ss"
        var test = "ss://aes-256-cfb:666666@65.49.201.127:9000"
        lateinit var instance: App
    }


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
