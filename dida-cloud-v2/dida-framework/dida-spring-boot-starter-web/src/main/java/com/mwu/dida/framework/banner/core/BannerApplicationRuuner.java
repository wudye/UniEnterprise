package com.mwu.dida.framework.banner.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class BannerApplicationRuuner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        CompletableFuture.runAsync(() -> {
           try {
               Thread.sleep(1000);
               log.info("bannerApplication run");
               System.out.println("bannerApplication run");

           } catch (InterruptedException e) {
               log.error("bannerApplication run error", e);
               System.out.println("bannerApplication run error: " + e.getMessage());
               Thread.currentThread().interrupt();
           }
        });
    }
}
