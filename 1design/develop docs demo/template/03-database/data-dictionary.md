# Data Dictionary

## Overview

This document provides comprehensive data dictionary definitions for the UniEnterprise project, including table naming conventions, field descriptions, data types, enumeration values, and business rules.

---

## Naming Conventions

### Table Naming

| Convention | Description | Example |
|------------|-------------|---------|
| Prefix | Use module prefix | `sys_user`, `bpm_task`, `mall_order` |
| Format | Lowercase with underscores | `sys_user_role` |
| Association Tables | Use both table names | `sys_user_role` |
| Suffixes | Use standard suffixes | `_log`, `_config`, `_dict` |

### Field Naming

| Convention | Description | Example |
|------------|-------------|---------|
| Format | Lowercase with underscores | `user_name`, `create_time` |
| Primary Key | Use `id` | `id` |
| Foreign Key | Use `{table}_id` | `user_id`, `dept_id` |
| Boolean | Use `is_` prefix or `enabled` | `is_deleted`, `enabled` |
| Timestamps | Use `{action}_time` | `create_time`, `update_time` |

### Common Field Definitions

| Field Name | Type | Description | Default | Nullable |
|------------|------|-------------|---------|----------|
| `id` | BIGINT(20) | Primary key | AUTO_INCREMENT | NO |
| `create_time` | DATETIME | Create time | CURRENT_TIMESTAMP | NO |
| `update_time` | DATETIME | Update time | CURRENT_TIMESTAMP | NO |
| `create_by` | BIGINT(20) | Creator ID | - | YES |
| `update_by` | BIGINT(20) | Updater ID | - | YES |
| `deleted` | TINYINT(1) | Deleted flag (0=no, 1=yes) | 0 | NO |
| `tenant_id` | BIGINT(20) | Tenant ID (for multi-tenant) | 0 | NO |

---

## Data Types

### Common Data Types

| Type | Size | Range | Usage |
|------|------|-------|-------|
| BIGINT(20) | 8 bytes | -9.22×10¹⁸ to 9.22×10¹⁸ | Primary keys, IDs, large numbers |
| INT(11) | 4 bytes | -2.14×10⁹ to 2.14×10⁹ | Counts, small numbers, flags |
| DECIMAL(10,2) | Variable | Precision 10, scale 2 | Amounts, prices, money values |
| VARCHAR(255) | Variable | Max 255 characters | Names, codes, short text |
| VARCHAR(500) | Variable | Max 500 characters | Descriptions, medium text |
| TEXT | Variable | Max 65,535 characters | Long text, content |
| DATETIME | 8 bytes | '1000-01-01 00:00:00' to '9999-12-31 23:59:59' | Timestamps |
| TIMESTAMP | 4 bytes | '1970-01-01 00:00:01' UTC to '2038-01-19' UTC | Auto-update timestamps |
| TINYINT(1) | 1 byte | 0 to 255 | Boolean, small integers |
| DATE | 3 bytes | '1000-01-01' to '9999-12-31' | Dates |

### Data Type Selection Guidelines

| Scenario | Recommended Type | Reason |
|----------|------------------|--------|
| Primary key | BIGINT(20) | Large range, auto-increment |
| Money value | DECIMAL(10,2) | Exact precision, no rounding errors |
| Boolean flag | TINYINT(1) | Space efficient |
| Email address | VARCHAR(100) | Email length typically < 100 |
| URL | VARCHAR(500) | URLs can be long |
| Password hash | VARCHAR(100) | Hashed password length |
| JSON data | TEXT | JSON can be large |
| Enum values | TINYINT(1) | Store enum index, save space |

---

## Enumeration Values

### System Status Enums

| Enum Name | Value | Description |
|-----------|-------|-------------|
| **User Status** | | |
| DISABLED | 0 | Disabled |
| ENABLED | 1 | Enabled |
| **Deleted Flag** | | |
| NO | 0 | Not deleted |
| YES | 1 | Deleted |
| **Gender** | | |
| MALE | 1 | Male |
| FEMALE | 2 | Female |
| UNKNOWN | 0 | Unknown |

### Order Status Enums

