# Technical Architecture Proposal

## Overview

This document presents two architectural approaches for the UniEnterprise project: Monolithic Architecture and Microservices Architecture. Both architectures will be maintained and developed in parallel, allowing teams to choose the appropriate approach based on project requirements and scale.

---

## Monolithic Architecture Solution

### Architecture Diagram

```
┌─────────────────────────────────────────────────────┐
│                  Client Layer                     │
│  ┌──────────────┐  ┌──────────────┐          │
│  │  Admin Web   │  │  Mobile H5   │          │
│  │  (Vue 3)    │  │  (Uni-app)    │          │
│  └──────┬───────┘  └──────┬───────┘          │
└─────────┼────────────────────┼─────────────────────┘
          │                    │
          └────────┬───────────┘
                   ▼
┌─────────────────────────────────────────────────────┐
│              API Gateway Layer                  │
│           (Nginx / Spring Gateway)               │
└────────────────────────┬─────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────┐
│          Application Layer (Spring Boot)            │
│  ┌──────────────────────────────────────────┐  │
│  │  uni-enterprise-admin                 │  │
│  │  ┌─────┬─────┬─────┬─────┐    │  │
│  │  │Sys  │Infra│BPM  │Paym │    │  │
│  │  │tem  │     │     │ent  │    │  │
│  │  └─────┴─────┴─────┴─────┘    │  │
│  │                                     │  │
│  └──────────────────────────────────────────┘  │
└────────────────────────┬─────────────────────────────┘
                     │
          ┌──────────┼──────────┐
          ▼          ▼          ▼
┌─────────────┐ ┌─────────┐ ┌─────────────┐
│   MySQL     │ │  Redis  │ │ RabbitMQ   │
│  Database   │ │  Cache  │ │  Message    │
└─────────────┘ └─────────┘ │  Queue     │
                               └─────────────┘
```

### Module Division

#### Core Modules
1. **uni-enterprise-admin**: Main application module
2. **uni-enterprise-api**: RESTful API layer
3. **uni-enterprise-common**: Common utilities and base classes

#### Business Modules
- **uni-system**: System management (users, roles, menus)
- **uni-tenant**: Multi-tenant management
- **uni-bpm**: Workflow process management
- **uni-payment**: Payment integration
- **uni-member**: Member management
- **uni-visualization**: Data visualization
- **uni-mall**: E-commerce module
- **uni-crm**: Customer relationship management
- **uni-erp**: Enterprise resource planning

### Package Structure Design

```
uni-enterprise/
├── uni-enterprise-admin/          # Main application
│   ├── src/main/java/com/uni/
│   │   ├── UniEnterpriseApplication.java
│   │   ├── controller/              # Controller layer
│   │   │   ├── system/
│   │   │   ├── tenant/
│   │   │   └── mall/
│   │   ├── service/                # Service layer
│   │   │   ├── system/
│   │   │   └── mall/
│   │   ├── mapper/                 # Data access layer
│   │   │   ├── system/
│   │   │   └── mall/
│   │   ├── model/                  # Entity/DTO/VO
│   │   ├── config/                 # Configuration
│   │   └── common/                # Common utilities
│   └── src/main/resources/
│       ├── application.yml
│       ├── application-dev.yml
│       └── application-prod.yml
├── uni-enterprise-common/          # Common module
│   └── src/main/java/com/uni/common/
│       ├── constant/               # Constants
│       ├── exception/              # Custom exceptions
│       ├── util/                   # Utilities
│       └── enums/                  # Enums
└── pom.xml
```

### Layered Design

#### Controller Layer
- **Responsibility**: Handle HTTP requests, parameter validation
- **Annotations**: `@RestController`, `@RequestMapping`, `@Validated`
- **Return Type**: Unified `Result<T>` wrapper

```java
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public Result<UserDTO> getUser(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return Result.success(user);
    }
}
```

#### Service Layer
- **Responsibility**: Business logic, transaction management
- **Annotations**: `@Service`, `@Transactional`
- **Interfaces**: Define service interfaces for decoupling

```java
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public UserDTO getUserById(Long id) {
        User user = userMapper.selectById(id);
        return BeanUtil.copyProperties(user, UserDTO.class);
    }
}
```

#### Mapper Layer
- **Responsibility**: Database operations
- **Framework**: MyBatis Plus
- **Base Methods**: CRUD operations inherited from BaseMapper

