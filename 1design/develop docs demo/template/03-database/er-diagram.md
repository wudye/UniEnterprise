# ER Diagram

## Overview

This document describes the entity-relationship (ER) diagram for the UniEnterprise project database schema.

---

## ER Diagram (Mermaid)

### System Management

```mermaid
erDiagram
    sys_user ||--o{ sys_user_role : "has"
    sys_role ||--o{ sys_user_role : "assigned to"
    sys_role ||--o{ sys_role_menu : "has"
    sys_menu ||--o{ sys_role_menu : "assigned to"
    sys_dept ||--o{ sys_user : "contains"
    sys_tenant ||--o{ sys_user : "belongs to"
    sys_tenant ||--o{ sys_role : "belongs to"

    sys_user {
        bigint id PK
        bigint tenant_id FK
        string username
        string password
        string real_name
        string email
        string phone
        string avatar
        bigint dept_id FK
        tinyint status
        tinyint deleted
        datetime create_time
        datetime update_time
    }

    sys_role {
        bigint id PK
        bigint tenant_id FK
        string role_code
        string role_name
        string description
        tinyint status
        tinyint deleted
        datetime create_time
        datetime update_time
    }

    sys_menu {
        bigint id PK
        bigint parent_id FK
        string menu_name
        string menu_type
        string menu_code
        string path
        string component
        string icon
        int sort_order
        tinyint visible
        tinyint status
        tinyint deleted
        datetime create_time
        datetime update_time
    }

    sys_dept {
        bigint id PK
        bigint tenant_id FK
        bigint parent_id FK
        string dept_name
        string dept_code
        bigint leader FK
        string phone
        string email
        string address
        int sort_order
        tinyint status
        tinyint deleted
        datetime create_time
        datetime update_time
    }

    sys_user_role {
        bigint id PK
        bigint user_id FK
        bigint role_id FK
        datetime create_time
    }

    sys_role_menu {
        bigint id PK
        bigint role_id FK
        bigint menu_id FK
        datetime create_time
    }

    sys_tenant {
        bigint id PK
        string tenant_code UK
        string tenant_name
        string contact_name
        string contact_phone
        string contact_email
        string logo
        tinyint status
        datetime expire_time
        tinyint deleted
        datetime create_time
        datetime update_time
    }
```

### Workflow Management

```mermaid
erDiagram
    sys_user ||--o{ bpm_task : "assigned to"
    sys_user ||--o{ bpm_process : "created by"
    bpm_process ||--o{ bpm_task : "has"
    bpm_task ||--o{ bpm_comment : "has"

    bpm_process {
        bigint id PK
        bigint tenant_id FK
        string process_key
        string process_name
        string category
        int version
        string deployment_id
        string resource_name
        string diagram_resource_name
        text description
        tinyint suspended
        tinyint deleted
        datetime create_time
        datetime update_time
        bigint create_by FK
        bigint update_by FK
    }

    bpm_task {
        bigint id PK
        bigint tenant_id FK
        string process_instance_id
        string task_key
        string task_name
        bigint assignee FK
        text candidate_users
        text candidate_groups
        int priority
        datetime due_date
        string status
        datetime start_time
        datetime end_time
        string delete_reason
        datetime create_time
        datetime update_time
    }

    bpm_comment {
        bigint id PK
        bigint task_id FK
        bigint user_id FK
        string type
        text message
        datetime create_time
    }
```

### Payment Management

```mermaid
erDiagram
    sys_user ||--o{ pay_order : "creates"
    pay_order ||--o{ pay_transaction : "has"

    pay_order {
        bigint id PK
        bigint tenant_id FK
        string order_no UK
        string order_type
        string business_type
        string business_id
        string payment_channel
        decimal amount
        string currency
        string status
        string transaction_id
        datetime payment_time
        datetime notify_time
        text notify_content
        string remark
        tinyint deleted
        datetime create_time
        datetime update_time
        bigint create_by FK
        bigint update_by FK
    }

    pay_transaction {
        bigint id PK
        bigint order_id FK
        string transaction_no UK
        string channel
        string type
        decimal amount
        string status
        text request_data
        text response_data
        datetime create_time
    }
```

### E-Commerce Management

