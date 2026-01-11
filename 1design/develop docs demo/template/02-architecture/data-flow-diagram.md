# Data Flow Diagram

## Overview

This document describes the data flow architecture for the UniEnterprise project, including request/response flows, data transformation, and integration patterns.

---

## System Data Flow Overview

```
┌─────────────────────────────────────────────────────────────┐
│                      Client Layer                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │  Admin Web   │  │  Mobile H5   │  │  Mini Program│  │
│  │  (Vue 3)    │  │  (Uni-app)   │  │ (WeChat)    │  │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  │
└─────────┼────────────────────┼────────────────────┼─────┘
          │                    │                    │
          └────────┬───────────┴────────┬───────────┘
                   │                    │
                   ▼                    ▼
              ┌──────────────────────────────────┐
              │      API Gateway Layer        │
              │  - Routing                   │
              │  - Authentication            │
              │  - Rate Limiting             │
              └──────────┬───────────────────┘
                         │
          ┌──────────────┼──────────────┐
          ▼              ▼              ▼
    ┌──────────┐   ┌──────────┐   ┌──────────┐
    │   App    │   │   App    │   │   App    │
    │ Service  │   │ Service  │   │ Service  │
    └────┬─────┘   └────┬─────┘   └────┬─────┘
         │              │              │
         └──────────────┼──────────────┘
                        │
        ┌───────────────┼───────────────┐
        ▼               ▼               ▼
   ┌──────────┐   ┌──────────┐   ┌──────────┐
   │   MySQL  │   │  Redis   │   │ RabbitMQ │
   │Database  │   │  Cache   │   │  Message │
   └──────────┘   └──────────┘   └──────────┘
```

---

## Request Flow: User Authentication

```
┌──────────┐
│  Client  │
└────┬─────┘
     │ 1. POST /api/v1/auth/login
     │    {username, password}
     ▼
┌──────────────────────────────────┐
│        API Gateway            │
│  - Route to Auth Service     │
│  - No authentication needed   │
└──────────┬───────────────────┘
           │
           ▼
┌──────────────────────────────────┐
│       Auth Service            │
│  1. Validate credentials     │
│  2. Query user from DB       │
│  3. Verify password hash     │
│  4. Generate JWT token      │
│  5. Cache token in Redis    │
└──────┬───────────────────────┘
       │
       ▼
┌──────────────────────────────────┐
│         MySQL                │
│  SELECT * FROM sys_user     │
│  WHERE username = ?         │
└──────────┬───────────────────┘
           │
           ▼
┌──────────────────────────────────┐
│         Redis                │
│  SET token:USER_ID value    │
│  EX 7200                   │
└──────────┬───────────────────┘
           │
           ▼
     ┌──────────┐
     │  Client  │
     │  2. Receive JWT token    │
     └──────────┘
```

---

## Request Flow: Order Creation

```
┌──────────┐
│  Client  │
└────┬─────┘
     │ 1. POST /api/v1/orders
     │    {products, amount, address}
     ▼
┌──────────────────────────────────┐
│        API Gateway            │
│  - Validate JWT token       │
│  - Check rate limit         │
│  - Route to Order Service   │
└──────────┬───────────────────┘
           │
           ▼
┌──────────────────────────────────┐
│      Order Service           │
│  1. Validate order data     │
│  2. Check inventory        │
│  3. Calculate total        │
│  4. Create order record    │
│  5. Publish order event   │
└──────┬───────────────────────┘
       │
       ├──► ┌──────────────────────────────────┐
       │    │         MySQL                │
       │    │  INSERT INTO mall_order     │
       │    └──────────────────────────────────┘
       │
       ├──► ┌──────────────────────────────────┐
       │    │        Redis                │
       │    │  DECR inventory:PROD_ID    │
       │    └──────────────────────────────────┘
       │
       └──► ┌──────────────────────────────────┐
            │       RabbitMQ             │
            │  Publish: order.created    │
            └──────────┬───────────────────┘
                       │
                       ▼
              ┌──────────────────────────────────┐
              │      Inventory Service      │
              │  Consume: order.created    │
              │  Update inventory count    │
              └──────────────────────────────────┘
```

---

## Request Flow: Payment Processing

```
┌──────────┐
│  Client  │
└────┬─────┘
     │ 1. POST /api/v1/orders/{id}/pay
     │    {paymentMethod}
     ▼
┌──────────────────────────────────┐
│        API Gateway            │
│  - Validate JWT token       │
│  - Route to Payment Service │
└──────────┬───────────────────┘
           │
           ▼
┌──────────────────────────────────┐
│     Payment Service          │
│  1. Create payment order   │
│  2. Generate payment URL    │
│  3. Redirect to payment    │
└──────┬───────────────────────┘
       │
       ├──► ┌──────────────────────────────────┐
       │    │         MySQL                │
       │    │  INSERT INTO pay_order     │
       │    └──────────────────────────────────┘
       │
       └──► ┌──────────────────────────────────┐
            │  Third-party Payment API  │
            │  (Alipay/WeChat Pay)   │
            │  ┌──────────────────────┐ │
            │  │  Redirect Client     │ │
            │  └──────────────────────┘ │
            │  ┌──────────────────────┐ │
            │  │  Payment Callback   │ │
            │  └──────────┬─────────┘ │
            └─────────────┼───────────┘
                          │
                          ▼
                 ┌──────────────────────────────────┐
                 │     Payment Service        │
                 │  1. Verify callback       │
                 │  2. Update order status  │
                 │  3. Publish payment event│
                 └──────────┬───────────────────┘
                            │
                            └──► ┌──────────────────────────────────┐
                                 │       RabbitMQ             │
                                 │  Publish: payment.paid   │
                                 └──────────────────────────────────┘
```

