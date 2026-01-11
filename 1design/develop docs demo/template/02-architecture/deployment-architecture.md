# Deployment Architecture Design

## Overview

This document describes the deployment architecture for UniEnterprise project, including infrastructure design, network topology, and deployment strategies for different environments.

---

## Deployment Environments

### Environment Overview

| Environment | Purpose | URL | Database | Remarks |
|-------------|---------|-----|----------|---------|
| Development | Local development | http://localhost:8080 | Local MySQL | Developer's machine |
| Testing | Integration testing | https://test.enterprise.com | Test MySQL | Shared team |
| Staging | Pre-production | https://staging.enterprise.com | Staging MySQL | Mirror of production |
| Production | Live environment | https://enterprise.com | Production MySQL | High availability |

---

## Infrastructure Architecture

### Production Infrastructure

```
┌─────────────────────────────────────────────────────────────┐
│                      CDN (CloudFlare)                     │
│              (Static Assets, DDoS Protection)             │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                  Load Balancer (Nginx)                  │
│                SSL Termination, HTTP/2                     │
└───────────────────────────┬─────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        │                   │                   │
        ▼                   ▼                   ▼
┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│ App Node 1   │   │ App Node 2   │   │ App Node 3   │
│ (Backend)    │   │ (Backend)    │   │ (Backend)    │
│ - Spring Boot │   │ - Spring Boot │   │ - Spring Boot │
│ - Port: 8080 │   │ - Port: 8080 │   │ - Port: 8080 │
└──────┬───────┘   └──────┬───────┘   └──────┬───────┘
       │                   │                   │
       └───────────────────┼───────────────────┘
                           │
        ┌──────────────────┼──────────────────┐
        ▼                  ▼                  ▼
┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│ Redis Cluster │   │ RabbitMQ     │   │ MinIO        │
│ (Cache)      │   │ (Message)    │   │ (Storage)    │
│ - Master     │   │ - Cluster    │   │ - Distributed│
│ - 2 Slaves   │   │ - High Avail │   │ - Replicated │
└──────────────┘   └──────────────┘   └──────────────┘
        │
        ▼
┌─────────────────────────────────────────────────────────────┐
│                MySQL Database Cluster                     │
│  ┌──────────┐         ┌──────────┐                    │
│  │  Master  │ ←────→  │ Slave 1  │                    │
│  │ (Write)  │         │ (Read)   │                    │
│  └──────────┘         └──────────┘                    │
│         │                    │                            │
│         └────────────────────┼─────────────┐             │
│                              │          │             │
│                         ┌──────▼─────────▼──────┐   │
│                         │      Slave 2 (Read)    │   │
│                         └─────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

---

## Network Architecture

### Network Segmentation

```
Internet
    │
    ▼
┌─────────────────────────────────────────┐
│        DMZ Network (Public)          │
│  - Load Balancer                     │
│  - Web Servers                      │
│  - Firewall (External)               │
└───────────────┬─────────────────────┘
                │
                ▼
┌─────────────────────────────────────────┐
│      Application Network (Private)     │
│  - Application Servers               │
│  - Redis Cluster                   │
│  - RabbitMQ Cluster                │
│  - Firewall (Internal)              │
└───────────────┬─────────────────────┘
                │
                ▼
┌─────────────────────────────────────────┐
│       Database Network (Private)      │
│  - MySQL Master/Slaves             │
│  - Backup Server                   │
│  - Firewall (Database)              │
└───────────────────────────────────────┘
```

### Security Groups

| Network | Inbound | Outbound | Description |
|---------|----------|----------|-------------|
| DMZ | 80, 443 from 0.0.0.0/0 | 8080 to App, 3306 to DB | Public access |
| Application | 8080 from LB | 6379 to Redis, 5672 to RabbitMQ, 3306 to DB | App servers |
| Database | 3306 from App | - | Database servers |

---

## Deployment Strategies

### Blue-Green Deployment

```
    ┌─────────┐
    │  Load   │
    │ Balancer│
    └────┬────┘
         │
    ┌────┴────┐
    ▼         ▼
 ┌──────┐ ┌──────┐
 │ Blue │ │Green │
 │  v1  │ │ v2   │
 └──────┘ └──────┘
