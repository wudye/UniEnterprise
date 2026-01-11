# Microservices Architecture Design Document

## Overview

This document describes the microservices architecture design for the UniEnterprise project, including service decomposition strategy, service list, inter-service communication, service governance, and distributed transaction handling.

---

## Microservices Architecture Overview

### Architecture Diagram

```
┌─────────────────────────────────────────────────────────┐
│                   Client Layer                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │  Admin Web   │  │  Mobile H5   │  │  Mini Program│  │
│  │  (Vue 3)    │  │  (Uni-app)   │  │ (WeChat)    │  │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  │
└─────────┼────────────────────┼────────────────────┼─────┘
          │                    │                    │
          └────────┬───────────┴────────┬───────────┘
                   │                    │
                   ▼                    ▼
┌─────────────────────────────────────────────────────────┐
│              API Gateway (Spring Cloud Gateway)        │
│  ┌──────────────────────────────────────────────────┐  │
│  │  - Route Management                              │  │
│  │  - Load Balancing                                │  │
│  │  - Authentication & Authorization               │  │
│  │  - Rate Limiting                                 │  │
│  │  - Circuit Breaker                               │  │
│  └──────────────────────────────────────────────────┘  │
└────────────────────────────────┬────────────────────────┘
                                 │
          ┌──────────────────────┼──────────────────────┐
          │                      │                      │
          ▼                      ▼                      ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│ uni-gateway  │    │  uni-auth    │    │ uni-system   │
│  (Gateway)   │    │   (Auth)     │    │  (System)    │
└──────────────┘    └──────────────┘    └──────────────┘

┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│ uni-tenant   │    │   uni-bpm    │    │ uni-payment  │
│  (Tenant)    │    │   (BPM)      │    │  (Payment)   │
└──────────────┘    └──────────────┘    └──────────────┘

┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│ uni-member   │    │  uni-mall    │    │   uni-crm    │
│  (Member)    │    │   (Mall)     │    │   (CRM)      │
└──────────────┘    └──────────────┘    └──────────────┘

┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│   uni-erp    │    │uni-monitor   │    │  uni-infra   │
│   (ERP)      │    │ (Monitor)    │    │ (Infra)      │
└──────────────┘    └──────────────┘    └──────────────┘
                                 │
┌────────────────────────────────┼────────────────────────┐
│              Infrastructure Layer                    │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌─────────┐ │
│  │  Nacos   │  │  Redis   │  │RabbitMQ  │ │  Seata  │ │
│  │(Registry)│  │ (Cache)  │  │   (MQ)   │ │  (TX)   │ │
│  └──────────┘  └──────────┘  └──────────┘  └─────────┘ │
│  ┌──────────┐  ┌──────────┐                        │ │
│  │  MySQL   │  │  MinIO   │                        │ │
│  │   (DB)   │  │(Storage) │                        │ │
│  └──────────┘  └──────────┘                        │ │
└───────────────────────────────────────────────────────┘
```

---

## Service Decomposition Strategy

### Decomposition Principles

1. **Single Responsibility**: Each service should have a single business responsibility
2. **High Cohesion**: Related functionality should be grouped together
3. **Loose Coupling**: Services should be loosely coupled
4. **Independent Deployment**: Each service can be deployed independently
5. **Data Ownership**: Each service owns its own database

### Decomposition Approach

- **Domain-Driven Design (DDD)**: Decompose based on business domains
- **Bounded Context**: Identify bounded contexts and create services
- **Microservice Size**: Keep services small but not too small

---

## Service List

### Gateway Services

| Service Name | Port | Description | Dependencies |
|--------------|------|-------------|--------------|
| uni-gateway | 8080 | API gateway service | Nacos, Redis |

### Core Services

| Service Name | Port | Description | Dependencies |
|--------------|------|-------------|--------------|
| uni-auth | 8081 | Authentication & authorization | Nacos, Redis, MySQL |
| uni-system | 8082 | System management | Nacos, Redis, MySQL |
| uni-tenant | 8083 | Multi-tenant management | Nacos, Redis, MySQL |
| uni-infra | 8084 | Infrastructure services | Nacos, Redis, MySQL, MinIO |

### Business Services

