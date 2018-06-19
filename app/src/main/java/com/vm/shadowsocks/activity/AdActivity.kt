package com.vm.shadowsocks.activity

import android.os.Bundle
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.vm.shadowsocks.App
import com.vm.shadowsocks.R
import com.vm.shadowsocks.tool.LogUtil
import org.jetbrains.anko.coroutines.experimental.asReference

class AdActivity : BaseActivity() {


    private lateinit var mInterstitialAd: InterstitialAd
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_ad)


        MobileAds.initialize(this, "ca-app-pub-9033563274040080~1800036213")

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId="ca-app-pub-9033563274040080/2783505895"
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        mInterstitialAd.adListener= object : AdListener() {

            override fun onAdLoaded() {


                hideCover()
            }

            override fun onAdClosed() {

                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show()
                }

                finish()
            }

            override fun onAdFailedToLoad(p0: Int) {
                LogUtil.e(App.tag,"load interist ad fail:"+p0)
                hideCover()
            }

        }
        showCover()
    }

    override fun onPostResume() {
        super.onPostResume()
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show()
        }

    }

}
