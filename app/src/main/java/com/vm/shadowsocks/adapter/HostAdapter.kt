package com.vm.shadowsocks.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.vm.shadowsocks.R
import com.vm.shadowsocks.domain.Server
import org.jetbrains.anko.imageResource
import java.util.*

/**
 * Created by wangzy on 2017/11/24.
 */

abstract class HostAdapter(private val context: Context, private val servers: ArrayList<Server>) : RecyclerView.Adapter<HostAdapter.ViewHolder>() {

    var selected: Int = -1
        get() = field
        set(value) {
            field = value
        }

    var random:Random?=null

    init {
        random= Random();
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(View.inflate(context, R.layout.item_hosts, null))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var server = servers[position]
        holder.textViewNodeName.setText(server.name)
        var ret=String.Companion.format(context.resources.getString(R.string.online_pre),""+(server.online))
        holder.textViewMethodPort.text =ret
        holder.imageViewHook.imageResource =if (random?.nextInt(10)?.div(2)!=0) R.drawable.icon_speed_fast else R.drawable.icon_speed_slow
        holder.rootView.setOnClickListener {
            onClickServerItem(server, this, position)
        }

    }

    abstract fun onClickServerItem(server: Server, hostAdapter: HostAdapter, position: Int)

    override fun getItemCount(): Int {
        return servers.size
    }

    inner class ViewHolder(var rootView: View) : RecyclerView.ViewHolder(rootView) {

        var imageViewFlag: ImageView
        var textViewNodeName: TextView
        var textViewMethodPort: TextView
        var imageViewHook: ImageView

        init {
            imageViewFlag = rootView.findViewById(R.id.imageViewFlag) as ImageView
            textViewMethodPort = rootView.findViewById(R.id.textViewCurrentOnline) as TextView
            textViewNodeName = rootView.findViewById(R.id.textViewHostName) as TextView
            imageViewHook = rootView.findViewById(R.id.imageViewHook) as ImageView

        }
    }
}