```mermaid
erDiagram
    sys_user ||--o{ mall_order : "places"
    sys_user ||--o{ mall_cart : "has"
    mall_category ||--o{ mall_product : "contains"
    mall_brand ||--o{ mall_product : "has"
    mall_product ||--o{ mall_order_item : "in"
    mall_product ||--o{ mall_cart_item : "in"
    mall_order ||--o{ mall_order_item : "contains"
    mall_order ||--o{ mall_order_log : "has"

    mall_category {
        bigint id PK
        bigint parent_id FK
        string category_name
        string category_code
        string icon
        int sort_order
        tinyint status
        tinyint deleted
        datetime create_time
        datetime update_time
    }

    mall_brand {
        bigint id PK
        string brand_name
        string brand_code
        string logo
        string description
        tinyint status
        tinyint deleted
        datetime create_time
        datetime update_time
    }

    mall_product {
        bigint id PK
        bigint tenant_id FK
        string product_code UK
        string product_name
        bigint category_id FK
        bigint brand_id FK
        string main_image
        text images
        decimal price
        decimal original_price
        int stock
        int sales
        string unit
        decimal weight
        decimal volume
        text description
        string keywords
        int sort_order
        tinyint status
        tinyint deleted
        datetime create_time
        datetime update_time
        bigint create_by FK
        bigint update_by FK
    }

    mall_order {
        bigint id PK
        bigint tenant_id FK
        string order_no UK
        bigint user_id FK
        string order_status
        string payment_status
        string shipping_status
        decimal total_amount
        decimal payment_amount
        decimal discount_amount
        decimal shipping_fee
        string receiver_name
        string receiver_phone
        string receiver_address
        string payment_channel
        datetime payment_time
        datetime shipping_time
        datetime finish_time
        datetime cancel_time
        string cancel_reason
        string remark
        tinyint deleted
        datetime create_time
        datetime update_time
    }

    mall_order_item {
        bigint id PK
        bigint order_id FK
        bigint product_id FK
        string product_name
        string product_image
        decimal price
        decimal original_price
        int quantity
        decimal total_amount
    }

    mall_cart {
        bigint id PK
        bigint user_id FK
        datetime create_time
        datetime update_time
    }

    mall_cart_item {
        bigint id PK
        bigint cart_id FK
        bigint product_id FK
        int quantity
        decimal price
        datetime create_time
        datetime update_time
    }

    mall_order_log {
        bigint id PK
        bigint order_id FK
        string action
        string status_from
        string status_to
        string remark
        bigint operator_id FK
        datetime create_time
    }
```

### CRM Management

```mermaid
erDiagram
    sys_user ||--o{ crm_lead : "manages"
    sys_user ||--o{ crm_customer : "manages"
    sys_user ||--o{ crm_opportunity : "manages"
    crm_lead ||--|| crm_customer : "converts to"
    crm_customer ||--o{ crm_opportunity : "has"
    crm_opportunity ||--o{ crm_contract : "results in"
    sys_user ||--o{ crm_contract : "signs"
    crm_contract ||--o{ crm_payment : "has"

    crm_lead {
        bigint id PK
        bigint tenant_id FK
        bigint owner_id FK
        string lead_name
        string company_name
        string phone
        string email
        string source
        string status
        int score
        text remark
        tinyint deleted
        datetime create_time
        datetime update_time
    }

    crm_customer {
        bigint id PK
        bigint tenant_id FK
        bigint owner_id FK
        string customer_name
        string company_name
        string contact_person
        string phone
        string email
        string address
        string industry
        int level
        text remark
        tinyint deleted
        datetime create_time
        datetime update_time
    }

    crm_opportunity {
        bigint id PK
        bigint tenant_id FK
        bigint customer_id FK
        bigint owner_id FK
        string opportunity_name
        decimal expected_amount
        datetime expected_close_date
        string stage
        int probability
        text description
        tinyint deleted
        datetime create_time
        datetime update_time
    }

    crm_contract {
        bigint id PK
        bigint tenant_id FK
        bigint customer_id FK
        bigint owner_id FK
        bigint opportunity_id FK
        string contract_no UK
        string contract_name
        decimal amount
        datetime start_date
        datetime end_date
        string status
        string file_url
        text remark
        tinyint deleted
        datetime create_time
        datetime update_time
    }

    crm_payment {
        bigint id PK
        bigint tenant_id FK
        bigint contract_id FK
        string payment_no UK
        decimal amount
        datetime payment_date
        string payment_method
        string status
        text remark
        tinyint deleted
        datetime create_time
        datetime update_time
    }
```

