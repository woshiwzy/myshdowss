package com.vm.shadowsocks.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.avos.avoscloud.AVObject
import com.vm.shadowsocks.R
import com.vm.shadowsocks.tool.Tool
import java.util.*


abstract class MessageAdapter(private val context: Context, private val messages: MutableList<AVObject>) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    var selected: Int = -1
        get() = field
        set(value) {
            field = value
        }

    var random: Random? = null

    init {
        random = Random();
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(View.inflate(context, R.layout.item_message, null))
    }


    fun isZh(context: Context): Boolean {
        val locale = context.resources.configuration.locale
        val language = locale.language
        return if (language.endsWith("zh"))
            true
        else
            false
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //        Picasso.with(context).load(avObject.getAVFile("icon").getUrl()).placeholder(R.drawable.loading).into(holder.imageViewFlag);


        var msg = messages[position]

        if (isZh(context)) {
            holder.textViewTitle.setText(msg.getString("title_cn"))
            holder.textViewContent.text = msg.getString("content_cn")
        } else {
            holder.textViewTitle.setText(msg.getString("title"))
            holder.textViewContent.text = msg.getString("content")
        }

//        0:default 1:rate,2:update,3:important
        when (msg.getInt("type")) {

            0 -> {//普通消息
                holder.textViewTitle.setTextColor(Color.BLACK)
                holder.textViewContent.setTextColor(Color.DKGRAY)
                holder.rootView.setOnClickListener({
                    Tool.ToastShow(context as Activity, "Thanks!")
                })
            }

            1 -> { //评分
                holder.textViewTitle.setTextColor(Color.GREEN)
                holder.textViewContent.setTextColor(Color.GREEN)
                holder.rootView.setOnClickListener({

                    openApplicationMarket(context.packageName)
                })
            }

            2 -> {//重要消息
                holder.textViewTitle.setTextColor(context.resources.getColor(android.R.color.holo_red_dark))
                holder.textViewContent.setTextColor(context.resources.getColor(android.R.color.holo_red_light))
                holder.rootView.setOnClickListener({
                    openApplicationMarket(context.packageName)
                })
            }

        }

    }


    private fun openApplicationMarket(packageName: String) {
        try {
            val str = "market://details?id=$packageName"
            val localIntent = Intent(Intent.ACTION_VIEW)
            localIntent.data = Uri.parse(str)
            context.startActivity(localIntent)
        } catch (e: Exception) {
            val url = "http://app.mi.com/detail/163525?ref=search"
            openLinkBySystem(url)
        }
    }

    private fun openLinkBySystem(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        context.startActivity(intent)
    }

    abstract fun onClickServerItem(server: AVObject, hostAdapter: MessageAdapter, position: Int)

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class ViewHolder(var rootView: View) : RecyclerView.ViewHolder(rootView) {

        var textViewTitle: TextView
        var textViewContent: TextView


        init {
            textViewTitle = rootView.findViewById(R.id.textViewTitle) as TextView
            textViewContent = rootView.findViewById(R.id.textViewContent) as TextView

        }
    }
}
