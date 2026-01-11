# API Security Document

## Overview

This document describes the API security measures for the UniEnterprise project, including authentication, authorization, encryption, rate limiting, and security audit logging.

---

## Authentication

### JWT Token-Based Authentication

**Technology:** JWT (JSON Web Token)

**Flow:**
1. Client sends login request with credentials
2. Server validates credentials
3. Server generates JWT token
4. Client stores token and includes it in subsequent requests
5. Server validates token on each request

### Login Endpoint

**Endpoint:** `POST /api/v1/auth/login`

**Request:**
```json
{
  "username": "admin",
  "password": "password123"
}
```

**Response:**
```json
{
  "code": 200,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 7200,
    "tokenType": "Bearer"
  }
}
```

### Token Configuration

```yaml
jwt:
  secret: uni-enterprise-secret-key-change-in-production
  expiration: 7200  # 2 hours in seconds
  refresh-expiration: 604800  # 7 days in seconds
  header: Authorization
  token-prefix: Bearer
```

### Token Structure

**Header:**
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

**Payload:**
```json
{
  "sub": "1234567890",
  "tenantId": "1",
  "username": "admin",
  "roles": ["ADMIN"],
  "iat": 1516239022,
  "exp": 1516246222
}
```

### Token Validation

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractToken(request);

        if (StringUtils.isNotBlank(token) && validateToken(token)) {
            UsernamePasswordAuthenticationToken authentication =
                getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(jwtSecret)
            .build()
            .parseClaimsJws(token)
            .getBody();

        String username = claims.getSubject();
        List<String> roles = claims.get("roles", List.class);
        Long tenantId = claims.get("tenantId", Long.class);

        // Set tenant ID in context
        TenantContextHolder.setTenantId(tenantId);

        Collection<SimpleGrantedAuthority> authorities =
            roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(
            username, null, authorities
        );
    }
}
```

### Token Refresh

**Endpoint:** `POST /api/v1/auth/refresh`

**Request:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response:**
```json
{
  "code": 200,
  "message": "Token refreshed",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 7200
  }
}
```

---

## Authorization

### RBAC (Role-Based Access Control)

**Principle:** Users are assigned roles, roles are assigned permissions

**Data Model:**
- **User**: Represents a system user
- **Role**: Represents a collection of permissions
- **Menu/Permission**: Represents a system permission
- **User-Role**: Many-to-many relationship
- **Role-Menu**: Many-to-many relationship

### Permission Configuration

```sql
-- Menu/Permission table
CREATE TABLE `sys_menu` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `menu_name` VARCHAR(50) NOT NULL COMMENT 'Menu name',
  `menu_type` VARCHAR(10) NOT NULL COMMENT 'Menu type (directory/menu/button)',
  `menu_code` VARCHAR(100) COMMENT 'Menu code/permission code',
  `path` VARCHAR(200) COMMENT 'Menu path',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Role-Menu association
CREATE TABLE `sys_role_menu` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `role_id` BIGINT(20) NOT NULL COMMENT 'Role ID',
  `menu_id` BIGINT(20) NOT NULL COMMENT 'Menu/Permission ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### Permission Annotation

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    String value() default "";
    String[] permissions() default {};
}
```

### Permission Check

```java
@Aspect
@Component
public class PermissionAspect {

    @Autowired
    private MenuMapper menuMapper;

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

        // Check if user has permissions
        boolean hasPermission = checkUserPermissions(username, permissions);

        if (!hasPermission) {
            throw new AccessDeniedException("Access denied");
        }

        return joinPoint.proceed();
    }

    private boolean checkUserPermissions(String username, String[] permissions) {
        // Query user permissions from database
        List<String> userPermissions = menuMapper.selectPermissionsByUsername(username);

        // Check if user has all required permissions
        return Arrays.stream(permissions)
            .allMatch(userPermissions::contains);
    }
}
```

### Usage Example

```java
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @GetMapping("/{id}")
    @RequirePermission(permissions = {"user:query"})
    public Result<UserVO> getUser(@PathVariable Long id) {
        // Only users with "user:query" permission can access
        UserVO user = userService.getUserById(id);
        return Result.success(user);
    }

    @PostMapping
    @RequirePermission(permissions = {"user:create"})
    public Result<Long> createUser(@RequestBody UserDTO userDTO) {
        // Only users with "user:create" permission can access
        Long userId = userService.createUser(userDTO);
        return Result.success(userId);
    }

    @DeleteMapping("/{id}")
    @RequirePermission(permissions = {"user:delete"})
    public Result<Void> deleteUser(@PathVariable Long id) {
        // Only users with "user:delete" permission can access
        userService.deleteUser(id);
        return Result.success();
    }
}
```

---

## Data Encryption

### Transmission Encryption (HTTPS)

**Configuration:**

```yaml
server:
  port: 8443
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: changeit
    key-store-type: PKCS12
    key-alias: uni-enterprise
```

**Force HTTPS:**

```java
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .requiresChannel()
                .anyRequest().requiresSecure()
            .and()
            // ... other security configuration
        return http.build();
    }
}
```

### Storage Encryption (Sensitive Data)

**Password Encryption (BCrypt):**

```java
@Service
public class UserServiceImpl {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void createUser(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        // ... other fields
        userMapper.insert(user);
    }
}
```

**Sensitive Field Encryption (AES):**

```java
@Component
public class EncryptionUtil {

