package com.mwu.dida.framework.banner.config;

import com.mwu.dida.framework.banner.core.BannerApplicationRuuner;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;

@AutoConfigureAfter
public class BanneApplicationConfig {

    @Bean
    public BannerApplicationRuuner bannerApplication() {
        return new BannerApplicationRuuner();
    }
}
