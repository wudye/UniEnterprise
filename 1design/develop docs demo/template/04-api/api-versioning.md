# API Versioning Document

## Overview

This document describes the API versioning strategy for the UniEnterprise project, including version management approaches, upgrade processes, and compatibility guidelines.

---

## Versioning Strategy

### Versioning Approaches Comparison

| Approach | Description | Advantages | Disadvantages |
|----------|-------------|------------|--------------|
| **URL Path Versioning** | Version in URL path (`/api/v1/users`) | Clear, easy to implement | URL changes |
| **Header Versioning** | Version in header (`Accept: application/vnd.api.v1+json`) | Clean URL | Harder to debug |
| **Query Parameter Versioning** | Version in query (`?v=1`) | Simple | Not RESTful |
| **Content Negotiation** | Version in media type | Flexible | Complex implementation |

### Recommended Approach: URL Path Versioning

**Selection Reasons:**
- Clear and explicit
- Easy to implement and maintain
- Better caching support
- Industry standard
- Easy to test and debug

---

## Version Management

### Version Number Format

**Format:** `{major}.{minor}.{patch}`

- **Major**: Breaking changes, incompatible API modifications
- **Minor**: New features, backward-compatible changes
- **Patch**: Bug fixes, backward-compatible changes

**Examples:**
- `v1.0.0` → `v1.1.0`: Added new endpoint (backward compatible)
- `v1.1.0` → `v2.0.0`: Breaking changes (incompatible)

### Supported Versions

| Version | Status | Release Date | Deprecation Date | Sunset Date |
|---------|--------|--------------|------------------|-------------|
| v1.0.0 | Stable | 2025-01-11 | - | - |
| v2.0.0 | Development | - | - | - |

### Version Lifecycle

```
Development → Stable → Deprecated → Sunset
     ↑          ↓           ↓           ↓
              Current      Warning     Removed
```

---

## Version Implementation

### URL Structure

```
/api/v{version}/{resource}

Examples:
- /api/v1/users
- /api/v1/orders
- /api/v2/users
```

### Versioned Controller Example

**v1 Controller:**

```java
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management v1", description = "User management APIs v1")
public class UserV1Controller {

    @GetMapping("/{id}")
    public Result<UserV1VO> getUser(@PathVariable Long id) {
        // v1 implementation
        UserV1VO user = userService.getUserV1(id);
        return Result.success(user);
    }
}
```

**v2 Controller:**

```java
@RestController
@RequestMapping("/api/v2/users")
@Tag(name = "User Management v2", description = "User management APIs v2")
public class UserV2Controller {

    @GetMapping("/{id}")
    public Result<UserV2VO> getUser(@PathVariable Long id) {
        // v2 implementation with enhanced features
        UserV2VO user = userService.getUserV2(id);
        return Result.success(user);
    }
}
```

### Shared Base Controller

```java
@RestController
@RequestMapping("/api/{version}/users")
public class UserController {

    @GetMapping("/{id}")
    public Result<UserVO> getUser(@PathVariable String version,
                                 @PathVariable Long id) {
        if ("v2".equals(version)) {
            UserVO user = userService.getUserV2(id);
            return Result.success(user);
        } else {
            UserVO user = userService.getUserV1(id);
            return Result.success(user);
        }
    }
}
```

---

## Version Upgrade Process

### Non-Breaking Changes (Minor/Patch)

**Example:** Adding new field to response

**v1 Response:**
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": 1,
    "username": "admin"
  }
}
```

**v1.1 Response (backward compatible):**
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": 1,
    "username": "admin",
    "email": "admin@example.com"  // New field
  }
}
```

**Process:**
1. Add new field to existing v1 endpoint
2. Update API documentation
3. Test backward compatibility
4. Deploy to production

### Breaking Changes (Major)

**Example:** Renaming response field

**v1 Response:**
```json
{
  "data": {
    "username": "admin"
  }
}
```

**v2 Response (breaking change):**
```json
{
  "data": {
    "userName": "admin"  // Renamed from username
  }
}
```

**Process:**
1. Create new v2 endpoint
2. Maintain v1 endpoint (parallel)
3. Update API documentation
4. Notify clients of deprecation
5. Gradually migrate clients to v2
6. Deprecate v1 after migration period (e.g., 6 months)
7. Remove v1 endpoint

---

## Compatibility Guidelines

### Backward Compatibility Rules

✅ **Backward Compatible:**
- Adding new endpoints
- Adding new optional fields to request
- Adding new fields to response
- Changing field order
- Expanding response data

❌ **Breaking Changes:**
- Removing endpoints
- Removing required fields from request
- Removing fields from response
- Changing field names
- Changing field types
- Changing HTTP methods
- Changing error codes
- Changing authentication mechanisms

### Version Transition Strategy

**Phase 1: Announce New Version**
- Publish new version documentation
- Provide migration guide
- Set deprecation notice for old version

**Phase 2: Support Both Versions**
- Maintain both versions
- Monitor usage of old version
- Encourage migration to new version

**Phase 3: Deprecate Old Version**
- Send deprecation warnings in response headers
- Set sunset date
- Notify remaining users

**Phase 4: Remove Old Version**
- Remove old version after sunset date
- Remove old version documentation

---

## Deprecation Headers

### Deprecation Warning

**Response Headers:**

```
Deprecation: true
Sunset: Sun, 11 Jul 2026 00:00:00 GMT
Link: <https://api.example.com/api/v2/users/123>; rel="successor-version"
Warning: 299 - "Deprecated API. Please migrate to v2."
```

### Header Implementation

```java
@ControllerAdvice
public class VersionAdvice {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DeprecatedVersion {
        String sunsetDate() default "";
        String successorVersion() default "";
    }

    @DeprecatedVersion(
        sunsetDate = "2026-07-11",
        successorVersion = "v2"
    )
    @GetMapping("/api/v1/users/{id}")
    public Result<UserVO> getUserV1(@PathVariable Long id) {
        return Result.success(userService.getUser(id));
    }
}

@Component
public class DeprecatedHeaderInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request,
                          HttpServletResponse response,
                          Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            DeprecatedVersion annotation =
                handlerMethod.getMethodAnnotation(DeprecatedVersion.class);

            if (annotation != null) {
                response.setHeader("Deprecation", "true");
                response.setHeader("Sunset", annotation.sunsetDate());
                response.setHeader("Link",
                    "<https://api.example.com/api/" + annotation.successorVersion() + ">; rel=\"successor-version\"");
                response.setHeader("Warning",
                    "299 - \"Deprecated API. Please migrate to " + annotation.successorVersion() + ".\"");
            }
        }
    }
}
```

---

## Client Migration Guide

### Step 1: Identify Used Endpoints

```bash
# Audit API usage
grep -r "api/v1/" src/
```

### Step 2: Update API Base URL

**Before:**
```javascript
const API_BASE_URL = 'https://api.example.com/api/v1';
```

**After:**
```javascript
const API_BASE_URL = 'https://api.example.com/api/v2';
```

### Step 3: Update Response Handling

**Before (v1):**
```javascript
const user = response.data;
console.log(user.username);
```

**After (v2):**
```javascript
const user = response.data;
console.log(user.userName);  // Updated field name
```

### Step 4: Test Migration

```javascript
// Test new endpoints
async function testMigration() {
    const response = await fetch('/api/v2/users/1');
    const data = await response.json();
    console.log(data);
}
```

---

## Version History

| Version | Date | Author | Changes |
|---------|-------|---------|----------|
| 1.0.0 | 2025-01-11 | Initial API versioning document |
