# User Stories

## User Role Definitions

### Administrator
- System administrator with full access to all system functions
- Responsible for system configuration, user management, and monitoring

### Regular User
- End users of the business systems (Mall, CRM, ERP, etc.)
- Access to specific modules based on assigned permissions

### Tenant Administrator
- Manages tenant-specific users and configurations in multi-tenant SaaS environment
- Controls tenant subscription and billing

### Third-Party System Integration Personnel
- Manages API integrations with external systems
- Monitors integration status and logs

---

## User Scenario Descriptions

### Authentication & Authorization

#### Story 1: User Registration
**As a** new user
**I want to** register an account
**So that** I can access the system

**Acceptance Criteria:**
- [x] User can register with email or phone number
- [x] System sends verification code to email/phone
- [x] User can set password during registration
- [x] User account is created after successful verification
- [x] User receives confirmation email/SMS

#### Story 2: User Login
**As a** registered user
**I want to** login to the system
**So that** I can access my account and perform authorized operations

**Acceptance Criteria:**
- [x] User can login with email/phone and password
- [x] System validates credentials and issues JWT token
- [x] User remains logged in until token expires or logout
- [x] System records login time and IP address
- [x] System locks account after 5 failed login attempts
- [x] User can reset password via email/phone

#### Story 3: Third-Party Login
**As a** user
**I want to** login using third-party accounts (WeChat, DingTalk, etc.)
**So that** I can quickly access the system without creating another account

**Acceptance Criteria:**
- [x] User can login using WeChat OAuth2
- [x] User can login using DingTalk OAuth2
- [x] System creates or links existing account upon first login
- [x] User can bind multiple third-party accounts

### System Management

#### Story 4: User Management
**As an** administrator
**I want to** manage user accounts
**So that** I can control who has access to the system

**Acceptance Criteria:**
- [x] Administrator can create, edit, delete users
- [x] Administrator can assign roles to users
- [x] Administrator can assign departments to users
- [x] Administrator can reset user passwords
- [x] Administrator can activate/deactivate user accounts
- [x] System logs all user management operations

#### Story 5: Role Management
**As an** administrator
**I want to** manage roles and permissions
**So that** I can control what users can do in the system

**Acceptance Criteria:**
- [x] Administrator can create, edit, delete roles
- [x] Administrator can assign menu permissions to roles
- [x] Administrator can assign data permissions to roles
- [x] Administrator can assign button permissions to roles
- [x] System applies role permissions immediately after assignment

#### Story 6: Menu Management
**As an** administrator
**I want to** configure system menus
**So that** I can control what menu items users see

**Acceptance Criteria:**
- [x] Administrator can create, edit, delete menu items
- [x] Menu items can be organized hierarchically
- [x] Menu items can be linked to specific routes
- [x] Menu items can have icons
- [x] System caches menu data for performance

### Multi-Tenant Management

#### Story 7: Tenant Management
**As a** system administrator
**I want to** manage tenants
**So that** I can support multiple organizations on the same system

**Acceptance Criteria:**
- [x] Administrator can create, edit, delete tenants
- [x] Each tenant has isolated data
- [x] Administrator can assign packages to tenants
- [x] System tracks tenant usage and billing
- [x] Tenant administrator can only manage their own users

#### Story 8: Tenant Packages
**As a** system administrator
**I want to** configure tenant packages
**So that** I can offer different service levels to tenants

**Acceptance Criteria:**
- [x] Administrator can create different package tiers
- [x] Packages define allowed features and modules
- [x] Packages define user limits
- [x] Administrator can upgrade/downgrade tenant packages

### Workflow Management

#### Story 9: Workflow Design
**As a** business user
**I want to** design business processes
**So that** I can automate approval workflows

**Acceptance Criteria:**
- [x] User can design workflows using drag-and-drop interface
- [x] User can define approval nodes
- [x] User can set approvers for each node
- [x] User can configure countersignature or or-sign
- [x] User can define conditions for branching
- [x] System validates workflow design before deployment

#### Story 10: Workflow Execution
**As a** user
**I want to** submit requests for approval
**So that** I can get approvals from designated approvers

**Acceptance Criteria:**
- [x] User can initiate a workflow
- [x] User can view pending tasks
- [x] Approver can approve or reject tasks
- [x] System notifies users of new tasks
- [x] System tracks workflow progress
- [x] User can view workflow history

### E-commerce

#### Story 11: Product Browsing
**As a** customer
**I want to** browse products
**So that** I can find items I want to purchase

**Acceptance Criteria:**
- [x] Customer can view product listings
- [x] Customer can search products by name or category
- [x] Customer can filter products by price, brand, etc.
- [x] Customer can view product details and images
- [x] System displays product availability

#### Story 12: Shopping Cart
**As a** customer
**I want to** add products to cart
**So that** I can purchase multiple items at once

**Acceptance Criteria:**
- [x] Customer can add products to cart
- [x] Customer can view cart contents
- [x] Customer can update quantity in cart
- [x] Customer can remove items from cart
- [x] Cart persists across sessions

#### Story 13: Order Placement
**As a** customer
**I want to** place an order
**So that** I can purchase products

**Acceptance Criteria:**
- [x] Customer can select shipping address
- [x] Customer can select payment method
- [x] Customer can apply coupon codes
- [x] System calculates order total including tax and shipping
- [x] Customer confirms order and proceeds to payment
- [x] System creates order record after successful payment

### CRM

#### Story 14: Lead Management
**As a** salesperson
**I want to** manage sales leads
**So that** I can track and convert potential customers

**Acceptance Criteria:**
- [x] Salesperson can create new leads
- [x] Salesperson can update lead information
- [x] System assigns leads to salespersons
- [x] Salesperson can view assigned leads
- [x] Salesperson can convert lead to customer
- [x] System tracks lead status

#### Story 15: Opportunity Management
**As a** salesperson
**I want to** track sales opportunities
**So that** I can manage the sales pipeline

**Acceptance Criteria:**
- [x] Salesperson can create opportunities
- [x] Salesperson can assign opportunities to different stages
- [x] System displays sales pipeline by stage
- [x] Salesperson can update opportunity details
- [x] System calculates opportunity value and probability

### AI Integration

#### Story 16: AI Chat
**As a** user
**I want to** chat with AI assistant
**So that** I can get help with various tasks

**Acceptance Criteria:**
- [x] User can send messages to AI
- [x] AI responds with context-aware answers
- [x] User can have multi-turn conversations
- [x] AI can generate code snippets
- [x] System maintains conversation history

#### Story 17: AI Knowledge Base
**As a** user
**I want to** ask questions based on uploaded documents
**So that** I can get answers from my own data

**Acceptance Criteria:**
- [x] User can upload documents (PDF, Word, etc.)
- [x] System processes and indexes uploaded documents
- [x] User can ask questions about the documents
- [x] AI provides answers with source references
- [x] System highlights relevant sections in documents

---

## User Requirement List

### High Priority
1. User authentication and authorization
2. User and role management
3. Multi-tenant support
4. RBAC permission system
5. Data permission control
6. Operation logging

### Medium Priority
1. Workflow engine integration
2. Payment integration
3. SMS and email notifications
4. Third-party login (OAuth2)
5. File storage management
6. Code generator

### Low Priority
1. AI integration
2. Real-time communication (WebSocket)
3. Advanced reporting and visualization
4. Mobile app support
5. WeChat mini program integration

---

## Version History

| Version | Date | Author | Changes |
|---------|-------|---------|----------|
| 1.0.0 | 2025-01-11 | Initial version |
