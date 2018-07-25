package com.vito.ad.base.entity;

public class VideoDetail {
    public int id = 0;
    public float duration=0;
    public int playTime = 0; // 记录播放的次数

    public VideoDetail(int id, float duration){
        this.id = id;
        this.duration = duration;
    }

}
