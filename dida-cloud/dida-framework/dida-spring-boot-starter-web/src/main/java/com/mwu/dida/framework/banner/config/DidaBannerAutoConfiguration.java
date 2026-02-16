package com.mwu.dida.framework.banner.config;

import com.mwu.dida.framework.banner.core.BannerApplication;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class DidaBannerAutoConfiguration {

    @Bean
    public BannerApplication bannerApplication() {
        return new BannerApplication();
    }
}
