# Functional Requirements Document

**Project Name**: UniEnterprise Enterprise Development Template  
**Document Version**: V1.0  
**Date**: 2024-XX-XX  
**Document Status**: Draft / Under Review / Approved  

---

## Revision History

| Version | Date | Author | Changes | Reviewer |
|---------|------|--------|---------|----------|
| V1.0 | 2024-XX-XX | Zhang San | Initial version | Li Si |

---

## Table of Contents

1. [Introduction](#1-introduction)
2. [Functional Requirements Overview](#2-functional-requirements-overview)
3. [Module Requirements](#3-module-requirements)
   3.1 [User Management Module](#31-user-management-module)
   3.2 [Permission Management Module](#32-permission-management-module)
   3.3 [Multi-Tenant Management Module](#33-multi-tenant-management-module)
   3.4 [Data Permission Module](#34-data-permission-module)
   3.5 [Workflow Module](#35-workflow-module)
   3.6 [CRM Module](#36-crm-module)
   3.7 [ERP Module](#37-erp-module)
   3.8 [E-commerce Module](#38-e-commerce-module)
   3.9 [AI Large Model Module](#39-ai-large-model-module)
   3.10 [IoT Module](#310-iot-module)
   3.11 [Payment Module](#311-payment-module)
   3.12 [SMS Module](#312-sms-module)
   3.13 [Third-Party Login Module](#313-third-party-login-module)
   3.14 [Mini Program Module](#314-mini-program-module)
4. [Business Rules](#4-business-rules)
5. [Acceptance Criteria](#5-acceptance-criteria)

---

## 1. Introduction

### 1.1 Purpose

This document describes the detailed functional requirements for UniEnterprise enterprise-level development template. It serves as a reference for developers and testers to understand system functionality.

### 1.2 Scope

This document covers:
- Detailed functional requirements for all modules
- Business rules and constraints
- Acceptance criteria for each module

### 1.3 Definitions

| Term | Definition |
|------|------------|
| RBAC | Role-Based Access Control |
| SaaS | Software as a Service |
| SKU | Stock Keeping Unit |

---

## 2. Functional Requirements Overview

The system includes 14 core functional modules:

| Module ID | Module Name | Priority | Estimated Effort |
|------------|-------------|----------|------------------|
| FR-01 | User Management | P0 | 2 weeks |
| FR-02 | Permission Management | P0 | 3 weeks |
| FR-03 | Multi-Tenant Management | P0 | 4 weeks |
| FR-04 | Data Permission | P1 | 2 weeks |
| FR-05 | Workflow | P1 | 3 weeks |
| FR-06 | CRM | P1 | 4 weeks |
| FR-07 | ERP | P2 | 4 weeks |
| FR-08 | E-commerce | P1 | 4 weeks |
| FR-09 | AI Large Model | P2 | 3 weeks |
| FR-10 | IoT | P2 | 3 weeks |
| FR-11 | Payment | P1 | 2 weeks |
| FR-12 | SMS | P1 | 2 weeks |
| FR-13 | Third-Party Login | P1 | 2 weeks |
| FR-14 | Mini Program | P1 | 4 weeks |

---

## 3. Module Requirements

### 3.1 User Management Module

**Module ID**: FR-01  
**Priority**: P0

#### 3.1.1 Functional Requirements

| Requirement ID | Description | Priority |
|---------------|-------------|----------|
| FR-01-01 | User shall be able to register with username, email, and password | P0 |
| FR-01-02 | User shall be able to login using username, email, or phone number | P0 |
| FR-01-03 | System shall validate username uniqueness during registration | P0 |
| FR-01-04 | System shall validate email format during registration | P0 |
| FR-01-05 | System shall validate password strength (min 8 chars, letters + numbers) | P0 |
| FR-01-06 | System shall lock account after 5 failed login attempts for 30 minutes | P1 |
| FR-01-07 | User shall be able to reset password via email/phone | P1 |
| FR-01-08 | Administrator shall be able to view user list with pagination | P0 |
| FR-01-09 | Administrator shall be able to edit user information | P0 |
| FR-01-10 | Administrator shall be able to enable/disable user account | P0 |
| FR-01-11 | Administrator shall be able to delete user account (soft delete) | P1 |
| FR-01-12 | System shall maintain user status (active, inactive, locked) | P0 |

#### 3.1.2 Business Rules

- **Username Rules**: Must be 4-20 characters, letters, numbers, underscores only
- **Password Rules**: Must be at least 8 characters, contain both letters and numbers
- **Email Rules**: Must be a valid email format
- **Phone Rules**: Must be a valid phone number format

#### 3.1.3 Acceptance Criteria

- [ ] User can successfully register with valid information
- [ ] User cannot register with duplicate username/email/phone
- [ ] User can login with correct credentials
- [ ] User cannot login with wrong credentials
- [ ] Account is locked after 5 failed attempts
- [ ] User can reset password via email/phone
- [ ] Administrator can view, edit, enable/disable, and delete users

---

### 3.2 Permission Management Module (RBAC)

**Module ID**: FR-02  
**Priority**: P0

#### 3.2.1 Functional Requirements

| Requirement ID | Description | Priority |
|---------------|-------------|----------|
| FR-02-01 | System shall support role-based access control (RBAC) | P0 |
| FR-02-02 | Administrator shall be able to create roles | P0 |
| FR-02-03 | Administrator shall be able to assign permissions to roles | P0 |
| FR-02-04 | Administrator shall be able to assign roles to users | P0 |
| FR-02-05 | System shall support menu permissions | P0 |
| FR-02-06 | System shall support button permissions | P0 |
| FR-02-07 | System shall support API permissions | P0 |
| FR-02-08 | User shall only access authorized menus | P0 |
| FR-02-09 | User shall only access authorized buttons | P0 |
| FR-02-10 | User shall only access authorized APIs | P0 |
| FR-02-11 | System shall support super administrator role | P0 |
| FR-02-12 | Super administrator shall have all permissions | P0 |
| FR-02-13 | Permission changes shall take effect immediately | P0 |

#### 3.2.2 Business Rules

- A user can have multiple roles
- A role can have multiple permissions
- Permissions include: menu, button, API, data
- Super administrator has all permissions by default

#### 3.2.3 Acceptance Criteria

- [ ] Administrator can create, edit, and delete roles
- [ ] Administrator can assign permissions to roles
- [ ] Administrator can assign roles to users
- [ ] User can only access authorized menus and buttons
- [ ] User can only call authorized APIs
- [ ] Permission changes take effect immediately
- [ ] Super administrator has all permissions

---

### 3.3 Multi-Tenant Management Module (SaaS)

**Module ID**: FR-03  
**Priority**: P0

#### 3.3.1 Functional Requirements

| Requirement ID | Description | Priority |
|---------------|-------------|----------|
| FR-03-01 | System shall support multi-tenant SaaS mode | P0 |
| FR-03-02 | Tenant shall be able to register | P0 |
| FR-03-03 | Tenant registration shall require approval | P1 |
| FR-03-04 | Administrator shall be able to approve/reject tenant registration | P0 |
| FR-03-05 | System shall support tenant data isolation | P0 |
| FR-03-06 | Tenant shall have independent configuration | P0 |
| FR-03-07 | System shall support shared database, independent schema mode | P0 |
| FR-03-08 | System shall support independent database mode | P1 |
| FR-03-09 | Tenant A cannot access Tenant B's data | P0 |
| FR-03-10 | Administrator shall be able to manage tenants | P0 |
| FR-03-11 | Tenant administrator shall manage tenant users | P0 |

#### 3.3.2 Business Rules

- Tenant code must be unique
- Tenant data isolation at schema or database level
- Tenant configuration is independent
- Tenant status: pending, active, suspended, terminated

#### 3.3.3 Acceptance Criteria

- [ ] Tenant can register
- [ ] Tenant registration requires approval
- [ ] Administrator can approve/reject tenant registration
- [ ] Tenant data is completely isolated
- [ ] Tenant A cannot access Tenant B's data
- [ ] Tenant configuration is independent
- [ ] Tenant administrator can manage tenant users

---

### 3.4 Data Permission Module

**Module ID**: FR-04  
**Priority**: P1

#### 3.4.1 Functional Requirements

| Requirement ID | Description | Priority |
|---------------|-------------|----------|
| FR-04-01 | System shall support row-level data permissions | P1 |
| FR-04-02 | System shall support column-level data permissions | P1 |
| FR-04-03 | Administrator shall be able to configure data permission rules | P1 |
| FR-04-04 | Data permission rules shall support department-based filtering | P1 |
| FR-04-05 | Data permission rules shall support role-based filtering | P1 |
| FR-04-06 | Data permission rules shall support user-based filtering | P1 |
| FR-04-07 | System shall support custom data permission rules | P1 |
| FR-04-08 | User shall only access data within permissions | P0 |

#### 3.4.2 Business Rules

- Data permissions are inherited from organizational structure
- Column-level permissions hide unauthorized columns
- Row-level permissions filter unauthorized rows
- Permission rules support AND/OR logic

#### 3.4.3 Acceptance Criteria

- [ ] Administrator can configure data permission rules
- [ ] User can only access data within permissions
- [ ] Row-level permissions work correctly
- [ ] Column-level permissions work correctly
- [ ] Custom permission rules work correctly

---

### 3.5 Workflow Module (Flowable)

**Module ID**: FR-05  
**Priority**: P1

#### 3.5.1 Functional Requirements

| Requirement ID | Description | Priority |
|---------------|-------------|----------|
| FR-05-01 | System shall integrate Flowable workflow engine | P1 |
| FR-05-02 | Administrator shall be able to define processes | P1 |
| FR-05-03 | System shall support process deployment | P1 |
| FR-05-04 | User shall be able to start a process instance | P1 |
| FR-05-05 | System shall support task assignment | P0 |
| FR-05-06 | User shall be able to approve/reject tasks | P0 |
| FR-05-07 | System shall support serial approval | P1 |
| FR-05-08 | System shall support parallel approval | P1 |
| FR-05-09 | System shall support dynamic approver specification | P1 |
| FR-05-10 | System shall record approval opinions | P0 |
| FR-05-11 | Administrator shall be able to view process history | P1 |

#### 3.5.2 Business Rules

- Task status: pending, approved, rejected, cancelled
- Approval types: serial, parallel
- Support task forwarding and delegation
- Approval history must be preserved

#### 3.5.3 Acceptance Criteria

- [ ] Administrator can define and deploy processes
- [ ] User can start process instances
- [ ] Tasks are assigned correctly
- [ ] User can approve/reject tasks
- [ ] Serial approval works correctly
- [ ] Parallel approval works correctly
- [ ] Approval opinions are recorded
- [ ] Process history is queryable

---

### 3.6 CRM Module

**Module ID**: FR-06  
**Priority**: P1

#### 3.6.1 Functional Requirements

| Requirement ID | Description | Priority |
|---------------|-------------|----------|
| FR-06-01 | System shall support customer management | P1 |
| FR-06-02 | System shall support lead management | P1 |
| FR-06-03 | System shall support opportunity management | P1 |
| FR-06-04 | System shall support follow-up records | P1 |
| FR-06-05 | System shall support contract management | P1 |
| FR-06-06 | Lead shall be convertible to opportunity | P1 |
| FR-06-07 | Opportunity shall be convertible to contract | P1 |
| FR-06-08 | System shall support sales reports | P2 |
| FR-06-09 | Customer data shall be associated with tenant | P0 |

#### 3.6.2 Business Rules

- Lead → Opportunity → Contract conversion flow
- Customer information is tenant-associated
- Follow-up records are mandatory for key actions
- Sales reports aggregate data by time period

#### 3.6.3 Acceptance Criteria

- [ ] User can create, edit, and delete customers
- [ ] User can manage leads
- [ ] User can convert leads to opportunities
- [ ] User can convert opportunities to contracts
- [ ] User can record follow-ups
- [ ] Sales reports are accurate
- [ ] Customer data is correctly associated with tenant

---

### 3.7 ERP Module

**Module ID**: FR-07  
**Priority**: P2

#### 3.7.1 Functional Requirements

| Requirement ID | Description | Priority |
|---------------|-------------|----------|
| FR-07-01 | System shall support procurement management | P2 |
| FR-07-02 | System shall support inventory management | P2 |
| FR-07-03 | System shall support supplier management | P2 |
| FR-07-04 | System shall support financial management | P2 |
| FR-07-05 | System shall support production planning | P2 |
| FR-07-06 | System shall support multi-dimensional reports | P2 |
| FR-07-07 | Purchase orders shall be supplier-associated | P1 |
| FR-07-08 | Inventory shall support inbound/outbound operations | P1 |

#### 3.7.2 Business Rules

- Purchase order flow: create → approve → procure → receive
- Inventory supports FIFO, LIFO, FEFO
- Financial data requires approval
- Reports can be generated by time, department, supplier

#### 3.7.3 Acceptance Criteria

- [ ] User can create and manage purchase orders
- [ ] User can manage inventory
- [ ] Inventory inbound/outbound operations work correctly
- [ ] User can manage suppliers
- [ ] Financial data is accurate
- [ ] Reports are accurate and can be customized

---

### 3.8 E-commerce Module

**Module ID**: FR-08  
**Priority**: P1

#### 3.8.1 Functional Requirements

| Requirement ID | Description | Priority |
|---------------|-------------|----------|
| FR-08-01 | System shall support product management | P1 |
| FR-08-02 | System shall support SKU management | P1 |
| FR-08-03 | System shall support order management | P1 |
| FR-08-04 | System shall support shopping cart | P1 |
| FR-08-05 | System shall support payment integration | P0 |
| FR-08-06 | System shall support logistics tracking | P1 |
| FR-08-07 | System shall support review management | P1 |
| FR-08-08 | System shall support marketing activities | P2 |
| FR-08-09 | System shall support coupons | P2 |
| FR-08-10 | System shall support order refunds | P1 |

#### 3.8.2 Business Rules

- Order status flow: pending payment → pending shipment → pending receipt → completed/cancelled
- Product supports multiple SKUs
- Support marketing activities: coupons, discounts
- Order refund requires approval

#### 3.8.3 Acceptance Criteria

- [ ] User can manage products and SKUs
- [ ] User can create and manage orders
- [ ] Shopping cart functionality works correctly
- [ ] Order status flow is correct
- [ ] Payment integration works
- [ ] Logistics tracking is updated in real-time
- [ ] User can submit reviews
- [ ] Coupons and discounts work correctly
- [ ] Order refund process works

---

### 3.9 AI Large Model Module

**Module ID**: FR-09  
**Priority**: P2

#### 3.9.1 Functional Requirements

| Requirement ID | Description | Priority |
|---------------|-------------|----------|
| FR-09-01 | System shall support AI large model integration | P2 |
| FR-09-02 | System shall support multiple model switching | P2 |
| FR-09-03 | System shall support model configuration | P2 |
| FR-09-04 | User shall be able to chat with AI | P2 |
| FR-09-05 | System shall support streaming output | P2 |
| FR-09-06 | System shall support prompt management | P2 |
| FR-09-07 | System shall support API key management | P2 |
| FR-09-08 | System shall support token billing | P2 |
| FR-09-09 | System shall support rate limiting | P1 |

#### 3.9.2 Business Rules

- Support models: GPT, Claude, etc.
- API key must be encrypted
- Token billing: count input + output tokens
- Rate limit: max requests per minute/hour

#### 3.9.3 Acceptance Criteria

- [ ] User can configure multiple models
- [ ] User can switch between models
- [ ] Chat with AI works correctly
- [ ] Streaming output works
- [ ] Prompt management works
- [ ] Token billing is accurate
- [ ] Rate limiting works correctly

---

### 3.10 IoT Module

**Module ID**: FR-10  
**Priority**: P2

#### 3.10.1 Functional Requirements

| Requirement ID | Description | Priority |
|---------------|-------------|----------|
| FR-10-01 | System shall support IoT device connection | P2 |
| FR-10-02 | System shall support MQTT protocol | P2 |
| FR-10-03 | User shall be able to manage devices | P2 |
| FR-10-04 | System shall support data collection | P2 |
| FR-10-05 | System shall support device control | P2 |
| FR-10-06 | System shall support device monitoring | P2 |
| FR-10-07 | System shall support alert management | P2 |
| FR-10-08 | System shall monitor device online/offline status | P1 |
| FR-10-09 | Data collection frequency shall be configurable | P1 |

#### 3.10.2 Business Rules

- Device status: online, offline, error
- Data collection frequency: 1 second to 1 hour
- Alert triggers: threshold exceeded, device offline
- Support remote device control

#### 3.10.3 Acceptance Criteria

- [ ] Devices can connect to system
- [ ] MQTT protocol works
- [ ] User can manage devices
- [ ] Data collection works
- [ ] Device control responds timely
- [ ] Device status is monitored
- [ ] Alerts trigger correctly

---

### 3.11 Payment Module

**Module ID**: FR-11  
**Priority**: P1

#### 3.11.1 Functional Requirements

| Requirement ID | Description | Priority |
|---------------|-------------|----------|
| FR-11-01 | System shall support WeChat Pay | P1 |
| FR-11-02 | System shall support Alipay | P1 |
| FR-11-03 | System shall support payment method configuration | P1 |
| FR-11-04 | System shall support payment order management | P0 |
| FR-11-05 | System shall process payment callbacks | P0 |
| FR-11-06 | System shall support order refunds | P1 |
| FR-11-07 | System shall support partial refunds | P1 |
| FR-11-08 | System shall support reconciliation | P2 |
| FR-11-09 | System shall support reconciliation file download | P2 |

#### 3.11.2 Business Rules

- Payment methods: WeChat Pay (JSAPI, H5, APP), Alipay (APP, H5)
- Order status: pending payment, paid, failed, refunded
- Refund: full refund, partial refund
- Reconciliation: daily, weekly, monthly

#### 3.11.3 Acceptance Criteria

- [ ] WeChat Pay works correctly
- [ ] Alipay works correctly
- [ ] Payment order creation works
- [ ] Payment callbacks are processed correctly
- [ ] Order status is updated correctly
- [ ] Full and partial refunds work
- [ ] Reconciliation data is accurate
- [ ] Reconciliation files can be downloaded

---

### 3.12 SMS Module

**Module ID**: FR-12  
**Priority**: P1

#### 3.12.1 Functional Requirements

| Requirement ID | Description | Priority |
|---------------|-------------|----------|
| FR-12-01 | System shall support SMS sending | P1 |
| FR-12-02 | System shall support SMS template management | P1 |
| FR-12-03 | System shall support verification code management | P1 |
| FR-12-04 | Verification code shall be 6 digits | P1 |
| FR-12-05 | Verification code shall be valid for 5 minutes | P1 |
| FR-12-06 | Same phone number max 10 SMS per day | P1 |
| FR-12-07 | System shall support multiple SMS providers | P1 |
| FR-12-08 | System shall support SMS provider switching | P1 |
| FR-12-09 | Administrator shall be able to query send records | P1 |
| FR-12-10 | System shall support SMS statistics | P2 |

#### 3.12.2 Business Rules

- SMS types: verification code, notification, marketing
- Verification code: 6 digits, 5 minutes validity
- Rate limit: 10 SMS per day per phone number
- Support SMS providers: Alibaba Cloud, Tencent Cloud

#### 3.12.3 Acceptance Criteria

- [ ] SMS can be sent successfully
- [ ] SMS delivery success rate > 99%
- [ ] Verification code functionality works
- [ ] Verification code is 6 digits
- [ ] Verification code expires after 5 minutes
- [ ] Rate limiting works correctly
- [ ] SMS providers can be switched
- [ ] Send records are complete
- [ ] SMS statistics are accurate

---

### 3.13 Third-Party Login Module

**Module ID**: FR-13  
**Priority**: P1

#### 3.13.1 Functional Requirements

| Requirement ID | Description | Priority |
|---------------|-------------|----------|
| FR-13-01 | System shall support OAuth2 login | P1 |
| FR-13-02 | System shall support WeChat login | P1 |
| FR-13-03 | System shall support DingTalk login | P1 |
| FR-13-04 | System shall support account binding | P1 |
| FR-13-05 | System shall support account unbinding | P1 |
| FR-13-06 | Third-party login shall create local account automatically | P1 |
| FR-13-07 | System shall support UnionID binding | P1 |
| FR-13-08 | Administrator shall be able to view login logs | P1 |

#### 3.13.2 Business Rules

- Third-party login creates local account if not exists
- Support account binding/unbinding
- UnionID used for cross-platform identification
- Login logs record: time, IP, device, method

#### 3.13.3 Acceptance Criteria

- [ ] Third-party login works correctly
- [ ] Account binding works correctly
- [ ] Account unbinding works correctly
- [ ] Local account created automatically
- [ ] UnionID binding works
- [ ] Login logs are complete
- [ ] Security requirements are met

---

### 3.14 Mini Program Module

**Module ID**: FR-14  
**Priority**: P1

#### 3.14.1 Functional Requirements

| Requirement ID | Description | Priority |
|---------------|-------------|----------|
| FR-14-01 | System shall support WeChat Mini Program | P1 |
| FR-14-02 | System shall support User Mini Program | P1 |
| FR-14-03 | Mini Program shall support authorization login | P1 |
| FR-14-04 | Mini Program shall support in-program payment | P1 |
| FR-14-05 | Mini Program shall support UnionID binding | P1 |
| FR-14-06 | Mini Program shall share business functions | P2 |

#### 3.14.2 Business Rules

- Mini Program authorization: scope, user info
- Mini Program payment: WeChat Pay Mini Program API
- UnionID binding for user identification
- Share business functions between mini programs

#### 3.14.3 Acceptance Criteria

- [ ] Mini Program authorization works
- [ ] Mini Program login works
- [ ] Mini Program payment works
- [ ] UnionID binding works
- [ ] User experience is good
- [ ] Business functions work correctly

---

## 4. Business Rules

### 4.1 Global Business Rules

1. **Data Isolation**: Tenant data must be completely isolated
2. **Permission Control**: All operations must be permission-validated
3. **Audit Logging**: All key operations must be logged
4. **Data Consistency**: All transactions must maintain data consistency
5. **Performance**: All operations must meet performance requirements

### 4.2 Module-Specific Rules

Each module has specific business rules defined in its section above.

---

## 5. Acceptance Criteria

### 5.1 Module Acceptance Criteria

All functional requirements must be implemented and tested:
- [ ] All P0 requirements implemented
- [ ] All P1 requirements implemented
- [ ] All P2 requirements implemented (if applicable)
- [ ] All test cases pass
- [ ] No P0, P1 bugs
- [ ] P2 bugs < 5

### 5.2 Integration Acceptance Criteria

- [ ] All modules integrate correctly
- [ ] Data flows correctly between modules
- [ ] Performance meets requirements
- [ ] Security meets requirements

---

**Document End**
