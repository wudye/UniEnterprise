# interface methods default is public

# ObjectProvider can check the bean exist or not,not exist return null or can create a new instance by supplier
# can @Autowired(required = false) to do, but if you forget to check null when use it, will cause NPE

# OpenAPI config
1. `SecurityScheme` = 定义认证方式（API Key、HTTP、OAuth2 等）
2. `SecurityRequirement` = 定义哪些接口需要认证（全局或特定接口）
3. `@Bean` 方法注册 OpenAPI 配置，Swagger UI 根据这些配置生成文档和认证界面
4. GroupedOpenApi = 定义接口分组，方便在 Swagger UI 中按模块查看接口

# webproperties in web
 define static class in this class , use can set the config levels in the application.yaml, 
 and can use @EnableConfigurationProperties to enable the properties class, 
 then can inject the properties class to use the config values.