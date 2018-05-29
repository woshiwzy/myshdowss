package com.vm.shadowsocks.activity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import com.avos.avoscloud.*
import com.facebook.ads.AdSize
import com.facebook.ads.AdView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vm.shadowsocks.App
import com.vm.shadowsocks.R
import com.vm.shadowsocks.adapter.HostAdapter
import com.vm.shadowsocks.config.Servers
import com.vm.shadowsocks.domain.Server
import com.vm.shadowsocks.tool.LogUtil
import com.wangzy.httpmodel.JsonSelector
import com.wangzy.httpmodel.KHttpRequester
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import okhttp3.Call
import okhttp3.Response
import java.io.IOException


class HostListActivity : BaseActivity() {

    private var recyclerViewHosts: RecyclerView? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_host_list)
        recyclerViewHosts = findViewById(R.id.recyclerViewHosts)

        loadHostsFromLean()
        findViewById<View>(R.id.imageViewBack).setOnClickListener { finish() }
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
                useDefaultHosts()
            }
        })

    }

    private fun useDefaultHosts() {
        var json = readAssetsTxt(this@HostListActivity, "proxy.json")
        setUpAdapter(json)

    }

    private fun loadHostsFromLean() {

        if (Servers.servers?.isEmpty()!!) {

            LogUtil.i(App.tag, "request host list")
            showCover()
            var quer = AVQuery<AVObject>("Host")
            quer.findInBackground(object : FindCallback<AVObject>() {
                override fun done(p0: MutableList<AVObject>?, p1: AVException?) {
                    hideCover()
                    if (null == p1) {
                        fillAdapter(leancloud2Server(p0))
                    } else {
                        useDefaultHosts()
                    }
                }
            })
        } else {
            LogUtil.i(App.tag, " not need request host list")
            fillAdapter(Servers.servers)
        }
    }

    fun leancloud2Server(p0: MutableList<AVObject>?): ArrayList<Server> {

        var list = ArrayList<Server>()
        for (host in p0!!) {
            var server = Server(host = host.getString("host"), method = host.getString("method"), port = (host.getString("port")).toInt(), passWord = host.getString("pwd"), name = host.getString("alias"))
            server.enable = host.getBoolean("enable")
            if (server.enable) {
                list.add(server)
            }
        }

        return list
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

        fillAdapter(servers)
    }

    fun fillAdapter(servers: ArrayList<Server>) {
        Servers.servers = servers
        Servers.servers.shuffle()

        LogUtil.e(App.tag, "host size:" + Servers.servers.size)

        val hostAdapter = object : HostAdapter(this@HostListActivity, servers) {
            override fun onClickServerItem(server: Server, hostAdapter: HostAdapter, position: Int) {


                MainActivity.selectDefaultServer = server

                setResult(Activity.RESULT_OK)
                hostAdapter.notifyDataSetChanged()
                hostAdapter.selected = position
                finish()

                AVAnalytics.onEvent(this@HostListActivity, "Select Proxy Server")
            }
        }

        val divider = DividerItemDecoration(this@HostListActivity, DividerItemDecoration.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.custom_divider))
        recyclerViewHosts!!.addItemDecoration(divider)

        recyclerViewHosts!!.adapter = hostAdapter
        recyclerViewHosts!!.layoutManager = LinearLayoutManager(this@HostListActivity, LinearLayoutManager.VERTICAL, false)
    }

}