### ERP Management

```mermaid
erDiagram
    sys_user ||--o{ erp_supplier : "manages"
    sys_user ||--o{ erp_purchase_order : "creates"
    erp_supplier ||--o{ erp_purchase_order : "provides"
    erp_supplier ||--o{ erp_product : "supplies"
    erp_purchase_order ||--o{ erp_purchase_order_item : "contains"
    erp_product ||--o{ erp_purchase_order_item : "is"

    erp_supplier {
        bigint id PK
        bigint tenant_id FK
        string supplier_code UK
        string supplier_name
        string contact_person
        string phone
        string email
        string address
        tinyint status
        tinyint deleted
        datetime create_time
        datetime update_time
    }

    erp_product {
        bigint id PK
        bigint tenant_id FK
        string product_code UK
        string product_name
        string specification
        string unit
        decimal price
        int stock
        int safety_stock
        tinyint status
        tinyint deleted
        datetime create_time
        datetime update_time
    }

    erp_purchase_order {
        bigint id PK
        bigint tenant_id FK
        bigint supplier_id FK
        string order_no UK
        datetime order_date
        decimal total_amount
        string status
        text remark
        tinyint deleted
        datetime create_time
        datetime update_time
    }

    erp_purchase_order_item {
        bigint id PK
        bigint order_id FK
        bigint product_id FK
        decimal price
        int quantity
        decimal total_amount
    }
```

---

## Relationships Summary

### Primary Relationships

| From Table | To Table | Relationship | Description |
|-------------|-----------|--------------|-------------|
| sys_user | sys_user_role | One-to-Many | User can have multiple roles |
| sys_role | sys_user_role | One-to-Many | Role can be assigned to multiple users |
| sys_role | sys_role_menu | One-to-Many | Role can have multiple menus |
| sys_menu | sys_role_menu | One-to-Many | Menu can be assigned to multiple roles |
| sys_dept | sys_user | One-to-Many | Department can have multiple users |
| sys_tenant | sys_user | One-to-Many | Tenant can have multiple users |
| mall_order | mall_order_item | One-to-Many | Order can have multiple items |
| mall_product | mall_order_item | One-to-Many | Product can be in multiple order items |

### Foreign Key Constraints

| Table | Column | References | On Delete |
|-------|--------|------------|-----------|
| sys_user_role | user_id | sys_user(id) | CASCADE |
| sys_user_role | role_id | sys_role(id) | CASCADE |
| sys_user | dept_id | sys_dept(id) | SET NULL |
| sys_user | tenant_id | sys_tenant(id) | CASCADE |
| mall_order | user_id | sys_user(id) | CASCADE |
| mall_order_item | order_id | mall_order(id) | CASCADE |
| mall_order_item | product_id | mall_product(id) | CASCADE |

---

## Index Strategy

### Primary Keys

All tables have `id` as primary key (BIGINT, AUTO_INCREMENT)

### Unique Indexes

| Table | Columns |
|-------|----------|
| sys_user | (tenant_id, username) |
| sys_role | (tenant_id, role_code) |
| sys_tenant | tenant_code |
| pay_order | order_no |
| mall_order | order_no |
| mall_product | (tenant_id, product_code) |
| crm_contract | contract_no |
| erp_product | (tenant_id, product_code) |

### Regular Indexes

| Table | Columns |
|-------|----------|
| sys_user | tenant_id, dept_id, email, phone |
| sys_role | tenant_id |
| sys_menu | parent_id, menu_code |
| pay_order | tenant_id, (business_type, business_id) |
| mall_order | tenant_id, user_id, order_status |
| mall_product | tenant_id, category_id |

---

## Version History

| Version | Date | Author | Changes |
|---------|-------|---------|----------|
| 1.0.0 | 2025-01-11 | Initial ER diagram |
