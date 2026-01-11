# API Interface Design Document

## Overview

This document defines the API interface design standards for the UniEnterprise project, including RESTful API specifications, unified response format, interface list, and request/response examples.

---

## API Design Principles

### RESTful API Standards

1. **Resource-based**: Use nouns to represent resources
2. **HTTP verbs**: Use appropriate HTTP methods for operations
3. **Stateless**: Each request contains all necessary information
4. **Versioning**: Include version in URL
5. **Consistent naming**: Use consistent naming conventions

### HTTP Methods

| Method | Description | Idempotent | Safe |
|--------|-------------|------------|------|
| GET | Retrieve resources | Yes | Yes |
| POST | Create resources | No | No |
| PUT | Update entire resource | Yes | No |
| PATCH | Partial update | No | No |
| DELETE | Delete resources | Yes | No |

### URL Design Standards

| Pattern | Description | Example |
|---------|-------------|---------|
| `/api/v1/{resource}` | Resource collection | `/api/v1/users` |
| `/api/v1/{resource}/{id}` | Single resource | `/api/v1/users/123` |
| `/api/v1/{resource}/{id}/{sub-resource}` | Sub-resource | `/api/v1/users/123/roles` |
| `/api/v1/{resource}?filter=value` | Filter | `/api/v1/users?status=1` |
| `/api/v1/{resource}?page=1&size=10` | Pagination | `/api/v1/users?page=1&size=10` |

---

## Unified Response Format

### Success Response

```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": 123,
    "username": "admin",
    "realName": "Admin User"
  },
  "timestamp": "2025-01-11T10:00:00Z"
}
```

### Error Response

```json
{
  "code": 400,
  "message": "Validation failed",
  "data": null,
  "timestamp": "2025-01-11T10:00:00Z",
  "errors": [
    {
      "field": "username",
      "message": "Username cannot be empty"
    }
  ]
}
```

### Response Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| code | Integer | Yes | Response code |
| message | String | Yes | Response message |
| data | Object/Array | No | Response data |
| timestamp | String | Yes | Response timestamp (ISO 8601) |
| errors | Array | No | Validation errors |

### Standard Response Codes

| Code | HTTP Status | Message | Description |
|------|-------------|---------|-------------|
| 200 | 200 | Success | Request successful |
| 400 | 400 | Bad Request | Invalid request parameters |
| 401 | 401 | Unauthorized | Authentication required |
| 403 | 403 | Forbidden | Access denied |
| 404 | 404 | Not Found | Resource not found |
| 500 | 500 | Internal Server Error | Server error |
| 10001 | 200 | User not found | Custom business error |
| 10002 | 200 | Duplicate username | Custom business error |

---

## API Interfaces

### User Management APIs

#### Get User List

**Endpoint:** `GET /api/v1/users`

**Description:** Get paginated user list

**Request Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| page | Integer | No | Page number (default: 1) |
| size | Integer | No | Page size (default: 10) |
| username | String | No | Username (fuzzy search) |
| status | Integer | No | User status (0=disabled, 1=enabled) |
| deptId | Long | No | Department ID |

**Response Example:**

