# Permission Management Solution

## Overview

This document describes the permission management system for the UniEnterprise project, including RBAC model implementation, data-level permissions, and permission configuration examples.

---

## RBAC Permission Model

### Model Overview

```
User ──< User-Role >── Role ──< Role-Menu >── Menu/Permission
        (Many-to-Many)        (Many-to-Many)
```

### Components

| Component | Description | Table |
|-----------|-------------|-------|
| **User** | System user | sys_user |
| **Role** | Collection of permissions | sys_role |
| **Menu/Permission** | UI element or operation permission | sys_menu |
| **User-Role** | Association between user and role | sys_user_role |
| **Role-Menu** | Association between role and menu | sys_role_menu |

---

## Database Schema

### sys_user (User Table)

```sql
CREATE TABLE `sys_user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `tenant_id` BIGINT(20) NOT NULL DEFAULT '0',
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  `real_name` VARCHAR(50),
  `email` VARCHAR(100),
  `phone` VARCHAR(20),
  `dept_id` BIGINT(20),
  `status` TINYINT(1) DEFAULT '1',
  `deleted` TINYINT(1) DEFAULT '0',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_username` (`tenant_id`, `username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### sys_role (Role Table)

```sql
CREATE TABLE `sys_role` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `tenant_id` BIGINT(20) NOT NULL DEFAULT '0',
  `role_code` VARCHAR(50) NOT NULL,
  `role_name` VARCHAR(50) NOT NULL,
  `description` VARCHAR(255),
  `status` TINYINT(1) DEFAULT '1',
  `deleted` TINYINT(1) DEFAULT '0',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_code` (`tenant_id`, `role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### sys_menu (Menu/Permission Table)

```sql
CREATE TABLE `sys_menu` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `parent_id` BIGINT(20) DEFAULT '0',
  `menu_name` VARCHAR(50) NOT NULL,
  `menu_type` VARCHAR(10) NOT NULL COMMENT 'directory/menu/button',
  `menu_code` VARCHAR(100) COMMENT 'Permission code',
  `path` VARCHAR(200),
  `component` VARCHAR(200),
  `icon` VARCHAR(50),
  `sort_order` INT(11) DEFAULT '0',
  `visible` TINYINT(1) DEFAULT '1',
  `status` TINYINT(1) DEFAULT '1',
  `deleted` TINYINT(1) DEFAULT '0',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_menu_code` (`menu_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### sys_user_role (User-Role Association)

```sql
CREATE TABLE `sys_user_role` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NOT NULL,
  `role_id` BIGINT(20) NOT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### sys_role_menu (Role-Menu Association)

```sql
CREATE TABLE `sys_role_menu` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `role_id` BIGINT(20) NOT NULL,
  `menu_id` BIGINT(20) NOT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## Permission Hierarchy Example

### Menu Structure

```
System Management (Directory)
├── User Management (Menu)
│   ├── User Query (Button) - permission: user:query
│   ├── User Create (Button) - permission: user:create
│   ├── User Update (Button) - permission: user:update
│   └── User Delete (Button) - permission: user:delete
├── Role Management (Menu)
│   ├── Role Query (Button) - permission: role:query
│   ├── Role Create (Button) - permission: role:create
│   ├── Role Update (Button) - permission: role:update
│   └── Role Delete (Button) - permission: role:delete
└── Menu Management (Menu)
    ├── Menu Query (Button) - permission: menu:query
    └── Menu Update (Button) - permission: menu:update
```

### Role Definitions

| Role Code | Role Name | Permissions |
|-----------|-----------|-------------|
| admin | Administrator | All permissions |
| user | Regular User | Basic permissions |
| viewer | Viewer | Read-only permissions |

---

## Permission Check Implementation

### Permission Annotation

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    String value() default "";
    String[] permissions() default {};
    Logical logical() default Logical.AND;

    public enum Logical {
        AND, OR
    }
}
```

### Permission Aspect

```java
@Aspect
@Component
public class PermissionAspect {

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint,
                                 RequirePermission requirePermission)
            throws Throwable {
        // Get current user
        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Get required permissions
        String[] permissions = requirePermission.permissions();
        Logical logical = requirePermission.logical();

        // Check if user has permissions
        boolean hasPermission = checkUserPermissions(username, permissions, logical);

        if (!hasPermission) {
            throw new AccessDeniedException("Access denied: Insufficient permissions");
        }

        return joinPoint.proceed();
    }

    private boolean checkUserPermissions(String username,
                                        String[] requiredPermissions,
                                        Logical logical) {
        // Try to get permissions from cache
        String cacheKey = "user:permissions:" + username;
        List<String> userPermissions = (List<String>)
            redisTemplate.opsForValue().get(cacheKey);

        if (userPermissions == null) {
            // Load from database
            userPermissions = menuMapper.selectPermissionsByUsername(username);
            // Cache for 1 hour
            redisTemplate.opsForValue().set(cacheKey, userPermissions, 3600);
        }

        if (logical == Logical.AND) {
            return Arrays.stream(requiredPermissions)
                .allMatch(userPermissions::contains);
        } else {
            return Arrays.stream(requiredPermissions)
                .anyMatch(userPermissions::contains);
        }
    }
}
```

### Permission Service

```java
@Service
public class PermissionService {

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Get user permissions
     */
    public List<String> getUserPermissions(Long userId) {
        String cacheKey = "user:permissions:" + userId;
        List<String> permissions = (List<String>)
            redisTemplate.opsForValue().get(cacheKey);

        if (permissions == null) {
            permissions = menuMapper.selectPermissionsByUserId(userId);
            redisTemplate.opsForValue().set(cacheKey, permissions, 3600);
        }

        return permissions;
    }

