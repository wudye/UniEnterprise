# System Architecture Design Document

## Overview

This document describes the overall system architecture design for the UniEnterprise project, including architecture diagrams, layered design, module division, deployment architecture, and data flow.

---

## Architecture Overview

### Overall Architecture Diagram

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
│                 API Gateway Layer                       │
│              (Nginx / Spring Gateway)                   │
└────────────────────────────────┬────────────────────────┘
                                 │
┌────────────────────────────────┼────────────────────────┐
│                    Application Layer                   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   System     │  │   Tenant     │  │     BPM      │  │
│  │  Module      │  │   Module     │  │   Module     │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   Payment    │  │    Mall      │  │     CRM      │  │
│  │  Module      │  │   Module     │  │   Module     │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
                                 │
┌────────────────────────────────┼────────────────────────┐
│                  Data Layer                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │    MySQL     │  │    Redis     │  │  RabbitMQ    │  │
│  │   Database   │  │    Cache     │  │  Message Queue│  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
```

---

## Layered Architecture Design

### Presentation Layer

**Responsibilities:**
- User interface rendering
- User interaction handling
- Client-side validation
- API communication

**Components:**
- Vue 3 / React Admin Frontend
- Uni-app Mobile Frontend
- WeChat Mini Program

**Technology Stack:**
- Vue 3 + TypeScript
- Element Plus / Ant Design Vue
- Vite Build Tool
- Pinia State Management

### API Gateway Layer

**Responsibilities:**
- Request routing
- Authentication & authorization
- Rate limiting
- Request/response logging
- Load balancing

**Technology Stack:**
- Nginx (Reverse Proxy)
- Spring Cloud Gateway (Microservices)

**Features:**
- Path-based routing
- Header-based routing
- Circuit breaker
- Retry mechanism

### Application Layer

**Responsibilities:**
- Business logic implementation
- Transaction management
- Service orchestration
- Data validation

**Technology Stack:**
- Spring Boot 3.x
- Spring Security
- MyBatis Plus
- Flowable (Workflow)

### Data Access Layer

**Responsibilities:**
- Database operations
- Caching management
- Data persistence
- Transaction handling

**Technology Stack:**
- MyBatis Plus
- HikariCP (Connection Pool)
- Redis (Cache)

### Data Storage Layer

**Responsibilities:**
- Data persistence
- Data backup
- Data replication

**Technology Stack:**
- MySQL 8.0 (Primary Database)
- Redis 7.0 (Cache)
- RabbitMQ (Message Queue)
- MinIO (Object Storage)

---

## Module Division

### Core Modules

| Module | Description | Technology |
|--------|-------------|------------|
| uni-system | User, role, menu, department management | Spring Boot |
| uni-tenant | Multi-tenant management | Spring Boot |
| uni-auth | Authentication & authorization | Spring Security + JWT |
| uni-infra | Infrastructure services (file, code generation) | Spring Boot |

### Business Modules

| Module | Description | Technology |
|--------|-------------|------------|
| uni-bpm | Workflow process management | Flowable |
| uni-payment | Payment integration | Alipay + WeChat Pay SDK |
| uni-member | Member management | Spring Boot |
| uni-mall | E-commerce functionality | Spring Boot |
| uni-crm | Customer relationship management | Spring Boot |
| uni-erp | Enterprise resource planning | Spring Boot |
| uni-monitor | Data monitoring & reports | Spring Boot |

---

## Deployment Architecture

### Development Environment

```
┌─────────────────────────────────────────────────┐
│              Developer's Machine                │
│  ┌──────────────────────────────────────────┐ │
│  │  IDE (IntelliJ IDEA / VSCode)           │ │
│  │  - Backend Code                          │ │
│  │  - Frontend Code                         │ │
│  └──────────────────────────────────────────┘ │
│  ┌──────────────────────────────────────────┐ │
│  │  Docker Compose                          │ │
│  │  - MySQL                                 │ │
│  │  - Redis                                 │ │
│  │  - RabbitMQ                              │ │
│  └──────────────────────────────────────────┘ │
└─────────────────────────────────────────────────┘
```

### Test Environment

```
┌─────────────────────────────────────────────────┐
│              Test Server                         │
│  ┌──────────────────────────────────────────┐ │
│  │  Nginx (Reverse Proxy)                  │ │
│  └──────────────────────────────────────────┘ │
│  ┌──────────────────────────────────────────┐ │
│  │  Application Server                      │ │
│  │  - Backend Services (Docker)             │ │
│  │  - Frontend (Nginx)                      │ │
│  └──────────────────────────────────────────┘ │
│  ┌──────────────────────────────────────────┐ │
│  │  Database Server                        │ │
│  │  - MySQL (Docker)                       │ │
│  │  - Redis (Docker)                       │ │
│  │  - RabbitMQ (Docker)                    │ │
│  └──────────────────────────────────────────┘ │
└─────────────────────────────────────────────────┘
```

### Production Environment

```
┌─────────────────────────────────────────────────────────┐
│                   Load Balancer                        │
│                    (Nginx / HAProxy)                   │
└─────────────────────────────────────────────────────────┘
                                 │