| Service Name | Port | Description | Dependencies |
|--------------|------|-------------|--------------|
| uni-bpm | 8090 | Workflow management | Nacos, Redis, MySQL |
| uni-payment | 8091 | Payment integration | Nacos, Redis, MySQL, Alipay SDK |
| uni-member | 8092 | Member management | Nacos, Redis, MySQL |
| uni-mall | 8093 | E-commerce | Nacos, Redis, MySQL |
| uni-crm | 8094 | Customer relationship management | Nacos, Redis, MySQL |
| uni-erp | 8095 | Enterprise resource planning | Nacos, Redis, MySQL |
| uni-monitor | 8096 | Data monitoring & reports | Nacos, Redis, MySQL |

---

## Inter-Service Communication

### Synchronous Communication (OpenFeign)

**Use Case:** Service-to-service API calls requiring immediate response

**Technology:** OpenFeign

**Example:** uni-mall calls uni-payment for payment processing

```java
@FeignClient(name = "uni-payment", path = "/api/v1/payment")
public interface PaymentFeignClient {

    @PostMapping("/create")
    Result<PaymentOrderDTO> createPayment(@RequestBody PaymentRequestDTO request);

    @GetMapping("/order/{orderNo}")
    Result<PaymentOrderDTO> getPaymentByOrderNo(@PathVariable String orderNo);

    @PostMapping("/refund")
    Result<RefundResultDTO> refund(@RequestBody RefundRequestDTO request);
}
```

**Configuration:**

```yaml
spring:
  cloud:
    openfeign:
      client:
        config:
          default:
            connectTimeout: 5000
            readTimeout: 5000
            loggerLevel: basic
```

### Asynchronous Communication (RabbitMQ)

**Use Case:** Event-driven communication, decoupled services

**Technology:** RabbitMQ

**Example:** uni-mall publishes order event, uni-bpm listens for order approval

**Publisher:**

```java
@Service
public class OrderEventPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publishOrderCreated(OrderCreatedEvent event) {
        rabbitTemplate.convertAndSend(
            "order.exchange",
            "order.created",
            event
        );
    }
}
```

**Consumer:**

```java
@Component
public class OrderEventConsumer {

    @Autowired
    private WorkflowService workflowService;

    @RabbitListener(queues = "order.created.queue")
    public void handleOrderCreated(OrderCreatedEvent event) {
        workflowService.startOrderApproval(event.getOrderId());
    }
}
```

**Message Queue Configuration:**

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    virtual-host: /uni-enterprise
```

### Communication Pattern Selection

| Pattern | Use Case | Technology |
|---------|----------|------------|
| Synchronous | Requires immediate response | OpenFeign |
| Asynchronous | Event-driven, decoupled | RabbitMQ |
| One-way | Fire and forget | RabbitMQ |
| Request-Reply | Request with callback | RabbitMQ |

---

## Service Governance

### Service Registry & Discovery

**Technology:** Nacos

**Purpose:** Service registration, discovery, health checks

**Configuration:**

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: uni-enterprise
        group: DEFAULT_GROUP
        service: ${spring.application.name}
        metadata:
          version: ${project.version}
          context-path: ${server.servlet.context-path}
```

**Service Registration:**

```java
@SpringBootApplication
@EnableDiscoveryClient
public class UniSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(UniSystemApplication.class, args);
    }
}
```

**Service Discovery:**

```java
@FeignClient(name = "uni-system")
public interface SystemFeignClient {
    // Feign client automatically discovers service from Nacos
}
```

### Configuration Center

**Technology:** Nacos Config

**Purpose:** Centralized configuration management

**Configuration:**

```yaml
spring:
  cloud:
    nacos:
      config:
        server-addr: localhost:8848
        namespace: uni-enterprise
        group: DEFAULT_GROUP
        file-extension: yaml
        refresh-enabled: true
```

**Dynamic Configuration:**

```java
@RefreshScope
@RestController
public class ConfigController {

    @Value("${app.feature.enabled:false}")
    private Boolean featureEnabled;

    @GetMapping("/feature/status")
    public Boolean getFeatureStatus() {
        return featureEnabled;
    }
}
```

### Service Gateway

**Technology:** Spring Cloud Gateway

