# Multi-Tenant Data Isolation Solution

## Overview

This document describes the multi-tenant data isolation strategy for the UniEnterprise project, including isolation approaches, tenant field design, data security, and migration solutions.

---

## Isolation Strategy

### Isolation Approaches Comparison

| Approach | Description | Advantages | Disadvantages | Use Case |
|----------|-------------|------------|--------------|----------|
| **Database-per-Tenant** | Each tenant has a separate database | Complete isolation, easy backup, easy scaling | High cost, complex management | Large enterprises, high security |
| **Schema-per-Tenant** | Each tenant has a separate schema in the same database | Good isolation, shared resources | Limited scaling, complex backups | Medium enterprises |
| **Row-Level (Shared)** | All tenants share the same database and schema, data isolated by tenant_id | Low cost, easy management, simple scaling | Requires app-level isolation | SMB, startups |

### Recommended Approach: Row-Level Isolation

**Selection Reasons:**
- Lower infrastructure cost
- Easier to manage and maintain
- Faster development time
- Suitable for most SMB scenarios
- Can migrate to database-per-tenant if needed

---

## Tenant Field Design

### Standard Tenant Table Schema

```sql
CREATE TABLE `sys_tenant` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
  `tenant_code` VARCHAR(50) NOT NULL COMMENT 'Tenant code',
  `tenant_name` VARCHAR(100) NOT NULL COMMENT 'Tenant name',
  `contact_name` VARCHAR(50) COMMENT 'Contact person',
  `contact_phone` VARCHAR(20) COMMENT 'Contact phone',
  `contact_email` VARCHAR(100) COMMENT 'Contact email',
  `logo` VARCHAR(255) COMMENT 'Tenant logo URL',
  `status` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Status (0=disabled, 1=enabled)',
  `expire_time` DATETIME COMMENT 'Expire time',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT 'Deleted (0=no, 1=yes)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
  `create_by` BIGINT(20) COMMENT 'Creator ID',
  `update_by` BIGINT(20) COMMENT 'Updater ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_code` (`tenant_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Tenant information table';
```

### Tenant Column in Business Tables

All business tables must include a `tenant_id` column for data isolation:

```sql
CREATE TABLE `sys_user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `tenant_id` BIGINT(20) NOT NULL DEFAULT '0' COMMENT 'Tenant ID',
  `username` VARCHAR(50) NOT NULL,
  -- ... other columns
  KEY `idx_tenant_id` (`tenant_id`),
  UNIQUE KEY `uk_tenant_username` (`tenant_id`, `username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### Tables Without Tenant Isolation

The following tables do not require tenant isolation:

- **System configuration tables**: Shared across all tenants
- **Dictionary tables**: Common reference data
- **Code generation tables**: Infrastructure data
- **Template tables**: Shared templates

---

## Data Isolation Implementation

### MyBatis Plus Tenant Plugin

```java
@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // Add tenant line interceptor
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(
            new TenantLineHandler() {
                @Override
                public Long getTenantId() {
                    // Get tenant ID from current context
                    return TenantContextHolder.getTenantId();
                }

                @Override
                public String getTenantIdColumn() {
                    return "tenant_id";
                }

                @Override
                public boolean ignoreTable(String tableName) {
                    // Ignore tables that don't need tenant isolation
                    Set<String> ignoreTables = new HashSet<>();
                    ignoreTables.add("sys_tenant");
                    ignoreTables.add("sys_dict");
                    ignoreTables.add("sys_dict_item");
                    return ignoreTables.contains(tableName);
                }
            }
        ));
        return interceptor;
    }
}
```

### Tenant Context Holder

```java
public class TenantContextHolder {

    private static final ThreadLocal<Long> TENANT_ID = new ThreadLocal<>();

    public static void setTenantId(Long tenantId) {
        TENANT_ID.set(tenantId);
    }

    public static Long getTenantId() {
        return TENANT_ID.get();
    }

    public static void clear() {
        TENANT_ID.remove();
    }
}
```

### Tenant Interceptor

```java
@Component
public class TenantInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler) throws Exception {
        // Extract tenant ID from token or request header
        String tenantId = request.getHeader("X-Tenant-Id");

        if (StringUtils.isNotBlank(tenantId)) {
            TenantContextHolder.setTenantId(Long.parseLong(tenantId));
        } else {
            // Get tenant ID from JWT token
            String token = request.getHeader("Authorization");
            if (StringUtils.isNotBlank(token)) {
                Long tenantIdFromToken = JwtUtil.getTenantIdFromToken(token);
                TenantContextHolder.setTenantId(tenantIdFromToken);
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                               HttpServletResponse response,
                               Object handler,
                               Exception ex) {
        TenantContextHolder.clear();
    }
}
```

---

## Data Security

### Tenant Isolation at Application Level

1. **Query Isolation**: All queries automatically filter by `tenant_id`
2. **Insert Isolation**: `tenant_id` automatically set on insert
3. **Update Isolation**: `tenant_id` automatically included in update conditions
4. **Delete Isolation**: `tenant_id` automatically included in delete conditions

### Tenant Isolation at API Level

```java
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @GetMapping("/{id}")
    public Result<UserVO> getUser(@PathVariable Long id) {
        // Automatically filtered by tenant_id
        User user = userService.getById(id);
        return Result.success(BeanUtil.copyProperties(user, UserVO.class));
    }

