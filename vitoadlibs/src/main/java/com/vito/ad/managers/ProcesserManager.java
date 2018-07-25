package com.vito.ad.managers;

import com.vito.ad.base.processor.IProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

// TODO
class ProcesserManager {
    private static ProcesserManager instance;
    private ArrayList<IProcessor> processers = new ArrayList<>(); // 可用的广告处理
    private ConcurrentHashMap<Integer, IProcessor> processersHashMap = new ConcurrentHashMap<>();
    private int currentIndex = 0;

    public static ProcesserManager getInstance(){
        if (instance==null){
            synchronized (ProcesserManager.class){
                if (instance == null)
                    instance = new ProcesserManager();
            }
        }
        return instance;
    }

    private ProcesserManager(){
    }

    public List<IProcessor> getProcessers(){
//        if (currentIndex<processers.size()){
//            return processers.get(currentIndex++);
//        }
//        currentIndex=0;
        return processers;

    }

    public void registerProcesser(int type, IProcessor processer){
        this.processersHashMap.put(type, processer);
        processers = new ArrayList<>(processersHashMap.values());
        // TODO 根据优先级排序
    }
}
