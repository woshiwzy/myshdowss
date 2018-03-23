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
//      var test = "ss://aes-256-cfb:666666@65.49.201.127:9000"
        return "ss://${method}:${passWord}@${host}:${port}"
    }

}