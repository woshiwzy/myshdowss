package com.vm.shadowsocks.activity.mvp.reward

import com.vm.shadowsocks.domain.RewardHistory


class RewardPresenterImpl(rewardView: RewardView) : RewardPresenter(rewardView) {


    override fun startLoad(uuid: String?) {

        var rewardHistory = RewardHistory()
        rewardHistory.loadRewardList(uuid!!,
                { rewars: List<RewardHistory> -> rewardView.onRewardListLoaded(rewars) }, {rewardView.startLoadRewardList()
        }, {
            rewardView.onComplete()
        })

    }


}
