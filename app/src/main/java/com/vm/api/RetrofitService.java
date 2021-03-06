package com.vm.api;

import com.vm.shadowsocks.domain.RewardHistory;
import com.vm.shadowsocks.domain.Server;
import com.vm.shadowsocks.domain.User;
import com.wangzy.httpmodel.gson.ext.Result;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

public interface RetrofitService {

    @GET("listservers")
    Observable<Result<List<Server>>> listServers();


    @FormUrlEncoded
    @POST("register_device")
    Observable<Result<User>> registerDevice(@Field("username") String username,
                                            @Field("mac") String mac,
                                            @Field("ip") String ip,
                                            @Field("brand") String brand,
                                            @Field("imei") String imei,
                                            @Field("system_version") String system_version,
                                            @Field("country") String country,
                                            @Field("app_version") String app_version,
                                            @Field("channel") String channel
    );

    @FormUrlEncoded
    @POST("cost_traffic")
    Observable<Result<User>> costtraffic(@Field("uuid") String uuid,@Field("cost_size") int cost_size);


    @FormUrlEncoded
    @POST("checkrewardHis")
    Observable<Result<List<RewardHistory>>> checkrewardHisToday(@Field("uuid") String uui);


    @FormUrlEncoded
    @POST("reward_traffic")
    Observable<Result<User>> rewardTraffic(@Field("uuid") String uui,@Field("rewardsize") String rewardsize,@Field("descption") String descption);

}
