用户想要比较不同的异步执行方式：
1. ScheduledExecutorService 和 Executors.newSingleThreadScheduledExecutor
2. taskExecutor.execute
3. CompletableFuture.runAsync
4. @Async + @EnableAsync

我需要详细解释每种方式的特点、适用场景、优缺点，并给出对比表格。
## 异步执行方式全面对比

### 1. **ScheduledExecutorService + Executors.newSingleThreadScheduledExecutor**

```java
try (ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor(r -> {
    Thread t = new Thread(r, "banner-task");
    t.setDaemon(true);
    return t;
})) {
    exec.schedule(() -> {
        log.info("bannerApplication run");
    }, 1, TimeUnit.SECONDS);
}
```

**特点：**
- ✅ 支持**定时/延迟**执行（`schedule`, `scheduleAtFixedRate`）
- ✅ Java 标准库，无外部依赖
- ✅ 单线程串行执行，线程安全
- ❌ 需要手动管理资源（try-with-resources）
- ❌ 配置相对繁琐（ThreadFactory 等）

**适用场景：**
- 需要定时任务、延迟执行
- 不想引入 Spring 依赖
- 需要精确控制线程池

---

### 2. **taskExecutor.execute**

```java
@Slf4j
public class BannerApplication implements ApplicationRunner {

    @Autowired
    private TaskExecutor taskExecutor;  // Spring 托管的线程池

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
}
```

**特点：**
- ✅ **复用 Spring 线程池**，资源利用率高
- ✅ 配置灵活，可在 application.yaml 中调整
- ✅ 不需要手动关闭资源
- ❌ 需要注入 TaskExecutor
- ❌ 不支持延迟执行（需手动 sleep）

**适用场景：**
- Spring Boot 项目
- 需要复用线程池
- 多个异步任务共享资源

---

### 3. **CompletableFuture.runAsync**

```java
@Override
public void run(ApplicationArguments args) throws Exception {
    CompletableFuture.runAsync(() -> {
        try {
            Thread.sleep(1000);
            log.info("bannerApplication run");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    });  // 可选：传入自定义 Executor
}
```

**特点：**
- ✅ **Java 8+ 原生**，无外部依赖
- ✅ 代码简洁优雅
- ✅ 支持链式操作（`thenApply`, `thenAccept`）
- ✅ 支持自定义线程池
- ❌ 默认使用 `ForkJoinPool.commonPool()`
- ❌ 不支持延迟执行（需手动 sleep）

**适用场景：**
- 快速实现异步逻辑
- 需要链式调用多个异步任务
- 非强依赖 Spring 的场景

**进阶用法：**
```java
CompletableFuture.runAsync(() -> {
    // 任务1
    log.info("Task 1");
}).thenRun(() -> {
    // 任务2（在任务1完成后执行）
    log.info("Task 2");
}).exceptionally(ex -> {
    // 异常处理
    log.error("Error", ex);
    return null;
});
```

---

### 4. **@Async + @EnableAsync**

```java
@Slf4j
@EnableAsync  // 在配置类上添加
public class BannerApplication implements ApplicationRunner {

    @Override
    @Async  // Spring 异步执行
    public void run(ApplicationArguments args) throws Exception {
        Thread.sleep(1000);
        log.info("bannerApplication run");
    }
}
```

**特点：**
- ✅ **声明式编程**，代码最简洁
- ✅ 完全由 Spring 管理，零代码负担
- ✅ 可配置超时、异常处理
- ❌ 需要启用 `@EnableAsync`
- ❌ 方法返回值受限（void/Future/CompletableFuture）
- ❌ 不支持延迟执行（需手动 sleep）

**适用场景：**
- Spring Boot 项目
- 需要大量异步方法
- 追求代码简洁性

**配置示例：**
```java
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        executor.initialize();
        return executor;
    }
}
```

---

## 综合对比表

| 特性 | ScheduledExecutorService | taskExecutor | CompletableFuture.runAsync | @Async |
|------|-------------------------|--------------|-------------------------|--------|
| **依赖** | Java 标准库 | Spring | Java 8+ | Spring |
| **代码简洁度** | ⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **资源管理** | 手动 | 自动 | 自动 | 自动 |
| **支持延迟执行** | ✅ 原生 | ❌ 需sleep | ❌ 需sleep | ❌ 需sleep |
| **线程池控制** | 完全控制 | 配置灵活 | 可自定义 | 可自定义 |
| **异常处理** | 手动 | 手动 | 链式处理 | AOP/全局 |
| **适用场景** | 定时任务 | Spring项目 | 快速异步 | Spring批处理 |
| **性能开销** | 中 | 低 | 低 | 低 |

---

## 针对你的 Banner 任务的推荐

### 方案选择决策树

```
需要延迟执行吗？
├─ 是 → ScheduledExecutorService（原生支持）
└─ 否（或接受手动 sleep）
    ├─ 是 Spring Boot 项目？
    │  ├─ 有大量异步方法？ → @Async
    │  └─ 只是个别场景？ → CompletableFuture.runAsync
    └─ 纯 Java 项目？
       → CompletableFuture.runAsync
```

### 最终推荐

根据你的场景（Banner 显示任务）：

| 优先级 | 方案 | 理由 |
|--------|------|------|
| 🥇 | **CompletableFuture.runAsync** | 简洁、无依赖、足够用 |
| 🥈 | **@Async** | Spring Boot 项目，代码最简洁 |
| 🥉 | **ScheduledExecutorService** | 原生支持延迟，无需 sleep |

### 我的建议：使用 CompletableFuture.runAsync

```java
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
```

**理由：**
- ✅ 满足异步执行需求
- ✅ 无需配置注解或注入 bean
- ✅ Java 原生支持
- ✅ 代码简洁易读
- ✅ 资源自动管理（使用 ForkJoinPool）

**唯一需要权衡的是**：`ForkJoinPool.commonPool()` 是全局共享的，如果应用有很多异步任务，可以考虑自定义线程池：

```java
private static final Executor bannerExecutor = Executors.newSingleThreadExecutor(r -> {
    Thread t = new Thread(r, "banner-task");
    t.setDaemon(true);
    return t;
});

@Override
public void run(ApplicationArguments args) throws Exception {
    CompletableFuture.runAsync(() -> {
        try {
            Thread.sleep(1000);
            log.info("bannerApplication run");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }, bannerExecutor);
}
```

这样既保持了简洁性，又拥有了完全的线程控制权！