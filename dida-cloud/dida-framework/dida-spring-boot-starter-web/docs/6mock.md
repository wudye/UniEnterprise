用户想了解 mock 和 mockito 的概念。这是一个很好的基础概念问题，我需要用清晰易懂的方式解释。让我详细说明这两个概念。
## Mock 和 Mockito 详解

### 什么是 Mock（模拟对象）？

**Mock（模拟）** 是一种用于测试的技术，用来创建**假的、可控制的**对象来替代真实的依赖对象。

---

### 为什么需要 Mock？

#### 场景对比

```java
// ❌ 真实对象测试（问题很多）
class UserServiceTest {
    @Test
    void testGetUser() {
        // 问题1：需要真实的数据库连接
        DatabaseService realDb = new DatabaseService("jdbc:mysql://...");
        
        // 问题2：需要真实的网络请求
        HttpClient realHttp = new HttpClient();
        
        // 问题3：依赖太多，测试速度慢
        UserService userService = new UserService(realDb, realHttp);
        
        // 问题4：数据库中必须有测试数据
        User user = userService.getUser(1);
        assertEquals("张三", user.getName());
    }
}

// ✅ Mock 对象测试（优雅高效）
class UserServiceTest {
    @Mock
    private DatabaseService mockDb;
    
    @Mock
    private HttpClient mockHttp;
    
    @Test
    void testGetUser() {
        // 优势1：无需真实数据库
        // 优势2：无需真实网络
        // 优势3：可以控制返回值
        when(mockDb.queryUser(1)).thenReturn(new User("张三"));
        
        UserService userService = new UserService(mockDb, mockHttp);
        User user = userService.getUser(1);
        
        assertEquals("张三", user.getName());
    }
}
```

---

### Mock 的核心特性

#### 1. **可配置的行为**

```java
// 真实对象：行为由内部逻辑决定
DatabaseService realDb = new DatabaseService();
User user = realDb.queryUser(1);  // 返回什么？不确定！

// Mock 对象：行为由你决定
@Mock
DatabaseService mockDb;
when(mockDb.queryUser(1)).thenReturn(new User("张三"));  // 返回张三
when(mockDb.queryUser(2)).thenReturn(new User("李四"));  // 返回李四
when(mockDb.queryUser(anyInt())).thenThrow(new RuntimeException());  // 抛异常
```

#### 2. **可验证的调用**

```java
// 真实对象：无法验证是否调用了某个方法
realDb.saveUser(user);  // 调用了吗？谁知道！

// Mock 对象：可以验证方法调用
mockDb.saveUser(user);
verify(mockDb).saveUser(user);        // ✅ 验证调用了
verify(mockDb, times(1)).saveUser(user);  // ✅ 验证调用了一次
verify(mockDb, never()).deleteUser(any());  // ✅ 验证从未调用
```

#### 3. **轻量级**

```
真实对象测试：
- 启动数据库：2-5秒
- 初始化连接池：1-2秒
- 准备测试数据：1-3秒
- 总计：4-10秒

Mock 对象测试：
- 创建 Mock：< 0.01秒
- 配置行为：< 0.01秒
- 总计：< 0.1秒（快100倍！）
```

---

### 可视化理解

```
真实对象测试架构：
┌─────────────────────────────────────────┐
│  测试代码                                │
│  ┌───────────────┐                      │
│  │  UserService  │ ←→ 真实 Database    │ ← 数据库服务器
│  └───────────────┘                      │
│         ↓                               │
│  ┌───────────────┐                      │
│  │  EmailService │ ←→ 真实 Email API   │ ← 邮件服务器
│  └───────────────┘                      │
└─────────────────────────────────────────┘
问题：依赖太多，速度慢，不稳定

Mock 对象测试架构：
┌─────────────────────────────────────────┐
│  测试代码                                │
│  ┌───────────────┐                      │
│  │  UserService  │ ←→ Mock Database    │ ← 在内存中
│  └───────────────┘                      │
│         ↓                               │
│  ┌───────────────┐                      │
│  │  EmailService │ ←→ Mock Email       │ ← 在内存中
│  └───────────────┘                      │
└─────────────────────────────────────────┘
优势：独立快速，完全可控
```