```

**Process:**
1. Deploy new version to Green environment
2. Run smoke tests on Green
3. Switch traffic from Blue to Green
4. Monitor Green for issues
5. Keep Blue for rollback

### Rolling Deployment

```
App Node 1  →  Update to v2  →  ✓ Verified
App Node 2  →  Update to v2  →  ✓ Verified
App Node 3  →  Update to v2  →  ✓ Verified
```

**Process:**
1. Update App Node 1
2. Verify Node 1 is healthy
3. Update App Node 2
4. Verify Node 2 is healthy
5. Update App Node 3
6. Verify Node 3 is healthy

---

## Container Orchestration

### Docker Compose (Development)

```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - DB_HOST=mysql
      - REDIS_HOST=redis
    depends_on:
      - mysql
      - redis

  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: uni_enterprise
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql

  redis:
    image: redis:7.0-alpine
    ports:
      - "6379:6379"

volumes:
  mysql-data:
```

### Kubernetes (Production)

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: uni-enterprise
  labels:
    app: uni-enterprise
spec:
  replicas: 3
  selector:
    matchLabels:
      app: uni-enterprise
  template:
    metadata:
      labels:
        app: uni-enterprise
    spec:
      containers:
      - name: app
        image: uni-enterprise:1.0.0
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: DB_HOST
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: db-host
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: uni-enterprise-service
spec:
  selector:
    app: uni-enterprise
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
  type: LoadBalancer
```

---

## Monitoring & Logging

### Monitoring Stack

```
┌─────────────────────────────────────────────────────┐
│              Prometheus (Metrics)                  │
│  - Collects metrics from all services             │
│  - Stores time-series data                        │
└───────────────────┬─────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────────────┐
│               Grafana (Visualization)              │
│  - Dashboards for metrics                        │
│  - Alerts and notifications                       │
└─────────────────────────────────────────────────────┘
```

### Logging Stack

```
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│ App Node 1   │  │ App Node 2   │  │ App Node 3   │
└──────┬───────┘  └──────┬───────┘  └──────┬───────┘
       │                  │                  │
       └──────────────────┼──────────────────┘
                          │
                          ▼
                  ┌──────────────┐
                  │ Filebeat     │
                  │ (Log Shipper)│
                  └──────┬───────┘
                         │
                         ▼
                  ┌──────────────┐
                  │  Logstash   │
                  │ (Log Parser)│
                  └──────┬───────┘
                         │
                         ▼
                  ┌──────────────┐
                  │ Elasticsearch│
                  │ (Log Storage)│
                  └──────┬───────┘
                         │
                         ▼
                  ┌──────────────┐
                  │  Kibana     │
                  │(Log Viewer) │
                  └──────────────┘
```

---

## Backup Strategy

### Database Backup

**Full Backup:** Daily at 2:00 AM

```bash
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
mysqldump -u root -p${MYSQL_PASSWORD} \
  --single-transaction \
  --routines \
  --triggers \
  uni_enterprise > /backup/mysql/uni_enterprise_${DATE}.sql

# Compress backup
gzip /backup/mysql/uni_enterprise_${DATE}.sql

# Upload to cloud storage
aws s3 cp /backup/mysql/uni_enterprise_${DATE}.sql.gz \
  s3://backup-bucket/mysql/
```

**Incremental Backup:** Every 6 hours

**Binlog Backup:** Real-time

**Retention:**
- Daily backups: 30 days
- Weekly backups: 12 weeks
- Monthly backups: 12 months

### File Backup

**MinIO Backup:**

```bash
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
rclone sync /data/minio/ \
  s3:backup-bucket/minio/${DATE}/ \
  --exclude "*.tmp"
```

---

## Disaster Recovery

### Recovery Time Objectives (RTO)

| System | RTO | RPO | Recovery Process |
|--------|-----|-----|----------------|
| Application | 1 hour | 15 minutes | Kubernetes rolling update |
| Database | 2 hours | 15 minutes | Restore from latest backup + binlog |
| Redis | 30 minutes | 5 minutes | Failover to slave |
| File Storage | 2 hours | 1 hour | Restore from backup |

### Failover Process

1. **Detect failure**
   - Health check failures
   - Alert triggered

2. **Verify failure**
   - Manual verification
   - Root cause analysis

3. **Initiate failover**
   - Switch traffic to backup
   - Scale up resources

4. **Verify recovery**
   - Smoke tests
   - Monitor metrics

5. **Post-mortem**
   - Document incident
   - Improve processes

---

## Version History

| Version | Date | Author | Changes |
|---------|-------|---------|----------|
| 1.0.0 | 2025-01-11 | Initial deployment architecture design |
