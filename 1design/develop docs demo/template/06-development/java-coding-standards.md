# Java Coding Standards

## Overview

This document defines the Java coding standards for the UniEnterprise project, based on the Google Java Style Guide, including naming conventions, code organization, comment standards, exception handling, and logging standards.

---

## Naming Conventions

### Package Names

**Format:** All lowercase, with dot separators

**Examples:**
```
com.uni.admin.controller
com.uni.admin.service
com.uni.admin.mapper
com.uni.admin.model.entity
com.uni.common.util
```

**Rules:**
- Use reverse domain naming
- All lowercase
- No underscores or hyphens
- Use meaningful names

### Class Names

**Format:** PascalCase (UpperCamelCase)

**Examples:**
```java
UserService
OrderController
UserMapper
UserVO
UserDTO
```

**Rules:**
- Noun or noun phrase
- No abbreviations unless widely known
- Avoid generic names (e.g., `Data`, `Info`)
- Use meaningful prefixes/suffixes:
  - `Controller` for Spring controllers
  - `Service` for service interfaces
  - `ServiceImpl` for service implementations
  - `Mapper` for MyBatis mappers
  - `VO` for view objects
  - `DTO` for data transfer objects
  - `Entity` for database entities

### Method Names

**Format:** camelCase (lowerCamelCase)

**Examples:**
```java
getUserById()
createUser()
updateUser()
deleteUser()
isValid()
hasPermission()
```

**Rules:**
- Verb or verb phrase
- Start with lowercase
- No underscores
- Use meaningful names
- Boolean methods start with `is`, `has`, `can`, `should`

### Variable Names

**Format:** camelCase

**Examples:**
```java
String userName;
int orderCount;
List<User> userList;
boolean isAvailable;
```

**Rules:**
- Start with lowercase
- No underscores
- Use meaningful names
- Avoid single-letter names except loop counters

### Constant Names

**Format:** UPPER_SNAKE_CASE

**Examples:**
```java
private static final int MAX_RETRY_COUNT = 3;
private static final String DEFAULT_PAGE_SIZE = "10";
private static final String API_BASE_URL = "https://api.example.com";
```

**Rules:**
- All uppercase
- Use underscores to separate words
- `private static final` for constants
- Use meaningful names

---

## Code Organization

### File Structure

```java
package com.uni.admin.service;

// 1. Import statements (standard library, third-party, project)
import org.springframework.stereotype.Service;
import com.uni.common.domain.Result;
import com.uni.admin.mapper.UserMapper;

// 2. Class Javadoc
/**
 * User service implementation.
 *
 * @author John Doe
 * @since 1.0.0
 */
@Service
public class UserServiceImpl implements IUserService {

    // 3. Class constants
    private static final int MAX_RETRY_COUNT = 3;

    // 4. Static variables (rarely used)

    // 5. Instance variables (dependencies)
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 6. Constructors

    // 7. Public methods (in logical order)
    @Override
    public Result<UserVO> getUserById(Long id) {
        // Implementation
    }

    @Override
    public Result<Long> createUser(UserDTO userDTO) {
        // Implementation
    }

    // 8. Protected methods

    // 9. Package-private methods

    // 10. Private methods
    private UserVO convertToVO(User user) {
        // Implementation
    }

    // 11. Inner classes (if any)
}
```

### Imports

**Order:**
1. Static imports
2. Standard library imports
3. Third-party library imports
4. Project imports

**Example:**
```java
import static java.util.Collections.emptyList;
import static org.springframework.util.StringUtils.isEmpty;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uni.common.domain.Result;
import com.uni.admin.model.entity.User;
import com.uni.admin.model.vo.UserVO;
```

---

## Comment Standards

### Class Javadoc

```java
/**
 * User service implementation.
 *
 * <p>Provides user CRUD operations, user authentication,
 * and user management functionality.</p>
 *
 * @author John Doe
 * @since 1.0.0
 */
@Service
public class UserServiceImpl implements IUserService {
    // ...
}
```

### Method Javadoc

```java
/**
 * Get user by ID.
 *
 * @param id the user ID
 * @return the user view object
 * @throws BusinessException if user not found
 */
@Override
public UserVO getUserById(Long id) {
    // Implementation
}
```

### Field Javadoc

```java
/**
 * Maximum retry count for failed operations.
 */
private static final int MAX_RETRY_COUNT = 3;
```

### Inline Comments

**Good:**
```java
// Check if user exists before creating
User existingUser = userMapper.selectByUsername(username);
if (existingUser != null) {
    throw new BusinessException("Username already exists");
}
```

**Bad:**
```java
// Get user
User user = userMapper.selectById(id);
// Check if null
if (user == null) {
    // Throw exception
    throw new BusinessException("User not found");
}
```

---

## Exception Handling

### Exception Hierarchy

```
RuntimeException
├── BaseException
│   ├── BusinessException
│   │   ├── UserNotFoundException
│   │   ├── PermissionDeniedException
│   │   └── InvalidParameterException
│   └── ServiceException
└── SystemException
    ├── DatabaseException
    ├── NetworkException
    └── CacheException
```

### Custom Exceptions

