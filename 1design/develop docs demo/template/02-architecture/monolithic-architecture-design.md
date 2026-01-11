# Monolithic Architecture Design Document

## Overview

This document describes the monolithic architecture design for the UniEnterprise project, including module division, package structure, layered design, and module dependencies.

---

## Architecture Overview

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
│              API Gateway Layer                          │
│              (Nginx Reverse Proxy)                      │
└────────────────────────────────┬────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────┐
│          Application Layer (Spring Boot)               │
│  ┌──────────────────────────────────────────────────┐  │
│  │         uni-enterprise-admin                  │  │
│  │  ┌──────────────────────────────────────────┐  │  │
│  │  │   Controller Layer                       │  │  │
│  │  │   - SystemController                    │  │  │
│  │  │   - TenantController                    │  │  │
│  │  │   - MallController                      │  │  │
│  │  └──────────────────────────────────────────┘  │  │
│  │                                                 │  │
│  │  ┌──────────────────────────────────────────┐  │  │
│  │  │   Service Layer                           │  │  │
│  │  │   - SystemService                        │  │  │
│  │  │   - TenantService                        │  │  │
│  │  │   - MallService                          │  │  │
│  │  └──────────────────────────────────────────┘  │  │
│  │                                                 │  │
│  │  ┌──────────────────────────────────────────┐  │  │
│  │  │   Mapper Layer (MyBatis Plus)            │  │  │
│  │  │   - SystemMapper                         │  │  │
│  │  │   - TenantMapper                         │  │  │
│  │  │   - MallMapper                           │  │  │
│  │  └──────────────────────────────────────────┘  │  │
│  └──────────────────────────────────────────────────┘  │
└────────────────────────────────┬────────────────────────┘
                                 │
          ┌──────────────────────┼──────────────────────┐
          ▼                      ▼                      ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│    MySQL     │    │    Redis     │    │  RabbitMQ    │
│   Database   │    │    Cache     │    │  Message     │
│              │    │              │    │   Queue      │
└──────────────┘    └──────────────┘    └──────────────┘
```

---

## Module Division

### Module Structure

```
uni-enterprise/
├── uni-enterprise-admin/          # Main application module
├── uni-enterprise-api/            # API module (interfaces & DTOs)
├── uni-enterprise-common/         # Common utilities module
├── uni-enterprise-framework/      # Framework configuration module
├── uni-system/                    # System management module
├── uni-tenant/                    # Multi-tenant module
├── uni-bpm/                       # Workflow module
├── uni-payment/                   # Payment module
├── uni-member/                    # Member module
├── uni-mall/                      # E-commerce module
├── uni-crm/                       # CRM module
└── uni-erp/                       # ERP module
```

### Module Descriptions

| Module | Description | Package | Dependencies |
|--------|-------------|---------|--------------|
| uni-enterprise-admin | Main application, entry point | com.uni.admin | All modules |
| uni-enterprise-api | API interfaces, DTOs, VOs | com.uni.api | None |
| uni-enterprise-common | Common utilities, base classes | com.uni.common | None |
| uni-enterprise-framework | Framework configuration | com.uni.framework | common |
| uni-system | User, role, menu management | com.uni.system | framework |
| uni-tenant | Multi-tenant management | com.uni.tenant | framework |
| uni-bpm | Workflow management | com.uni.bpm | framework |
| uni-payment | Payment integration | com.uni.payment | framework |
| uni-member | Member management | com.uni.member | framework |
| uni-mall | E-commerce | com.uni.mall | framework, payment |
| uni-crm | Customer relationship management | com.uni.crm | framework |
| uni-erp | Enterprise resource planning | com.uni.erp | framework |

---

## Package Structure Design

### Main Application Package Structure

```
uni-enterprise-admin/
├── src/main/java/com/uni/admin/
│   ├── UniEnterpriseApplication.java          # Application entry
│   ├── config/                                # Configuration
│   │   ├── RedisConfig.java
│   │   ├── MybatisPlusConfig.java
│   │   ├── FlowableConfig.java
│   │   └── SecurityConfig.java
│   ├── controller/                            # Controllers
│   │   ├── system/
│   │   │   ├── UserController.java
│   │   │   ├── RoleController.java
│   │   │   └── MenuController.java
│   │   ├── tenant/
│   │   │   └── TenantController.java
│   │   ├── mall/
│   │   │   ├── ProductController.java
│   │   │   └── OrderController.java
│   │   └── common/
│   │       ├── FileController.java
│   │       └── GenController.java
│   ├── service/                               # Service interfaces
│   │   ├── system/
│   │   │   ├── IUserService.java
│   │   │   ├── IRoleService.java
│   │   │   └── IMenuService.java
│   │   └── mall/
│   │       ├── IProductService.java
│   │       └── IOrderService.java
│   ├── service/impl/                          # Service implementations
│   │   ├── system/
│   │   │   ├── UserServiceImpl.java
│   │   │   └── RoleServiceImpl.java
│   │   └── mall/
│   │       └── ProductServiceImpl.java
│   ├── mapper/                                # Mappers
│   │   ├── system/
│   │   │   ├── UserMapper.java
│   │   │   └── RoleMapper.java
│   │   └── mall/
│   │       ├── ProductMapper.java
│   │       └── OrderMapper.java
│   ├── model/                                 # Entities, DTOs, VOs
│   │   ├── entity/
│   │   │   ├── system/
│   │   │   │   └── User.java
│   │   │   └── mall/
│   │   │       └── Product.java
│   │   ├── dto/
│   │   │   ├── system/
│   │   │   │   └── UserDTO.java
│   │   │   └── mall/
│   │   │       └── ProductDTO.java
│   │   └── vo/
│   │       ├── system/
│   │       │   └── UserVO.java
│   │       └── mall/
│   │           └── ProductVO.java
│   └── common/                                # Common utilities
│       ├── constant/
│       │   ├── RedisConstant.java
│       │   └── SystemConstant.java
│       ├── exception/
│       │   ├── BusinessException.java
│       │   └── GlobalExceptionHandler.java
│       ├── util/
│       │   ├── BeanUtil.java
│       │   └── SecurityUtil.java
│       └── enums/
│           ├── StatusEnum.java
│           └── ErrorCode.java
└── src/main/resources/
    ├── application.yml
    ├── application-dev.yml
    ├── application-test.yml
    ├── application-prod.yml
    ├── logback-spring.xml
    └── mapper/
        ├── system/
        │   └── UserMapper.xml
        └── mall/
            └── ProductMapper.xml
