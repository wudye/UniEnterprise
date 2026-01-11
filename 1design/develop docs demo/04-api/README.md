# 04-API

This folder contains all API-related documents.

## Folder Structure

```
04-api/
├── README.md                                    # This file
├── API-Design-Document.md                       # API Design Document
├── API-Specifications.md                          # API Specifications (OpenAPI/Swagger)
├── RESTful-API-Standards.md                      # RESTful API Standards
├── API-Versioning-Strategy.md                     # API Versioning Strategy
├── API-Security-Guide.md                        # API Security Guide
├── API-Error-Codes.md                          # API Error Codes
├── API-Testing-Guide.md                         # API Testing Guide
├── Postman-Collections.md                        # Postman Collections
└── Mock-API-Documentation.md                     # Mock API Documentation
```

## Document Descriptions

| Document | Description | Author | Status |
|----------|-------------|---------|---------|
| API Design Document | API design principles and guidelines | - | Pending |
| API Specifications | Complete API specifications (OpenAPI/Swagger) | - | Pending |
| RESTful API Standards | RESTful API standards and conventions | - | Pending |
| API Versioning Strategy | API versioning strategy and guidelines | - | Pending |
| API Security Guide | API security best practices | - | Pending |
| API Error Codes | API error codes and handling | - | Pending |
| API Testing Guide | API testing guide and best practices | - | Pending |
| Postman Collections | Postman collections for API testing | - | Pending |
| Mock API Documentation | Mock API documentation for development | - | Pending |

## API Overview

### API Architecture

```
┌─────────────────────────────────────────────────┐
│              Frontend Applications            │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐ │
│  │   Admin  │  │   H5 Web  │  │Mini Apps │ │
│  └──────────┘  └──────────┘  └──────────┘ │
└─────────────────────────────────────────────────┘
                    ↓ HTTPS
┌─────────────────────────────────────────────────┐
│              API Gateway                     │
│          (Spring Cloud Gateway)               │
└─────────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────────┐
│              API Services                    │
│  ┌────────┐ ┌────────┐ ┌────────┐  │
│  │Auth   │ │System  │ │Business│  │
│  │Service│ │Service │ │Service │  │
│  └────────┘ └────────┘ └────────┘  │
└─────────────────────────────────────────────────┘
```

### API Types

| API Type | Description | Authentication |
|-----------|-------------|------------------|
| Public API | Public accessible APIs | None |
| User API | User-specific APIs | JWT |
| Admin API | Admin-specific APIs | JWT + Role |
| Internal API | Internal service APIs | Service-to-Service |

## RESTful API Standards

### HTTP Methods

| Method | Description | Example |
|---------|-------------|----------|
| GET | Retrieve resources | GET /api/v1/users |
| POST | Create resources | POST /api/v1/users |
| PUT | Update resources (full) | PUT /api/v1/users/{id} |
| PATCH | Update resources (partial) | PATCH /api/v1/users/{id} |
| DELETE | Delete resources | DELETE /api/v1/users/{id} |

### URL Structure

```
/api/{version}/{resource}/{id}/{sub-resource}
```

**Examples**:
- GET /api/v1/users
- GET /api/v1/users/{id}
- GET /api/v1/users/{id}/roles
- POST /api/v1/users

### HTTP Status Codes

| Status Code | Description |
|-------------|-------------|
| 200 OK | Request successful |
| 201 Created | Resource created |
| 204 No Content | Request successful, no content returned |
| 400 Bad Request | Invalid request |
| 401 Unauthorized | Authentication failed |
| 403 Forbidden | Permission denied |
| 404 Not Found | Resource not found |
| 409 Conflict | Resource conflict |
| 500 Internal Server Error | Server error |

## Response Format

### Success Response

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "email": "admin@example.com"
  },
  "timestamp": 1234567890
}
```

### Error Response

```json
{
  "code": 400,
  "message": "Invalid request",
  "data": null,
  "errors": [
    {
      "field": "username",
      "message": "Username is required"
    }
  ],
  "timestamp": 1234567890
}
```

### Paginated Response

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "items": [
      {
        "id": 1,
        "username": "admin"
      }
    ],
    "total": 100,
    "page": 1,
    "size": 10,
    "pages": 10
  },
  "timestamp": 1234567890
}
```

