# UniEnterprise

> A comprehensive enterprise-level development template featuring monolithic and microservices architectures.

The project can be used for e-commerce malls, SCRM systems, OA systems, logistics systems, ERP systems, CMS systems, HIS systems, payment systems, IM chat, social media integrations (Facebook, Instagram, Twitter/X, LinkedIn, TikTok), and more.

---




## Project Highlights
### 🎓 Education & Training
- Complete business modules for teaching and learning
- Well-documented code with clear architecture
- Best practices and design patterns

### 🏢 Industry & Business
- Production-ready codebase
- Scalable architecture
- Comprehensive business features

### 🔧 Modular Design
- Select only the modules you need
- Easy to customize and extend
- Quick start for new projects

### 🌍 International Support
- Multi-language support (De/EN/CN)
- Timezone handling
- Multi-currency support (for e-commerce)

---

## Requirements
1. All features are guaranteed high quality through unit testing.
2. Clean code and clean architecture, following the coding standards of "Effective Java" and "Clean Code".

---

## Key Features
1. Multiple Frontend Versions: angular and react native
2. Mobile Admin: React Native solution supporting APP, Mini Program, and H5 from a single codebase
3. Multi-Database Support: MySQL, PostgreSQLetc.
4. Flexible Message Queue: Event, Redis, RabbitMQ, Kafka, RocketMQ, etc.
5. Advanced Authentication: Spring Security + Token + Redis, supports multi-terminal, multi-user, and SSO
6. Dynamic Permission Control: Dynamic menu loading, button-level permissions, Redis caching for performance
7. Multi-Tenant SaaS: Customizable permissions per tenant with transparent multi-tenant encapsulation
8. Flowable Workflow: Dynamic forms, online process design, countersignature/or-sign, multiple task allocation methods
9. Code Generator: One-click generation of Java, Vue, SQL scripts, API docs; supports single table, tree table, master-detail tables
10. Real-time Communication: Spring WebSocket with built-in token authentication and WebSocket cluster support
11. Third-Party Login: Facebook, Google, LinkedIn, Twitter/X, GitHub, etc.
12. Payment Integration: Stripe, PayPal, Braintree, Square with payment and refund support
13. SMS & Cloud Storage:  MinIO
14. Report Design: Report designer and dashboard designer for creating stunning reports via drag-and-drop
15. Built-in Business Modules: Mall, OA, ERP, CRM, CMS, BBS, AI Agent
16. Operation Logs & Audit Logs

---

## Architecture Overview

| Module | Project | Description |
|--------|---------|-------------|
| unienterprise-dependencies | Maven Dependency Version Management | Centralized Maven dependency version control |
| unienterprise-framework | Java Framework Extension | Core Java framework extensions and utilities |
| unienterprise-server | Management Backend + User App Server | Admin backend and user application server |
| unienterprise-module-system | System Module | System management and core functionality |
| unienterprise-module-member | Member Center Module | Member and user center functionality |
| unienterprise-module-infra | Infrastructure Module | Infrastructure and common services |
| unienterprise-module-bpm | Workflow Module | Business Process Management (BPM) workflow |
| unienterprise-module-pay | Payment System Module | Payment processing and transaction management |
| unienterprise-module-mall | Mall System Module | E-commerce and shopping mall functionality |
| unienterprise-module-erp | ERP System Module | Enterprise Resource Planning |
| unienterprise-module-crm | CRM System Module | Customer Relationship Management |
| unienterprise-module-ai | AI Module | AI large language model integration |
| unienterprise-module-social | Social Media Integration Module | Facebook, Instagram, Twitter/X, LinkedIn, TikTok integration |
| unienterprise-module-report | Report & Dashboard Module | Large screen reporting and analytics |

### Framework Modules (unienterprise-framework)