---

## 什么是 Mockito？

**Mockito** 是 Java 最流行的 Mock 框架，专门用于创建和管理 Mock 对象。

### Mockito 的特点

| 特性 | 说明 |
|------|------|
| **简单易用** | API 直观，学习曲线平缓 |
| **注解驱动** | `@Mock` 自动创建，无需手动 |
| **类型安全** | 编译时检查，减少运行时错误 |
| **IDE 支持** | 所有主流 IDE 都支持 |
| **社区活跃** | 文档丰富，问题容易解决 |

---

### Mockito 核心概念

#### 1. **创建 Mock 对象**

```java
// 方式1：使用 @Mock 注解（推荐）
@ExtendWith(MockitoExtension.class)
class MyTest {
    @Mock
    private DatabaseService mockDb;
    
    @Test
    void test() {
        // MockitoExtension 自动初始化 mockDb
    }
}

// 方式2：使用 Mockito.mock() 方法
DatabaseService mockDb = Mockito.mock(DatabaseService.class);

// 方式3：使用 @MockBean（Spring Boot）
@SpringBootTest
class MyTest {
    @MockBean
    private DatabaseService mockDb;
}
```

#### 2. **配置 Mock 行为（Stubbing）**

```java
@Mock
private UserService userService;

@Test
void testStubbing() {
    // 1. 固定返回值
    when(userService.getUserName(1)).thenReturn("张三");
    assertEquals("张三", userService.getUserName(1));
    
    // 2. 匹配任意参数
    when(userService.getUserName(anyInt())).thenReturn("匿名用户");
    assertEquals("匿名用户", userService.getUserName(999));
    
    // 3. 抛出异常
    when(userService.getUser(-1)).thenThrow(new IllegalArgumentException("无效ID"));
    assertThrows(IllegalArgumentException.class, () -> userService.getUser(-1));
    
    // 4. 调用真实方法
    when(userService.getUserName(anyInt())).thenCallRealMethod();
    
    // 5. 多次调用不同返回值
    when(userService.getUserName(1))
        .thenReturn("张三")
        .thenReturn("李四");
    assertEquals("张三", userService.getUserName(1));  // 第一次返回
    assertEquals("李四", userService.getUserName(1));  // 第二次返回
}
```

#### 3. **验证方法调用**

```java
@Mock
private UserService userService;

@Test
void testVerification() {
    // 执行操作
    userService.createUser("张三");
    userService.createUser("李四");
    userService.getUserName(1);
    
    // 1. 验证调用
    verify(userService).createUser("张三");  // ✅ 调用了一次
    verify(userService, times(2)).createUser(anyString());  // ✅ 调用了2次
    
    // 2. 验证从未调用
    verify(userService, never()).deleteUser(anyInt());  // ✅ 从未调用
    
    // 3. 验证调用顺序
    InOrder inOrder = inOrder(userService);
    inOrder.verify(userService).createUser("张三");
    inOrder.verify(userService).createUser("李四");
    
    // 4. 验证参数
    verify(userService).createUser(argThat(name -> name.length() > 0));
    
    // 5. 验证没有其他调用
    verifyNoMoreInteractions(userService);
}
```

#### 4. **参数匹配器**

```java
@Mock
private UserService userService;

@Test
void testMatchers() {
    // 1. 匹配任意值
    when(userService.getUser(anyInt())).thenReturn(new User());
    
    // 2. 匹配特定值
    when(userService.getUser(eq(1))).thenReturn(new User("张三"));
    
    // 3. 匹配字符串
    when(userService.findByName(contains("张"))).thenReturn(new User());
    
    // 4. 匹配 null
    when(userService.getUser(isNull())).thenReturn(null);
    
    // 5. 自定义匹配器
    when(userService.createUser(argThat(user -> user.getName() != null)))
        .thenReturn(true);
    
    // ⚠️ 注意：要么全部使用匹配器，要么全部使用实际值
    // ❌ 错误
    when(userService.update(1, "张三")).thenReturn(true);  // 混用了匹配器和实际值
    
    // ✅ 正确
    when(userService.update(eq(1), eq("张三"))).thenReturn(true);
}
```

