# Project Architecture Description

## Three-Level Architecture Structure

| Level | Category | Components |
|-------|----------|------------|
| **Base Level** | Framework Components | Web, Security, Redis, MQ, Job, Monitor, Test, Flowable, Data Permission, Tenant, Payment, SMS, Social, Operate Log |
| **Middle Level** | Common Modules | System, Infrastructure, BPM, Payment, Member, Visualization |
| **Top Level** | Business Systems | Mall, OA, ERP, CRM, CMS, BBS, AI Agent |

---

## Base Level - Framework Components

### Technical Components

| Component | Description |
|-----------|-------------|
| **Web** | Web technology component that provides HTTP request handling, RESTful API support, and web layer infrastructure for the application. |
| **Security** | Security technology component that handles authentication, authorization, JWT token management, and RBAC (Role-Based Access Control) to protect application resources. |
| **Redis** | Redis technology component that provides distributed caching, session management, rate limiting, and high-performance data storage capabilities. |
| **MQ** | Message Queue technology component (RabbitMQ/RocketMQ) that enables asynchronous communication, event-driven architecture, and reliable message delivery between services. |
| **Job** | Job scheduling technology component that supports distributed task scheduling, cron expressions, and background job execution for periodic operations. |
| **Monitor** | Monitoring technology component that provides application health monitoring, performance metrics collection, log aggregation, and real-time alerting. |
| **Test** | Testing technology component that offers unit testing, integration testing, API testing support, and test utilities for quality assurance. |
| **Flowable** | Flowable workflow technology component that provides business process management (BPM), workflow engine, and process modeling capabilities. |
| **Data Permission** | Data permission technology component that implements fine-grained data access control based on organizational structure, roles, and custom rules. |
| **Tenant** | Tenant technology component that provides multi-tenancy SaaS support, tenant isolation, and tenant-specific data management. |

### Business Components

| Component | Description |
|-----------|-------------|
| **Payment** | Payment technology component that abstracts payment gateway integrations (Stripe, PayPal, etc.) and provides unified payment APIs. |
| **SMS** | SMS business component that handles short message service delivery, verification codes, notification messages, and SMS template management. |
| **Social** | Social business component that manages third-party OAuth2 integrations (Facebook, Google, LinkedIn, Twitter/X, GitHub, etc.) and unified user authentication. |
| **Operate Log** | Operation log business component that records user operation history, audit trails, and system activity logs for security and compliance. |

---

## Middle Level - Common Modules

| Module | Description |
|--------|-------------|
| **System** | System functionality module that provides user management, role management, menu management, department management, dictionary management, and parameter configuration as the core system foundation. |
| **Infrastructure** | Infrastructure module that offers common services like file storage, code generation, API documentation, configuration center integration, and utility services supporting the entire application. |
| **BPM** | Business Process Management module built on Flowable that provides workflow definition, process deployment, task management, form design, and business process automation capabilities. |
| **Payment** | Payment system module that implements payment order management, payment callback handling, refund processing, transaction records, and payment reconciliation for business scenarios requiring payment functionality. |
| **Member** | Member center module that handles user registration, member levels, points management, wallet balance, coupons, and member-related business logic for user engagement and loyalty programs. |
| **Visualization** | Data reporting module that provides data visualization, statistical dashboards, chart rendering, BI analysis, and report generation for business intelligence and decision support. |

---

## Top Level - Business Systems

| System | Description |
|--------|-------------|
| **Mall** | Electronic Mall system that provides a complete e-commerce solution including product management, shopping cart, order processing, payment integration, logistics tracking, and customer service. |
| **OA** | Office Automation system that streamlines enterprise operations with features like approval workflows, document management, meeting scheduling, task tracking, and internal communication tools. |
| **ERP** | Enterprise Resource Planning system that manages core business processes including procurement, inventory, production planning, supply chain management, and financial accounting. |
| **CRM** | Customer Relationship Management system that helps businesses manage customer interactions, sales pipelines, marketing campaigns, customer service, and customer data analysis. |
| **CMS** | Content Management System that enables content creation, publishing, and management with features like article management, category organization, media library, and SEO optimization. |
| **BBS** | Forum and Community system that provides discussion board functionality, user posts, replies, comments, topic management, and community engagement features. |
| **AI Agent** | AI Agent system that integrates large language models (GPT, Claude, etc.) to provide intelligent conversational capabilities, knowledge base Q&A, task automation, AI-powered assistants, and natural language processing features for business scenarios. |

---

## Architecture Benefits

### Modularity
- **Clear Layering**: Three-level separation ensures concerns are properly isolated
- **Pluggable Design**: Each component can be enabled/disabled based on business needs
- **Easy Maintenance**: Independent components simplify updates and bug fixes

### Flexibility
- **Multi-Scenario Support**: From simple monolithic to complex microservices architectures
- **Technology Agnostic**: Components can be swapped with alternative implementations
- **Scalable Structure**: New components and modules can be added without disrupting existing code

### Enterprise-Ready
- **Production Quality**: Each component follows best practices and industry standards
- **Well-Documented**: Clear interfaces and usage documentation for each component
- **Battle-Tested**: Components designed for real-world enterprise applications
