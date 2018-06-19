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
import com.vm.api.APIManager
import com.vm.shadowsocks.R
import com.vm.shadowsocks.adapter.HostAdapter
import com.vm.shadowsocks.constant.Constant.TAG
import com.vm.shadowsocks.domain.Server
import com.vm.shadowsocks.tool.LogUtil
import com.wangzy.httpmodel.JsonSelector
import com.wangzy.httpmodel.gson.ext.Result
import kotlinx.android.synthetic.main.activity_host_list.*
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.IOException


class HostListActivity : BaseActivity() {

    private var recyclerViewHosts: RecyclerView? = null
    var allServers = java.util.ArrayList<Server>()
    lateinit var hostAdapter: HostAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_host_list)
        recyclerViewHosts = findViewById(R.id.recyclerViewHosts)
        findViewById<View>(R.id.imageViewBack).setOnClickListener { finish() }

        initView()
        swipeRefreshLayout.setOnRefreshListener {
            requestServers(false)
        }
        requestServers(true)


    }

    fun initView() {

        hostAdapter = object : HostAdapter(this@HostListActivity, allServers) {
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

    fun setUpAdapter(json: String) {
        val data = JsonSelector.getJsonObject(json, "data")
        val gson = Gson()
        val servers = gson.fromJson<ArrayList<Server>>(data, object : TypeToken<ArrayList<Server>>() {}.type)
        fillAdapter(servers)
    }

    private fun useDefaultHosts() {
        var json = readAssetsTxt(this@HostListActivity, "proxy.json")
        setUpAdapter(json)

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

    private fun stopRefresh() {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false)
        }
    }

    private fun requestServers(showdialog: Boolean) {
        val dataManager = APIManager(this)
        val subscription = dataManager.listservers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Result<List<Server>>>() {
                    override fun onCompleted() {

                        if (showdialog) {
                            hideCover()
                        }
                        stopRefresh()
                    }

                    override fun onError(e: Throwable) {

                        if (showdialog) {
                            hideCover()
                        }
                        stopRefresh()
                    }

                    override fun onNext(listResult: Result<List<Server>>) {
                        if (showdialog) {
                            hideCover()
                        }
                        LogUtil.i(TAG, "serversult:" + listResult.data.size)
                        fillAdapter(listResult.data as ArrayList<Server>)
                    }

                    override fun onStart() {
                        if (showdialog) {
                            showCover()
                        }
                    }
                })

    }


    fun fillAdapter(servers: ArrayList<Server>) {

        allServers.clear()
        allServers.addAll(servers)
        hostAdapter.notifyDataSetChanged()
    }


}
