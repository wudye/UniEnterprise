package com.mwu.dida.framework.swagger.cofig;

import com.github.xiaoymin.knife4j.spring.configuration.Knife4jProperties;
import io.swagger.v3.oas.models.OpenAPI;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.HashMap;
import java.util.Map;

@Primary
@Configuration
@Slf4j
public class Knife4jOpenApiCustomizer extends com.github.xiaoymin.knife4j.spring.extension.Knife4jOpenApiCustomizer
        implements GlobalOpenApiCustomizer {

    final Knife4jProperties knife4jProperties;
    final SpringDocConfigProperties properties;

    // Use ObjectProvider to avoid hard autowiring errors in IDE when the bean may be absent.
    // We supply sensible defaults if providers don't contain a bean.
    public Knife4jOpenApiCustomizer(ObjectProvider<Knife4jProperties> knife4jPropertiesProvider,
                                   ObjectProvider<SpringDocConfigProperties> propertiesProvider) {
        super(knife4jPropertiesProvider.getIfAvailable(Knife4jProperties::new),
                propertiesProvider.getIfAvailable(SpringDocConfigProperties::new));
        this.knife4jProperties = knife4jPropertiesProvider.getIfAvailable(Knife4jProperties::new);
        this.properties = propertiesProvider.getIfAvailable(SpringDocConfigProperties::new);
    }

    @Override
    public void customise(OpenAPI openApi) {

    }
}
