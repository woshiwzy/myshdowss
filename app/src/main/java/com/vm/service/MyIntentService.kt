package com.vm.service

import android.app.IntentService
import android.content.Intent
import com.vm.api.RetrofitHelper
import com.vm.shadowsocks.App
import com.vm.shadowsocks.constant.Constant
import com.vm.shadowsocks.core.LocalVpnService
import com.vm.shadowsocks.domain.EventMessage
import com.vm.shadowsocks.domain.User
import com.vm.shadowsocks.tool.LogUtil
import com.vm.shadowsocks.tool.SharePersistent
import com.wangzy.httpmodel.HttpRequester
import com.wangzy.httpmodel.MyNetCallBackExtend
import com.wangzy.httpmodel.gson.ext.Result
import okhttp3.Call
import org.greenrobot.eventbus.EventBus
import java.util.*

class MyIntentService : IntentService("MyIntentService") {


    var timer: Timer? = null
    var timerTask: TimerTask? = null

    override fun onCreate() {
        super.onCreate()
        initTimer()
    }

    private fun initTimer() {
        if (null == timer || null == timerTask) {
            timer = Timer()
            timerTask = object : TimerTask() {
                override fun run() {

                    LogUtil.e(Constant.TAG, "timer update traffic")
                    updateTraffic()
                }
            }

            timer?.schedule(timerTask, 0, 30 * 1000)
        }
    }


    override fun onHandleIntent(intent: Intent?) {
        initTimer()
    }


    private fun updateTraffic() {

        var totalbyte = (SharePersistent.getFloat(this@MyIntentService, "adflaksdflasfjaldskj") / 1024).toLong()

        LogUtil.e(Constant.TAG, "流量======>:$totalbyte")
        totalbyte = if (totalbyte > 0) totalbyte else 0
        val finalTotalbyte = totalbyte

        if (null == App.instance.user) {
            LogUtil.e(Constant.TAG, "没有用户无法更新流量")
            return
        }

        if (finalTotalbyte <= 0) {
            //                        LogUtil.e(Constant.TAG, "没有正流量:"+finalTotalbyte);
            return
        }

        if (null != App.instance.user && finalTotalbyte > 0) {

            val map = HashMap<String, String>()
            map["uuid"] = App.instance.user!!.uuid
            map["cost_size"] = finalTotalbyte.toString()
            HttpRequester.postHashMap(RetrofitHelper.BASE_URL + "cost_traffic", map, object : MyNetCallBackExtend<User>(User::class.java, false) {

                override fun onFailureFinish(call: Call, e: Exception) {
                    LogUtil.e(Constant.TAG, "onFailureFinish:" + e.localizedMessage)
                    call.cancel()
                }

                override fun onResponseResult(result: Result<User>, call: okhttp3.Call) {
                    call.cancel()
                    val user = result.getData()

                    App.instance.user = user

                    LocalVpnService.m_ReceivedBytes = 0
                    LocalVpnService.m_SentBytes = 0
                    LocalVpnService.logDataSaved(this@MyIntentService, 0, true)

                    LogUtil.e(Constant.TAG,"upate success")
                    if (App.instance.user!!.getRemaining_bytes()<=0 || App.instance.user!!.enable==false){

                        LogUtil.e(Constant.TAG,"out off traffic ")
                        LocalVpnService.IsRunning=false
                    }


                    var eventMsg = EventMessage()
                    eventMsg.type = EventMessage.TYPE_MSG_REGIST
                    EventBus.getDefault().post(eventMsg)


                }
            })
        } else {
            LogUtil.e(Constant.TAG, "条件不足无法更新流量")
        }

    }

}