```java
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // Custom query methods
    @Select("SELECT * FROM sys_user WHERE tenant_id = #{tenantId}")
    List<User> selectByTenantId(@Param("tenantId") Long tenantId);
}
```

### Advantages
- **Simplicity**: Easier to develop, test, and deploy
- **Performance**: No network overhead between services
- **Debugging**: Easier to trace and debug issues
- **Lower Cost**: Lower infrastructure and operational costs

### Disadvantages
- **Scalability**: Limited horizontal scalability
- **Maintenance**: Large codebase becomes harder to maintain
- **Technology**: Limited flexibility in technology choices
- **Deployment**: Entire application must be redeployed for updates

---

## Microservices Architecture Solution

### Architecture Diagram

```
┌─────────────────────────────────────────────────────┐
│                  Client Layer                     │
│  ┌──────────────┐  ┌──────────────┐          │
│  │  Admin Web   │  │  Mobile H5   │          │
│  │  (Vue 3)    │  │  (Uni-app)    │          │
│  └──────┬───────┘  └──────┬───────┘          │
└─────────┼────────────────────┼─────────────────────┘
          │                    │
          └────────┬───────────┘
                   ▼
┌─────────────────────────────────────────────────────┐
│           API Gateway (Spring Cloud Gateway)     │
│  ┌──────────────────────────────────────────┐  │
│  │  - Route management                 │  │
│  │  - Load balancing                 │  │
│  │  - Authentication & Authorization   │  │
│  │  - Rate limiting                  │  │
│  │  - Request/Response logging        │  │
│  └──────────────────────────────────────────┘  │
└────────────────────────┬─────────────────────────────┘
                     │
          ┌──────────┼──────────┬──────────┐
          ▼          ▼          ▼          ▼
┌────────────┐ ┌──────────┐ ┌──────────┐ ┌────────────┐
│ uni-gateway│ │ uni-auth  │ │uni-system │ │uni-tenant  │
│ (Gateway)  │ │ (Auth)    │ │(System)   │ │(Tenant)    │
└────────────┘ └──────────┘ └──────────┘ └────────────┘
                                              │
┌────────────┐ ┌──────────┐ ┌──────────┐ ┌────────────┐
│ uni-bpm   │ │uni-payment│ │uni-member │ │uni-mall    │
│  (BPM)    │ │ (Payment) │ │ (Member)  │ │ (Mall)     │
└────────────┘ └──────────┘ └──────────┘ └────────────┘
                                              │
┌─────────────────────────────────────────────────────┐
│              Infrastructure Layer                  │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐  │
│  │   Nacos   │ │  Redis   │ │RabbitMQ  │  │
│  │ (Registry)│ │  (Cache) │ │ (MQ)     │  │
│  └──────────┘ └──────────┘ └──────────┘  │
│  ┌──────────┐ ┌──────────┐              │
│  │   MySQL   │ │  Seata   │              │
│  │ (DB)     │ │ (TX)     │              │
│  └──────────┘ └──────────┘              │
└─────────────────────────────────────────────────────┘
```

### Service Decomposition Strategy

#### Gateway Service (uni-gateway)
- **Responsibility**: API gateway, routing, authentication
- **Technology**: Spring Cloud Gateway
- **Features**: Load balancing, rate limiting, request logging

#### Auth Service (uni-auth)
- **Responsibility**: Authentication, authorization, token management
- **Features**: Login, logout, token refresh, OAuth2 integration

#### System Service (uni-system)
- **Responsibility**: User, role, menu, department management
- **Features**: RBAC, dictionary management

#### Tenant Service (uni-tenant)
- **Responsibility**: Multi-tenant management
- **Features**: Tenant registration, tenant configuration, data isolation

#### Business Services
- **uni-bpm**: Workflow management
- **uni-payment**: Payment processing
- **uni-member**: Member management
- **uni-mall**: E-commerce functionality
- **uni-crm**: Customer relationship management
- **uni-erp**: Enterprise resource planning

### Service Communication

#### Synchronous Communication
- **Framework**: OpenFeign
- **Use Case**: Service-to-service API calls
- **Example**: uni-mall calls uni-payment for payment processing

```java
@FeignClient(name = "uni-payment", path = "/api/v1/payment")
public interface PaymentFeignClient {

    @PostMapping("/create")
    Result<PaymentOrderDTO> createPayment(@RequestBody PaymentRequestDTO request);
}
```