---

## Data Flow: Workflow Approval

```
┌──────────┐
│  Client  │
└────┬─────┘
     │ 1. POST /api/v1/workflows/start
     │    {processKey, businessKey, variables}
     ▼
┌──────────────────────────────────┐
│        API Gateway            │
│  - Validate JWT token       │
│  - Route to Workflow Service │
└──────────┬───────────────────┘
           │
           ▼
┌──────────────────────────────────┐
│     Workflow Service         │
│  1. Start process instance │
│  2. Create first task      │
│  3. Assign to user        │
└──────┬───────────────────────┘
       │
       ├──► ┌──────────────────────────────────┐
       │    │   Flowable Engine         │
       │    │  - Process instance      │
       │    │  - Task assignment       │
       │    └──────────────────────────────────┘
       │
       └──► ┌──────────────────────────────────┐
            │       RabbitMQ             │
            │  Publish: task.created    │
            └──────────┬───────────────────┘
                       │
                       ▼
              ┌──────────────────────────────────┐
              │   Notification Service    │
              │  Send task notification   │
              └──────────────────────────────────┘
```

---

## Caching Strategy

### Cache-Aside Pattern

```
┌──────────┐
│  Client  │
└────┬─────┘
     │ 1. GET /api/v1/users/{id}
     ▼
┌──────────────────────────────────┐
│     Application Layer       │
│  1. Check Redis cache     │
└──────────┬───────────────────┘
           │
     ┌─────┴─────┐
     ▼             ▼
  Cache Hit    Cache Miss
     │             │
     ▼             ▼
┌──────────┐   ┌──────────────────────────────────┐
│  Return  │   │         MySQL                │
│  Data    │   │  SELECT * FROM sys_user     │
└──────────┘   │  WHERE id = ?              │
               └──────────┬───────────────────┘
                          │ 2. Query data
                          │
                          ▼
               ┌──────────────────────────────────┐
               │         Redis                │
               │  SET user:{id} value       │
               │  EX 3600                   │
               └──────────┬───────────────────┘
                          │
                          │ 3. Return data
                          ▼
                     ┌──────────┐
                     │  Client  │
                     └──────────┘
```

### Cache Invalidation

```
┌──────────┐
│  Client  │
└────┬─────┘
     │ 1. PUT /api/v1/users/{id}
     ▼
┌──────────────────────────────────┐
│     Application Layer       │
│  1. Update MySQL data       │
│  2. Delete Redis cache      │
└──────┬───────────────────────┘
       │
       ├──► ┌──────────────────────────────────┐
       │    │         MySQL                │
       │    │  UPDATE sys_user           │
       │    │  SET ...                  │
       │    │  WHERE id = ?             │
       │    └──────────────────────────────────┘
       │
       └──► ┌──────────────────────────────────┐
            │         Redis                │
            │  DEL user:{id}             │
            └──────────────────────────────────┘
```

---

## Message Queue Integration

### Asynchronous Event Processing

```
┌──────────────────────────────────┐
│   Order Service               │
│  - Create order              │
│  - Publish order.created      │
└──────────┬───────────────────┘
           │
           ▼
┌──────────────────────────────────┐
│        RabbitMQ              │
│  Exchange: order.exchange     │
│  Queue: order.created.queue  │
└──────┬───────────────────────┘
       │
       ├──► ┌──────────────────────────────────┐
       │    │   Inventory Service        │
       │    │  - Deduct inventory       │
       │    │  - Update stock status   │
       │    └──────────────────────────────────┘
       │
       ├──► ┌──────────────────────────────────┐
       │    │   Notification Service    │
       │    │  - Send order confirm     │
       │    │  - Email/SMS             │
       │    └──────────────────────────────────┘
       │
       └──► ┌──────────────────────────────────┐
            │   Workflow Service        │
            │  - Start approval process  │
            └──────────────────────────────────┘
```

---

## Data Synchronization

### Database Replication

```
                    Write Operation
                         │
                         ▼
┌──────────────────────────────────┐
│        MySQL Master          │
│  - Accept writes             │
│  - Binlog enabled          │
└──────────┬───────────────────┘
           │
           │ Binlog Stream
           ▼
┌──────────────────────────────────┐
│      MySQL Slave 1           │
│  - Replicate from master     │
│  - Handle read queries      │
└──────────────────────────────────┘

┌──────────────────────────────────┐
│      MySQL Slave 2           │
│  - Replicate from master     │
│  - Handle read queries      │
└──────────────────────────────────┘
```

---

## Version History

| Version | Date | Author | Changes |
|---------|-------|---------|----------|
| 1.0.0 | 2025-01-11 | Initial data flow diagram |