┌────────────────────────────────┼────────────────────────┐
│               Application Cluster                      │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐              │
│  │ App Node1│  │ App Node2│  │ App Node3│              │
│  └──────────┘  └──────────┘  └──────────┘              │
└─────────────────────────────────────────────────────────┘
                                 │
┌────────────────────────────────┼────────────────────────┐
│                  Database Cluster                      │
│  ┌──────────┐  ┌──────────┐                          │
│  │  Master  │  │  Slave1  │                          │
│  │  MySQL   │  │  MySQL   │                          │
│  └──────────┘  └──────────┘                          │
└─────────────────────────────────────────────────────────┘
                                 │
┌────────────────────────────────┼────────────────────────┐
│                  Infrastructure                       │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐            │
│  │  Redis   │  │RabbitMQ  │  │  MinIO   │            │
│  │  Cluster │  │ Cluster  │  │  Cluster │            │
│  └──────────┘  └──────────┘  └──────────┘            │
└─────────────────────────────────────────────────────────┘
```

---

## Data Flow Diagram

### User Authentication Flow

```
┌──────────┐     1. Login Request      ┌──────────────┐
│  Client  │ ─────────────────────────> │ API Gateway  │
└──────────┘                           └──────┬───────┘
                                              │
                                     2. Route to Auth Service
                                              │
                                              ▼
                                    ┌──────────────────┐
                                    │   Auth Service   │
                                    └────────┬─────────┘
                                             │
                              3. Validate Credentials
                                             │
                                             ▼
                                    ┌──────────────────┐
                                    │    Database      │
                                    └────────┬─────────┘
                                             │
                                    4. Return User Data
                                             │
                                             ▼
                                    ┌──────────────────┐
                                    │   Auth Service   │
                                    └────────┬─────────┘
                                             │
                              5. Generate JWT Token
                                             │
                                             ▼
                                    ┌──────────────┐
                                    │   Client     │
                                    └──────────────┘
```

### Order Creation Flow

```
┌──────────┐     1. Create Order     ┌──────────────┐
│  Client  │ ──────────────────────> │ API Gateway  │
└──────────┘                           └──────┬───────┘
                                              │
                                     2. Route to Mall Service
                                              │
                                              ▼
                                    ┌──────────────────┐
                                    │   Mall Service   │
                                    └────────┬─────────┘
                                             │
                              3. Validate Order Data
                                             │
                                              ▼
                                    ┌──────────────────┐
                                    │    Database      │
                                    └────────┬─────────┘
                                             │
                                    4. Create Order Record
                                             │
                                              ▼
                                    ┌──────────────────┐
                                    │   Mall Service   │
                                    └────────┬─────────┘
                                             │
                              5. Publish Order Event
                                             │
                                              ▼
                                    ┌──────────────────┐
                                    │   RabbitMQ       │
                                    └────────┬─────────┘
                                             │
                              6. Consume Order Event
                                             │
                                              ▼
                                    ┌──────────────────┐
                                    │  BPM Service     │
                                    │ (Start Approval) │
                                    └──────────────────┘
```

---

## Technology Stack Summary

### Backend Technology Stack

| Category | Technology | Version | Description |
|----------|------------|---------|-------------|
| Language | Java | 17 | Programming language |
| Framework | Spring Boot | 3.2.x | Application framework |
| Framework | Spring Cloud | 2023.x | Microservices framework |
| Security | Spring Security | 6.x | Security framework |
| ORM | MyBatis Plus | 3.5.x | ORM framework |
| Workflow | Flowable | 7.0.x | Workflow engine |
| Database | MySQL | 8.0 | Relational database |
| Cache | Redis | 7.0 | In-memory cache |
| Message Queue | RabbitMQ | 3.12.x | Message broker |

### Frontend Technology Stack

| Category | Technology | Version | Description |
|----------|------------|---------|-------------|
| Framework | Vue.js | 3.4.x | Frontend framework |
| Language | TypeScript | 5.x | Programming language |
| UI Library | Element Plus | 2.5.x | Component library |
| Build Tool | Vite | 5.x | Build tool |
| State Management | Pinia | 2.x | State management |
| HTTP Client | Axios | 1.x | HTTP client |

---

## Performance Considerations

### Scalability

- Horizontal scaling of application servers
- Database read-write splitting
- Redis clustering for cache
- CDN for static resources

### High Availability

- Load balancing
- Service redundancy
- Database master-slave replication
- Automatic failover

### Performance Optimization

- Database indexing
- Query optimization
- Caching strategy
- Connection pooling

---

## Security Considerations

### Authentication

- JWT token-based authentication
- OAuth 2.0 integration
- Multi-factor authentication support

### Authorization

- Role-based access control (RBAC)
- Data-level permission control
- API permission management

### Data Security

- Data encryption at rest
- Data encryption in transit
- Sensitive data masking

---

## Version History

| Version | Date | Author | Changes |
|---------|-------|---------|----------|
| 1.0.0 | 2025-01-11 | Initial system architecture design |