#### Asynchronous Communication
- **Framework**: RabbitMQ
- **Use Case**: Event-driven communication, decoupled services
- **Example**: uni-mall publishes order event, uni-bpm listens for order approval

```java
@RabbitListener(queues = "order.created.queue")
public void handleOrderCreated(OrderCreatedEvent event) {
    // Process order created event
    workflowService.startOrderApproval(event.getOrderId());
}
```

### Service Governance

#### Service Registry & Discovery
- **Technology**: Nacos
- **Purpose**: Service registration, discovery, health checks
- **Features**: Dynamic configuration, service metadata

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: uni-enterprise
        group: DEFAULT_GROUP
```

#### Configuration Center
- **Technology**: Nacos Config
- **Purpose**: Centralized configuration management
- **Features**: Dynamic configuration refresh, version control

#### Service Gateway
- **Technology**: Spring Cloud Gateway
- **Purpose**: API gateway, routing, authentication
- **Features**: Load balancing, rate limiting, circuit breaker

#### Distributed Transaction
- **Technology**: Seata
- **Purpose**: Ensure data consistency across services
- **Pattern**: TCC (Try-Confirm-Cancel) or Saga

### Advantages
- **Scalability**: Independent scaling of services
- **Flexibility**: Different technologies per service
- **Maintenance**: Smaller codebase, easier to understand
- **Deployment**: Independent deployment and updates
- **Team Organization**: Teams can work independently on services

### Disadvantages
- **Complexity**: Higher system complexity
- **Performance**: Network overhead between services
- **Debugging**: Distributed tracing required
- **Cost**: Higher infrastructure and operational costs

---

## Architecture Comparison Analysis

| Aspect | Monolithic | Microservices | Recommendation |
|---------|-------------|----------------|---------------|
| **Complexity** | Low | High | Use monolithic for MVP |
| **Scalability** | Limited | Excellent | Use microservices for scale |
| **Development Speed** | Fast | Medium | Monolithic for rapid development |
| **Deployment** | Simple | Complex | Monolithic for simplicity |
| **Team Size** | Small team | Large team | Match to team size |
| **Performance** | Better (no network) | Good (with optimization) | Similar for most cases |
| **Maintenance** | Harder at scale | Easier at scale | Depends on project size |
| **Infrastructure** | Lower cost | Higher cost | Consider budget |

---

## Recommended Solution

### Phase 1: MVP (Monolithic)
- **Timeline**: 0-6 months
- **Architecture**: Monolithic
- **Reasoning**: Faster development, easier to test, lower cost
- **Deliverables**: Core modules (System, Tenant, BPM, Payment, Mall)

### Phase 2: Growth (Both)
- **Timeline**: 6-12 months
- **Architecture**: Both monolithic and microservices
- **Reasoning**: Allow customers to choose based on needs
- **Deliverables**: Complete microservices architecture, migration guide

### Phase 3: Production (Microservices)
- **Timeline**: 12+ months
- **Architecture**: Microservices (primary), Monolithic (maintenance)
- **Reasoning**: Scale to enterprise requirements
- **Deliverables**: Full microservices ecosystem, advanced features

---

## Migration Strategy

### Monolithic to Microservices Migration Path

1. **Strangler Fig Pattern**: Gradually extract services from monolith
2. **Database Splitting**: Migrate to service-specific databases
3. **API Gateway**: Introduce gateway to route requests
4. **Service-by-Service**: Migrate one service at a time
5. **Verification**: Thoroughly test each migrated service
6. **Decommission**: Remove migrated code from monolith

### Rollback Strategy

- Maintain monolith during transition
- Feature flags to switch between architectures
- Database compatibility during migration
- Monitoring and alerts for both systems

---

## Conclusion

Both monolithic and microservices architectures have their place in enterprise development. The recommended approach is to start with monolithic for rapid development, then gradually migrate to microservices as the system grows and scaling requirements increase.

**Key Decision Factors:**
- Project timeline and budget
- Team size and expertise
- Expected user volume and growth
- Business requirements for scalability
- Maintenance and operational capabilities

---

## Version History

| Version | Date | Author | Changes |
|---------|-------|---------|----------|
| 1.0.0 | 2025-01-11 | Initial architecture proposal |