    /**
     * Get user menu tree
     */
    public List<MenuVO> getUserMenuTree(Long userId) {
        List<String> permissions = getUserPermissions(userId);
        List<Menu> menus = menuMapper.selectMenusByPermissions(permissions);
        return buildMenuTree(menus, 0L);
    }

    /**
     * Check if user has permission
     */
    public boolean hasPermission(Long userId, String permission) {
        List<String> permissions = getUserPermissions(userId);
        return permissions.contains(permission);
    }

    /**
     * Build menu tree
     */
    private List<MenuVO> buildMenuTree(List<Menu> menus, Long parentId) {
        return menus.stream()
            .filter(menu -> menu.getParentId().equals(parentId))
            .map(menu -> {
                MenuVO menuVO = new MenuVO();
                BeanUtil.copyProperties(menu, menuVO);
                menuVO.setChildren(buildMenuTree(menus, menu.getId()));
                return menuVO;
            })
            .collect(Collectors.toList());
    }
}
```

### Mapper Interface

```java
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * Get permissions by username
     */
    @Select("SELECT m.menu_code FROM sys_menu m " +
             "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
             "INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
             "INNER JOIN sys_user u ON ur.user_id = u.id " +
             "WHERE u.username = #{username} AND m.menu_type = 'button'")
    List<String> selectPermissionsByUsername(@Param("username") String username);

    /**
     * Get permissions by user ID
     */
    @Select("SELECT m.menu_code FROM sys_menu m " +
             "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
             "INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
             "WHERE ur.user_id = #{userId} AND m.menu_type = 'button'")
    List<String> selectPermissionsByUserId(@Param("userId") Long userId);

    /**
     * Get menus by permissions
     */
    List<Menu> selectMenusByPermissions(@Param("permissions") List<String> permissions);
}
```

---

## Data-Level Permissions

### Data Permission Model

```sql
CREATE TABLE `sys_data_scope` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `role_id` BIGINT(20) NOT NULL,
  `scope_type` VARCHAR(20) NOT NULL COMMENT 'all/custom/dept/dept_and_child/only_self',
  `dept_id` BIGINT(20) COMMENT 'Department ID',
  `user_id` BIGINT(20) COMMENT 'User ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Data scope configuration';
