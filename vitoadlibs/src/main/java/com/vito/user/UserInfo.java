package com.vito.user;

public class UserInfo {
    private static UserInfo instance = null;
    private String uid; // user id

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
}
