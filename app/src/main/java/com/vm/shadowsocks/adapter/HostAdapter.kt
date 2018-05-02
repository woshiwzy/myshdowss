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

/**
 * Created by wangzy on 2017/11/24.
 */

abstract class HostAdapter(private val context: Context, private val servers: ArrayList<Server>) : RecyclerView.Adapter<HostAdapter.ViewHolder>() {

    var selected: Int = -1
        get() = field
        set(value) {
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(View.inflate(context, R.layout.item_hosts, null))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //        Picasso.with(context).load(avObject.getAVFile("icon").getUrl()).placeholder(R.drawable.loading).into(holder.imageViewFlag);


        var server = servers[position]


        holder.textViewNodeName.setText(server.name)

//        if(selected==position){
//            holder.imageViewHook.imageResource=R.drawable.icon_yes
//        }else{
//            holder.imageViewHook.imageResource=R.drawable.icon_yes_p
//        }

        holder.textViewMethodPort.text = server.method

//        holder.textViewNodeCount.setText(context.getResources().getString(R.string.current) + String.valueOf(avObject.get("client_count")));
//        holder.imageViewSpeed.setImageResource("fast".equalsIgnoreCase(avObject.getString("speed")) ? R . drawable . icon_speed_fast : R . drawable . icon_speed_slow);

        holder.imageViewHook.imageResource = R.drawable.icon_speed_fast

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
            textViewMethodPort = rootView.findViewById(R.id.textViewMethodPort) as TextView
            textViewNodeName = rootView.findViewById(R.id.textViewHostName) as TextView
            imageViewHook = rootView.findViewById(R.id.imageViewHook) as ImageView

        }
    }
}
