package com.kangpei.task.execute;

import com.kangpei.task.timer.SystemClock;
import com.kangpei.task.worker.WorkWrapper;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * description: Executor <br>
 * date: 2020/8/2 6:29 下午 <br>
 * author: kangpei <br>
 * version: 1.0 <br>
 */
public final class Executor {

    private static final int PROCESSORS = Runtime.getRuntime().availableProcessors();

    /**
     * 默认的执行任务线程池，
     */
    private static final ThreadPoolExecutor COMMON_POOL
            = new ThreadPoolExecutor(PROCESSORS * 2, PROCESSORS * 10,
            15L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));


    public static void work(ThreadPoolExecutor pool, int timeOut, List<WorkWrapper> workWrappers) {

        if (workWrappers == null || workWrappers.size() == 0) {
            return;
        }
        long now = SystemClock.now();
        for (WorkWrapper wrapper : workWrappers) {

            pool.submit(wrapper::work);
        }
    }


    public static void work(int timeOut, List<WorkWrapper> workWrappers) {

        work(COMMON_POOL, timeOut, workWrappers);
    }

    public static void work(int timeOut, WorkWrapper... workWrappers) {

        List<WorkWrapper> wrappers = Arrays.asList(workWrappers);
        work(timeOut, wrappers);
    }
}
