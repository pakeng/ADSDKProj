package com.vito.ad.base.entity;

/**
 * @author liuchonglong
 * @version V1.0
 * @Description: 请求api广告传过来的设备参数
 * @date 2018/5/25 10:14
 */
public class EquipmentParamsModel {
    private String imei;//移动设备标识码
    private String imsi;
    private String mac;// mac 地址
    private String androidId;
    private String brand; // 制造商
    private String model; //手机型号
    private String sys; // 系统版本
    private String sdk;//api版本号
    private String memeory;//手机内存大小 MB
    private String cpu; // 手机cpu类型
    private String ratio;// 屏幕分辨率1280X768
    private String density;// 屏幕密度

    private String appPackage;
    private String channel;//渠道
    private String appName;//点点消宝藏
    private String appVersion; // 游戏版本号 1.0.0

    private String so;// 横竖屏  1 竖屏 2 横屏
    private String connectionType;// 链接类型  2g 3g 4g wifi  unknown
    private String operator;//运营商
    private String addr; //经纬度  经度##维度
    private String ua; // userAgent
    private String ip;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSys() {
        return sys;
    }

    public void setSys(String sys) {
        this.sys = sys;
    }

    public String getSdk() {
        return sdk;
    }

    public void setSdk(String sdk) {
        this.sdk = sdk;
    }

    public String getMemeory() {
        return memeory;
    }

    public void setMemeory(String memeory) {
        this.memeory = memeory;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    public String getDensity() {
        return density;
    }

    public void setDensity(String density) {
        this.density = density;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getSo() {
        return so;
    }

    public void setSo(String so) {
        this.so = so;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getUa() {
        return ua;
    }

    public void setUa(String ua) {
        this.ua = ua;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }



}
