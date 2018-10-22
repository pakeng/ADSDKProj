package com.vito.user;

public class UserInfo {
    private static UserInfo instance = null;
    private String uid = ""; // user id
    private String channel = "";
    private String token = "";
    private String deviceId = "";

    public static UserInfo getInstance() {
        if (instance == null){
            synchronized (UserInfo.class){
                instance = new UserInfo();
            }
        }
        return instance;
    }

    public UserInfo(){
        if (instance == null){
            instance = this;
        }
    }

    public String  getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }



    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
