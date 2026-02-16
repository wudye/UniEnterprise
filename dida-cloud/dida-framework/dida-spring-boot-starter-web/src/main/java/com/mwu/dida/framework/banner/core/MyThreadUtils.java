package com.mwu.dida.framework.banner.core;




import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class MyThreadUtils {

    private static final ScheduledExecutorService SCHED = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "dida-threadutils");
            t.setDaemon(true);
            return t;
        }
    });

    public static void execute(Runnable task) {
        SCHED.execute(task);
    }

    public static ScheduledFuture<?> schedule(Runnable task, long delay, TimeUnit unit) {
        return SCHED.schedule(task, delay, unit);
    }

    public static void sleep(long duration, TimeUnit unit) {
        try {
            unit.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void shutdown() {
        SCHED.shutdown();
    }
}