| Enum Name | Value | Description |
|-----------|-------|-------------|
| **Order Status** | | |
| PENDING | 0 | Pending payment |
| PAID | 1 | Paid |
| SHIPPED | 2 | Shipped |
| COMPLETED | 3 | Completed |
| CANCELLED | 4 | Cancelled |
| REFUNDED | 5 | Refunded |
| **Payment Status** | | |
| UNPAID | 0 | Unpaid |
| PAYING | 1 | Paying |
| PAID | 2 | Paid |
| FAILED | 3 | Payment failed |
| REFUNDING | 4 | Refunding |
| REFUNDED | 5 | Refunded |

### Workflow Status Enums

| Enum Name | Value | Description |
|-----------|-------|-------------|
| **Task Status** | | |
| CREATED | 0 | Created |
| RUNNING | 1 | Running |
| COMPLETED | 2 | Completed |
| CANCELLED | 3 | Cancelled |
| **Process Status** | | |
| DRAFT | 0 | Draft |
| RUNNING | 1 | Running |
| COMPLETED | 2 | Completed |
| TERMINATED | 3 | Terminated |

---

## Field Descriptions by Table

### System Tables

#### sys_user

| Field Name | Type | Description | Constraints |
|------------|------|-------------|-------------|
| id | BIGINT(20) | Primary key | PK, Auto Increment |
| tenant_id | BIGINT(20) | Tenant ID | Index |
| username | VARCHAR(50) | Username | Unique (per tenant) |
| password | VARCHAR(100) | Password (BCrypt hashed) | Not null |
| real_name | VARCHAR(50) | Real name | - |
| email | VARCHAR(100) | Email address | Index |
| phone | VARCHAR(20) | Phone number | Index |
| avatar | VARCHAR(255) | Avatar URL | - |
| dept_id | BIGINT(20) | Department ID | FK |
| status | TINYINT(1) | Status (0=disabled, 1=enabled) | Default: 1 |
| deleted | TINYINT(1) | Deleted flag | Default: 0 |
| create_time | DATETIME | Create time | Auto |
| update_time | DATETIME | Update time | Auto |
| create_by | BIGINT(20) | Creator ID | - |
| update_by | BIGINT(20) | Updater ID | - |

#### sys_role

| Field Name | Type | Description | Constraints |
|------------|------|-------------|-------------|
| id | BIGINT(20) | Primary key | PK, Auto Increment |
| tenant_id | BIGINT(20) | Tenant ID | Index |
| role_code | VARCHAR(50) | Role code | Unique (per tenant) |
| role_name | VARCHAR(50) | Role name | Not null |
| description | VARCHAR(255) | Role description | - |
| status | TINYINT(1) | Status (0=disabled, 1=enabled) | Default: 1 |
| deleted | TINYINT(1) | Deleted flag | Default: 0 |
| create_time | DATETIME | Create time | Auto |
| update_time | DATETIME | Update time | Auto |
| create_by | BIGINT(20) | Creator ID | - |
| update_by | BIGINT(20) | Updater ID | - |

#### sys_menu

| Field Name | Type | Description | Constraints |
|------------|------|-------------|-------------|
| id | BIGINT(20) | Primary key | PK, Auto Increment |
| parent_id | BIGINT(20) | Parent menu ID | Index |
| menu_name | VARCHAR(50) | Menu name | Not null |
| menu_type | VARCHAR(10) | Menu type (directory/menu/button) | Not null |
| menu_code | VARCHAR(100) | Menu code/permission code | Index |
| path | VARCHAR(200) | Menu path | - |
| component | VARCHAR(200) | Component path | - |
| icon | VARCHAR(50) | Menu icon | - |
| sort_order | INT(11) | Sort order | Default: 0 |
| visible | TINYINT(1) | Visible (0=hidden, 1=visible) | Default: 1 |
| status | TINYINT(1) | Status (0=disabled, 1=enabled) | Default: 1 |
| deleted | TINYINT(1) | Deleted flag | Default: 0 |
| create_time | DATETIME | Create time | Auto |
| update_time | DATETIME | Update time | Auto |
| create_by | BIGINT(20) | Creator ID | - |
| update_by | BIGINT(20) | Updater ID | - |

#### sys_tenant

