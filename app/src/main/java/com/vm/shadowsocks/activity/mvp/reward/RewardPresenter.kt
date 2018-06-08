package com.vm.shadowsocks.activity.mvp.reward

import com.vm.shadowsocks.domain.RewardHistory

abstract class RewardPresenter( var rewardView: RewardView) {

    abstract fun startLoad(uuid:String?)

}