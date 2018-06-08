package com.vm.api;

import android.content.Context;

import com.vm.shadowsocks.domain.RewardHistory;
import com.vm.shadowsocks.domain.Server;
import com.vm.shadowsocks.domain.User;
import com.wangzy.httpmodel.gson.ext.Result;

import java.util.List;

import rx.Observable;

public class APIManager {
    private RetrofitService mRetrofitService;

    public APIManager(Context context) {
        this.mRetrofitService = RetrofitHelper.getInstance(context).getServer();
    }

    public Observable<Result<List<Server>>> listservers() {
        return mRetrofitService.listServers();
    }

    public Observable<Result<User>> registerDevice(String username, String mac, String ip, String brand, String imei, String system_version, String country, String app_version,String channel) {
        return mRetrofitService.registerDevice(username, mac, ip, brand, imei, system_version, country, app_version, channel);
    }

    public Observable<Result<User>> costtraffic(String uuid, int traffic) {
        return mRetrofitService.costtraffic(uuid, traffic);
    }

    public Observable<Result<List<RewardHistory>>> rewardList(String uuid) {
        return mRetrofitService.checkrewardHisToday(uuid);
    }
    public Observable<Result<User>> rewardTraffic(String uuid,String rewardsize,String descption) {
        return mRetrofitService.rewardTraffic(uuid,rewardsize,descption);
    }

}