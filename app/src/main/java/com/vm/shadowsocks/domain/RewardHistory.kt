package com.vm.shadowsocks.domain

import com.google.gson.annotations.SerializedName
import com.vm.api.APIManager
import com.vm.shadowsocks.constant.Constant
import com.vm.shadowsocks.tool.LogUtil
import com.wangzy.httpmodel.gson.ext.Result
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class RewardHistory(@SerializedName("username") var username: String,
                    @SerializedName("reward_size") var reward_size: Int,
                    @SerializedName("create_time") var create_time: String,
                    @SerializedName("descption") var descption: String
) {


    constructor() : this(username = "", reward_size = 0, create_time = "", descption = "") {

    }


    fun loadRewardList(uuid: String, done: (rewards: List<RewardHistory>) -> Unit, start: () -> Unit, complete: () -> Unit) {

        var apiManager = APIManager(null)
        val subscription = apiManager.rewardList(uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Result<List<RewardHistory>>>() {

                    override fun onStart() {
                        start()
                    }

                    override fun onNext(t: Result<List<RewardHistory>>?) {
                        done(t?.data!!)
                    }

                    override fun onCompleted() {
                        complete()

                    }

                    override fun onError(e: Throwable) {

                        LogUtil.e(Constant.TAG, "loadRewardList error:" + e.localizedMessage)
                    }
                })


    }
}