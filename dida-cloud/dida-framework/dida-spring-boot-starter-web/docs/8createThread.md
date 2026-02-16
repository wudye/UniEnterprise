用户问了三个问题：
1. 创建线程的方式有哪些？
2. ThreadLocal 是什么？和线程的区别？
3. 虚拟线程（Virtual Thread）是什么？和普通线程的区别？

让我先读取当前的 MyThreadUtils.java 文件，然后给出全面的解答。
让我先查看当前文件，然后详细讲解创建线程的方式、ThreadLocal 和虚拟线程。
好的，现在让我全面解答用户的问题：
1. 创建线程的方式
2. ThreadLocal 与线程的区别
3. 虚拟线程（Virtual Thread）与普通线程的区别

这是一个很好的问题，需要系统性地讲解。
## 创建线程的完整方式对比

### 方式1：直接创建 Thread（基础方式）

```java
// ✅ 基础方式
Thread thread = new Thread(() -> {
    System.out.println("任务执行");
});
thread.start();

// ❌ 问题：无限制创建会导致 OOM
for (int i = 0; i < 100000; i++) {
    new Thread(() -> doSomething()).start();  // 崩溃！
}
```

---

### 方式2：继承 Thread 类（不推荐）

```java
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("任务执行");
    }
}

// 使用
MyThread thread = new MyThread();
thread.start();

// ❌ 问题：Java 单继承，无法继承其他类
```

---

### 方式3：实现 Runnable 接口（推荐）

```java
class MyTask implements Runnable {
    @Override
    public void run() {
        System.out.println("任务执行");
    }
}

// 使用方式1：Thread + Runnable
Thread thread = new Thread(new MyTask());
thread.start();

// 使用方式2：Lambda（更简洁）
Thread thread = new Thread(() -> {
    System.out.println("任务执行");
});
thread.start();
```

---

### 方式4：使用线程池（ExecutorService）

```java
// 固定大小线程池
ExecutorService pool1 = Executors.newFixedThreadPool(10);

// 缓存线程池（自动扩展）
ExecutorService pool2 = Executors.newCachedThreadPool();

// 单线程池（串行执行）
ExecutorService pool3 = Executors.newSingleThreadExecutor();

// 提交任务
pool1.submit(() -> {
    System.out.println("任务执行");
});

// 关闭线程池
pool1.shutdown();
```

---

### 方式5：使用 ThreadFactory（你的方式）

```java
// 自定义线程工厂
ThreadFactory factory = r -> {
    Thread t = new Thread(r);
    t.setName("my-thread");
    t.setDaemon(true);
    return t;
};

// 使用
ExecutorService pool = Executors.newFixedThreadPool(10, factory);
```

---

### 方式6：使用 CompletableFuture（Java 8+）

```java
// 异步执行
CompletableFuture.runAsync(() -> {
    System.out.println("任务执行");
});

// 链式调用
CompletableFuture.supplyAsync(() -> {
    return "结果";
}).thenApply(result -> {
    return result.toUpperCase();
}).thenAccept(result -> {
    System.out.println(result);
});

// 自定义线程池
ExecutorService pool = Executors.newFixedThreadPool(10);
CompletableFuture.runAsync(() -> {
    System.out.println("任务执行");
}, pool);
```

---

### 方式7：使用 Spring @Async（Spring Boot）

```java
@Configuration
@EnableAsync
class AsyncConfig {
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        return executor;
    }
}

@Service
class MyService {
    @Async
    public void asyncMethod() {
        System.out.println("异步执行");
    }
}
```

---

### 方式8：使用虚拟线程（Java 21+）

```java
// 虚拟线程（轻量级线程）
Thread virtualThread = Thread.ofVirtual().start(() -> {
    System.out.println("虚拟线程执行");
});

// Executors.newVirtualThreadPerTaskExecutor()
ExecutorService virtualExecutor = Executors.newVirtualThreadPerTaskExecutor();
virtualExecutor.submit(() -> {
    System.out.println("虚拟线程执行");
});
```

---

## 创建方式对比表

