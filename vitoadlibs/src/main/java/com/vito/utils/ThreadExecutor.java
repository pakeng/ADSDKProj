package com.vito.utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadExecutor {
    private static ThreadPoolExecutor executor = null;
    private static ThreadExecutor instance = null;

    public static ThreadExecutor getInstance(){
        if (instance==null){
            synchronized (ThreadExecutor.class){
                if (instance==null){
                    instance = new ThreadExecutor();
                    instance.init();
                }
            }
        }


        return instance;
    }


    private ThreadExecutor(){

    }

    private void init() {
        if (executor==null){
            synchronized (this){
                if (executor==null){
                    executor = new ThreadPoolExecutor(5, 10, 500, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(5));
                }
            }
        }
    }

    public boolean addTask(Runnable task){

        if (executor==null)
            return false;

        executor.execute(task);
        return true;
    }

}