    @PostMapping
    public Result<Long> createUser(@RequestBody UserDTO userDTO) {
        // tenant_id automatically set
        Long userId = userService.createUser(userDTO);
        return Result.success(userId);
    }
}
```

### Cross-Tenant Access Prevention

```java
@Service
public class UserServiceImpl {

    public User getUserById(Long userId) {
        User user = baseMapper.selectById(userId);

        // Verify tenant ID matches
        if (user != null &&
            !user.getTenantId().equals(TenantContextHolder.getTenantId())) {
            throw new BusinessException("Access denied: Cross-tenant access");
        }

        return user;
    }
}
```

---

## Tenant Configuration

### Tenant-Specific Configuration

Each tenant can have its own configuration:

```sql
CREATE TABLE `sys_tenant_config` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `tenant_id` BIGINT(20) NOT NULL COMMENT 'Tenant ID',
  `config_key` VARCHAR(100) NOT NULL COMMENT 'Configuration key',
  `config_value` TEXT COMMENT 'Configuration value',
  `config_type` VARCHAR(20) COMMENT 'Configuration type (string/json/boolean)',
  `description` VARCHAR(255) COMMENT 'Description',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_key` (`tenant_id`, `config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### Tenant Configuration Loading

```java
@Service
public class TenantConfigService {

    @Autowired
    private TenantConfigMapper tenantConfigMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public String getConfigValue(String configKey) {
        Long tenantId = TenantContextHolder.getTenantId();
        String cacheKey = "tenant:config:" + tenantId + ":" + configKey;

        // Try cache first
        String value = redisTemplate.opsForValue().get(cacheKey);
        if (StringUtils.isNotBlank(value)) {
            return value;
        }

        // Load from database
        TenantConfig config = tenantConfigMapper.selectByTenantIdAndKey(
            tenantId, configKey
        );
        if (config != null) {
            redisTemplate.opsForValue().set(cacheKey, config.getConfigValue(), 3600);
            return config.getConfigValue();
        }

        return null;
    }
}
```

---

## Tenant Migration

### Migration from Single-Tenant to Multi-Tenant

**Step 1: Add tenant_id column to existing tables**

```sql
ALTER TABLE sys_user ADD COLUMN tenant_id BIGINT(20) NOT NULL DEFAULT 1;
ALTER TABLE sys_role ADD COLUMN tenant_id BIGINT(20) NOT NULL DEFAULT 1;
ALTER TABLE sys_menu ADD COLUMN tenant_id BIGINT(20) NOT NULL DEFAULT 0; -- 0 = shared
-- ... add to all other tables
```

**Step 2: Create indexes for tenant_id**

```sql
CREATE INDEX idx_tenant_id ON sys_user(tenant_id);
CREATE INDEX idx_tenant_id ON sys_role(tenant_id);
-- ... create for all other tables
```

**Step 3: Update unique constraints to include tenant_id**

```sql
ALTER TABLE sys_user DROP INDEX uk_username;
ALTER TABLE sys_user ADD UNIQUE KEY uk_tenant_username (tenant_id, username);
```

**Step 4: Update application code to handle tenant isolation**

- Enable MyBatis Plus tenant plugin
- Update all queries to filter by tenant_id
- Add tenant context holder
- Add tenant interceptor

**Step 5: Test migration**

```sql
-- Verify data integrity
SELECT COUNT(*) FROM sys_user WHERE tenant_id = 0;
SELECT COUNT(*) FROM sys_user WHERE tenant_id = 1;

-- Verify cross-tenant access prevention
-- ... run security tests
```

---

## Tenant Backup and Restore

### Tenant-Specific Backup

```bash
# Backup single tenant data
mysqldump -u root -p uni_enterprise \
  --where="tenant_id=123" \
  sys_user > tenant_123_users.sql

mysqldump -u root -p uni_enterprise \
  --where="tenant_id=123" \
  sys_role > tenant_123_roles.sql
```

### Tenant Restore

```bash
# Restore single tenant data
mysql -u root -p uni_enterprise < tenant_123_users.sql
mysql -u root -p uni_enterprise < tenant_123_roles.sql
```

---

## Version History

| Version | Date | Author | Changes |
|---------|-------|---------|----------|
| 1.0.0 | 2025-01-11 | Initial multi-tenant isolation solution |