| 方式 | 代码量 | 灵活性 | 性能 | 推荐度 | 适用场景 |
|------|--------|--------|------|--------|---------|
| `new Thread()` | 少 | 高 | 差 | ⭐ | 简单测试 |
| `extends Thread` | 多 | 低 | 差 | ❌ | 几乎不用 |
| `implements Runnable` | 中 | 中 | 差 | ⭐⭐ | 学习示例 |
| `ExecutorService` | 中 | 高 | 好 | ⭐⭐⭐⭐ | 生产环境 |
| `ThreadFactory` | 多 | 高 | 好 | ⭐⭐⭐⭐ | 需要自定义 |
| `CompletableFuture` | 少 | 高 | 好 | ⭐⭐⭐⭐⭐ | 链式调用 |
| `@Async` | 最少 | 中 | 好 | ⭐⭐⭐⭐⭐ | Spring Boot |
| `Virtual Thread` | 少 | 高 | 极好 | ⭐⭐⭐⭐⭐ | 高并发 |

---

## Thread vs ThreadLocal

### 核心概念对比

```
Thread（线程）：
┌─────────────────────────────────┐
│  线程1                          │
│  ┌───────────────────────────┐  │
│  │ Stack（栈）               │  │  ← 局部变量
│  │ - method1()                │  │
│  │ - method2()                │  │
│  └───────────────────────────┘  │
│  ┌───────────────────────────┐  │
│  │ ThreadLocalMap           │  │  ← 线程本地存储
│  │ - userId: "user1"         │  │
│  │ - requestId: "req1"       │  │
│  └───────────────────────────┘  │
└─────────────────────────────────┘

ThreadLocal（线程本地存储）：
- 每个线程都有自己独立的副本
- 线程间互不干扰
- 类似"线程作用域"的变量
```

---

### ThreadLocal 详解

#### 1. **ThreadLocal 是什么？**

`ThreadLocal` 是 Java 提供的**线程局部变量**，每个线程都有自己独立的副本，互不影响。

#### 2. **基本使用**

```java
public class ThreadLocalDemo {
    
    // 创建 ThreadLocal 变量
    private static ThreadLocal<String> userContext = new ThreadLocal<>();
    
    public static void main(String[] args) {
        // 线程1
        Thread thread1 = new Thread(() -> {
            userContext.set("用户1");
            System.out.println("线程1: " + userContext.get());  // 输出: 用户1
            userContext.remove();  // 清除
        });
        
        // 线程2
        Thread thread2 = new Thread(() -> {
            userContext.set("用户2");
            System.out.println("线程2: " + userContext.get());  // 输出: 用户2
            userContext.remove();  // 清除
        });
        
        thread1.start();
        thread2.start();
    }
}
```

#### 3. **ThreadLocal vs 普通变量**

```java
// ❌ 普通变量：线程间共享（不安全）
public class SharedVariable {
    private static String userContext;  // 所有线程共享
    
    public static void main(String[] args) {
        new Thread(() -> {
            userContext = "用户1";
            System.out.println(userContext);  // 可能输出: 用户1 或 用户2
        }).start();
        
        new Thread(() -> {
            userContext = "用户2";
            System.out.println(userContext);  // 可能输出: 用户1 或 用户2
        }).start();
    }
}

// ✅ ThreadLocal：线程隔离（安全）
public class ThreadLocalDemo {
    private static ThreadLocal<String> userContext = new ThreadLocal<>();
    
    public static void main(String[] args) {
        new Thread(() -> {
            userContext.set("用户1");
            System.out.println(userContext.get());  // 输出: 用户1
        }).start();
        
        new Thread(() -> {
            userContext.set("用户2");
            System.out.println(userContext.get());  // 输出: 用户2
        }).start();
    }
}
```

---

### Thread vs ThreadLocal 对比表