| Field Name | Type | Description | Constraints |
|------------|------|-------------|-------------|
| id | BIGINT(20) | Primary key | PK, Auto Increment |
| tenant_code | VARCHAR(50) | Tenant code | Unique |
| tenant_name | VARCHAR(100) | Tenant name | Not null |
| contact_name | VARCHAR(50) | Contact person | - |
| contact_phone | VARCHAR(20) | Contact phone | - |
| contact_email | VARCHAR(100) | Contact email | - |
| logo | VARCHAR(255) | Tenant logo URL | - |
| status | TINYINT(1) | Status (0=disabled, 1=enabled) | Default: 1 |
| expire_time | DATETIME | Expire time | - |
| deleted | TINYINT(1) | Deleted flag | Default: 0 |
| create_time | DATETIME | Create time | Auto |
| update_time | DATETIME | Update time | Auto |
| create_by | BIGINT(20) | Creator ID | - |
| update_by | BIGINT(20) | Updater ID | - |

### Business Tables

#### bpm_process

| Field Name | Type | Description | Constraints |
|------------|------|-------------|-------------|
| id | BIGINT(20) | Primary key | PK, Auto Increment |
| tenant_id | BIGINT(20) | Tenant ID | Index |
| process_key | VARCHAR(50) | Process key | Not null |
| process_name | VARCHAR(100) | Process name | Not null |
| category | VARCHAR(50) | Process category | - |
| version | INT(11) | Version | Default: 1 |
| deployment_id | VARCHAR(100) | Deployment ID | - |
| resource_name | VARCHAR(255) | Resource file name | - |
| diagram_resource_name | VARCHAR(255) | Diagram file name | - |
| description | TEXT | Process description | - |
| suspended | TINYINT(1) | Suspended (0=active, 1=suspended) | Default: 0 |
| deleted | TINYINT(1) | Deleted flag | Default: 0 |
| create_time | DATETIME | Create time | Auto |
| update_time | DATETIME | Update time | Auto |
| create_by | BIGINT(20) | Creator ID | - |
| update_by | BIGINT(20) | Updater ID | - |

#### pay_order

| Field Name | Type | Description | Constraints |
|------------|------|-------------|-------------|
| id | BIGINT(20) | Primary key | PK, Auto Increment |
| tenant_id | BIGINT(20) | Tenant ID | Index |
| order_no | VARCHAR(50) | Order number | Unique |
| order_type | VARCHAR(20) | Order type | Not null |
| business_type | VARCHAR(50) | Business type | - |
| business_id | VARCHAR(50) | Business ID | - |
| payment_channel | VARCHAR(20) | Payment channel (alipay/wechat/balance) | Not null |
| amount | DECIMAL(10,2) | Payment amount | Not null |
| currency | VARCHAR(10) | Currency | Default: CNY |
| status | VARCHAR(20) | Order status | Not null |
| transaction_id | VARCHAR(100) | Third-party transaction ID | - |
| payment_time | DATETIME | Payment time | - |
| notify_time | DATETIME | Notification time | - |
| notify_content | TEXT | Notification content | - |
| remark | VARCHAR(255) | Remark | - |
| deleted | TINYINT(1) | Deleted flag | Default: 0 |
| create_time | DATETIME | Create time | Auto |
| update_time | DATETIME | Update time | Auto |
| create_by | BIGINT(20) | Creator ID | - |
| update_by | BIGINT(20) | Updater ID | - |

#### mall_product

| Field Name | Type | Description | Constraints |
|------------|------|-------------|-------------|
| id | BIGINT(20) | Primary key | PK, Auto Increment |
| tenant_id | BIGINT(20) | Tenant ID | Index |
| product_code | VARCHAR(50) | Product code | Unique (per tenant) |
| product_name | VARCHAR(100) | Product name | Not null |
| category_id | BIGINT(20) | Category ID | FK |
| brand_id | BIGINT(20) | Brand ID | FK |
| main_image | VARCHAR(255) | Main image URL | - |
| images | TEXT | Product images (JSON array) | - |
| price | DECIMAL(10,2) | Product price | Not null |
| original_price | DECIMAL(10,2) | Original price | - |
| stock | INT(11) | Stock quantity | Default: 0 |
| sales | INT(11) | Sales count | Default: 0 |
| unit | VARCHAR(20) | Unit | - |
| weight | DECIMAL(10,2) | Weight (kg) | - |
| volume | DECIMAL(10,2) | Volume (m³) | - |
| description | TEXT | Product description | - |
| keywords | VARCHAR(255) | SEO keywords | - |
| sort_order | INT(11) | Sort order | Default: 0 |
| status | TINYINT(1) | Status (0=disabled, 1=enabled) | Default: 1 |
| deleted | TINYINT(1) | Deleted flag | Default: 0 |
| create_time | DATETIME | Create time | Auto |
| update_time | DATETIME | Update time | Auto |
| create_by | BIGINT(20) | Creator ID | - |
| update_by | BIGINT(20) | Updater ID | - |

