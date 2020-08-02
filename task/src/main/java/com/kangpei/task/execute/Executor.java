package com.kangpei.task.execute;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * description: Executor <br>
 * date: 2020/8/2 6:29 下午 <br>
 * author: kangpei <br>
 * version: 1.0 <br>
 */
public class Executor {

    private static final int PROCESSORS = Runtime.getRuntime().availableProcessors();

    /**
     * 默认的执行任务线程池，
     */
    private static final ExecutorService COMMON_POOL
            = new ThreadPoolExecutor(PROCESSORS * 2, PROCESSORS * 10,
            15L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));
}