---

### Mockito vs 其他 Mock 框架

| 框架 | 优势 | 劣势 |
|------|------|------|
| **Mockito** | 简单易用，API 优雅 | 不支持 final 类（需要 mockito-inline） |
| **EasyMock** | 性能好 | API 复杂，学习曲线陡峭 |
| **PowerMock** | 支持 static/final 方法 | 性能差，已停止维护 |
| **MockK** | Kotlin 原生支持 | 仅支持 Kotlin |

---

### 实战示例：测试业务逻辑

```java
// 被测类
class OrderService {
    private final DatabaseService database;
    private final PaymentService payment;
    
    public OrderService(DatabaseService database, PaymentService payment) {
        this.database = database;
        this.payment = payment;
    }
    
    public boolean processOrder(int orderId) {
        Order order = database.getOrder(orderId);
        if (order == null) {
            return false;
        }
        
        boolean paid = payment.charge(order.getAmount());
        if (!paid) {
            return false;
        }
        
        order.setStatus("PAID");
        database.updateOrder(order);
        return true;
    }
}

// 测试类
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private DatabaseService mockDatabase;
    
    @Mock
    private PaymentService mockPayment;
    
    @InjectMocks
    private OrderService orderService;  // 自动注入 Mock
    
    @Test
    @DisplayName("订单不存在时返回false")
    void processOrder_WhenOrderNotFound_ReturnsFalse() {
        // Given - 配置 Mock 返回 null
        when(mockDatabase.getOrder(1)).thenReturn(null);
        
        // When - 调用被测方法
        boolean result = orderService.processOrder(1);
        
        // Then - 验证结果
        assertFalse(result);
        
        // And - 验证没有调用支付
        verify(mockPayment, never()).charge(anyDouble());
    }
    
    @Test
    @DisplayName("支付失败时返回false")
    void processOrder_WhenPaymentFails_ReturnsFalse() {
        // Given
        Order order = new Order(1, 100.0);
        when(mockDatabase.getOrder(1)).thenReturn(order);
        when(mockPayment.charge(100.0)).thenReturn(false);
        
        // When
        boolean result = orderService.processOrder(1);
        
        // Then
        assertFalse(result);
        
        // And - 验证没有更新订单
        verify(mockDatabase, never()).updateOrder(any());
    }
    
    @Test
    @DisplayName("成功时返回true并更新订单")
    void processOrder_WhenPaymentSucceeds_ReturnsTrue() {
        // Given
        Order order = new Order(1, 100.0);
        when(mockDatabase.getOrder(1)).thenReturn(order);
        when(mockPayment.charge(100.0)).thenReturn(true);
        
        // When
        boolean result = orderService.processOrder(1);
        
        // Then
        assertTrue(result);
        assertEquals("PAID", order.getStatus());
        
        // And - 验证调用顺序
        InOrder inOrder = inOrder(mockDatabase, mockPayment);
        inOrder.verify(mockDatabase).getOrder(1);
        inOrder.verify(mockPayment).charge(100.0);
        inOrder.verify(mockDatabase).updateOrder(order);
    }
}
```

---

## 总结

### Mock 的核心价值

```
真实依赖                Mock 依赖
   ↓                      ↓
不可控                完全可控
速度慢                速度快
不稳定                稳定可靠
难以隔离              完全隔离
```

### Mockito 的核心方法

| 方法 | 用途 | 示例 |
|------|------|------|
| `mock()` | 创建 Mock | `mock(Service.class)` |
| `@Mock` | 注解创建 | `@Mock Service service;` |
| `when().thenReturn()` | 配置返回值 | `when(service.foo()).thenReturn("bar")` |
| `verify()` | 验证调用 | `verify(service).foo()` |
| `any()`, `eq()` | 参数匹配 | `when(service.foo(anyInt()))` |

Mock 和 Mockito 是现代 Java 测试的**必备工具**，它们让单元测试变得**快速、稳定、可维护**！