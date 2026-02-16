用户想深入了解 ObjectProvider 的工作原理，特别是 `getIfAvailable` 方法和它的关系。让我详细解释 ObjectProvider 是什么，以及它提供的各种方法。
# ObjectProvider 详解

## 1️⃣ ObjectProvider 是什么？

`ObjectProvider` 是 Spring Framework **4.3** 引入的一个接口，它是 `ObjectFactory` 的扩展，提供更强大的依赖查找能力。

```java
public interface ObjectProvider<T> extends ObjectFactory<T> {
    T getObject(Object... args);           // 获取 Bean，可带构造参数
    T getIfAvailable();                     // 获取 Bean，不存在返回 null
    T getIfAvailable(Supplier<T> defaultSupplier);  // 获取 Bean，不存在返回默认值
    T getIfUnique();                        // 获取唯一 Bean，不唯一抛异常
    Stream<T> stream();                     // 获取所有该类型的 Bean 流
}
```

---

## 2️⃣ getIfAvailable 与 ObjectProvider 的关系

**是的，`getIfAvailable()` 是 `ObjectProvider` 接口的方法**，必须使用 `ObjectProvider` 才能调用。

| 方法 | 签名 | Bean 存在 | Bean 不存在 |
|------|------|-----------|-------------|
| `getIfAvailable()` | `T getIfAvailable()` | 返回 Bean | 返回 `null` |
| `getIfAvailable(Supplier)` | `T getIfAvailable(Supplier<T>)` | 返回 Bean | 执行 Supplier，返回默认值 |

```java
// ❌ 错误 - 普通 Bean 没有 getIfAvailable 方法
@Autowired
private Knife4jProperties properties;

properties.getIfAvailable(...);  // 编译错误！


// ✅ 正确 - ObjectProvider 才有 getIfAvailable
@Autowired
private ObjectProvider<Knife4jProperties> provider;

provider.getIfAvailable(Knife4jProperties::new);  // 正常调用
```

---

## 3️⃣ ObjectProvider 的核心方法对比

```java
@Service
public class MyService {
    
    @Autowired
    private ObjectProvider<MyBean> provider;
    
    public void demo() {
        // 1. getObject() - 同 @Autowired，不存在则报错
        MyBean bean1 = provider.getObject();
        
        // 2. getIfAvailable() - 不存在返回 null（需判空）
        MyBean bean2 = provider.getIfAvailable();
        if (bean2 != null) { ... }
        
        // 3. getIfAvailable(Supplier) - 不存在返回默认值 ⭐
        MyBean bean3 = provider.getIfAvailable(MyBean::new);
        // bean3 一定不为 null，要么是用容器里的，要么是新建的
        
        // 4. getIfUnique() - 要求只有一个 Bean，否则抛异常
        MyBean bean4 = provider.getIfUnique();
        
        // 5. stream() - 获取所有该类型的 Bean
        provider.stream().forEach(bean -> ...);
    }
}
```

---

## 4️⃣ 你的代码执行流程

```java
public Knife4jOpenApiCustomizer(ObjectProvider<Knife4jProperties> knife4jPropertiesProvider,
                               ObjectProvider<SpringDocConfigProperties> propertiesProvider) {
    // 调用父类构造器
    super(
        knife4jPropertiesProvider.getIfAvailable(Knife4jProperties::new),   // 逻辑1
        propertiesProvider.getIfAvailable(SpringDocConfigProperties::new)   // 逻辑2
    );
    // 赋值给当前类字段
    this.knife4jProperties = knife4jPropertiesProvider.getIfAvailable(Knife4jProperties::new);       // 逻辑3
    this.properties = propertiesProvider.getIfAvailable(SpringDocConfigProperties::new);             // 逻辑4
}
```

**执行流程图：**

```
┌─────────────────────────────────────────────────────────┐
│  knife4jPropertiesProvider.getIfAvailable(...)          │
├─────────────────────────────────────────────────────────┤
│  容器中是否存在 Knife4jProperties Bean?                  │
│       │                                                 │
│       ├─ YES ──→ 返回已存在的 Bean（使用用户配置）        │
│       │                                                 │
│       └─ NO ───→ 执行 Knife4jProperties::new            │
│                   创建新的默认实例                        │
│                   （使用默认配置）                        │
└─────────────────────────────────────────────────────────┘
```

---

## 5️⃣ ObjectProvider vs @Autowired(required = false)

| 对比项 | `@Autowired(required = false)` | `ObjectProvider<T>` |
|--------|-------------------------------|---------------------|
| **注入时机** | 立即注入 | 延迟获取（按需） |
| **不存在时** | 字段为 `null` | 通过 API 控制行为 |
| **NPE 风险** | 高（容易忘记判空） | 低（可用默认值） |
| **灵活性** | 低 | 高（多种获取方式） |
| **代码优雅度** | 需 `if (bean != null)` | `getIfAvailable(() -> new Xxx())` |

---

## 6️⃣ 完整使用场景

```java
// 场景1：完全可选，不需要默认值
ObjectProvider<OptionalService> provider;
OptionalService service = provider.getIfAvailable();  // 可能返回 null

// 场景2：有默认值（你的代码场景）
ObjectProvider<ConfigProperties> provider;
ConfigProperties config = provider.getIfAvailable(ConfigProperties::new);

// 场景3：从多个实现中选一个唯一的
ObjectProvider<Strategy> provider;
Strategy strategy = provider.getIfUnique();  // 要求只有一个 Strategy Bean

// 场景4：遍历所有实现（类似 Map<String, T>）
ObjectProvider<Plugin> provider;
provider.stream().forEach(plugin -> plugin.execute());
```

---

## 总结

```
┌─────────────────────────────────────────────────────────┐
│  ObjectProvider = 延迟注入 + 安全获取 + 灵活控制           │
│                                                         │
│  getIfAvailable(Supplier) = Bean 存在则用之，            │
│                            不存在则创建默认              │
│                                                         │
│  这让你的组件：                                          │
│  • 不强制依赖其他 Bean                                    │
│  • 在缺失依赖时仍能正常工作                               │
│  • 避免 IDE/编译器报错                                    │
└─────────────────────────────────────────────────────────┘
```