**Base Exception:**

```java
public class BaseException extends RuntimeException {

    private String errorCode;

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getErrorCode() {
        return errorCode;
    }
}
```

**Business Exception:**

```java
public class BusinessException extends BaseException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String errorCode, String message) {
        super(errorCode, message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

**Specific Exception:**

```java
public class UserNotFoundException extends BusinessException {

    public UserNotFoundException(Long userId) {
        super("USER_NOT_FOUND", "User not found: " + userId);
    }
}
```

### Exception Handling Best Practices

**Try-Catch-Finally:**

```java
public User getUserById(Long id) {
    try {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new UserNotFoundException(id);
        }
        return user;
    } catch (Exception e) {
        log.error("Failed to get user by id: {}", id, e);
        throw new ServiceException("Failed to get user", e);
    }
}
```

**Try-With-Resources:**

```java
public void exportUsersToCsv(String filePath) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
        List<User> users = userMapper.selectAll();
        for (User user : users) {
            writer.write(user.toCsvLine());
            writer.newLine();
        }
    } catch (IOException e) {
        log.error("Failed to export users to CSV: {}", filePath, e);
        throw new BusinessException("Export failed");
    }
}
```

**Global Exception Handler:**

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("Business exception: {}", e.getMessage());
        return Result.fail(e.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        log.warn("Validation exception: {}", message);
        return Result.fail("VALIDATION_ERROR", message);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("System exception: ", e);
        return Result.fail("SYSTEM_ERROR", "System error, please try again later");
    }
}
```

---

## Logging Standards

### Log Levels

| Level | Description | Usage |
|-------|-------------|-------|
| TRACE | Most detailed information | Rarely used |
| DEBUG | Debug information | Development |
| INFO | Informational messages | Normal operation |
| WARN | Warning messages | Potential issues |
| ERROR | Error messages | Errors and exceptions |

### Logger Declaration

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    // ...
}
```

### Logging Best Practices

**Log Format:**
```java
// Good: Use placeholders
log.debug("User login: username={}, ip={}", username, ip);

// Bad: String concatenation
log.debug("User login: username=" + username + ", ip=" + ip);
```

**Log Levels:**

```java
// DEBUG: Detailed debugging information
log.debug("Querying user by id: {}", userId);

// INFO: Important events
log.info("User created: id={}, username={}", userId, username);
log.info("Order placed: orderId={}, userId={}", orderId, userId);

// WARN: Potential issues
log.warn("Failed to send email to user: {}", email);
log.warn("Cache miss for key: {}", cacheKey);

// ERROR: Errors and exceptions
log.error("Failed to create user: {}", userDTO, exception);
log.error("Database connection failed", exception);
```

**Exception Logging:**
```java
try {
    // Code that may throw exception
} catch (Exception e) {
    // Always include exception in error logs
    log.error("Operation failed: {}", description, e);
    throw new BusinessException("Operation failed", e);
}
```

### Performance Logging

```java
public User getUserById(Long id) {
    long startTime = System.currentTimeMillis();

    try {
        User user = userMapper.selectById(id);
        return user;
    } finally {
        long elapsed = System.currentTimeMillis() - startTime;
        log.debug("getUserById took {}ms", elapsed);
    }
}
```

---

## Code Quality

### Code Smells to Avoid

**Long Method:**
```java
// Bad: Too long, does too many things
public void processOrder(OrderDTO orderDTO) {
    // 100+ lines of code
}

// Good: Split into smaller methods
public void processOrder(OrderDTO orderDTO) {
    validateOrder(orderDTO);
    deductInventory(orderDTO);
    createPayment(orderDTO);
    notifyUser(orderDTO);
}
```

**Magic Numbers:**
```java
// Bad: Magic number
if (retryCount > 3) {
    // ...
}

// Good: Named constant
private static final int MAX_RETRY_COUNT = 3;

if (retryCount > MAX_RETRY_COUNT) {
    // ...
}
```

**Deep Nesting:**
```java
// Bad: Deep nesting
if (user != null) {
    if (user.isActive()) {
        if (user.hasPermission()) {
            // Do something
        }
    }
}

// Good: Early return
if (user == null || !user.isActive() || !user.hasPermission()) {
    return;
}
// Do something
```

---

## Best Practices

### Immutability

```java
// Good: Use final for immutable fields
public class UserVO {
    private final Long id;
    private final String username;

    public UserVO(Long id, String username) {
        this.id = id;
        this.username = username;
    }
}
```

### Null Safety

```java
// Good: Use Optional
public Optional<User> findUserById(Long id) {
    User user = userMapper.selectById(id);
    return Optional.ofNullable(user);
}

// Good: Use StringUtils
if (StringUtils.isNotBlank(username)) {
    // Process username
}
```

### Stream API

```java
// Good: Use Stream API
List<String> usernames = users.stream()
    .map(User::getUsername)
    .filter(StringUtils::isNotBlank)
    .collect(Collectors.toList());
```

---

## Version History

| Version | Date | Author | Changes |
|---------|-------|---------|----------|
| 1.0.0 | 2025-01-11 | Initial Java coding standards |
