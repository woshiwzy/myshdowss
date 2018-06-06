package com.vm.shadowsocks.domain

import com.google.gson.annotations.SerializedName

/**
 * Created by wangzy on 2018/3/21.
 */
class Server(
        @SerializedName("base64str") var base64str: String,
        @SerializedName("name") var name: String,
        @SerializedName("available_band") var available_band: String,
        @SerializedName("ip") var host: String,
        @SerializedName("id") var id: String,
        @SerializedName("port") var port: Int,
        @SerializedName("online") var online: Int,
        @SerializedName("total_band") var total_band: String,
        @SerializedName("password") var passWord: String,
        @SerializedName("method") var method: String

) {

    var enable = true
        get() = field
        set(value) {
            field = value
        }

    override fun toString(): String {
        return "ss://${method}:${dec(passWord)}@${host}:${port}"
    }


    fun dec(output: String): String {
        val xss = output.toCharArray()
        val sbf = StringBuffer()
        for (x in xss) {
            val xi = Integer.parseInt(x + "")
            if (xi > 4) {
                val xs = xi - 4
                sbf.append(xs)
            } else {
                val xs = 9 - xi
                sbf.append(xs)
            }
        }
        return sbf.toString()
    }
}