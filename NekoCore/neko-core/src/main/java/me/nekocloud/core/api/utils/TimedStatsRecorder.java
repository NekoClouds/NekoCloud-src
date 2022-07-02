package me.nekocloud.core.api.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class TimedStatsRecorder {

    private final long limit;
    private final AtomicInteger count;
    private long time;

    public synchronized boolean canRecord() {
        return System.currentTimeMillis() < this.time && this.time != 0L;
    }

    public synchronized int getRecordCount() {
        return this.count.get();
    }

    public synchronized void record() {
        this.count.incrementAndGet();
    }

    public synchronized int getRecordCountAndReset() {
        time = System.currentTimeMillis() + this.limit;
        return count.getAndSet(0);
    }

    public TimedStatsRecorder(long limit) {
        count = new AtomicInteger();
        this.limit = limit;
    }
}

