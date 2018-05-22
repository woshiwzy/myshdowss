package com.vm.shadowsocks.domain;

import com.vm.shadowsocks.App;
import com.vm.shadowsocks.tool.SystemUtil;
import com.vm.shadowsocks.tool.Tool;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class Log {

    @Id(autoincrement = true)
    private Long id;
    private String mac;
    private String ip;
    private String brand;
    private String model;
    private String imei;
    private String system_version;
    private String country;
    private String app_version;

    private Integer port;
    private String method;
    private long time;

    @Generated(hash = 1364647056)
    public Log() {


    }

    @Generated(hash = 1706800390)
    public Log(Long id, String mac, String ip, String brand, String model,
            String imei, String system_version, String country, String app_version,
            Integer port, String method, long time) {
        this.id = id;
        this.mac = mac;
        this.ip = ip;
        this.brand = brand;
        this.model = model;
        this.imei = imei;
        this.system_version = system_version;
        this.country = country;
        this.app_version = app_version;
        this.port = port;
        this.method = method;
        this.time = time;
    }

    public void init() {
        String address = Tool.getAdresseMAC(App.instance);
        String ip = Tool.getLocalIpAddress();
        String brand = SystemUtil.getDeviceBrand();
        String model = SystemUtil.getSystemModel();
        String imei = SystemUtil.getIMEI(App.instance);

        this.mac = address;
        this.ip = ip;
        this.brand = brand;
        this.model = model;
        this.imei = imei;
        this.system_version = Tool.getSystemVersion();
        this.country = Tool.getCountryCode();
        this.app_version = Tool.getVersionName(App.instance);
    }

    public void init(int port,String method){
        init();
        this.method=method;
        this.port=port;
    }

//     avObject.put("mac",address);
//            avObject.put("ip",ip);
//            avObject.put("brand",brand +","+model);
//            avObject.put("imei",imei);
//
//            avObject.put("system_version",Tool.getSystemVersion());
//            avObject.put("country",Tool.getCountryCode());
//            avObject.put("app_version",Tool.getVersionName(MainActivity.this));
//
//        avObject.put("user",avUser);
//        avObject.put("tag",avUser.get("alias_tag"));


    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getSystem_version() {
        return system_version;
    }

    public void setSystem_version(String system_version) {
        this.system_version = system_version;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
