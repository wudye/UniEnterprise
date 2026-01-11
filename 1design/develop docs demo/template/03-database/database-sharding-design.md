# Database Sharding Design Document

## Overview

This document describes the database sharding strategy for the UniEnterprise project, including sharding approaches, sharding key selection, routing rules, and data migration solutions.

---

## Sharding Strategy

### When to Shard

**Indicators for sharding:**
- Single database table > 50 million rows
- Single database size > 200 GB
- Query performance degraded
- High write concurrency > 10,000 TPS

### Sharding Approaches

| Approach | Description | Advantages | Disadvantages |
|----------|-------------|------------|--------------|
| **Vertical Sharding** | Split tables by module | Simple, reduces I/O | Limited by single table size |
| **Horizontal Sharding** | Split tables by data rows | Better performance, scalability | Complex routing, joins |
| **Hybrid Sharding** | Combine vertical and horizontal | Best performance | Most complex |

### Recommended Strategy: Hybrid Sharding

1. **Vertical Sharding**: Split databases by business modules
   - System database: User, role, menu, tenant
   - Business database: Mall, CRM, ERP, BPM
   - Log database: Operation logs, audit logs

2. **Horizontal Sharding**: Split large tables by sharding key
   - Order table: Sharded by order_no hash
   - Transaction log: Sharded by date range

---

## Sharding Key Selection

### Sharding Key Principles

1. **High cardinality**: Key should have many distinct values
2. **Even distribution**: Data should be evenly distributed
3. **Query frequency**: Key should be frequently used in queries
4. **Business meaning**: Key should have business significance

### Sharding Key Examples

| Table | Sharding Key | Sharding Algorithm | Reason |
|-------|--------------|-------------------|--------|
| mall_order | order_no | Hash modulo 16 | High cardinality, even distribution |
| pay_order | order_no | Hash modulo 16 | High cardinality, joins with mall_order |
| sys_log | create_time | Date range (monthly) | Time-series data, easy archiving |
| mall_transaction_log | user_id | Hash modulo 8 | Query by user frequently |

---

## Sharding Configuration

### Apache ShardingSphere Configuration

```yaml
spring:
  shardingsphere:
    datasource:
      names: ds0,ds1,ds2,ds3
      ds0:
        type: com.zaxxer.hikari.HikariDataSource
        driver-class-name: com.mysql.cj.jdbc.Driver
        jdbc-url: jdbc:mysql://localhost:3306/uni_enterprise_0?useSSL=false&serverTimezone=UTC
        username: root
        password: password
      ds1:
        type: com.zaxxer.hikari.HikariDataSource
        driver-class-name: com.mysql.cj.jdbc.Driver
        jdbc-url: jdbc:mysql://localhost:3306/uni_enterprise_1?useSSL=false&serverTimezone=UTC
        username: root
        password: password
      # ... ds2, ds3 configuration
    rules:
      sharding:
        tables:
          mall_order:
            actual-data-nodes: ds$->{0..3}.mall_order_$->{0..3}
            database-strategy:
              standard:
                sharding-column: order_no
                sharding-algorithm-name: order_db_algorithm
            table-strategy:
              standard:
                sharding-column: order_no
                sharding-algorithm-name: order_table_algorithm
        sharding-algorithms:
          order_db_algorithm:
            type: MOD
            props:
              sharding-count: 4
          order_table_algorithm:
            type: MOD
            props:
              sharding-count: 16
```

### Custom Sharding Algorithm

```java
public class OrderShardingAlgorithm implements PreciseShardingAlgorithm<String> {

    @Override
    public String doSharding(Collection<String> availableTargetNames,
                            PreciseShardingValue<String> shardingValue) {
        String orderNo = shardingValue.getValue();
        int hashCode = orderNo.hashCode();
        int shardIndex = Math.abs(hashCode) % availableTargetNames.size();

        return availableTargetNames.stream()
            .sorted()
            .collect(Collectors.toList())
            .get(shardIndex);
    }
}
```

---

## Routing Rules

### Database Routing Rules

| Table | Routing Rule | Example |
|-------|--------------|---------|
| mall_order | `hash(order_no) % 4` | `hash("ORD20250111001") % 4 = 2` → ds2 |
| pay_order | `hash(order_no) % 4` | Same as mall_order |
| sys_log | `year(create_time) * 12 + month(create_time)` | Jan 2025 → 202501 |

### Table Routing Rules

| Table | Routing Rule | Example |
|-------|--------------|---------|
| mall_order | `hash(order_no) % 16` | `hash("ORD20250111001") % 16 = 7` → mall_order_7 |
| pay_order | `hash(order_no) % 16` | Same as mall_order |
| sys_log | `year(create_time) * 12 + month(create_time)` | Jan 2025 → sys_log_202501 |

