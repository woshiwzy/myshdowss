package com.vm.shadowsocks.activity

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.AVQuery
import com.avos.avoscloud.FindCallback
import com.facebook.ads.*
import com.vm.shadowsocks.App
import com.vm.shadowsocks.R
import com.vm.shadowsocks.adapter.MessageAdapter
import com.vm.shadowsocks.config.messages
import com.vm.shadowsocks.tool.LogUtil
import com.vm.shadowsocks.tool.Tool
import kotlinx.android.synthetic.main.activity_messages.*

class MessagesActivity : BaseActivity() {

    private var adView: AdView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        initAd()
        loadMessage()

        imageViewBack.setOnClickListener {
            finish()
            Tool.startActivity(this, MainActivity::class.java)
        }
    }


    override fun onDestroy() {
        adView?.destroy()
        super.onDestroy()
    }

    private fun initAd() {
        adView = AdView(this, "870289033159365_870296759825259", AdSize.BANNER_HEIGHT_50)
        adView?.setAdListener(object : AdListener {
            override fun onAdClicked(p0: Ad?) {
            }

            override fun onError(p0: Ad?, p1: AdError?) {
                LogUtil.e(App.tag, "aderror:" + p1?.errorCode + " " + p1?.errorMessage)
            }

            override fun onAdLoaded(p0: Ad?) {
            }

            override fun onLoggingImpression(p0: Ad?) {
            }

        })

        var adContainer = findViewById<LinearLayout>(R.id.banner_container)
        adContainer.addView(adView)
        adView?.loadAd()
    }

    override fun onResume() {
        super.onResume()
    }

    fun loadMessage() {

        if (messages?.isEmpty()!!) {
            showCover()

            try {
                var avQuer = AVQuery<AVObject>("BMessage")
                avQuer.orderByAscending("createdAt")
                avQuer.findInBackground(object : FindCallback<AVObject>() {

                    override fun done(p0: MutableList<AVObject>?, p1: AVException?) {
                        hideCover()
                        if (null == p1) {
                            fillAdapter(p0)
                        } else {
                            Tool.ToastShow(this@MessagesActivity, "No message.")
                        }
                    }
                })
            } catch (e: Exception) {

            }


        } else {
            fillAdapter(messages)
        }

    }

    fun fillAdapter(p0: MutableList<AVObject>?) {
        messages = p0

        val messageAdapter = object : MessageAdapter(this@MessagesActivity, p0!!) {

            override fun onClickServerItem(server: AVObject, hostAdapter: MessageAdapter, position: Int) {

            }
        }

        val divider = DividerItemDecoration(this@MessagesActivity, DividerItemDecoration.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.custom_divider))
        recyclerViewMessages!!.addItemDecoration(divider)
        recyclerViewMessages!!.adapter = messageAdapter
        recyclerViewMessages!!.layoutManager = LinearLayoutManager(this@MessagesActivity, LinearLayoutManager.VERTICAL, false)
    }
}