| 特性 | Thread | ThreadLocal |
|------|--------|-----------|
| **定义** | 执行线程 | 线程局部变量 |
| **作用域** | 执行任务 | 存储线程特定数据 |
| **线程间** | 并发执行 | 数据隔离 |
| **存储位置** | JVM 进程 | 每个线程的 ThreadLocalMap |
| **内存** | 共享堆内存 | 每个线程独立 |
| **清理** | GC 回收 | 需要手动 remove() |
| **典型用途** | 异步执行 | 用户上下文、事务、连接 |

---

### ThreadLocal 实际应用场景

#### 1. **用户上下文传递**

```java
// 用户上下文
public class UserContext {
    private static final ThreadLocal<User> USER_CONTEXT = new ThreadLocal<>();
    
    public static void setUser(User user) {
        USER_CONTEXT.set(user);
    }
    
    public static User getUser() {
        return USER_CONTEXT.get();
    }
    
    public static void clear() {
        USER_CONTEXT.remove();
    }
}

// 在 Filter 中设置
@Component
public class UserFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) {
        try {
            User user = authenticate((HttpServletRequest) request);
            UserContext.setUser(user);
            chain.doFilter(request, response);
        } finally {
            UserContext.clear();  // 必须清除！
        }
    }
}

// 在 Service 中使用
@Service
class OrderService {
    public void createOrder() {
        User user = UserContext.getUser();  // 无需传递参数
        // 创建订单
    }
}
```

#### 2. **数据库连接管理（Transaction）**

```java
// 事务管理器
public class TransactionManager {
    private static final ThreadLocal<Connection> CONNECTION = new ThreadLocal<>();
    
    public static void begin() {
        Connection conn = dataSource.getConnection();
        conn.setAutoCommit(false);
        CONNECTION.set(conn);
    }
    
    public static Connection getConnection() {
        return CONNECTION.get();
    }
    
    public static void commit() {
        CONNECTION.get().commit();
        CONNECTION.remove();
    }
    
    public static void rollback() {
        CONNECTION.get().rollback();
        CONNECTION.remove();
    }
}

// 使用
public void transferMoney() {
    TransactionManager.begin();
    try {
        // 业务逻辑
        TransactionManager.commit();
    } catch (Exception e) {
        TransactionManager.rollback();
    }
}
```

#### 3. **SimpleDateFormat 线程安全**

```java
// ❌ 不线程安全
private static final SimpleDateFormat DATE_FORMAT = 
    new SimpleDateFormat("yyyy-MM-dd");

// ✅ 使用 ThreadLocal 实现线程安全
public class DateUtils {
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT = 
        ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));
    
    public static String format(Date date) {
        return DATE_FORMAT.get().format(date);
    }
}
```

---

### ThreadLocal 内存泄漏问题

```java
// ⚠️ 问题：ThreadLocal 未清除
public class ThreadLocalLeak {
    private static ThreadLocal<byte[]> DATA = new ThreadLocal<>();
    
    public static void main(String[] args) {
        new Thread(() -> {
            DATA.set(new byte[10 * 1024 * 1024]);  // 10MB
            // 忘记调用 DATA.remove()
            // 线程结束，但 ThreadLocal 引用仍存在
            // 内存泄漏！
        }).start();
    }
}

// ✅ 解决方案：必须在 finally 中清除
public class ThreadLocalSafe {
    private static ThreadLocal<byte[]> DATA = new ThreadLocal<>();
    
    public static void main(String[] args) {
        new Thread(() -> {
            try {
                DATA.set(new byte[10 * 1024 * 1024]);
                // 使用数据
            } finally {
                DATA.remove();  // 必须清除！
            }
        }).start();
    }
}
```

---

## 普通线程 vs 虚拟线程

### 核心概念

```
普通线程：
┌─────────────────────────────────────┐
│  操作系统线程（OS Thread）         │
│  - 与 OS 线程 1:1 映射           │
│  - 创建成本高（约1-2MB栈内存）   │
│  - 切换成本高（上下文切换）       │
│  - 数量受限（几千到几万）         │
└─────────────────────────────────────┘

虚拟线程：
┌─────────────────────────────────────┐
│  Java 虚拟线程（Virtual Thread）   │
│  - 由 JVM 管理                    │
│  - 创建成本低（约几KB）           │
│  - 切换成本低（用户态切换）       │
│  - 数量无限（百万级）             │
│  - 载体（Carrier Thread）执行     │
└─────────────────────────────────────┘
```

