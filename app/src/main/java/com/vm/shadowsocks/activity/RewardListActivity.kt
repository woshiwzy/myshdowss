package com.vm.shadowsocks.activity

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.widget.ImageView
import com.avos.avoscloud.AVObject
import com.vm.shadowsocks.App
import com.vm.shadowsocks.R
import com.vm.shadowsocks.activity.mvp.reward.RewardPresenter
import com.vm.shadowsocks.activity.mvp.reward.RewardPresenterImpl
import com.vm.shadowsocks.activity.mvp.reward.RewardView
import com.vm.shadowsocks.adapter.RewardAdapter
import com.vm.shadowsocks.constant.Constant
import com.vm.shadowsocks.domain.RewardHistory
import com.vm.shadowsocks.tool.LogUtil
import kotlinx.android.synthetic.main.activity_reward_list.*


class RewardListActivity : BaseActivity(), RewardView {


    lateinit var rewardPresenter: RewardPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reward_list)
        rewardPresenter = RewardPresenterImpl(this@RewardListActivity)
        rewardPresenter.startLoad(App.instance.user?.uuid)

        imageViewBack.setOnClickListener { finish() }
    }


    override fun onRewardListLoaded(rewardList: List<RewardHistory>) {
        LogUtil.e(Constant.TAG, "onRewardListLoaded:" + rewardList.size)
        fillAdapter(rewardList)
        hideCover()
    }

    override fun startLoadRewardList() {
        showCover()
    }

    override fun onComplete() {
        hideCover()
    }

    override fun onPause() {
        hideCover()
        super.onPause()
    }


    fun fillAdapter(p0: List<RewardHistory>?) {

        val messageAdapter = object : RewardAdapter(this@RewardListActivity, p0!!) {
            override fun onClickServerItem(server: AVObject, hostAdapter: RewardAdapter, position: Int) {
            }

        }

        val divider = DividerItemDecoration(this@RewardListActivity, DividerItemDecoration.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.custom_divider))
        recyclerViewRewardList!!.addItemDecoration(divider)
        recyclerViewRewardList!!.adapter = messageAdapter
        recyclerViewRewardList!!.layoutManager = LinearLayoutManager(this@RewardListActivity, LinearLayoutManager.VERTICAL, false)

        var total=0
        for(rw in p0){
                total+=rw.reward_size;
        }

        textViewTitle.setText(textViewTitle.text.toString()+"("+total+"M)")

    }
}
