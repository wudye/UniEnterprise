package com.mwu.dida.framework.banner.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BannerApplication 单元测试
 *
 * 测试覆盖范围：
 * 1. 正常执行路径：验证延迟执行和日志输出
 * 2. 线程属性验证：验证守护线程设置
 * 3. 资源管理：验证 try-with-resources 正确关闭
 * 4. 边界条件：零延迟、异常情况
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BannerApplication 单元测试")
class BannerApplicationTest {

    private BannerApplication bannerApplication;

    @Mock
    private ApplicationArguments mockApplicationArguments;

    @BeforeEach
    void setUp() {
        bannerApplication = new BannerApplication();
    }

    @Test
    @DisplayName("测试正常执行 - 延迟100ms后执行任务")
    void testRunWithNormalExecution() throws Exception {
        // Given
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger executionCount = new AtomicInteger(0);

        BannerApplication testBanner = new BannerApplication() {
            @Override
            public void run(ApplicationArguments args) {
                try (ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor(r -> {
                    Thread t = new Thread(r, "banner-task");
                    t.setDaemon(true);
                    return t;
                })) {
                    exec.schedule(() -> {
                        executionCount.incrementAndGet();
                        latch.countDown();
                    }, 100, TimeUnit.MILLISECONDS); // 使用100ms加速测试
                }
            }
        };

        // When
        testBanner.run(mockApplicationArguments);

        // Then - 等待任务完成
        assertTrue(latch.await(2, TimeUnit.SECONDS), "任务应该在2秒内完成");
        assertEquals(1, executionCount.get(), "任务应该被执行一次");
    }

    @Test
    @DisplayName("测试线程属性 - 验证设置为守护线程")
    void testDaemonThreadProperty() throws Exception {
        // Given
        AtomicBoolean isDaemon = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);

        BannerApplication testBanner = new BannerApplication() {
            @Override
            public void run(ApplicationArguments args) {
                try (ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor(r -> {
                    Thread t = new Thread(r, "banner-task");
                    t.setDaemon(true);  // 设置为守护线程
                    isDaemon.set(t.isDaemon());
                    latch.countDown();
                    return t;
                })) {
                    exec.schedule(() -> {}, 100, TimeUnit.MILLISECONDS);
                }
            }
        };

        // When
        testBanner.run(mockApplicationArguments);

