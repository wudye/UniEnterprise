# Database Design

## Overview

This document defines the database schema design for the UniEnterprise project, including table structures, relationships, indexes, and data types. The design follows normalized database principles while considering performance optimization.

---

## Design Principles

### Database Standards

- **Naming Convention**: snake_case for tables and columns
- **Primary Keys**: Auto-increment `id` for all tables
- **Audit Fields**: `create_time`, `update_time`, `create_by`, `update_by`
- **Logical Delete**: `deleted` flag (0=active, 1=deleted)
- **Tenant Isolation**: `tenant_id` for multi-tenant tables
- **Soft Delete**: Use logical delete instead of physical delete

### Data Types

| Type | Description | Usage |
|------|-------------|-------|
| BIGINT(20) | Large integer | Primary keys, IDs |
| INT(11) | Standard integer | Counts, small numbers |
| VARCHAR(255) | Variable string | Names, codes |
| TEXT | Long text | Descriptions, content |
| DATETIME | Date and time | Timestamps |
| DECIMAL(10,2) | Decimal numbers | Amounts, prices |
| TINYINT(1) | Boolean | Flags, status |

---

## System Tables

### sys_user

User account information table.

```sql
CREATE TABLE `sys_user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
  `tenant_id` BIGINT(20) NOT NULL DEFAULT '0' COMMENT 'Tenant ID',
  `username` VARCHAR(50) NOT NULL COMMENT 'Username',
  `password` VARCHAR(100) NOT NULL COMMENT 'Password (hashed)',
  `real_name` VARCHAR(50) COMMENT 'Real name',
  `email` VARCHAR(100) COMMENT 'Email address',
  `phone` VARCHAR(20) COMMENT 'Phone number',
  `avatar` VARCHAR(255) COMMENT 'Avatar URL',
  `dept_id` BIGINT(20) COMMENT 'Department ID',
  `status` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Status (0=disabled, 1=enabled)',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT 'Deleted (0=no, 1=yes)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
  `create_by` BIGINT(20) COMMENT 'Creator ID',
  `update_by` BIGINT(20) COMMENT 'Updater ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_username` (`tenant_id`, `username`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User account table';
```

### sys_role

Role definition table.

```sql
CREATE TABLE `sys_role` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
  `tenant_id` BIGINT(20) NOT NULL DEFAULT '0' COMMENT 'Tenant ID',
  `role_code` VARCHAR(50) NOT NULL COMMENT 'Role code',
  `role_name` VARCHAR(50) NOT NULL COMMENT 'Role name',
  `description` VARCHAR(255) COMMENT 'Role description',
  `status` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Status (0=disabled, 1=enabled)',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT 'Deleted (0=no, 1=yes)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
  `create_by` BIGINT(20) COMMENT 'Creator ID',
  `update_by` BIGINT(20) COMMENT 'Updater ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_code` (`tenant_id`, `role_code`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Role definition table';
```

### sys_menu

Menu and permission table.

```sql
CREATE TABLE `sys_menu` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
  `parent_id` BIGINT(20) NOT NULL DEFAULT '0' COMMENT 'Parent menu ID',
  `menu_name` VARCHAR(50) NOT NULL COMMENT 'Menu name',
  `menu_type` VARCHAR(10) NOT NULL COMMENT 'Menu type (directory/menu/button)',
  `menu_code` VARCHAR(100) COMMENT 'Menu code/permission code',
  `path` VARCHAR(200) COMMENT 'Menu path',
  `component` VARCHAR(200) COMMENT 'Component path',
  `icon` VARCHAR(50) COMMENT 'Menu icon',
  `sort_order` INT(11) NOT NULL DEFAULT '0' COMMENT 'Sort order',
  `visible` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Visible (0=hidden, 1=visible)',
  `status` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Status (0=disabled, 1=enabled)',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT 'Deleted (0=no, 1=yes)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
  `create_by` BIGINT(20) COMMENT 'Creator ID',
  `update_by` BIGINT(20) COMMENT 'Updater ID',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_menu_code` (`menu_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Menu and permission table';
```

### sys_dept

Department organization table.

```sql
CREATE TABLE `sys_dept` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
  `tenant_id` BIGINT(20) NOT NULL DEFAULT '0' COMMENT 'Tenant ID',
  `parent_id` BIGINT(20) NOT NULL DEFAULT '0' COMMENT 'Parent department ID',
  `dept_name` VARCHAR(50) NOT NULL COMMENT 'Department name',
  `dept_code` VARCHAR(50) COMMENT 'Department code',
  `leader` BIGINT(20) COMMENT 'Department leader user ID',
  `phone` VARCHAR(20) COMMENT 'Contact phone',
  `email` VARCHAR(100) COMMENT 'Contact email',
  `address` VARCHAR(255) COMMENT 'Department address',
  `sort_order` INT(11) NOT NULL DEFAULT '0' COMMENT 'Sort order',
  `status` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Status (0=disabled, 1=enabled)',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT 'Deleted (0=no, 1=yes)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
  `create_by` BIGINT(20) COMMENT 'Creator ID',
  `update_by` BIGINT(20) COMMENT 'Updater ID',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Department organization table';