| Submodule | Description |
|-------------------------------------------------------|----------------------|
| unienterprise-common                                            | Common base module that defines base POJO classes, enumerations, exception system, and utility classes. Provides unified API response formats (CommonResult, PageResult), pagination parameters (PageParam), global error code specifications, business exception handling, and common utility class collections (cache, collections, date, JSON, Spring, Servlet, etc.). Supports SkyWalking distributed tracing and data translation functionality. |
| unienterprise-spring-boot-starter-security            | Security framework that provides unified authentication, authorization, token validation, multi-tenant security, cross-thread context propagation, and other core security capabilities. Supports OAuth2, JWT, and RBAC permission model. |
| unienterprise-spring-boot-starter-biz-data-permission | Data permission framework starter that implements data permission control by parsing SQL with JSqlParser and dynamically appending WHERE conditions. Provides @DataPermission annotation-driven data permission rule system, supports department-based data permission filtering (department ID/user ID), custom data permission rule extension, annotation-level enable/disable permission control, and include/exclude rule priority configuration. Suitable for enterprise-level application data permission isolation scenarios. |
| unienterprise-spring-boot-starter-biz-ip              | IP address and region information processing module that supports IP address to city information lookup (based on ip2region), regional tree structure management , region path formatting, and parent-child region queries. Suitable for geolocation identification, user access analysis, and data statistics scenarios. |
| unienterprise-spring-boot-starter-biz-tenant           |Multi-tenant framework starter that supports full-chain tenant isolation across database, Redis, Web, Security, scheduled tasks, message queues, and async operations. Implements database-level tenant filtering based on MyBatis Plus (TenantBaseDO base class), controls table/method-level tenant ignoring through @TenantIgnore annotation, automatically parses tenant-id from Web layer headers, executes jobs in parallel by tenant, propagates tenant information through MQ headers, and appends tenant ID to Redis keys. Suitable for enterprise-level SaaS applications. |
| unienterprise-spring-boot-starter-excel               | Excel processing module that implements Excel import/export functionality based on FastExcel. Provides @DictFormat dictionary formatting annotation, @ExcelColumnSelect dropdown selection annotation, automatic column width adaptation, Long type precision conversion, and common data converters for JSON/Munienterprisey/Area. Supports exporting from HTTP response streams and importing data from MultipartFile. |
| unienterprise-spring-boot-starter-job                 | Scheduled task and async task module that implements in-process scheduled task scheduling based on Quartz, using MySQL cluster solution to ensure high availability. Implements async task execution based on Spring Async, integrated with Alibaba TTL (TransmittableThreadLocal) to ensure async thread context propagation. Provides @JobHandler interface, SchedulerManager task manager, task CRUD operations, and CRON expression parsing.|
| unienterprise-spring-boot-starter-monitor             |  Service monitoring and distributed tracing module that implements distributed tracing and logging center based on SkyWalking, integrated with Micrometer for Prometheus metrics collection and Spring Boot Admin health monitoring. Provides @BizTrace business tracing annotation, TraceFilter filter, exception logging, and TraceID response header configuration.  |
| unienterprise-spring-boot-starter-mq                  | Message queue module that supports four message middleware options: Redis (Pub/Sub and Stream), RabbitMQ, RocketMQ, and Kafka. Provides unified RedisMQTemplate operation template, message interceptor mechanism, pending message retry task, Stream message cleanup task, Jackson message converter, and message header support. |
| unienterprise-spring-boot-starter-jpa                 |JPA persistence layer enhancement module integrated with connection pool, dynamic data source, transaction management, and multi-database support (MySQL/Oracle/PostgreSQL/SQL Server/DM/KingBase/GaussDB/TDengine). Provides BaseMapperX extended Mapper, BaseDO base entity, join query capability, field encryption, collection type handlers, and data translation features.  |
| unienterprise-spring-boot-starter-protection          |Service protection framework that provides distributed locks (based on Lock4j), API idempotency (@Idempotent annotation), rate limiting (@RateLimiter annotation), and API signature verification (@ApiSignature annotation) as core protection capabilities. Supports multiple key resolvers (global/user/IP/server node/custom expression), implemented with Redis-based distributed storage.|
| unienterprise-spring-boot-starter-redis               | Redis cache module that implements distributed caching based on Spring Data Redis and Redisson. Provides TimeoutRedisCacheManager with custom expiration time support (@Cacheable(cacheNames = "key#1h")), JSON serialization, key prefix configuration, and batch scan optimization. Supports flexible time unit configuration (d/h/m/s). |
| unienterprise-spring-boot-starter-test                |  Testing framework module that provides unit/integration testing dependencies and test tool configurations (such as spring-boot-starter-test, mockito, etc.). |
| unienterprise-spring-boot-starter-web                 | Web base framework module built on Spring Boot Web. Provides global exception handling (GlobalExceptionHandler), global response body wrapping (GlobalResponseBodyHandler), API request logging (ApiRequestFilter), request body caching (CacheRequestBodyFilter), and other common web functionalities. Integrated with Knife4j/SpringDoc for automatic API documentation generation and includes security mechanisms such as XSS filtering. |
| unienterprise-spring-boot-starter-websocket           | WebSocket framework module built on Spring Boot WebSocket. Supports multi-node broadcasting mechanism, compatible with various message middlewares including Redis, RabbitMQ, RocketMQ, Kafka, and local broadcasting. Provides WebSocketSessionManager for session management, integration with Security  for user identity association, and multi-tenant isolated broadcasting capabilities. |