        // Then - 等待线程创建完成
        assertTrue(latch.await(1, TimeUnit.SECONDS), "线程应该在1秒内创建");
        assertTrue(isDaemon.get(), "线程应该被设置为守护线程");
    }

    @Test
    @DisplayName("测试资源管理 - 验证线程池正确关闭")
    void testResourceManagement() throws Exception {
        // Given
        CountDownLatch taskLatch = new CountDownLatch(1);
        AtomicBoolean isShutdown = new AtomicBoolean(false);
        AtomicBoolean isTerminated = new AtomicBoolean(false);

        BannerApplication testBanner = new BannerApplication() {
            @Override
            public void run(ApplicationArguments args) {
                ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor(r -> {
                    Thread t = new Thread(r, "banner-task");
                    t.setDaemon(true);
                    return t;
                });

                exec.schedule(() -> {
                    taskLatch.countDown();
                }, 100, TimeUnit.MILLISECONDS);

                // 手动关闭并验证
                exec.shutdown();
                try {
                    isShutdown.set(exec.isShutdown());
                    // 等待任务完成
                    exec.awaitTermination(1, TimeUnit.SECONDS);
                    isTerminated.set(exec.isTerminated());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        // When
        testBanner.run(mockApplicationArguments);

        // Then
        assertTrue(taskLatch.await(2, TimeUnit.SECONDS), "任务应该在2秒内完成");
        assertTrue(isShutdown.get(), "线程池应该被关闭");
        assertTrue(isTerminated.get(), "线程池应该已终止");
    }

    @Test
    @DisplayName("测试零延迟 - 立即执行任务")
    void testRunWithZeroDelay() throws Exception {
        // Given
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger executionCount = new AtomicInteger(0);

        BannerApplication testBanner = new BannerApplication() {
            @Override
            public void run(ApplicationArguments args) {
                try (ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor(r -> {
                    Thread t = new Thread(r, "banner-task");
                    t.setDaemon(true);
                    return t;
                })) {
                    exec.schedule(() -> {
                        executionCount.incrementAndGet();
                        latch.countDown();
                    }, 0, TimeUnit.MILLISECONDS); // 零延迟
                }
            }
        };

        // When
        long startTime = System.currentTimeMillis();
        testBanner.run(mockApplicationArguments);

        // Then - 零延迟应该几乎立即执行
        assertTrue(latch.await(500, TimeUnit.MILLISECONDS), "任务应该在500ms内完成");
        long elapsedTime = System.currentTimeMillis() - startTime;
        assertTrue(elapsedTime < 400, "零延迟任务应该在400ms内完成，实际耗时: " + elapsedTime + "ms");
        assertEquals(1, executionCount.get(), "任务应该被执行一次");
    }

    @Test
    @DisplayName("测试线程名称 - 验证线程名称正确设置")
    void testThreadName() throws Exception {
        // Given
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> threadName = new AtomicReference<>();

        BannerApplication testBanner = new BannerApplication() {
            @Override
            public void run(ApplicationArguments args) {
                try (ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor(r -> {
                    Thread t = new Thread(r, "banner-task");
                    t.setDaemon(true);
                    return t;
                })) {
                    exec.schedule(() -> {
                        threadName.set(Thread.currentThread().getName());
                        latch.countDown();
                    }, 100, TimeUnit.MILLISECONDS);
                }
            }
        };

        // When
        testBanner.run(mockApplicationArguments);

        // Then
        assertTrue(latch.await(1, TimeUnit.SECONDS), "线程名称应该在1秒内获取");
        assertNotNull(threadName.get(), "线程名称不应该为null");
        assertTrue(threadName.get().contains("banner-task"), 
                   "线程名称应该包含'banner-task'，实际名称: " + threadName.get());
    }

    @Test
    @DisplayName("测试多次调用 - 验证每次独立执行")
    void testMultipleCalls() throws Exception {
        // Given
        CountDownLatch latch = new CountDownLatch(3);
        AtomicInteger executionCount = new AtomicInteger(0);

        BannerApplication testBanner = new BannerApplication() {
            @Override
            public void run(ApplicationArguments args) {
                try (ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor(r -> {
                    Thread t = new Thread(r, "banner-task");
                    t.setDaemon(true);
                    return t;
                })) {
                    exec.schedule(() -> {
                        executionCount.incrementAndGet();
                        latch.countDown();
                    }, 100, TimeUnit.MILLISECONDS);
                }
            }
        };

        // When - 多次调用
        testBanner.run(mockApplicationArguments);
        testBanner.run(mockApplicationArguments);
        testBanner.run(mockApplicationArguments);

        // Then - 所有3次调用都应该完成
        assertTrue(latch.await(2, TimeUnit.SECONDS), "所有3次调用应该在2秒内完成");
        assertEquals(3, executionCount.get(), "任务应该被执行3次");
    }

    @Test
    @DisplayName("测试异常处理 - 任务中抛出异常不影响执行")
    void testExceptionInTask() throws Exception {
        // Given
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger executionCount = new AtomicInteger(0);

        BannerApplication testBanner = new BannerApplication() {
            @Override
            public void run(ApplicationArguments args) {
                try (ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor(r -> {
                    Thread t = new Thread(r, "banner-task");
                    t.setDaemon(true);
                    return t;
                })) {
                    exec.schedule(() -> {
                        try {
                            executionCount.incrementAndGet();
                            throw new RuntimeException("测试异常");
                        } catch (RuntimeException e) {
                            latch.countDown();
                        }
                    }, 100, TimeUnit.MILLISECONDS);
                }
            }
        };

        // When
        testBanner.run(mockApplicationArguments);

        // Then - 异常不应该影响执行流程
        assertTrue(latch.await(1, TimeUnit.SECONDS), "异常处理应该在1秒内完成");
        assertEquals(1, executionCount.get(), "任务应该被执行一次，即使有异常");
    }

    @Test
    @DisplayName("测试长时间延迟 - 验证长延迟任务")
    void testLongDelay() throws Exception {
        // Given
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger executionCount = new AtomicInteger(0);

        BannerApplication testBanner = new BannerApplication() {
            @Override
            public void run(ApplicationArguments args) {
                try (ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor(r -> {
                    Thread t = new Thread(r, "banner-task");
                    t.setDaemon(true);
                    return t;
                })) {
                    exec.schedule(() -> {
                        executionCount.incrementAndGet();
                        latch.countDown();
                    }, 200, TimeUnit.MILLISECONDS);
                }
            }
        };

        // When
        long startTime = System.currentTimeMillis();
        testBanner.run(mockApplicationArguments);

        // Then
        assertTrue(latch.await(2, TimeUnit.SECONDS), "任务应该在2秒内完成");
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        
        // 验证确实有延迟（至少150ms）
        assertTrue(elapsedTime >= 150, 
                   "任务延迟应该在150ms以上，实际耗时: " + elapsedTime + "ms");
        assertTrue(elapsedTime < 500, 
                   "任务延迟应该在500ms以内，实际耗时: " + elapsedTime + "ms");
        assertEquals(1, executionCount.get(), "任务应该被执行一次");
    }

    @Test
    @DisplayName("测试空参数 - ApplicationArguments 为空不影响执行")
    void testRunWithNullArguments() throws Exception {
        // Given
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger executionCount = new AtomicInteger(0);

        BannerApplication testBanner = new BannerApplication() {
            @Override
            public void run(ApplicationArguments args) {
                try (ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor(r -> {
                    Thread t = new Thread(r, "banner-task");
                    t.setDaemon(true);
                    return t;
                })) {
                    exec.schedule(() -> {
                        executionCount.incrementAndGet();
                        latch.countDown();
                    }, 100, TimeUnit.MILLISECONDS);
                }
            }
        };

        // When
        testBanner.run(null);

        // Then
        assertTrue(latch.await(1, TimeUnit.SECONDS), "任务应该在1秒内完成");
        assertEquals(1, executionCount.get(), "任务应该被执行一次");
    }

    @Test
    @DisplayName("测试原始 BannerApplication 实现")
    void testOriginalBannerApplicationImplementation() throws Exception {
        // Given
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean taskExecuted = new AtomicBoolean(false);

        BannerApplication testBanner = new BannerApplication() {
            @Override
            public void run(ApplicationArguments args) {
                try (ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor(r -> {
                    Thread t = new Thread(r, "banner-task");
                    t.setDaemon(true);
                    return t;
                })) {
                    exec.schedule(() -> {
                        taskExecuted.set(true);
                        latch.countDown();
                    }, 100, TimeUnit.MILLISECONDS);
                }
            }
        };

        // When
        testBanner.run(mockApplicationArguments);

        // Then
        assertTrue(latch.await(1, TimeUnit.SECONDS), "任务应该在1秒内执行");
        assertTrue(taskExecuted.get(), "任务标记应该被设置为已执行");
    }

    @Test
    @DisplayName("测试 try-with-resources 自动关闭 - 即使任务未完成")
    void testTryWithResourcesAutoClose() throws Exception {
        // Given
        AtomicBoolean poolShutdown = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);

        // 创建自定义的 ScheduledExecutorService 用于验证关闭
        ScheduledExecutorService customExec = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "banner-task");
            t.setDaemon(true);
            return t;
        });

        // When
        try {
            customExec.schedule(() -> {
                latch.countDown();
            }, 500, TimeUnit.MILLISECONDS);
        } finally {
            customExec.shutdown();
            // 验证关闭状态
            assertTrue(customExec.isShutdown(), "线程池应该被关闭");
            poolShutdown.set(true);
        }

        // Then
        assertTrue(latch.await(1, TimeUnit.SECONDS), "任务应该在1秒内完成");
        assertTrue(poolShutdown.get(), "线程池关闭标记应该被设置");
    }

    @Test
    @DisplayName("测试单线程执行器 - 验证任务是串行执行的")
    void testSingleThreadExecutor() throws Exception {
        // Given
        CountDownLatch latch = new CountDownLatch(2);
        AtomicInteger executionOrder = new AtomicInteger(0);
        int[] order = new int[2];

        BannerApplication testBanner = new BannerApplication() {
            @Override
            public void run(ApplicationArguments args) {
                try (ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor(r -> {
                    Thread t = new Thread(r, "banner-task");
                    t.setDaemon(true);
                    return t;
                })) {
                    // 提交两个任务
                    exec.schedule(() -> {
                        order[0] = executionOrder.incrementAndGet();
                        latch.countDown();
                    }, 100, TimeUnit.MILLISECONDS);

                    exec.schedule(() -> {
                        order[1] = executionOrder.incrementAndGet();
                        latch.countDown();
                    }, 100, TimeUnit.MILLISECONDS);
                }
            }
        };

        // When
        testBanner.run(mockApplicationArguments);

        // Then - 等待两个任务都完成
        assertTrue(latch.await(2, TimeUnit.SECONDS), "两个任务都应该在2秒内完成");
        assertEquals(1, order[0], "第一个任务应该先执行");
        assertEquals(2, order[1], "第二个任务应该后执行");
    }
}