```

### sys_user_role

User-role association table.

```sql
CREATE TABLE `sys_user_role` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
  `user_id` BIGINT(20) NOT NULL COMMENT 'User ID',
  `role_id` BIGINT(20) NOT NULL COMMENT 'Role ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User-role association table';
```

### sys_role_menu

Role-menu association table.

```sql
CREATE TABLE `sys_role_menu` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
  `role_id` BIGINT(20) NOT NULL COMMENT 'Role ID',
  `menu_id` BIGINT(20) NOT NULL COMMENT 'Menu ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Role-menu association table';
```

### sys_tenant

Multi-tenant information table.

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Multi-tenant information table';
```

---

## Business Tables

### bpm_process

Workflow process definition table.

```sql
CREATE TABLE `bpm_process` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
  `tenant_id` BIGINT(20) NOT NULL DEFAULT '0' COMMENT 'Tenant ID',
  `process_key` VARCHAR(50) NOT NULL COMMENT 'Process key',
  `process_name` VARCHAR(100) NOT NULL COMMENT 'Process name',
  `category` VARCHAR(50) COMMENT 'Process category',
  `version` INT(11) NOT NULL DEFAULT '1' COMMENT 'Version',
  `deployment_id` VARCHAR(100) COMMENT 'Deployment ID',
  `resource_name` VARCHAR(255) COMMENT 'Resource file name',
  `diagram_resource_name` VARCHAR(255) COMMENT 'Diagram file name',
  `description` TEXT COMMENT 'Process description',
  `suspended` TINYINT(1) NOT NULL DEFAULT '0' COMMENT 'Suspended (0=active, 1=suspended)',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT 'Deleted (0=no, 1=yes)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
  `create_by` BIGINT(20) COMMENT 'Creator ID',
  `update_by` BIGINT(20) COMMENT 'Updater ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_key_version` (`process_key`, `version`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Workflow process definition table';
```

### bpm_task

Workflow task table.

```sql
CREATE TABLE `bpm_task` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
  `tenant_id` BIGINT(20) NOT NULL DEFAULT '0' COMMENT 'Tenant ID',
  `process_instance_id` VARCHAR(100) NOT NULL COMMENT 'Process instance ID',
  `task_key` VARCHAR(50) NOT NULL COMMENT 'Task key',
  `task_name` VARCHAR(100) NOT NULL COMMENT 'Task name',
  `assignee` BIGINT(20) COMMENT 'Assignee user ID',
  `candidate_users` TEXT COMMENT 'Candidate user IDs (comma separated)',
  `candidate_groups` TEXT COMMENT 'Candidate group IDs (comma separated)',
  `priority` INT(11) NOT NULL DEFAULT '50' COMMENT 'Priority',
  `due_date` DATETIME COMMENT 'Due date',
  `status` VARCHAR(20) NOT NULL COMMENT 'Task status',
  `start_time` DATETIME COMMENT 'Start time',
  `end_time` DATETIME COMMENT 'End time',
  `delete_reason` VARCHAR(255) COMMENT 'Delete reason',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_process_instance` (`process_instance_id`),
  KEY `idx_assignee` (`assignee`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Workflow task table';
```

### pay_order

Payment order table.

```sql
CREATE TABLE `pay_order` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
  `tenant_id` BIGINT(20) NOT NULL DEFAULT '0' COMMENT 'Tenant ID',
  `order_no` VARCHAR(50) NOT NULL COMMENT 'Order number',
  `order_type` VARCHAR(20) NOT NULL COMMENT 'Order type',
  `business_type` VARCHAR(50) COMMENT 'Business type',
  `business_id` VARCHAR(50) COMMENT 'Business ID',
  `payment_channel` VARCHAR(20) NOT NULL COMMENT 'Payment channel (alipay/wechat/balance)',
  `amount` DECIMAL(10,2) NOT NULL COMMENT 'Payment amount',
  `currency` VARCHAR(10) NOT NULL DEFAULT 'CNY' COMMENT 'Currency',
  `status` VARCHAR(20) NOT NULL COMMENT 'Order status',
  `transaction_id` VARCHAR(100) COMMENT 'Third-party transaction ID',
  `payment_time` DATETIME COMMENT 'Payment time',
  `notify_time` DATETIME COMMENT 'Notification time',
  `notify_content` TEXT COMMENT 'Notification content',
  `remark` VARCHAR(255) COMMENT 'Remark',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT 'Deleted (0=no, 1=yes)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
  `create_by` BIGINT(20) COMMENT 'Creator ID',
  `update_by` BIGINT(20) COMMENT 'Updater ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_business` (`business_type`, `business_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Payment order table';
```

### mall_product

Product information table.

```sql
CREATE TABLE `mall_product` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
  `tenant_id` BIGINT(20) NOT NULL DEFAULT '0' COMMENT 'Tenant ID',
  `product_code` VARCHAR(50) NOT NULL COMMENT 'Product code',
  `product_name` VARCHAR(100) NOT NULL COMMENT 'Product name',
  `category_id` BIGINT(20) COMMENT 'Category ID',
  `brand_id` BIGINT(20) COMMENT 'Brand ID',
  `main_image` VARCHAR(255) COMMENT 'Main image URL',
  `images` TEXT COMMENT 'Product images (JSON array)',
  `price` DECIMAL(10,2) NOT NULL COMMENT 'Product price',
  `original_price` DECIMAL(10,2) COMMENT 'Original price',
  `stock` INT(11) NOT NULL DEFAULT '0' COMMENT 'Stock quantity',
  `sales` INT(11) NOT NULL DEFAULT '0' COMMENT 'Sales count',
  `unit` VARCHAR(20) COMMENT 'Unit',
  `weight` DECIMAL(10,2) COMMENT 'Weight (kg)',
  `volume` DECIMAL(10,2) COMMENT 'Volume (m³)',
  `description` TEXT COMMENT 'Product description',
  `keywords` VARCHAR(255) COMMENT 'SEO keywords',
  `sort_order` INT(11) NOT NULL DEFAULT '0' COMMENT 'Sort order',
  `status` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'Status (0=disabled, 1=enabled)',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT 'Deleted (0=no, 1=yes)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
  `create_by` BIGINT(20) COMMENT 'Creator ID',
  `update_by` BIGINT(20) COMMENT 'Updater ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_code` (`tenant_id`, `product_code`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Product information table';
```

### mall_order

Order information table.

```sql
CREATE TABLE `mall_order` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
  `tenant_id` BIGINT(20) NOT NULL DEFAULT '0' COMMENT 'Tenant ID',
  `order_no` VARCHAR(50) NOT NULL COMMENT 'Order number',
  `user_id` BIGINT(20) NOT NULL COMMENT 'User ID',
  `order_status` VARCHAR(20) NOT NULL COMMENT 'Order status',
  `payment_status` VARCHAR(20) NOT NULL COMMENT 'Payment status',
  `shipping_status` VARCHAR(20) NOT NULL COMMENT 'Shipping status',
  `total_amount` DECIMAL(10,2) NOT NULL COMMENT 'Total amount',
  `payment_amount` DECIMAL(10,2) COMMENT 'Payment amount',
  `discount_amount` DECIMAL(10,2) NOT NULL DEFAULT '0.00' COMMENT 'Discount amount',
  `shipping_fee` DECIMAL(10,2) NOT NULL DEFAULT '0.00' COMMENT 'Shipping fee',
  `receiver_name` VARCHAR(50) NOT NULL COMMENT 'Receiver name',
  `receiver_phone` VARCHAR(20) NOT NULL COMMENT 'Receiver phone',
  `receiver_address` VARCHAR(255) NOT NULL COMMENT 'Receiver address',
  `payment_channel` VARCHAR(20) COMMENT 'Payment channel',
  `payment_time` DATETIME COMMENT 'Payment time',
  `shipping_time` DATETIME COMMENT 'Shipping time',
  `finish_time` DATETIME COMMENT 'Finish time',
  `cancel_time` DATETIME COMMENT 'Cancel time',
  `cancel_reason` VARCHAR(255) COMMENT 'Cancel reason',
  `remark` VARCHAR(255) COMMENT 'Remark',
  `deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT 'Deleted (0=no, 1=yes)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_order_status` (`order_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Order information table';
```

---

## Index Design Guidelines

### Index Creation Rules

1. Create indexes on frequently queried columns
2. Create indexes on foreign keys
3. Create composite indexes for multi-column queries
4. Limit number of indexes per table (max 5-7)
5. Monitor index usage regularly

### Common Indexes

| Table | Index Name | Columns | Type |
|-------|------------|---------|------|
| sys_user | uk_tenant_username | tenant_id, username | UNIQUE |
| sys_user | idx_tenant_id | tenant_id | NORMAL |
| sys_role | uk_tenant_code | tenant_id, role_code | UNIQUE |
| sys_menu | idx_menu_code | menu_code | NORMAL |
| pay_order | uk_order_no | order_no | UNIQUE |
| pay_order | idx_business | business_type, business_id | NORMAL |
| mall_order | uk_order_no | order_no | UNIQUE |
| mall_order | idx_user_id | user_id | NORMAL |

---

## Data Migration

### Version Control

- Use Flyway or Liquibase for database version control
- Each migration script is numbered sequentially
- Never modify existing migration scripts

### Migration Script Example

```sql
-- V1.0.1__create_user_table.sql
CREATE TABLE `sys_user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  -- ... other columns
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## Performance Optimization

### Partitioning Strategy

- Partition large tables by date range
- Partition monthly for time-series data

### Caching Strategy

- Cache frequently accessed data in Redis
- Use cache warming for critical data

### Query Optimization

- Use EXPLAIN to analyze query performance
- Optimize slow queries regularly
- Use connection pooling

---

## Version History

| Version | Date | Author | Changes |
|---------|-------|---------|----------|
| 1.0.0 | 2025-01-11 | Initial database design |