```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "records": [
      {
        "id": 1,
        "username": "admin",
        "realName": "Admin User",
        "email": "admin@example.com",
        "phone": "13800138000",
        "deptId": 1,
        "deptName": "IT Department",
        "status": 1,
        "createTime": "2025-01-11T10:00:00Z"
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

#### Get User by ID

**Endpoint:** `GET /api/v1/users/{id}`

**Description:** Get user details by ID

**Path Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | Long | Yes | User ID |

**Response Example:**

```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": 1,
    "username": "admin",
    "realName": "Admin User",
    "email": "admin@example.com",
    "phone": "13800138000",
    "avatar": "https://example.com/avatar.jpg",
    "deptId": 1,
    "deptName": "IT Department",
    "roles": [
      {
        "id": 1,
        "roleCode": "admin",
        "roleName": "Administrator"
      }
    ],
    "status": 1,
    "createTime": "2025-01-11T10:00:00Z"
  }
}
```

#### Create User

**Endpoint:** `POST /api/v1/users`

**Description:** Create new user

**Request Body:**

```json
{
  "username": "newuser",
  "password": "Password123",
  "realName": "New User",
  "email": "newuser@example.com",
  "phone": "13900139000",
  "deptId": 1,
  "roleIds": [2, 3],
  "status": 1
}
```

**Response Example:**

```json
{
  "code": 200,
  "message": "User created successfully",
  "data": {
    "id": 2
  }
}
```

#### Update User

**Endpoint:** `PUT /api/v1/users/{id}`

**Description:** Update user information

**Request Body:**

```json
{
  "realName": "Updated Name",
  "email": "updated@example.com",
  "phone": "13900139001",
  "deptId": 2,
  "roleIds": [2],
  "status": 1
}
```

**Response Example:**

```json
{
  "code": 200,
  "message": "User updated successfully",
  "data": null
}
```

#### Delete User

**Endpoint:** `DELETE /api/v1/users/{id}`

**Description:** Delete user (soft delete)

**Response Example:**

```json
{
  "code": 200,
  "message": "User deleted successfully",
  "data": null
}
```

---

### Role Management APIs

#### Get Role List

**Endpoint:** `GET /api/v1/roles`

**Description:** Get role list

**Response Example:**

```json
{
  "code": 200,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "roleCode": "admin",
      "roleName": "Administrator",
      "description": "System administrator",
      "status": 1
    }
  ]
}
```

---

### Order Management APIs

#### Get Order List

**Endpoint:** `GET /api/v1/orders`

**Description:** Get paginated order list

**Request Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| page | Integer | No | Page number (default: 1) |
| size | Integer | No | Page size (default: 10) |
| orderNo | String | No | Order number |
| orderStatus | String | No | Order status |
| startTime | String | No | Start time (ISO 8601) |
| endTime | String | No | End time (ISO 8601) |

**Response Example:**

```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "records": [
      {
        "id": 1,
        "orderNo": "ORD2025011100001",
        "userId": 123,
        "userName": "John Doe",
        "totalAmount": 99.99,
        "orderStatus": "PAID",
        "paymentStatus": "PAID",
        "shippingStatus": "PENDING",
        "createTime": "2025-01-11T10:00:00Z"
      }
    ],
    "total": 50,
    "size": 10,
    "current": 1,
    "pages": 5
  }
}
```

---

## Authentication & Authorization

### Login API

**Endpoint:** `POST /api/v1/auth/login`

**Request Body:**

```json
{
  "username": "admin",
  "password": "password123"
}
```

**Response Example:**

```json
{
  "code": 200,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 7200,
    "user": {
      "id": 1,
      "username": "admin",
      "realName": "Admin User",
      "avatar": "https://example.com/avatar.jpg"
    }
  }
}
```

### Logout API

**Endpoint:** `POST /api/v1/auth/logout`

**Headers:**
```
Authorization: Bearer {token}
```

**Response Example:**

```json
{
  "code": 200,
  "message": "Logout successful",
  "data": null
}
```

### Refresh Token API

**Endpoint:** `POST /api/v1/auth/refresh`

**Request Body:**

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response Example:**

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

## API Documentation

### Swagger Configuration

```java
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("UniEnterprise API")
                .version("1.0.0")
                .description("UniEnterprise API Documentation"))
            .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
            .components(new Components()
                .addSecuritySchemes("Bearer Authentication",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
    }
}
```

### API Documentation Example

```java
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "User management APIs")
public class UserController {

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Get user details by user ID")
    @Parameter(name = "id", description = "User ID", required = true)
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "404", description = "User not found")
    public Result<UserVO> getUser(@PathVariable Long id) {
        UserVO user = userService.getUserById(id);
        return Result.success(user);
    }
}
```

---

## Version History

| Version | Date | Author | Changes |
|---------|-------|---------|----------|
| 1.0.0 | 2025-01-11 | Initial API interface design |