---

### 虚拟线程详解

#### 1. **什么是虚拟线程？**

虚拟线程（Virtual Thread）是 Java 21 引入的**轻量级线程**，由 JVM 管理，不直接映射到 OS 线程。

#### 2. **基本使用**

```java
// 创建虚拟线程
Thread vThread = Thread.ofVirtual().start(() -> {
    System.out.println("虚拟线程执行");
});

// 使用 Executors
ExecutorService virtualExecutor = Executors.newVirtualThreadPerTaskExecutor();
virtualExecutor.submit(() -> {
    System.out.println("虚拟线程执行");
});

// 与普通线程对比
Thread platformThread = new Thread(() -> {
    System.out.println("平台线程");
});  // 重量级

Thread virtualThread = Thread.ofVirtual().start(() -> {
    System.out.println("虚拟线程");
});  // 轻量级
```

---

### 普通线程 vs 虚拟线程对比表

| 特性 | 平台线程 | 虚拟线程 |
|------|---------|---------|
| **创建成本** | 高（~1-2MB） | 低（~几KB） |
| **切换成本** | 高（OS 上下文切换） | 低（JVM 切换） |
| **数量限制** | 几千到几万 | 百万级 |
| **阻塞行为** | 阻塞 OS 线程 | 挂起虚拟线程 |
| **适合场景** | CPU 密集型 | I/O 密集型 |
| **Java 版本** | 所有版本 | 21+ |
| **载体** | OS 线程 | 载体线程 |

---

### 实际性能对比

#### 1. **创建 100 万个线程**

```java
// ❌ 普通线程：崩溃
public class PlatformThreadTest {
    public static void main(String[] args) {
        for (int i = 0; i < 1_000_000; i++) {
            new Thread(() -> {}).start();  // OOM！
        }
    }
}

// ✅ 虚拟线程：轻松运行
public class VirtualThreadTest {
    public static void main(String[] args) {
        try (ExecutorService executor = 
                Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 1_000_000; i++) {
                executor.submit(() -> {
                    Thread.sleep(1000);
                });
            }
        }
    }
}
```

#### 2. **高并发 I/O 场景**

```java
// ❌ 普通线程：需要几千个线程
public class HttpRequestWithPlatform {
    public void fetchUrls(List<String> urls) {
        ExecutorService pool = Executors.newFixedThreadPool(100);
        for (String url : urls) {
            pool.submit(() -> {
                HttpResponse response = HttpClient.newHttpClient()
                    .send(HttpRequest.newBuilder(URI.create(url)).build(), 
                           HttpResponse.BodyHandlers.ofString());
            });
        }
    }
}

// ✅ 虚拟线程：可以创建百万个
public class HttpRequestWithVirtual {
    public void fetchUrls(List<String> urls) {
        try (ExecutorService executor = 
                Executors.newVirtualThreadPerTaskExecutor()) {
            for (String url : urls) {
                executor.submit(() -> {
                    HttpResponse response = HttpClient.newHttpClient()
                        .send(HttpRequest.newBuilder(URI.create(url)).build(), 
                               HttpResponse.BodyHandlers.ofString());
                });
            }
        }
    }
}
```

---

### 虚拟线程原理

```
虚拟线程执行模型：

应用代码
    ↓
虚拟线程
    ↓
Carrier Thread（载体线程）
    ↓
OS 线程

当虚拟线程遇到 I/O 阻塞：
1. 虚拟线程挂起（释放载体线程）
2. 载体线程执行其他虚拟线程
3. I/O 完成后，虚拟线程恢复执行

优势：
- 不阻塞 OS 线程
- 载体线程可以执行其他虚拟线程
- 大幅提高并发能力
```

---

### 虚拟线程适用场景

#### ✅ **适合使用虚拟线程**

