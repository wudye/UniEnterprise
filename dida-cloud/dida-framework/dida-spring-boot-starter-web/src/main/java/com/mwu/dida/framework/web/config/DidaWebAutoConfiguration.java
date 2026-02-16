package com.mwu.dida.framework.web.config;

import cn.hutool.core.text.AntPathMatcher;
import cn.hutool.core.util.StrUtil;
import com.mwu.dida.framework.common.biz.infra.logger.ApiErrorLogCommonApi;
import com.mwu.dida.framework.web.core.handler.GlobalExceptionHandler;
import com.mwu.dida.framework.web.core.handler.GlobalResponseBodyHandler;
import com.mwu.dida.framework.web.core.util.WebFrameworkUtils;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import com.google.common.collect.Maps;


import java.util.Map;
import java.util.function.Predicate;

@AutoConfiguration
@EnableConfigurationProperties(WebProperties.class)
public class DidaWebAutoConfiguration {

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public WebMvcRegistrations webMvcRegistrations(WebProperties webProperties) {
        return new WebMvcRegistrations() {
            @Override
            public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
                RequestMappingHandlerMapping mapping = new RequestMappingHandlerMapping();
                mapping.setPathPrefixes(buildPathPrefixes(webProperties));
                return WebMvcRegistrations.super.getRequestMappingHandlerMapping();
            }

            private Map<String, Predicate<Class<?>>> buildPathPrefixes(WebProperties webProperties) {
                AntPathMatcher antPathMatcher = new AntPathMatcher(".");
                Map<String, Predicate<Class<?>>> pathPrefixes = Maps.newLinkedHashMapWithExpectedSize(2);
                putPathPrefix(pathPrefixes, webProperties.getAdminApi(), antPathMatcher);
                putPathPrefix(pathPrefixes, webProperties.getAppApi(), antPathMatcher);
                return pathPrefixes;
            }

            private void putPathPrefix(Map<String, Predicate<Class<?>>> pathPrefixes, WebProperties.@NotNull(message = "Admin API 不能为空") Api adminApi, AntPathMatcher antPathMatcher) {
                if (adminApi == null || StrUtil.isEmpty(adminApi.getPrefix())) {
                    return;
                }
                pathPrefixes.put(adminApi.getPrefix(),
                        clazz -> clazz.isAnnotationPresent(RestController.class)
                                && antPathMatcher.match(adminApi.getController(), clazz.getPackage().getName())
                        );
            }
        };
    }

    @Bean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public GlobalExceptionHandler globalExceptionHandler(ApiErrorLogCommonApi apiErrorLogApi) {
        return new GlobalExceptionHandler(applicationName, apiErrorLogApi);
    }

    @Bean
    public GlobalResponseBodyHandler globalResponseBodyHandler() {
        return new GlobalResponseBodyHandler();
    }

    @Bean
    @SuppressWarnings("InstantiationOfUtilityClass")
    public WebFrameworkUtils webFrameworkUtils(WebProperties webProperties) {
        // 由于 WebFrameworkUtils 需要使用到 webProperties 属性，所以注册为一个 Bean
        return new WebFrameworkUtils(webProperties);
    }


}
