package com.vm.api_okapi;

import com.vm.api.RetrofitHelper;
import com.wangzy.httpmodel.HttpRequester;
import com.wangzy.httpmodel.MyNetCallBackExtend;

import okhttp3.Call;

public class NetInterface {

    public static Call offline(String hostId, MyNetCallBackExtend myNetCallBackExtend) {
        return HttpRequester.get(RetrofitHelper.BASE_URL + "offline?hostid=" + hostId, null, myNetCallBackExtend);
    }

    public static Call online(String hostId, MyNetCallBackExtend myNetCallBackExtend) {
        return HttpRequester.get(RetrofitHelper.BASE_URL + "online?hostid=" + hostId, null, myNetCallBackExtend);
    }

}
