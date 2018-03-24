package com.vm.shadowsocks.domain

import com.google.gson.annotations.SerializedName

/**
 * Created by wangzy on 2018/3/21.
 */
class Server(@SerializedName("serverurl") var host: String,
             @SerializedName("method") var method: String,
             @SerializedName("port") var port: Int,
             @SerializedName("password") var passWord: String,
             @SerializedName("name") var name:String) {

    override fun toString(): String {
        return "ss://${method}:${passWord}@${host}:${port}"
    }
}