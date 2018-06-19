package com.vm.shadowsocks.activity

import android.os.Bundle
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
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

class MyGoogleAdActivity : BaseActivity() {

    lateinit var mInterstitialAd: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_ad)

        showCover()

        initGoogleAd()
    }


    override fun onResume() {
        super.onResume()


        if (mInterstitialAd.isLoaded) {
            mInterstitialAd.show()
        } else {
            LogUtil.d(Constant.TAG, "The interstitial wasn't loaded yet.")
        }
    }

    /**
     * 初始化Google Ad
     */
    private fun initGoogleAd() {
        //===init goole ad=======
        MobileAds.initialize(this, "ca-app-pub-9033563274040080~1800036213")
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-9033563274040080/7427964504"
        mInterstitialAd.loadAd(AdRequest.Builder().addTestDevice("A597024318FAD982551E3EEDA0E1D2C8").build())
        mInterstitialAd.adListener = object : com.google.android.gms.ads.AdListener() {

            override fun onAdLeftApplication() {
                getreard()
                LogUtil.e(Constant.TAG, "onAdLeftApplication")
            }

            override fun onAdClosed() {
                finish()
            }

            override fun onAdLoaded() {
                hideCover()
                LogUtil.e(Constant.TAG, "onAdLoaded")

                if (mInterstitialAd.isLoaded) {
                    mInterstitialAd.show()
                } else {
                    LogUtil.d(Constant.TAG, "The interstitial wasn't loaded yet.")
                }
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                Tool.ToastShow(this@MyGoogleAdActivity, R.string.retry)
                finish()
                LogUtil.e(Constant.TAG, " Google onAdFailedToLoad:$errorCode")
            }

            override fun onAdClicked() {
                LogUtil.e(Constant.TAG, "onAdClicked:")


            }

            override fun onAdOpened() {
                LogUtil.e(Constant.TAG, " onAdOpened onAdFailedToLoad:")

            }
        }

    }

    private fun getreard() {

        if (null != App.instance.user) {

            var apiManager = APIManager(this@MyGoogleAdActivity)

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

                                Tool.ToastShow(this@MyGoogleAdActivity, String.Companion.format(resources.getString(R.string.getreward), reward.toString()))
                            } else {
                                Tool.ToastShow(this@MyGoogleAdActivity, result.message)
                            }


                        }

                        override fun onStart() {
                        }
                    })

        }
    }


}
