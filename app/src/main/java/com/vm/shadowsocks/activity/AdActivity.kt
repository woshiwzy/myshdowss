package com.vm.shadowsocks.activity

import android.os.Bundle
import android.widget.LinearLayout
import com.facebook.ads.*
import com.vm.shadowsocks.App
import com.vm.shadowsocks.R
import com.vm.shadowsocks.constant.Constant.TAG
import com.vm.shadowsocks.tool.LogUtil

class AdActivity : BaseActivity() {


    var interstitialAd: InterstitialAd? = null
     var adView: AdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad)
        showCover()
        initAd()
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


        interstitialAd = InterstitialAd(this, "870289033159365_870289343159334")
        interstitialAd?.setAdListener(object : InterstitialAdListener {
            override fun onInterstitialDisplayed(ad: Ad) {
                hideCover()
            }

            override fun onInterstitialDismissed(ad: Ad) {
                finish()
            }

            override fun onError(ad: Ad, adError: AdError) {
                hideCover()
                LogUtil.e(TAG, " main ad error:" + adError.errorMessage + " code:" + adError.errorCode)
            }

            override fun onAdLoaded(ad: Ad) {
                hideCover()
            }

            override fun onAdClicked(ad: Ad) {

            }

            override fun onLoggingImpression(ad: Ad) {

            }
        })
        interstitialAd?.loadAd()
    }

    override fun onResume() {
        interstitialAd?.show()
        super.onResume()
    }

    override fun onDestroy() {
        interstitialAd?.destroy()
        adView?.destroy()
        super.onDestroy()
    }
}
