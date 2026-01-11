# UniEnterprise

> A comprehensive enterprise-level development template featuring monolithic and microservices architectures.

The project can be used for e-commerce malls, SCRM systems, OA systems, logistics systems, ERP systems, CMS systems, HIS systems, payment systems, IM chat, social media integrations (Facebook, Instagram, Twitter/X, LinkedIn, TikTok), and more.

---

## Requirements
1. All features are guaranteed high quality through unit testing.
2. Clean code and clean architecture, following the coding standards of "Effective Java" and "Clean Code".

---

## Key Features
✅ Multi-JDK Support: Master branch (JDK 8 + Spring Boot 2.7), master-jdk17 branch (JDK 17/21 + Spring Boot 3.2)
✅ Multiple Frontend Versions: Vue3 with element-plus or vben (ant-design-vue), Vue2 with element-ui
✅ Mobile Admin: Uni-app solution supporting APP, Mini Program, and H5 from a single codebase
✅ Multi-Database Support: MySQL, Oracle, PostgreSQL, SQL Server, MariaDB, Dameng DM, TiDB, etc.
✅ Flexible Message Queue: Event, Redis, RabbitMQ, Kafka, RocketMQ, etc.
✅ Advanced Authentication: Spring Security + Token + Redis, supports multi-terminal, multi-user, and SSO
✅ Dynamic Permission Control: Dynamic menu loading, button-level permissions, Redis caching for performance
✅ Multi-Tenant SaaS: Customizable permissions per tenant with transparent multi-tenant encapsulation
✅ Flowable Workflow: Dynamic forms, online process design, countersignature/or-sign, multiple task allocation methods
✅ Code Generator: One-click generation of Java, Vue, SQL scripts, API docs; supports single table, tree table, master-detail tables
✅ Real-time Communication: Spring WebSocket with built-in token authentication and WebSocket cluster support
✅ Third-Party Login: Facebook, Google, LinkedIn, Twitter/X, GitHub, etc.
✅ Payment Integration: Stripe, PayPal, Braintree, Square with payment and refund support
✅ SMS & Cloud Storage: Aliyun, Tencent Cloud SMS; MinIO, Aliyun, Tencent Cloud, Qiniu Cloud storage
✅ Report Design: Report designer and dashboard designer for creating stunning reports via drag-and-drop
✅ Built-in Business Modules: Mall, OA, ERP, CRM, CMS, BBS, AI Agent
✅ Operation Logs & Audit Logs

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
| one-common                                            | Common base module that defines base POJO classes, enumerations, exception system, and utility classes. Provides unified API response formats (CommonResult, PageResult), pagination parameters (PageParam), global error code specifications, business exception handling, and common utility class collections (cache, collections, date, JSON, Spring, Servlet, etc.). Supports SkyWalking distributed tracing and data translation functionality. |
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

Common Modules (Required): System Functions, Infrastructure
Common Modules (Optional): Workflow Engine, Payment System, Data Reports, Member Center
Business Systems (On-Demand): ERP System, CRM System, Mall System, Social Media Integration, AI Agent
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
│  │         Visualization                                         │ │
│  └─────────────────────────────────────────────────────────────────|
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
│  │              Operate Log                           │             │
│  └────────────────────────────────────────────────────┘             │
└─────────────────────────────────────────────────────────────────────┘

---

## 🔗 Tech Stack

### Backend

#### Spring Boot (Monolithic Architecture)

| Framework | Description | Version |
|-----------|-------------|---------|
| Spring Boot | Application Development Framework | 2.7.18 |
| MySQL | Database Server | 5.7 / 8.0+ |
| Druid | JDBC Connection Pool & Monitoring | 1.2.23 |
| MyBatis Plus | MyBatis Enhancement Toolkit | 3.5.7 |
| Dynamic Datasource | Dynamic Data Source | 3.6.1 |
| Redis | Key-Value Database | 5.0 / 6.0 / 7.0 |
| Redisson | Redis Client | 3.32.0 |
| Spring MVC | MVC Framework | 5.3.24 |
| Spring Security | Spring Security Framework | 5.7.11 |
| Hibernate Validator | Parameter Validation | 6.2.5 |
| Flowable | Workflow Engine | 6.8.0 |
| Quartz | Task Scheduling | 2.3.2 |
| Springdoc | Swagger Documentation | 1.7.0 |
| SkyWalking | Distributed Tracing | 8.12.0 |
| Spring Boot Admin | Spring Boot Monitoring | 2.7.10 |
| Jackson | JSON Library | 2.13.5 |
| MapStruct | Java Bean Mapping | 1.6.3 |
| Lombok | Reduce Boilerplate Code | 1.18.34 |
| JUnit | Java Unit Testing | 5.8.2 |
| Mockito | Java Mock Framework | 4.8.0 |

#### Spring Cloud Alibaba (Microservices Architecture)

| Framework | Description | Version |
|-----------|-------------|---------|
| Spring Cloud | Microservices Framework | 2024.0.0 |
| Eureka | Configuration Center & Service Registry | 2.3.2 |
| RocketMQ | Message Queue | 5.2.0 |
| Resilience4j | Service Protection | 1.8.6 |
| XXL Job | Distributed Task Scheduling | 2.3.1 |
| Spring Cloud Gateway | Service Gateway | 3.4.1 |
| Spring cloud Stream ? | Distributed Transaction | 1.6.1 |
| MySQL | Database Server | 5.7 / 8.0+ |
| HikariCP | JDBC Connection Pool & Monitoring | 1.2.23 |
| Dynamic Datasource | Dynamic Data Source | 4.3.1 |
| Redis | Key-Value Database | 5.0 / 6.0 |
| Redisson | Redis Client | 3.32.0 |
| Spring MVC | MVC Framework | 5.3.24 |
| Spring Security | Spring Security Framework | 5.7.5 |
| Hibernate Validator | Parameter Validation | 6.2.5 |
| Flowable | Workflow Engine | 6.8.0 |
| Knife4j | Enhanced Swagger UI | 4.5.0 |
| SkyWalking | Distributed Tracing | 8.12.0 |
| Spring Boot Admin | Spring Boot Monitoring | 2.7.10 |
| Jackson | JSON Library | 2.13.3 |
| MapStruct | Java Bean Mapping | 1.6.3 |
| Lombok | Reduce Boilerplate Code | 1.18.34 |
| JUnit | Java Unit Testing | 5.8.2 |
| Mockito | Java Mock Framework | 4.8.0 |

### Frontend

- **Management System:** Angular
- **User App:** ReactNative

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