```

### Common Module Package Structure

```
uni-enterprise-common/
├── src/main/java/com/uni/common/
│   ├── constant/
│   │   ├── CommonConstant.java
│   │   ├── CacheConstant.java
│   │   └── SecurityConstant.java
│   ├── domain/
│   │   ├── BaseEntity.java                 # Base entity
│   │   └── Result.java                      # Unified response
│   ├── exception/
│   │   ├── BaseException.java
│   │   ├── BusinessException.java
│   │   └── ServiceException.java
│   ├── util/
│   │   ├── DateUtil.java
│   │   ├── StringUtil.java
│   │   ├── BeanUtil.java
│   │   ├── FileUtil.java
│   │   └── SecurityUtil.java
│   ├── enums/
│   │   ├── StatusEnum.java
│   │   ├── OperationEnum.java
│   │   └── ErrorCode.java
│   └── annotation/
│       ├── DataScope.java
│       └── ExcelField.java
└── pom.xml
```

---

## Layered Design

### Controller Layer

**Responsibilities:**
- Handle HTTP requests
- Parameter validation
- Call service layer
- Return response

**Code Example:**

```java
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "User management APIs")
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public Result<UserVO> getUser(@PathVariable Long id) {
        UserVO user = userService.getUserById(id);
        return Result.success(user);
    }

    @PostMapping
    @Operation(summary = "Create user")
    public Result<Long> createUser(@Validated @RequestBody UserDTO userDTO) {
        Long userId = userService.createUser(userDTO);
        return Result.success(userId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    public Result<Void> updateUser(@PathVariable Long id,
                                   @Validated @RequestBody UserDTO userDTO) {
        userService.updateUser(id, userDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    @GetMapping("/page")
    @Operation(summary = "Get user page")
    public Result<IPage<UserVO>> getUserPage(@RequestParam(defaultValue = "1") Integer page,
                                             @RequestParam(defaultValue = "10") Integer size) {
        IPage<UserVO> userPage = userService.getUserPage(page, size);
        return Result.success(userPage);
    }
}
```

**Best Practices:**
- Use `@RestController` for API controllers
- Use `@RequestMapping` for base path
- Use `@Validated` for parameter validation
- Return unified `Result<T>` wrapper
- Use Swagger annotations for API documentation

### Service Layer

**Responsibilities:**
- Business logic implementation
- Transaction management
- Call mapper layer
- Data transformation

**Interface Example:**

```java
public interface IUserService extends IService<User> {

    /**
     * Get user by ID
     */
    UserVO getUserById(Long id);

    /**
     * Create user
     */
    Long createUser(UserDTO userDTO);

    /**
     * Update user
     */
    void updateUser(Long id, UserDTO userDTO);

    /**
     * Delete user
     */
    void deleteUser(Long id);

    /**
     * Get user page
     */
    IPage<UserVO> getUserPage(Integer page, Integer size);

    /**
     * Reset user password
     */
    void resetPassword(Long id, String newPassword);
}
```

**Implementation Example:**

```java
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleMapper roleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO getUserById(Long id) {
        User user = this.getById(id);
        if (user == null) {
            throw new BusinessException("User not found");
        }
        return BeanUtil.copyProperties(user, UserVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(UserDTO userDTO) {
        // Check if username exists
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, userDTO.getUsername());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("Username already exists");
        }

        // Create user
        User user = BeanUtil.copyProperties(userDTO, User.class);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        this.save(user);

        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(Long id, UserDTO userDTO) {
        User user = this.getById(id);
        if (user == null) {
            throw new BusinessException("User not found");
        }

        // Update user
        BeanUtil.copyProperties(userDTO, user);
        this.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        User user = this.getById(id);
        if (user == null) {
            throw new BusinessException("User not found");
        }

        // Soft delete
        user.setDeleted(1);
        this.updateById(user);
    }

    @Override
    public IPage<UserVO> getUserPage(Integer page, Integer size) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getDeleted, 0);
        wrapper.orderByDesc(User::getCreateTime);

        IPage<User> userPage = this.page(pageParam, wrapper);
        return userPage.convert(user -> BeanUtil.copyProperties(user, UserVO.class));
    }
}
```

**Best Practices:**
- Define service interfaces for decoupling
- Use `@Service` annotation
- Use `@Transactional` for transaction management
- Use MyBatis Plus's `IService` and `ServiceImpl` for base CRUD operations
- Throw business exceptions for error handling

### Mapper Layer

**Responsibilities:**
- Database operations
- SQL queries
- Custom queries

**Interface Example:**

```java
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * Get user by username
     */
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND deleted = 0")
    User selectByUsername(@Param("username") String username);

    /**
     * Get users by role ID
     */
    @Select("SELECT u.* FROM sys_user u " +
             "INNER JOIN sys_user_role ur ON u.id = ur.user_id " +
             "WHERE ur.role_id = #{roleId} AND u.deleted = 0")
    List<User> selectUsersByRoleId(@Param("roleId") Long roleId);

    /**
     * Get users by department ID
     */
    @Select("SELECT * FROM sys_user WHERE dept_id = #{deptId} AND deleted = 0")
    List<User> selectUsersByDeptId(@Param("deptId") Long deptId);

    /**
     * Search users with conditions
     */
    List<User> searchUsers(@Param("keyword") String keyword,
                           @Param("status") Integer status,
                           @Param("deptId") Long deptId);
}
```

**XML Mapper Example:**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.uni.admin.mapper.system.UserMapper">

    <select id="searchUsers" resultType="com.uni.admin.model.entity.system.User">
        SELECT *
        FROM sys_user
        WHERE deleted = 0
        <if test="keyword != null and keyword != ''">
            AND (username LIKE CONCAT('%', #{keyword}, '%')
            OR real_name LIKE CONCAT('%', #{keyword}, '%')
            OR phone LIKE CONCAT('%', #{keyword}, '%'))
        </if>
        <if test="status != null">
            AND status = #{status}
        </if>
        <if test="deptId != null">
            AND dept_id = #{deptId}
        </if>
        ORDER BY create_time DESC
    </select>

</mapper>
```

