package com.vito.ad.channels.adhub;

public class Config {
    public static final int ADTYPE = 2;
    public static final String ADBASEURL = "http://api.htp.hubcloud.com.cn:45600/json/v1/sdk0"; // 正式服
    public static final String Version = "1.8.7";
    public static String appid = "1916";
    public static String spaceID = "5499";
    public static final String testString = "{\"version\":\"0.2.2\",\"srcType\":1,\"reqType\":1,\"timeStamp\":1499743507,\"appid\":\"196\",\"appVersion\":\"1.0\",\"devInfo\":{\"sdkUID\":\"121AF4B6-2807-4A35-A2D0-9D7C1DB3D5B8\",\"imei\":\"355308089354037\",\"mac\":\"02:00:00:00:00:00\",\"phone\":[\"14522098604\"],\"os\":\"22 (5.1)\",\"platform\":2,\"devType\":1,\"brand\":\"HUAWEI\",\"model\":\"HUAWEI TAG-AL00\",\"resolution\":\"720_1184\",\"screenSize\":\"5.7\",\"language\":\"zh\",\"density\":\"2.0\",\"androidID\":\"5a3b287f2b13bef8\"},\"envInfo\":{\"net\":4,\"isp\":1,\"ip\":\"106.117.103.121\",\"userAgent\":\"Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_2 like Mac OS X) AppleWebKit/603.2.4 (KHTML, like Gecko) Mobile/14F89\",\"geo\":{\"longitude\":\"23.458635\",\"latitude\":\"-50.273971\"},\"age\":12,\"yob\":1982,\"gender\":1,\"income\":15000},\"adReqInfo\":[{\"spaceID\":\"714\",\"spaceParam\":\"\",\"screenStatus\":1}]}";
    public enum NetType{
        NET_UNKNOWN,
        NET_3G,
        NET_4G,
        NET_5G,
        NET_WIFI,
        NET_OTHER,
        NET_2G,
    }

    public enum IspType{
        ISP_UNKNOWN,
        ISP_CN_MOBILE,
        ISP_CN_UNICOM,
        ISP_CN_TEL,
        ISP_OTHER,
    }
}