---

## Data Migration

### Migration Steps

**Step 1: Create sharded databases and tables**

```sql
-- Create sharded databases
CREATE DATABASE uni_enterprise_0;
CREATE DATABASE uni_enterprise_1;
CREATE DATABASE uni_enterprise_2;
CREATE DATABASE uni_enterprise_3;

-- Create sharded tables in each database
CREATE TABLE uni_enterprise_0.mall_order_0 (...);
CREATE TABLE uni_enterprise_0.mall_order_1 (...);
-- ... create all sharded tables
```

**Step 2: Migrate data from single database to sharded databases**

```bash
# Export data from single database
mysqldump -u root -p uni_enterprise mall_order > mall_order_backup.sql

# Use data migration tool (e.g., DTS, Canal)
# Configure routing rules and start migration
```

**Step 3: Verify data integrity**

```sql
-- Count records in source
SELECT COUNT(*) FROM uni_enterprise.mall_order;

-- Count records in sharded databases
SELECT SUM(cnt) FROM (
    SELECT COUNT(*) AS cnt FROM uni_enterprise_0.mall_order_0
    UNION ALL
    SELECT COUNT(*) FROM uni_enterprise_0.mall_order_1
    -- ... all sharded tables
) AS total;
```

**Step 4: Switch application to sharded databases**

- Update ShardingSphere configuration
- Restart application
- Monitor performance

**Step 5: Clean up old data**

```sql
-- After verification, drop old tables
DROP TABLE uni_enterprise.mall_order;
```

---

## Cross-Shard Operations

### Cross-Shard Query

**Challenge:** Querying data across multiple shards

**Solution 1: Distributed Query Engine**

```java
@Service
public class OrderService {

    @Autowired
    private ShardingSphereDataSource dataSource;

    public List<Order> searchOrders(String keyword) {
        // ShardingSphere automatically queries all shards
        return orderMapper.searchOrders(keyword);
    }
}
```

**Solution 2: Application-Level Aggregation**

```java
public List<Order> getOrdersByUserId(Long userId) {
    List<Order> allOrders = new ArrayList<>();

    // Query each shard
    for (int i = 0; i < 16; i++) {
        List<Order> shardOrders = orderMapper.selectByUserIdAndShard(userId, i);
        allOrders.addAll(shardOrders);
    }

    return allOrders;
}
```

### Cross-Shard Transaction

**Challenge:** Maintaining ACID across shards

**Solution: Distributed Transaction (Seata)**

```java
@Service
public class OrderService {

    @GlobalTransactional(name = "create-order", rollbackFor = Exception.class)
    public void createOrder(OrderDTO orderDTO) {
        // Insert order to shard 0
        orderMapper.insert(orderDTO);

        // Insert payment to shard 0 (same shard)
        paymentMapper.insert(paymentDTO);

        // If any operation fails, all will be rolled back
    }
}
```

---

## Performance Optimization

### Read-Write Splitting

```yaml
spring:
  shardingsphere:
    rules:
      readwrite-splitting:
        data-sources:
          readwrite_ds:
            type: Static
            props:
              write-data-source-name: write_ds
              read-data-source-names: read_ds_0,read_ds_1
```

### Caching Strategy

```java
@Service
public class OrderService {

    @Autowired
    private RedisTemplate<String, Order> redisTemplate;

    public Order getOrder(String orderNo) {
        String cacheKey = "order:" + orderNo;

        // Try cache first
        Order order = redisTemplate.opsForValue().get(cacheKey);
        if (order != null) {
            return order;
        }

        // Load from database
        order = orderMapper.selectByOrderNo(orderNo);
        if (order != null) {
            redisTemplate.opsForValue().set(cacheKey, order, 3600);
        }

        return order;
    }
}
```

---

## Monitoring and Maintenance

### Sharding Metrics

| Metric | Description | Threshold |
|--------|-------------|-----------|
| Shard size | Data size per shard | < 50 GB |
| Shard row count | Row count per shard | < 50 million |
| Query latency | Average query time | < 100 ms |
| Cross-shard query rate | Cross-shard query percentage | < 5% |

### Maintenance Tasks

1. **Rebalance shards**: When data distribution is uneven
2. **Add shards**: When capacity is exceeded
3. **Archive old data**: Move old data to archive storage
4. **Optimize indexes**: Rebuild indexes periodically

---

## Version History

| Version | Date | Author | Changes |
|---------|-------|---------|----------|
| 1.0.0 | 2025-01-11 | Initial database sharding design |
