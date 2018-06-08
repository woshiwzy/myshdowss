package com.vm.shadowsocks.activity.mvp.reward

import com.vm.shadowsocks.domain.RewardHistory

interface RewardView {

     fun onRewardListLoaded(rewardList: List<RewardHistory>)
     fun startLoadRewardList()
     fun onComplete()
}