    private static final String SECRET_KEY = "your-secret-key-32-bytes";
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    public String encrypt(String data) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(new byte[16]);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decrypt(String encryptedData) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(new byte[16]);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        byte[] decoded = Base64.getDecoder().decode(encryptedData);
        byte[] decrypted = cipher.doFinal(decoded);

        return new String(decrypted);
    }
}
```

---

## Rate Limiting

### Rate Limiting Strategy

**Technology:** Redis + Lua Script

**Configuration:**

```yaml
rate-limit:
  enabled: true
  default-limit: 100  # 100 requests per minute
  login-limit: 5       # 5 login attempts per 15 minutes
  api-limits:
    - path: /api/v1/auth/login
      limit: 5
      window: 900  # 15 minutes
    - path: /api/v1/orders
      limit: 20
      window: 60   # 1 minute
```

### Rate Limiting Implementation

```java
@Component
public class RateLimiter {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String LUA_SCRIPT =
        "local key = KEYS[1]\n" +
        "local limit = tonumber(ARGV[1])\n" +
        "local window = tonumber(ARGV[2])\n" +
        "local current = redis.call('INCR', key)\n" +
        "if current == 1 then\n" +
        "    redis.call('EXPIRE', key, window)\n" +
        "end\n" +
        "if current > limit then\n" +
        "    return 0\n" +
        "else\n" +
        "    return 1\n" +
        "end";

    public boolean allowRequest(String identifier, int limit, int window) {
        String key = "rate:limit:" + identifier;

        Long result = redisTemplate.execute(
            new DefaultRedisScript<>(LUA_SCRIPT, Long.class),
            Collections.singletonList(key),
            String.valueOf(limit),
            String.valueOf(window)
        );

        return result != null && result == 1;
    }
}
```

### Rate Limiting Filter

```java
@Component
public class RateLimitFilter implements Filter {

    @Autowired
    private RateLimiter rateLimiter;

    @Override
    public void doFilter(ServletRequest request,
                        ServletResponse response,
                        FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String clientIp = getClientIp(httpRequest);
        String path = httpRequest.getRequestURI();

        // Check rate limit
        boolean allowed = rateLimiter.allowRequest(
            clientIp + ":" + path,
            getLimitForPath(path),
            getWindowForPath(path)
        );

        if (!allowed) {
            httpResponse.setStatus(429);
            httpResponse.getWriter().write("{\"code\":429,\"message\":\"Too many requests\"}");
            return;
        }

        chain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
```

---

## Security Audit Logging

### Audit Log Model

```sql
CREATE TABLE `sys_audit_log` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `tenant_id` BIGINT(20) NOT NULL DEFAULT '0' COMMENT 'Tenant ID',
  `user_id` BIGINT(20) COMMENT 'User ID',
  `username` VARCHAR(50) COMMENT 'Username',
  `operation` VARCHAR(50) COMMENT 'Operation type',
  `method` VARCHAR(10) COMMENT 'HTTP method',
  `url` VARCHAR(500) COMMENT 'Request URL',
  `ip` VARCHAR(50) COMMENT 'Client IP',
  `location` VARCHAR(255) COMMENT 'IP location',
  `params` TEXT COMMENT 'Request parameters',
  `result` TEXT COMMENT 'Response result',
  `status` TINYINT(1) COMMENT 'Status (0=failed, 1=success)',
  `error_msg` VARCHAR(500) COMMENT 'Error message',
  `execution_time` INT(11) COMMENT 'Execution time (ms)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Audit log table';
```

### Audit Log Annotation

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditLog {
    String operation() default "";
    String module() default "";
}
```

### Audit Log Aspect

```java
@Aspect
@Component
public class AuditLogAspect {

    @Autowired
    private AuditLogMapper auditLogMapper;

    @Autowired
    private HttpServletRequest request;

    @Around("@annotation(auditLog)")
    public Object logAudit(ProceedingJoinPoint joinPoint, AuditLog auditLog)
            throws Throwable {
        long startTime = System.currentTimeMillis();

        AuditLogEntity log = new AuditLogEntity();
        log.setTenantId(TenantContextHolder.getTenantId());
        log.setUserId(getCurrentUserId());
        log.setUsername(getCurrentUsername());
        log.setOperation(auditLog.operation());
        log.setModule(auditLog.module());
        log.setMethod(request.getMethod());
        log.setUrl(request.getRequestURI());
        log.setIp(getClientIp());

        try {
            Object result = joinPoint.proceed();
            log.setStatus(1);
            log.setResult(JSON.toJSONString(result));
            return result;
        } catch (Exception e) {
            log.setStatus(0);
            log.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            log.setExecutionTime((int) (System.currentTimeMillis() - startTime));
            auditLogMapper.insert(log);
        }
    }
}
```

---

## Version History

| Version | Date | Author | Changes |
|---------|-------|---------|----------|
| 1.0.0 | 2025-01-11 | Initial API security document |
