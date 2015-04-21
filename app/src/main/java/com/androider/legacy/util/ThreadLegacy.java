package com.androider.legacy.util;

import java.lang.reflect.Array;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Think on 2015/4/19.
 */
public class ThreadLegacy {
    private ThreadLegacy(){

    }
    private static class SingletonHolder{
        private static ThreadLegacy instance = new ThreadLegacy();
    }
    public static ThreadLegacy getInstance(){
        return SingletonHolder.instance;
    }
    private static int CORE_POOL_SIZE = 5;
    private static int MAX_POLL_SIZE = 10;
    private static int KEEP_ALIVE_TIME = 10000;

    private static ThreadFactory threadFactory = new ThreadFactory() {
        private final AtomicInteger integer = new AtomicInteger();
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Thread" + integer.getAndIncrement());
        }
    };

    private static ArrayBlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(10);

    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POLL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue, threadFactory);

    public static void execute(Runnable r){
        threadPool.execute(r);
    }
}
