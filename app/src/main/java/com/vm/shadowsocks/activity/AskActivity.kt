package com.vm.shadowsocks.activity

import android.app.Activity
import android.os.Bundle
import com.vm.shadowsocks.R
import kotlinx.android.synthetic.main.activity_ask.*

class AskActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ask)

        imageViewBack.setOnClickListener({
            finish()
        })
    }
}
