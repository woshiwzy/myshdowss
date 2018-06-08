package com.vm.shadowsocks.domain;

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;


@Entity
public class User {


    @Id(autoincrement = true)
    private Long localid;


    @SerializedName("enable")
    private boolean enable;

    @SerializedName("uuid")
    private String uuid;

    @SerializedName("disableMessage")
    private String disableMessage;
    @SerializedName("disableMessageCn")
    private String disableMessageCn;


    @SerializedName("usedByte")
    private long usedByte;

    @SerializedName("remaining_bytes")
    private long remaining_bytes;


    @SerializedName("total_bytes")
    private long total_bytes;


    @SerializedName("remainingM")
    private long remainingM;

    @SerializedName("personalMsg")
    private String personalMsg;

    @SerializedName("showad")
    private boolean showad;

    @Generated(hash = 403504436)
    public User(Long localid, boolean enable, String uuid, String disableMessage,
            String disableMessageCn, long usedByte, long remaining_bytes,
            long total_bytes, long remainingM, String personalMsg, boolean showad) {
        this.localid = localid;
        this.enable = enable;
        this.uuid = uuid;
        this.disableMessage = disableMessage;
        this.disableMessageCn = disableMessageCn;
        this.usedByte = usedByte;
        this.remaining_bytes = remaining_bytes;
        this.total_bytes = total_bytes;
        this.remainingM = remainingM;
        this.personalMsg = personalMsg;
        this.showad = showad;
    }

    @Generated(hash = 586692638)
    public User() {
    }


    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDisableMessage() {
        return disableMessage;
    }

    public void setDisableMessage(String disableMessage) {
        this.disableMessage = disableMessage;
    }

    public String getDisableMessageCn() {
        return disableMessageCn;
    }

    public void setDisableMessageCn(String disableMessageCn) {
        this.disableMessageCn = disableMessageCn;
    }

    public long getUsedByte() {
        return usedByte;
    }

    public void setUsedByte(long usedByte) {
        this.usedByte = usedByte;
    }

    public long getRemaining_bytes() {
        return remaining_bytes;
    }

    public void setRemaining_bytes(long remaining_bytes) {
        this.remaining_bytes = remaining_bytes;
    }

    public long getTotal_bytes() {
        return total_bytes;
    }

    public void setTotal_bytes(long total_bytes) {
        this.total_bytes = total_bytes;
    }

    public long getRemainingM() {
        return remainingM;
    }

    public void setRemainingM(long remainingM) {
        this.remainingM = remainingM;
    }

    public Long getLocalid() {
        return localid;
    }

    public void setLocalid(Long localid) {
        this.localid = localid;
    }

    public boolean getEnable() {
        return this.enable;
    }

    public String getPersonalMsg() {
        return personalMsg;
    }

    public void setPersonalMsg(String personalMsg) {
        this.personalMsg = personalMsg;
    }

    public boolean isShowad() {
        return showad;
    }

    public void setShowad(boolean showad) {
        this.showad = showad;
    }

    public boolean getShowad() {
        return this.showad;
    }
}