**Purpose:** API gateway, routing, authentication

**Configuration:**

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: uni-system-route
          uri: lb://uni-system
          predicates:
            - Path=/api/system/**
          filters:
            - StripPrefix=2

        - id: uni-mall-route
          uri: lb://uni-mall
          predicates:
            - Path=/api/mall/**
          filters:
            - StripPrefix=2
```

### Load Balancing

**Technology:** Spring Cloud LoadBalancer

**Strategy:** Round-robin (default)

```yaml
spring:
  cloud:
    loadbalancer:
      ribbon:
        enabled: false
      cache:
        enabled: true
        ttl: 30s
```

### Circuit Breaker

**Technology:** Resilience4j

**Purpose:** Prevent cascade failures

```yaml
resilience4j:
  circuitbreaker:
    instances:
      paymentService:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10s
        permitted-number-of-calls-in-half-open-state: 3
```

**Usage:**

```java
@FeignClient(name = "uni-payment", fallbackFactory = PaymentFeignFallbackFactory.class)
public interface PaymentFeignClient {
    // Feign client with circuit breaker
}
```

---

## Distributed Transaction Solution

### Technology: Seata

**Pattern:** AT Mode (default) / TCC (for complex scenarios)

### Configuration

**Application.yml:**

```yaml
seata:
  enabled: true
  application-id: uni-enterprise
  tx-service-group: uni-enterprise-tx-group
  registry:
    type: nacos
    nacos:
      server-addr: localhost:8848
      namespace: uni-enterprise
      group: SEATA_GROUP
  config:
    type: nacos
    nacos:
      server-addr: localhost:8848
      namespace: uni-enterprise
      group: SEATA_GROUP
```

### Global Transaction Usage

```java
@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private InventoryFeignClient inventoryClient;

    @GlobalTransactional(name = "create-order", rollbackFor = Exception.class)
    public void createOrder(OrderDTO orderDTO) {
        // Step 1: Create order
        orderMapper.insert(order);

        // Step 2: Deduct inventory
        inventoryClient.deductInventory(order.getProductId(), order.getQuantity());

        // Step 3: If any step fails, all operations will be rolled back
    }
}
```

### TCC Mode (Advanced)

```java
@LocalTCC
public interface InventoryService {

    @TwoPhaseBusinessAction(name = "inventoryDeduct", commitMethod = "commit", rollbackMethod = "rollback")
    boolean deductInventory(@BusinessActionContextParameter(paramName = "productId") Long productId,
                            @BusinessActionContextParameter(paramName = "quantity") Integer quantity);

    boolean commit(BusinessActionContext context);

    boolean rollback(BusinessActionContext context);
}
```

---

## Service Deployment Strategy

### Containerized Deployment

**Dockerfile Example:**

```dockerfile
FROM openjdk:17-jre-slim
WORKDIR /app
COPY target/uni-system.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Docker Compose Example:**

```yaml
version: '3.8'
services:
  uni-system:
    build: ./uni-system
    ports:
      - "8082:8082"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - NACOS_SERVER_ADDR=nacos:8848
    depends_on:
      - nacos
      - mysql
      - redis
```

### Kubernetes Deployment

**Deployment Example:**

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: uni-system
spec:
  replicas: 3
  selector:
    matchLabels:
      app: uni-system
  template:
    metadata:
      labels:
        app: uni-system
    spec:
      containers:
      - name: uni-system
        image: uni-system:latest
        ports:
        - containerPort: 8082
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: prod
        - name: NACOS_SERVER_ADDR
          value: nacos-service:8848
```

---

## Monitoring & Observability

### Metrics Collection

**Technology:** Micrometer + Prometheus

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

### Distributed Tracing

**Technology:** Spring Cloud Sleuth + Zipkin

```yaml
spring:
  sleuth:
    zipkin:
      base-url: http://zipkin:9411
    sampler:
      probability: 1.0
```

### Logging

**Technology:** Logback + ELK Stack

```xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    <root level="INFO">
        <appender-ref ref="STDOUT" />
    </root>
</configuration>
```

---

## Version History

| Version | Date | Author | Changes |
|---------|-------|---------|----------|
| 1.0.0 | 2025-01-11 | Initial microservices architecture design |