---

## Built-in Features

**Common Modules (Required):** System Functions, Infrastructure  
**Common Modules (Optional):** Workflow Engine, Payment System, Data Reports, Member Center  
**Business Systems (On-Demand):** ERP System, CRM System, Mall System, Social Media Integration, AI Agent

### Architecture Diagram

```
┌──────────────────────────────────────────────────────────────────┐
│                          TOP LEVEL                               │
│                     Business Systems                             │
│  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐  │
│  │ Mall │ │  OA  │ │ ERP  │ │ CRM  │ │ CMS  │ │ BBS  │ │ AI   │  │
│  └──┬───┘ └──┬───┘ └──┬───┘ └──┬───┘ └──┬───┘ └──┬───┘ └──┬───┘  │
└─────┼────────┼────────┼────────┼────────┼────────┼────────┼──────┘
      │        │        │        │        │        │        │
      └────────┴────────┴────────┴────────┴────────┴────────┘
                               │ depends on
                               ▼
┌────────────────────────────────────────────────────────────────────┐
│                        MIDDLE LEVEL                                │
│                        Common Modules                              │
│  ┌────────┐ ┌───────────────┐ ┌───────┐ ┌─────────┐ ┌───────────┐  │
│  │ System │ │ Infrastructure│ │  BPM  │ │ Payment │ │  Member   │  │
│  └───┬────┘ └───────┬───────┘ └───┬───┘ └────┬────┘ └─────┬─────┘  │
│  ┌───┴───────────────┴───────────┴───────┴─────────┴─────────────┐ │
│  │                 Visualization & Reports                       │ │
│  └─────────────────────────────────────────────────────────────────┘ │
└─────┼───────────────┼───────────────┼───────────────┼──────────────┘
      │               │               │               │
      └───────────────┴───────────────┴───────────────┘
                               │ depends on
                               ▼
┌─────────────────────────────────────────────────────────────────────┐
│                         BASE LEVEL                                  │
│                     Framework Components                            │
│                                                                     │
│  ┌──────────┐ ┌──────────┐ ┌───────┐ ┌──────┐ ┌──────────┐          │
│  │   Web    │ │ Security │ │ Redis │ │  MQ  │ │   Job    │          │
│  └──────────┘ └──────────┘ └───────┘ └──────┘ └──────────┘          │
│                                                                     │
│  ┌──────────┐ ┌──────────┐ ┌────────────┐ ┌───────────┐             │
│  │ Monitor  │ │   Test   │ │  Flowable  │ │Data Perm. │             │
│  └──────────┘ └──────────┘ └────────────┘ └───────────┘             │
│                                                                     │
│  ┌──────────┐ ┌───────┐ ┌──────────┐ ┌──────────────┐               │
│  │  Tenant  │ │Payment │ │   SMS    │ │   Social     │              │
│  └──────────┘ └───────┘ └──────────┘ └──────────────┘               │
│                                                                     │
│  ┌────────────────────────────────────────────────────┐             │
│  │              Operation & Audit Logs                │             │
│  └────────────────────────────────────────────────────┘             │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 🔗 Tech Stack(version will be updated)

### Backend

#### Spring Boot (Monolithic Architecture)

| Framework | Description | Version |
|-----------|-------------|---------|
| Java | Programming Language | 21 (LTS) |
| Spring Boot | Application Development Framework | 3.5.x |
| MySQL | Database Server | 5.7 / 8.0+ |
| HikariCP | JDBC Connection Pool & Monitoring | 5.1.0 |
| Dynamic Datasource | Dynamic Data Source | 4.4.0 |
| Redis | Key-Value Database | 7.0+ |
| Redisson | Redis Client | 3.35.0 |
| Spring MVC | MVC Framework | 6.1.x |
| Spring Security | Spring Security Framework | 6.3.x |
| Hibernate Validator | Parameter Validation | 8.0.1 |
| Flowable | Workflow Engine | 6.9.0 |
| Quartz | Task Scheduling | 2.5.0 |
| Springdoc | Swagger Documentation | 2.3.0 |
| SkyWalking | Distributed Tracing | 9.0.0 |
| Spring Boot Admin | Spring Boot Monitoring | 3.5.x |
| Jackson | JSON Library | 2.17.0 |
| MapStruct | Java Bean Mapping | 1.6.3 |
| Lombok | Reduce Boilerplate Code | 1.18.34 |
| JUnit | Java Unit Testing | 5.10.1 |
| Mockito | Java Mock Framework | 5.7.0 |

#### Spring Cloud (Microservices Architecture)

| Framework | Description | Version |
|-----------|-------------|---------|
| Java | Programming Language | 21 (LTS) |
| Spring Cloud | Microservices Framework | 2024.0.0 |
| Spring Cloud Alibaba | Cloud Integration | 2024.0.1.0 |
| Spring Boot | Application Development Framework | 3.5.x |
| Eureka | Configuration & Service Discovery |  |
| RocketMQ | Message Queue | 5.2.0+ |
| Resilience4j | Service Protection | 2.2.0 |
| XXL Job | Distributed Task Scheduling | 2.3.1 |
| Spring Cloud Gateway | Service Gateway | 4.1.x |
| Spring Cloud Stream | Message Stream Binder | 2024.0.0 |
| Spring DTX | Distributed Transaction | 2.0.0 |
| MySQL | Database Server | 5.7 / 8.0+ |
| HikariCP | JDBC Connection Pool & Monitoring | 5.1.0 |
| Dynamic Datasource | Dynamic Data Source | 4.4.0 |
| Redis | Key-Value Database | 7.0+ |
| Redisson | Redis Client | 3.35.0 |
| Spring MVC | MVC Framework | 6.1.x |
| Spring Security | Spring Security Framework | 6.3.x |
| Hibernate Validator | Parameter Validation | 8.0.1 |
| Flowable | Workflow Engine | 6.9.0 |
| Knife4j | Enhanced Swagger UI | 4.5.0 |
| SkyWalking | Distributed Tracing | 9.0.0 |
| Spring Boot Admin | Spring Boot Monitoring | 3.5.x |
| Jackson | JSON Library | 2.17.0 |
| MapStruct | Java Bean Mapping | 1.6.3 |
| Lombok | Reduce Boilerplate Code | 1.18.34 |
| JUnit | Java Unit Testing | 5.10.1 |
| Mockito | Java Mock Framework | 5.7.0 |

### Frontend

- **Management System:** Angular
- **User App:** ReactNative

---