package com.vm.shadowsocks.activity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.Window
import com.avos.avoscloud.AVAnalytics
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vm.shadowsocks.R
import com.vm.shadowsocks.adapter.HostAdapter
import com.vm.shadowsocks.domain.Server
import com.wangzy.httpmodel.HttpRequester
import com.wangzy.httpmodel.JsonSelector
import com.wangzy.httpmodel.KHttpRequester
import com.wangzy.httpmodel.MyNetCallBack
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import okhttp3.Call
import okhttp3.Response
import java.io.IOException
import java.util.*


class HostListActivity : BaseActivity() {

    private var recyclerViewHosts: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_host_list)
        recyclerViewHosts = findViewById(R.id.recyclerViewHosts)

        findViewById<View>(R.id.imageViewBack).setOnClickListener { finish() }
        loadHosts()
    }

    private fun loadHosts() {
        showCover()


        KHttpRequester.get("list_servers", onstart = { call: Call? ->


        }, onResponseCallBack = { call: Call?, response: Response? ->
            async(UI) {
                hideCover()
                setUpAdapter(response?.body()!!.string()!!)
            }

        }, onFailureCallBack = { call: Call?, e: IOException? ->
            async(UI) {
                hideCover()
                var json = readAssetsTxt(this@HostListActivity, "proxy.json")
                setUpAdapter(json)
            }
        })

//        HttpRequester.get("list_servers", object : MyNetCallBack() {
//
//            override fun onSuccessFinish(call: Call, response: Response) {
//
//                async(UI) {
//                    hideCover()
//                    setUpAdapter(response.body()!!.string()!!)
//                }
//
//            }
//
//            override fun onFailureFinish(call: Call, e: Exception) {
//                async(UI) {
//                    hideCover()
//                    var json = readAssetsTxt(this@HostListActivity, "proxy.json")
//                    setUpAdapter(json)
//                }
//            }
//        })

    }


    fun readAssetsTxt(context: Context, fileName: String): String {

        try {
            val ins = context.getAssets().open(fileName)
            val size = ins.available()
            val buffer = ByteArray(size)
            ins.read(buffer)
            ins.close()
            return String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return ""
    }


    fun setUpAdapter(json: String) {
        val data = JsonSelector.getJsonObject(json, "data")
        val gson = Gson()
        val servers = gson.fromJson<ArrayList<Server>>(data, object : TypeToken<ArrayList<Server>>() {}.type)
        servers.shuffle()

        val hostAdapter = object : HostAdapter(this@HostListActivity, servers) {
            override fun onClickServerItem(server: Server, hostAdapter: HostAdapter, position: Int) {


                MainActivity.selectDefaultServer = server

                setResult(Activity.RESULT_OK)
                hostAdapter.notifyDataSetChanged()
                hostAdapter.selected = position
                finish()

                AVAnalytics.onEvent(this@HostListActivity,"Select Proxy Server")
            }
        }

        val divider = DividerItemDecoration(this@HostListActivity, DividerItemDecoration.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.custom_divider))
        recyclerViewHosts!!.addItemDecoration(divider)

        recyclerViewHosts!!.adapter = hostAdapter
        recyclerViewHosts!!.layoutManager = LinearLayoutManager(this@HostListActivity, LinearLayoutManager.VERTICAL, false)
    }


}
