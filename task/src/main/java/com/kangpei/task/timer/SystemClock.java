package com.kangpei.task.timer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * description: SystemClock
 * 提供一个系统的时钟，由于在高并发的情况下。
 * 直接调用System.currentTimeMills 会出现卡顿现象。
 * 甚至比创建一个对象的时间周期还要长，所以这里重新实现了获取当前时间戳的方法<br>
 * date: 2020/8/2 6:03 下午 <br>
 * author: kangpei <br>
 * version: 1.0 <br>
 */
public class SystemClock {


    private int period;

    private AtomicLong clock;

    public SystemClock(int period) {
        this.period = period;
        this.clock = new AtomicLong(System.currentTimeMillis());
        scheduleAtFixedRate();
    }

    public static class SystemClockHolder {

        private static final SystemClock INSTANCE = new SystemClock(1);
    }

    private void scheduleAtFixedRate() {

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        });
        executor.scheduleAtFixedRate(() -> this.clock.set(System.currentTimeMillis()), period, period, TimeUnit.MILLISECONDS);

    }

    private static SystemClock instance() {

        return SystemClockHolder.INSTANCE;
    }

    public static long now() {

        return instance().clock.get();
    }
}
