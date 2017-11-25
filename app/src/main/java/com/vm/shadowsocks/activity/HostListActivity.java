package com.vm.shadowsocks.activity;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.vm.shadowsocks.App;
import com.vm.shadowsocks.R;
import com.vm.shadowsocks.adapter.HostAdapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HostListActivity extends BaseActivity {

    private RecyclerView recyclerViewHosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_host_list);
        recyclerViewHosts = findViewById(R.id.recyclerViewHosts);

        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });


        loadHosts();

    }

    private void loadHosts() {

        showCover();

        AVQuery<AVObject> avQuery = new AVQuery<>("ss_server");
        avQuery.include("icon");

        avQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, final AVException e) {

                hideCover();

                Log.i(App.tag, "size:" + (list.size()));

                AVObject avObject = list.get(0);

                Log.i(App.tag, "avobjects:" + avObject.get(""));

                Collections.sort(list, new Comparator<AVObject>() {
                    @Override
                    public int compare(AVObject avObject, AVObject t1) {

                        int c1 = avObject.getInt("client_count");
                        int c2 = t1.getInt("client_count");


                        return c1-c2;

                    }
                });


                HostAdapter hostAdapter = new HostAdapter(HostListActivity.this, list) {
                    @Override
                    public void onClickServerItem(AVObject avObject, HostAdapter hostAdapter) {

                        MainActivity.selectDefaultServer = avObject;

                        setResult(RESULT_OK);
                        finish();


//                        hostAdapter.notifyDataSetChanged();

                    }
                };


                DividerItemDecoration divider = new DividerItemDecoration(HostListActivity.this, DividerItemDecoration.VERTICAL);
                divider.setDrawable(getResources().getDrawable((R.drawable.custom_divider)));
                recyclerViewHosts.addItemDecoration(divider);

                recyclerViewHosts.setAdapter(hostAdapter);
                recyclerViewHosts.setLayoutManager(new LinearLayoutManager(HostListActivity.this, LinearLayoutManager.VERTICAL, false));


            }
        });


    }


}