| 场景 | 理由 |
|------|------|
| 高并发 HTTP 请求 | 大量 I/O 阻塞 |
| 数据库查询 | 等待响应 |
| 消息队列消费 | 处理大量消息 |
| 定时任务 | 大量延迟任务 |
| 微服务调用 | 远程调用 |

#### ❌ **不适合使用虚拟线程**

| 场景 | 理由 |
|------|------|
| CPU 密集计算 | 不能提高 CPU 利用率 |
| synchronized 锁 | 会钉住载体线程 |
| native 方法 | 无法优化 |
| 遗留代码 | 可能不兼容 |

---

### 完整示例：改造你的代码

```java
public class VirtualThreadUtils {

    // 虚拟线程执行器
    private static final ScheduledExecutorService VIRTUAL_SCHED = 
        Executors.newScheduledThreadPool(2, Thread.ofVirtual().factory());

    // 平台线程执行器（用于 CPU 密集任务）
    private static final ScheduledExecutorService PLATFORM_SCHED = 
        Executors.newScheduledThreadPool(2, Thread.ofPlatform().factory());

    /**
     * 异步执行（使用虚拟线程）
     */
    public static void executeVirtual(Runnable task) {
        Thread.ofVirtual().start(wrapTask(task));
    }

    /**
     * 异步执行（使用平台线程）
     */
    public static void executePlatform(Runnable task) {
        Thread.ofPlatform().start(wrapTask(task));
    }

    /**
     * 虚拟线程池
     */
    public static void executeWithVirtualPool(Runnable task) {
        VIRTUAL_SCHED.execute(wrapTask(task));
    }

    /**
     * 虚拟线程数量对比
     */
    public static void compareThreadCreation() {
        // 平台线程
        long start = System.currentTimeMillis();
        try (ExecutorService pool = Executors.newFixedThreadPool(100)) {
            for (int i = 0; i < 10_000; i++) {
                pool.submit(() -> {});
            }
        }
        System.out.println("平台线程: " + 
            (System.currentTimeMillis() - start) + "ms");

        // 虚拟线程
        start = System.currentTimeMillis();
        try (ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 10_000; i++) {
                pool.submit(() -> {});
            }
        }
        System.out.println("虚拟线程: " + 
            (System.currentTimeMillis() - start) + "ms");
    }

    private static Runnable wrapTask(Runnable task) {
        return () -> {
            try {
                task.run();
            } catch (Throwable e) {
                System.err.println("任务异常: " + e.getMessage());
            }
        };
    }
}
```

---

## 总结

### 创建线程的方式（按推荐度排序）

| 方式 | 适用场景 | 推荐度 |
|------|---------|--------|
| `Virtual Thread` | 高并发 I/O（Java 21+） | ⭐⭐⭐⭐⭐ |
| `@Async` | Spring Boot 项目 | ⭐⭐⭐⭐⭐ |
| `CompletableFuture` | 链式异步调用 | ⭐⭐⭐⭐⭐ |
| `ExecutorService` | 生产环境通用 | ⭐⭐⭐⭐ |
| `ThreadFactory` | 需要自定义配置 | ⭐⭐⭐ |
| `new Thread()` | 简单测试 | ⭐ |
| `extends Thread` | 几乎不用 | ❌ |

### Thread vs ThreadLocal

| 特性 | Thread | ThreadLocal |
|------|--------|-----------|
| **本质** | 执行单元 | 存储单元 |
| **用途** | 异步执行 | 线程隔离 |
| **隔离性** | 线程间并发 | 数据完全隔离 |
| **清理** | GC 自动 | 需要手动 remove() |

### 平台线程 vs 虚拟线程

| 特性 | 平台线程 | 虚拟线程 |
|------|---------|---------|
| **成本** | 高（1-2MB） | 低（几KB） |
| **数量** | 几千 | 百万 |
| **适合** | CPU 密集 | I/O 密集 |
| **版本** | 所有版本 | Java 21+ |

**建议：**
- 高并发 I/O → 使用虚拟线程（Java 21+）
- CPU 密集任务 → 使用平台线程池
- 需要线程隔离 → 使用 ThreadLocal
- Spring Boot 项目 → 优先用 @Async