**Best Practices:**
- Use MyBatis Plus's `BaseMapper` for base CRUD operations
- Use `@Select`, `@Insert`, `@Update`, `@Delete` for simple queries
- Use XML mapper for complex queries
- Use `@Param` for parameter binding
- Follow naming conventions for result mapping

---

## Module Dependency Diagram

```
┌─────────────────────────────────────────────────────┐
│          uni-enterprise-admin                       │
│              (Main Application)                     │
└─────────────────────────────────────────────────────┘
                    │
        ┌───────────┼───────────┬───────────┐
        ▼           ▼           ▼           ▼
┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────┐
│uni-system │ │uni-tenant │ │uni-mall   │ │uni-crm    │
└───────────┘ └───────────┘ └───────────┘ └───────────┘
        │           │           │           │
        └───────────┴───────────┴───────────┘
                    │
                    ▼
        ┌───────────────────────────┐
        │ uni-enterprise-framework  │
        └───────────────────────────┘
                    │
        ┌───────────┴───────────┐
        ▼                       ▼
┌──────────────────┐  ┌──────────────────┐
│uni-enterprise-api│  │uni-enterprise-common│
└──────────────────┘  └──────────────────┘
```

---

## Module Dependencies

| Module | Depends On |
|--------|------------|
| uni-enterprise-admin | All business modules |
| uni-system | uni-enterprise-framework |
| uni-tenant | uni-enterprise-framework |
| uni-bpm | uni-enterprise-framework |
| uni-payment | uni-enterprise-framework |
| uni-mall | uni-enterprise-framework, uni-payment |
| uni-crm | uni-enterprise-framework |
| uni-erp | uni-enterprise-framework |
| uni-enterprise-framework | uni-enterprise-common, uni-enterprise-api |
| uni-enterprise-common | None |
| uni-enterprise-api | None |

---

## Version History

| Version | Date | Author | Changes |
|---------|-------|---------|----------|
| 1.0.0 | 2025-01-11 | Initial monolithic architecture design |
