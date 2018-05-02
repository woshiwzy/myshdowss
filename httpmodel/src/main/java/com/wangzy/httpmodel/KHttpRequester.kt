package com.wangzy.httpmodel

import okhttp3.*
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by wangzy on 2018/3/22.
 */

class KHttpRequester {

    fun postUpload(path: String, file: File) {

        val request = Request.Builder().url(baseHost + path).post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file)).build()

        okHttpClient!!.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {

            }
        })

    }

    companion object {

        var okHttpClient: OkHttpClient? = null
        var baseHost = "http://65.49.201.127:7000/"
        val MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8")

        val time_out = 60

        init {
            okHttpClient = OkHttpClient()
            okHttpClient!!.newBuilder().connectTimeout(time_out.toLong(), TimeUnit.SECONDS)
            okHttpClient!!.newBuilder().readTimeout(time_out.toLong(), TimeUnit.SECONDS)
            okHttpClient!!.newBuilder().writeTimeout(time_out.toLong(), TimeUnit.SECONDS)
        }


//        operator fun get(path: String, netCallBack: MyNetCallBack): Call {
//            val request = Request.Builder().url(baseHost + path).build()
//            val call = okHttpClient!!.newCall(request)
//            call.enqueue(netCallBack)
//            netCallBack.onStart(call)
//            return call
//        }

        fun get(path: String, onstart: (call:Call?) -> Unit, onResponseCallBack: (call: Call?, response: Response?) -> Unit, onFailureCallBack: (call: Call?, e: IOException?) -> Unit) {

            val request = Request.Builder().url(baseHost + path).build()
            val call = okHttpClient!!.newCall(request)
            call.enqueue(object : Callback {

                override fun onResponse(call: Call?, response: Response?) {
                    onResponseCallBack(call,response)
                }
                override fun onFailure(call: Call?, e: IOException?) {
                    onFailureCallBack(call,e)
                }
            })

            onstart(call)

        }


        fun postJson(path: String, json: String, netCallBack: MyNetCallBack): Call {

            val body = RequestBody.create(MediaType.parse("application/json"), json)
            val request = Request.Builder().url(baseHost + path).post(body).build()
            val call = okHttpClient!!.newCall(request)
            call.enqueue(netCallBack)
            netCallBack.onStart(call)
            return call
        }

        fun postHashMap(path: String, hashMap: HashMap<String, String>, netCallBack: MyNetCallBack): Call {

            val body = FormBody.Builder()

            for ((key, value) in hashMap) {
                body.add(key, value)
            }

            val request = Request.Builder().url(baseHost + path).post(body.build()).build()

            val call = okHttpClient!!.newCall(request)
            call.enqueue(netCallBack)

            netCallBack.onStart(call)

            return call
        }
    }

}