#### mall_order

| Field Name | Type | Description | Constraints |
|------------|------|-------------|-------------|
| id | BIGINT(20) | Primary key | PK, Auto Increment |
| tenant_id | BIGINT(20) | Tenant ID | Index |
| order_no | VARCHAR(50) | Order number | Unique |
| user_id | BIGINT(20) | User ID | FK |
| order_status | VARCHAR(20) | Order status | Not null |
| payment_status | VARCHAR(20) | Payment status | Not null |
| shipping_status | VARCHAR(20) | Shipping status | Not null |
| total_amount | DECIMAL(10,2) | Total amount | Not null |
| payment_amount | DECIMAL(10,2) | Payment amount | - |
| discount_amount | DECIMAL(10,2) | Discount amount | Default: 0.00 |
| shipping_fee | DECIMAL(10,2) | Shipping fee | Default: 0.00 |
| receiver_name | VARCHAR(50) | Receiver name | Not null |
| receiver_phone | VARCHAR(20) | Receiver phone | Not null |
| receiver_address | VARCHAR(255) | Receiver address | Not null |
| payment_channel | VARCHAR(20) | Payment channel | - |
| payment_time | DATETIME | Payment time | - |
| shipping_time | DATETIME | Shipping time | - |
| finish_time | DATETIME | Finish time | - |
| cancel_time | DATETIME | Cancel time | - |
| cancel_reason | VARCHAR(255) | Cancel reason | - |
| remark | VARCHAR(255) | Remark | - |
| deleted | TINYINT(1) | Deleted flag | Default: 0 |
| create_time | DATETIME | Create time | Auto |
| update_time | DATETIME | Update time | Auto |

---

## Business Rules

### User Management Rules

1. **Username uniqueness**: Username must be unique within a tenant
2. **Email uniqueness**: Email must be unique within a tenant
3. **Password requirements**: Minimum 8 characters, must contain letters and numbers
4. **Default password**: New users should reset password on first login
5. **Delete protection**: System admin cannot be deleted

### Order Management Rules

1. **Order number**: Must be unique, format: ORD{yyyyMMdd}{HHmmss}{random}
2. **Payment timeout**: Orders unpaid for 30 minutes will be auto-cancelled
3. **Inventory check**: Stock must be sufficient before order creation
4. **Price validation**: Order amount must match product price × quantity
5. **Shipping address**: Must be complete and valid

### Workflow Rules

1. **Process version**: Process definitions support versioning
2. **Task assignment**: Tasks can be assigned to users or groups
3. **Approval chain**: Approval chain follows defined process flow
4. **Counter-sign**: Multiple users can review in parallel
5. **Delegation**: Tasks can be delegated to other users

### Payment Rules

1. **Payment channels**: Support Stripe, PayPal, Braintree, Square, and balance payment
2. **Order amount**: Must match payment request amount
3. **Refund rules**: Refunds only allowed within 7 days of order completion
4. **Timeout**: Payment timeout is 30 minutes
5. **Retry**: Failed payment can be retried up to 3 times

---

## Index Strategy

### Index Design Principles

1. Create indexes on frequently queried columns
2. Create indexes on foreign keys
3. Create composite indexes for multi-column queries
4. Limit number of indexes per table (max 5-7)
5. Monitor index usage regularly

### Common Indexes

| Table | Index Type | Columns | Description |
|-------|------------|---------|-------------|
| sys_user | UNIQUE | tenant_id, username | Tenant + username |
| sys_user | INDEX | tenant_id | Tenant filtering |
| sys_user | INDEX | dept_id | Department filtering |
| sys_role | UNIQUE | tenant_id, role_code | Tenant + role code |
| sys_menu | INDEX | parent_id | Parent menu filtering |
| pay_order | UNIQUE | order_no | Order number |
| pay_order | INDEX | business_type, business_id | Business filtering |
| mall_order | UNIQUE | order_no | Order number |
| mall_order | INDEX | user_id | User filtering |
| mall_order | INDEX | order_status | Status filtering |

---

## Version History

| Version | Date | Author | Changes |
|---------|-------|---------|----------|
| 1.0.0 | 2025-01-11 | Initial data dictionary |
