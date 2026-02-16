The user is asking two questions:
1. How many ways can create new threads in Java?
2. What is thread daemon?

Let me provide a comprehensive answer about different ways to create threads and explain daemon threads.
# Creating Threads & Daemon Threads in Java

## 1. Ways to Create New Threads in Java

### Method 1: Extend `Thread` Class
```java
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Running in: " + Thread.currentThread().getName());
    }
}

MyThread thread = new MyThread();
thread.start();
```

### Method 2: Implement `Runnable` Interface
```java
Runnable runnable = () -> {
    System.out.println("Running in: " + Thread.currentThread().getName());
};

Thread thread = new Thread(runnable);
thread.start();
```

### Method 3: Implement `Callable` + `FutureTask` (Returns Result)
```java
Callable<String> callable = () -> {
    return "Result from " + Thread.currentThread().getName();
};

FutureTask<String> futureTask = new FutureTask<>(callable);
new Thread(futureTask).start();

String result = futureTask.get(); // Blocks until result available
```

### Method 4: Thread Pool (Executor Framework) ⭐ Recommended
```java
// Fixed size pool
ExecutorService fixedPool = Executors.newFixedThreadPool(4);

// Cached pool (creates threads as needed)
ExecutorService cachedPool = Executors.newCachedThreadPool();

// Single thread
ExecutorService singleThread = Executors.newSingleThreadExecutor();

// Scheduled tasks
ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(2);

// Submit tasks
fixedPool.submit(() -> {
    // Async task
});
```

### Method 5: CompletableFuture (Modern Java 8+)
```java
// Using default ForkJoinPool
CompletableFuture.runAsync(() -> {
    // Async task
});

// Using custom Executor
ExecutorService executor = Executors.newFixedThreadPool(4);
CompletableFuture.supplyAsync(() -> {
    return "result";
}, executor);
```

### Method 6: Fork/Join Framework (Java 7+)
```java
ForkJoinPool pool = new ForkJoinPool();
pool.submit(() -> {
    // Parallel task
});
```

### Method 7: Spring @Async
```java
@EnableAsync
@SpringBootApplication
public class Application { }

@Service
public class MyService {
    @Async
    public void asyncMethod() {
        // Runs in separate thread from Spring's task executor
    }
    
    @Async("customExecutor") // Use specific executor
    public void asyncWithExecutor() { }
}
```

---

## 2. Daemon Thread (守护线程)

### What is a Daemon Thread?

| Feature | Description |
|---------|-------------|
| **Definition** | A thread that runs in the background and doesn't prevent JVM from exiting |
| **Purpose** | Support/background tasks (GC, monitoring, logging) |
| **Lifecycle** | Automatically terminates when all user threads finish |
| **Default** | `false` (threads are non-daemon by default) |

### Example: Daemon vs Non-Daemon

```java
// Non-Daemon Thread (default)
Thread userThread = new Thread(() -> {
    while (true) {
        System.out.println("User thread running...");
        Thread.sleep(1000);
    }
});
userThread.start();
// JVM will NOT exit until this thread finishes

// Daemon Thread
Thread daemonThread = new Thread(() -> {
    while (true) {
        System.out.println("Daemon thread running...");
        Thread.sleep(1000);
    }
});
daemonThread.setDaemon(true);  // Must set BEFORE start()
daemonThread.start();
// JVM WILL exit when main thread finishes, killing daemon thread
```

### Daemon Thread in Your Code Context

Looking at your `BannerApplicationRuuner.java`:

```java:1:10
@Slf4j
public class BannerApplicationRuuner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) {
        CompletableFuture.runAsync(() -> {
            // This runs in ForkJoinPool.commonPool() worker thread
            // By default, ForkJoinPool threads are NON-DAEMON
        });
    }
}
```

**ForkJoinPool.commonPool()** threads are non-daemon by default, so they can keep JVM running.

### How to Check if Thread is Daemon

```java
Thread t = new Thread(() -> {});
t.setDaemon(true);
System.out.println(t.isDaemon()); // true
```

### Best Practice: Using Daemon Threads

```java
ExecutorService executor = Executors.newFixedThreadPool(4, r -> {
    Thread t = new Thread(r);
    t.setDaemon(true);  // Make pool threads daemon
    return t;
});
```

---

## Summary Table

| Method | Best For | Thread Type |
|--------|----------|-------------|
| Extend `Thread` | Simple cases | Non-daemon (default) |
| `Runnable` | Decoupling task from thread | Non-daemon (default) |
| `Callable` | Need return value | Non-daemon (default) |
| Thread Pool | Production code | Configurable |
| `CompletableFuture` | Async chaining | Uses ForkJoinPool |
| Fork/Join | Divide & conquer tasks | Work-stealing pool |
| Spring `@Async` | Spring applications | Configurable |



Need async execution?
├── Simple one-off task?
│   └── Use: CompletableFuture.runAsync()
├── Part of Spring application?
│   └── Use: @Async
├── Need return value / chain operations?
│   └── Use: CompletableFuture.supplyAsync()
├── Multiple similar tasks?
│   └── Use: Thread Pool (ExecutorService)
├── CPU-intensive parallel processing?
│   └── Use: ForkJoinPool / parallelStream()
└── Just learning / simple experiment?
└── Use: new Thread() (NOT for production)

# Golden Rules
Never use new Thread() in production - use pools
Always shutdown executors - prevent memory leaks
Use daemon threads for background tasks - allow graceful shutdown
Prefer @Async in Spring apps - integrates with Spring lifecycle
Name your threads - easier debugging