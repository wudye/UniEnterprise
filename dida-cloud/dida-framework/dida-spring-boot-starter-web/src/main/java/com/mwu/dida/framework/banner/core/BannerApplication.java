package com.mwu.dida.framework.banner.core;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class BannerApplication implements ApplicationRunner {


//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        ThreadUtil.execute(()-> {
//            ThreadUtil.sleep(1, TimeUnit.SECONDS);
//            log.info("bannerApplication run");
//        });
//    }


    @Override
    public void run(ApplicationArguments args) {
        try (ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "banner-task");
            t.setDaemon(true);
            return t;
        })) {
            exec.schedule(() -> {
                log.info("bannerApplication run");
            }, 1, TimeUnit.SECONDS);
        }
    }


/*
    @Autowired
    private TaskExecutor taskExecutor;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        taskExecutor.execute(() -> {
            try {
                Thread.sleep(1000);
                log.info("bannerApplication run");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
@Override
public void run(ApplicationArguments args) throws Exception {
    CompletableFuture.runAsync(() -> {
        try {
            Thread.sleep(1000);
            log.info("bannerApplication run");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    });
}

@Slf4j
@EnableAsync  // 需要在配置类上添加
public class BannerApplication implements ApplicationRunner {

    @Override
    @Async  // Spring 异步执行
    public void run(ApplicationArguments args) throws Exception {
        Thread.sleep(1000);
        log.info("bannerApplication run");
    }
}
 */

}