```

### Data Scope Types

| Scope Type | Description | SQL Condition |
|------------|-------------|---------------|
| all | All data | No condition |
| custom | Custom data | IN (dept_ids) |
| dept | Current department | dept_id = current_dept_id |
| dept_and_child | Current department and children | dept_id IN (current_dept_id, children) |
| only_self | Only own data | user_id = current_user_id |

### Data Scope Annotation

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataScope {
    String deptAlias() default "d";
    String userAlias() default "u";
}
```

### Data Scope Interceptor

```java
@Aspect
@Component
public class DataScopeAspect {

    @Autowired
    private DataScopeMapper dataScopeMapper;

    @Before("@annotation(dataScope)")
    public void doBefore(JoinPoint point, DataScope dataScope) {
        handleDataScope(point, dataScope);
    }

    protected void handleDataScope(JoinPoint point, DataScope dataScope) {
        // Get current user
        Long userId = SecurityUtil.getUserId();

        // Get user's data scope
        List<DataScope> dataScopes = dataScopeMapper.selectDataScopeByUserId(userId);

        if (dataScopes.isEmpty()) {
            return;
        }

        // Build data scope SQL
        StringBuilder sql = new StringBuilder();
        for (DataScope scope : dataScopes) {
            if ("all".equals(scope.getScopeType())) {
                // All data, no condition
                return;
            } else if ("custom".equals(scope.getScopeType())) {
                sql.append(String.format(" OR %s.dept_id IN (%s)",
                    dataScope.deptAlias(), scope.getDeptIds()));
            } else if ("dept".equals(scope.getScopeType())) {
                sql.append(String.format(" OR %s.dept_id = %d",
                    dataScope.deptAlias(), scope.getDeptId()));
            } else if ("dept_and_child".equals(scope.getScopeType())) {
                List<Long> childDeptIds = getChildrenDeptIds(scope.getDeptId());
                sql.append(String.format(" OR %s.dept_id IN (%d,%s)",
                    dataScope.deptAlias(), scope.getDeptId(),
                    StringUtils.join(childDeptIds, ",")));
            } else if ("only_self".equals(scope.getScopeType())) {
                sql.append(String.format(" OR %s.id = %d",
                    dataScope.userAlias(), userId));
            }
        }

        // Set data scope in ThreadLocal
        if (sql.length() > 0) {
            String dataScopeSql = " AND (" + sql.substring(4) + ")";
            DataScopeContextHolder.setDataScope(dataScopeSql);
        }
    }
}
```

---

## Permission Configuration Examples

### Admin Role Configuration

**Role: Administrator**

```json
{
  "roleCode": "admin",
  "roleName": "Administrator",
  "description": "System administrator with all permissions",
  "permissions": [
    "user:query", "user:create", "user:update", "user:delete",
    "role:query", "role:create", "role:update", "role:delete",
    "menu:query", "menu:create", "menu:update", "menu:delete",
    "dept:query", "dept:create", "dept:update", "dept:delete",
    "tenant:query", "tenant:create", "tenant:update", "tenant:delete",
    "order:query", "order:create", "order:update", "order:delete",
    "product:query", "product:create", "product:update", "product:delete",
    "customer:query", "customer:create", "customer:update", "customer:delete",
    "workflow:query", "workflow:deploy", "workflow:manage"
  ],
  "dataScope": {
    "scopeType": "all"
  }
}
```

### Regular User Role Configuration

**Role: Regular User**

```json
{
  "roleCode": "user",
  "roleName": "Regular User",
  "description": "Regular user with basic permissions",
  "permissions": [
    "profile:view", "profile:update",
    "password:update",
    "order:query", "order:create",
    "product:query"
  ],
  "dataScope": {
    "scopeType": "only_self"
  }
}
```

### Viewer Role Configuration

**Role: Viewer**

```json
{
  "roleCode": "viewer",
  "roleName": "Viewer",
  "description": "Read-only access",
  "permissions": [
    "user:query",
    "role:query",
    "order:query",
    "product:query",
    "customer:query"
  ],
  "dataScope": {
    "scopeType": "dept",
    "deptId": 1
  }
}
```

---

## Version History

| Version | Date | Author | Changes |
|---------|-------|---------|----------|
| 1.0.0 | 2025-01-11 | Initial permission management solution |