## API Versioning

### Versioning Strategy

URL-based versioning:
```
/api/v1/users
/api/v2/users
```

### Version Lifecycle

| Version | Status | Support End Date |
|---------|--------|-----------------|
| v1 | Deprecated | 2025-12-31 |
| v2 | Current | - |
| v3 | Beta | - |

## API Security

### Authentication

- JWT Token Authentication
- OAuth 2.0
- API Key (for third-party integration)

### Authorization

- Role-Based Access Control (RBAC)
- Permission-based access
- Data permissions

### Rate Limiting

| API Type | Rate Limit |
|-----------|-----------|
| Public API | 100 requests/minute |
| User API | 1000 requests/minute |
| Admin API | 500 requests/minute |

## API Documentation Tools

- **Swagger UI**: `/swagger-ui.html`
- **OpenAPI Spec**: `/api-docs`
- **Postman**: Available in repository

## API Testing

### Testing Tools

- **Postman**: Manual testing and collection
- **Swagger UI**: Interactive API documentation
- **JMeter**: Load testing
- **Rest Assured**: Automated API testing (Java)

### Testing Strategy

1. **Unit Tests**: Test API endpoints in isolation
2. **Integration Tests**: Test API with database
3. **Contract Tests**: Test API contracts with consumers
4. **Load Tests**: Test API performance under load
5. **Security Tests**: Test API security vulnerabilities

## Document Templates

### 1. API Design Document
- **File**: `API-Design-Document.md`
- **Purpose**: API design principles and guidelines
- **Audience**: Architects, Developers
- **Sections**: API Principles, Design Patterns, Best Practices

### 2. API Specifications
- **File**: `API-Specifications.md`
- **Purpose**: Complete API specifications
- **Audience**: Developers, Testers
- **Sections**: API List, Endpoint Details, Request/Response Examples

### 3. RESTful API Standards
- **File**: `RESTful-API-Standards.md`
- **Purpose**: RESTful API standards and conventions
- **Audience**: Developers
- **Sections**: HTTP Methods, URL Structure, Status Codes, Response Format

### 4. API Versioning Strategy
- **File**: `API-Versioning-Strategy.md`
- **Purpose**: API versioning strategy and guidelines
- **Audience**: Architects, Developers
- **Sections**: Versioning Strategy, Version Lifecycle, Migration Guide

### 5. API Security Guide
- **File**: `API-Security-Guide.md`
- **Purpose**: API security best practices
- **Audience**: Developers, Security Engineers
- **Sections**: Authentication, Authorization, Rate Limiting, Security Headers

### 6. API Error Codes
- **File**: `API-Error-Codes.md`
- **Purpose**: API error codes and handling
- **Audience**: Developers, Testers
- **Sections**: Error Code List, Error Handling, Best Practices

### 7. API Testing Guide
- **File**: `API-Testing-Guide.md`
- **Purpose**: API testing guide and best practices
- **Audience**: Testers, Developers
- **Sections**: Testing Strategy, Testing Tools, Test Cases

### 8. Postman Collections
- **File**: `Postman-Collections.md`
- **Purpose**: Postman collections for API testing
- **Audience**: Testers, Developers
- **Sections**: Collection List, Environment Setup, Usage Guide

### 9. Mock API Documentation
- **File**: `Mock-API-Documentation.md`
- **Purpose**: Mock API documentation for development
- **Audience**: Developers
- **Sections**: Mock Server Setup, Mock Data, Usage Guide

## API Change Management

### Change Process

1. **Proposal**: Submit API change proposal
2. **Review**: API change review by team
3. **Approval**: Change approved by API owner
4. **Implementation**: Implement API change
5. **Testing**: Test API change
6. **Documentation**: Update API documentation
7. **Release**: Release new API version

### Breaking Changes

Breaking changes require:
- New major version
- Advance notice (minimum 3 months)
- Migration guide
- Support period for old version (minimum 6 months)

---

**Last Updated**: 2024-XX-XX  
**Maintained By**: API Team
