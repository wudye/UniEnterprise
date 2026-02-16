用户询问的是 `GroupedOpenApi` 是什么。这是 SpringDoc OpenAPI 中的一个概念，用于对 API 进行分组管理。

让我先检查一下是否有相关的代码在项目中使用了 GroupedOpenApi。
好的，我找到了代码中的使用。让我查看更多上下文来完整解释。
好的，现在我可以看到代码中如何使用 GroupedOpenApi。让我解释这个概念。
# GroupedOpenApi 详解

`GroupedOpenApi` 是 **SpringDoc OpenAPI** 提供的 API 分组功能，用于将接口文档按业务维度划分成多个组。

---

## 核心概念

```
┌─────────────────────────────────────────────────────────────┐
│  场景：一个项目中有多个模块的 API                               │
│  ├── 管理后台 API (/admin-api/user/**)                        │
│  ├── 移动端 API (/app-api/order/**)                           │
│  └── 开放 API (/open-api/auth/**)                             │
│                                                             │
│  问题：所有接口混在一起，文档混乱难找                           │
│                                                             │
│  解决：用 GroupedOpenApi 分组展示                              │
│  ├── 分组 "admin" → 只显示 /admin-api/**                      │
│  ├── 分组 "app" → 只显示 /app-api/**                          │
│  └── 分组 "all" → 显示所有接口                                │
└─────────────────────────────────────────────────────────────┘
```

---

## 代码中的使用

```114:132:dida-framework/dida-spring-boot-starter-web/src/main/java/com/mwu/dida/framework/swagger/cofig/DidaSwaggerAutoConfiguration.java
@Bean
public GroupedOpenApi allGroupedOpenApi() {
    return buildGroupedOpenApi("all", "");
}

public static GroupedOpenApi buildGroupedOpenApi(String group, String path) {
    return GroupedOpenApi.builder()
            .group(group)                          // 分组名称
            .pathsToMatch("/admin-api/" + path + "/**", "/app-api/" + path + "/**")  // 匹配路径
            .addOperationCustomizer((operation, handlerMethod) -> operation
                    .addParametersItem(buildTenantHeaderParameter())      // 添加公共请求头
                    .addParametersItem(buildSecurityHeaderParameter()))
            .addOperationCustomizer(buildOpetationIdCustomizer())         // 自定义 operationId
            .build();
}
```

---

## 效果展示

访问 Knife4j/Swagger UI 时，右上角会有分组选择器：

```
┌────────────────────────────────────────┐
│  🔄 分组: [ all ▼ ]                    │
├────────────────────────────────────────┤
│  可选分组：                             │
│  • all        - 所有接口                │
│  • system     - 系统模块 (system/**)    │
│  • user       - 用户模块 (user/**)      │
│  • order      - 订单模块 (order/**)     │
└────────────────────────────────────────┘
```

---

## 常见用法

```java
// 1. 简单分组 - 按路径
@Bean
public GroupedOpenApi userApi() {
    return GroupedOpenApi.builder()
            .group("用户模块")
            .pathsToMatch("/api/user/**")
            .build();
}

// 2. 多路径匹配
@Bean
public GroupedOpenApi adminApi() {
    return GroupedOpenApi.builder()
            .group("管理后台")
            .pathsToMatch("/admin-api/**", "/system/**")
            .build();
}

// 3. 按包名分组
@Bean
public GroupedOpenApi orderApi() {
    return GroupedOpenApi.builder()
            .group("订单模块")
            .packagesToScan("com.example.controller.order")
            .build();
}

// 4. 排除特定路径
@Bean
public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
            .group("开放接口")
            .pathsToMatch("/api/**")
            .pathsToExclude("/api/admin/**", "/api/internal/**")
            .build();
}
```

---

## 核心 API 一览

| 方法 | 说明 |
|------|------|
| `.group(String)` | 分组名称（显示在 UI 上） |
| `.pathsToMatch(String...)` | 包含的路径模式（Ant 风格） |
| `.pathsToExclude(String...)` | 排除的路径模式 |
| `.packagesToScan(String...)` | 扫描的包路径 |
| `.packagesToExclude(String...)` | 排除的包路径 |
| `.addOpenApiCustomizer(...)` | OpenAPI 级别自定义 |
| `.addOperationCustomizer(...)` | 操作级别自定义 |

---

## 总结

```
┌─────────────────────────────────────────────────────────────┐
│  GroupedOpenApi = API 文档的"文件夹"功能                      │
│                                                             │
│  作用：                                                      │
│  1. 将大量接口按业务/模块分类                                 │
│  2. 不同角色看不同分组（开发看全部，测试看特定模块）            │
│  3. 每个分组独立生成 OpenAPI 规范文件（api-docs/{group}）     │
│                                                             │
│  代码中：                                                    │
│  • "all" 分组显示所有 /admin-api/** 和 /app-api/** 接口       │
│  • 自动添加租户头、安全头等公共参数                             │
└─────────────────────────────────────────────────────────────┘
```