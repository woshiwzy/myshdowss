package com.vm.shadowsocks.domain;

public class EventMessage {


    public long sent;
    public long received;
    public long totalUsed;

    public int type = 500;
    public Object extra;

    public static final int TYPE_MSG_ERROR = 500;
    public static final int TYPE_MSG_REGIST = 501;

}
