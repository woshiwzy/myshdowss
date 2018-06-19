package com.vm.shadowsocks.activity

import android.app.Activity
import android.os.Bundle
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.InterstitialAd
import com.facebook.ads.InterstitialAdListener
import com.vm.api.APIManager
import com.vm.shadowsocks.App
import com.vm.shadowsocks.R
import com.vm.shadowsocks.constant.Constant
import com.vm.shadowsocks.constant.Constant.MAX_DEFAULT_REWARD_M
import com.vm.shadowsocks.domain.User
import com.vm.shadowsocks.tool.LogUtil
import com.vm.shadowsocks.tool.Tool
import com.wangzy.httpmodel.gson.ext.Result
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

class MyFaceBookAdActivity : BaseActivity() {


    lateinit var interstitialAd: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_face_book_ad)

        interstitialAd = InterstitialAd(this, "870289033159365_870289343159334")
        interstitialAd.setAdListener(object : InterstitialAdListener {
            override fun onInterstitialDisplayed(p0: Ad?) {
            }

            override fun onAdClicked(p0: Ad?) {
                getreard(this@MyFaceBookAdActivity)
            }

            override fun onInterstitialDismissed(p0: Ad?) {
            }

            override fun onError(p0: Ad?, p1: AdError?) {
                hideCover()
                Tool.ToastShow(this@MyFaceBookAdActivity, R.string.retry)
                LogUtil.e(Constant.TAG, " MyFaceBookAdActivity:${p1?.errorMessage}")
//                finish()
            }

            override fun onAdLoaded(p0: Ad?) {
                hideCover()
            }

            override fun onLoggingImpression(p0: Ad?) {
            }

        })
        interstitialAd.loadAd()
        showCover()
    }


    private fun getreard(activity: Activity) {

        if (null != App.instance.user) {

            var apiManager = APIManager(activity)

            var random = Random()
            val reward = random.nextInt(MAX_DEFAULT_REWARD_M)
            apiManager.rewardTraffic(App.instance.user?.uuid, reward.toString(), "open ad")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Subscriber<Result<User>>() {
                        override fun onCompleted() {
                        }

                        override fun onError(e: Throwable) {
                            LogUtil.e(Constant.TAG, "ad reward error:" + e.localizedMessage)

                        }

                        override fun onNext(result: Result<User>) {
                            if (result.code.equals("200")) {
                                App.instance.user = result.data

                                Tool.ToastShow(activity, String.Companion.format(resources.getString(R.string.getreward), reward.toString()))
                            } else {
                                Tool.ToastShow(activity, result.message)
                            }

                        }

                        override fun onStart() {
                        }
                    })

        }
    }

    override fun onResume() {
        if (interstitialAd != null) {
            interstitialAd.show()
        }

        super.onResume()
    }


    override fun onDestroy() {
        if (interstitialAd != null) {
            interstitialAd.destroy()
        }
        super.onDestroy()
    }

}
