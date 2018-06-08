package com.vm.shadowsocks.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.avos.avoscloud.AVObject
import com.vm.shadowsocks.R
import com.vm.shadowsocks.domain.RewardHistory
import java.util.*


abstract class RewardAdapter(private val context: Context, private val messages: List<RewardHistory>) : RecyclerView.Adapter<RewardAdapter.ViewHolder>() {

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
        return ViewHolder(View.inflate(context, R.layout.item_reward, null))
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

        var msg = messages[position]
        holder.textViewTitle.setText(String.format(context.resources.getString(R.string.get_ward),msg.reward_size))
        holder.textViewContent.text =String.format(context.resources.getString(R.string.reawason),msg.descption)
        holder.textViewTime.text=msg.create_time
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

    abstract fun onClickServerItem(server: AVObject, hostAdapter: RewardAdapter, position: Int)

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class ViewHolder(var rootView: View) : RecyclerView.ViewHolder(rootView) {

        var textViewTitle: TextView
        var textViewContent: TextView
        var textViewTime:TextView

        init {
            textViewTitle = rootView.findViewById(R.id.textViewTitle) as TextView
            textViewContent = rootView.findViewById(R.id.textViewContent) as TextView
            textViewTime= rootView.findViewById(R.id.textViewTime) as TextView

        }
    }
}
