package com.vm.shadowsocks.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.squareup.picasso.Picasso;
import com.vm.shadowsocks.R;

import java.util.List;

/**
 * Created by wangzy on 2017/11/24.
 */

public abstract class HostAdapter extends RecyclerView.Adapter<HostAdapter.ViewHolder> {


    private List<AVObject> avObjects;
    private Context context;

    public HostAdapter(Context context, List<AVObject> avObjects) {
        this.context = context;
        this.avObjects = avObjects;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(View.inflate(context, R.layout.item_hosts, null));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        final AVObject avObject = avObjects.get(position);

        Picasso.with(context).load(avObject.getAVFile("icon").getUrl()).placeholder(R.drawable.loading).into(holder.imageViewFlag);


//        String country= Tool.isZh()?avObject.getString("country"):avObject.getString("country_en");

        holder.textViewNodeName.setText(avObject.getString("name"));

        holder.textViewNodeCount.setText(context.getResources().getString(R.string.current) + String.valueOf(avObject.get("client_count")));

        holder.imageViewSpeed.setImageResource("fast".equalsIgnoreCase(avObject.getString("speed")) ? R.drawable.icon_speed_fast : R.drawable.icon_speed_slow);


        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onClickServerItem(avObject, HostAdapter.this);
            }
        });


    }

    public abstract void onClickServerItem(AVObject avObject, HostAdapter hostAdapter);

    @Override
    public int getItemCount() {
        return avObjects.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewFlag;
        TextView textViewNodeName;
        TextView textViewNodeCount;
        ImageView imageViewSpeed;

        View rootView;


        public ViewHolder(View itemView) {
            super(itemView);
            imageViewFlag = itemView.findViewById(R.id.imageViewFlag);
            textViewNodeCount = itemView.findViewById(R.id.textViewConectCount);
            textViewNodeName = itemView.findViewById(R.id.textViewHostName);

            imageViewSpeed = itemView.findViewById(R.id.imageViewSpeed);

            rootView = itemView;

        }
    }
}
