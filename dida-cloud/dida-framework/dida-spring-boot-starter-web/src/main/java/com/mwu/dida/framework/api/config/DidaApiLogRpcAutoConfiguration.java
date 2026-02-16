package com.mwu.dida.framework.api.config;

import com.mwu.dida.framework.common.biz.infra.logger.ApiAccessLogCommonApi;
import com.mwu.dida.framework.common.biz.infra.logger.ApiErrorLogCommonApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
@AutoConfiguration
@EnableFeignClients(clients = {ApiAccessLogCommonApi.class, ApiErrorLogCommonApi.class})
public class DidaApiLogRpcAutoConfiguration {
}
