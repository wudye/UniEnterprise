# Project Architecture Relationship Table

## Three-Level Dependency Structure

```
┌─────────────────────────────────────────────────────────────────────┐
│                          TOP LEVEL                                  │
│                     Business Systems                                │
│  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐   │
│  │ Mall │ │  OA  │ │ ERP  │ │ CRM  │ │ CMS  │ │ BBS  │ │ AI   │   │
│  └──┬───┘ └──┬───┘ └──┬───┘ └──┬───┘ └──┬───┘ └──┬───┘ └──┬───┘   │
└─────┼────────┼────────┼────────┼────────┼────────┼────────┼──────┘
      │        │        │        │        │        │        │
      └────────┴────────┴────────┴────────┴────────┴────────┘
                               │ depends on
                               ▼
┌─────────────────────────────────────────────────────────────────────┐
│                        MIDDLE LEVEL                                 │
│                        Common Modules                                │
│  ┌────────┐ ┌───────────────┐ ┌───────┐ ┌─────────┐ ┌───────────┐  │
│  │ System │ │ Infrastructure│ │  BPM  │ │ Payment │ │  Member   │  │
│  └───┬────┘ └───────┬───────┘ └───┬───┘ └────┬────┘ └─────┬─────┘  │
│  ┌───┴───────────────┴───────────┴───────┴─────────┴─────────────┐  │
│  │         Visualization                                         │  │
│  └───────────────────────────────────────────────────────────────────┘
└─────┼───────────────┼───────────────┼───────────────┼────────────────┘
      │               │               │               │
      └───────────────┴───────────────┴───────────────┘
                               │ depends on
                               ▼
┌─────────────────────────────────────────────────────────────────────┐
│                         BASE LEVEL                                  │
│                     Framework Components                             │
│                                                                       │
│  ┌──────────┐ ┌──────────┐ ┌───────┐ ┌──────┐ ┌──────────┐           │
│  │   Web    │ │ Security │ │ Redis │ │  MQ  │ │   Job    │           │
│  └──────────┘ └──────────┘ └───────┘ └──────┘ └──────────┘           │
│                                                                       │
│  ┌──────────┐ ┌──────────┐ ┌────────────┐ ┌───────────┐              │
│  │ Monitor  │ │   Test   │ │  Flowable  │ │Data Perm. │              │
│  └──────────┘ └──────────┘ └────────────┘ └───────────┘              │
│                                                                       │
│  ┌──────────┐ ┌───────┐ ┌──────────┐ ┌──────────────┐               │
│  │  Tenant  │ │Payment │ │   SMS    │ │   Social     │               │
│  └──────────┘ └───────┘ └──────────┘ └──────────────┘               │
│                                                                       │
│  ┌────────────────────────────────────────────────────┐               │
│  │              Operate Log                           │               │
│  └────────────────────────────────────────────────────┘               │
└───────────────────────────────────────────────────────────────────────┘
```

---

## Detailed Dependency Matrix

### Top Level → Middle Level Dependencies

| Business System | Dependent Modules (Middle Level) |
|-----------------|----------------------------------|
| **Mall** | System, Infrastructure, Payment, Member, Visualization |
| **OA** | System, Infrastructure, BPM, Member, Visualization |
| **ERP** | System, Infrastructure, BPM, Payment, Visualization |
| **CRM** | System, Infrastructure, Member, Visualization |
| **CMS** | System, Infrastructure, Member, Visualization |
| **BBS** | System, Infrastructure, Member, Visualization |
| **AI Agent** | System, Infrastructure, Visualization |

### Middle Level → Base Level Dependencies

| Common Module | Dependent Components (Base Level) |
|---------------|-----------------------------------|
| **System** | Web, Security, Redis, Data Permission, Tenant, Operate Log |
| **Infrastructure** | Web, Security, Redis, MQ, Job, Test, SMS |
| **BPM** | Flowable, Web, Security, Redis, Operate Log |
| **Payment** | Web, Security, Payment, Redis, MQ, Operate Log |
| **Member** | Web, Security, Redis, Operate Log, SMS, Social |
| **Visualization** | Web, Security, Redis, MQ |

### Full Dependency Tree

```
Mall
├── System
│   ├── Web
│   ├── Security
│   ├── Redis
│   ├── Data Permission
│   ├── Tenant
│   └── Operate Log
├── Infrastructure
│   ├── Web
│   ├── Security
│   ├── Redis
│   ├── MQ
│   ├── Job
│   ├── Test
│   └── SMS
├── Payment
│   ├── Web
│   ├── Security
│   ├── Payment (Base)
│   ├── Redis
│   ├── MQ
│   └── Operate Log
├── Member
│   ├── Web
│   ├── Security
│   ├── Redis
│   ├── Operate Log
│   ├── SMS
│   └── Social
└── Visualization
    ├── Web
    ├── Security
    ├── Redis
    └── MQ

OA
├── System
│   └── [Same dependencies as above]
├── Infrastructure
│   └── [Same dependencies as above]
├── BPM
│   ├── Flowable
│   ├── Web
│   ├── Security
│   ├── Redis
│   └── Operate Log
├── Member
│   └── [Same dependencies as above]
└── Visualization
    └── [Same dependencies as above]

ERP
├── System
├── Infrastructure
├── BPM
├── Payment
└── Visualization

CRM
├── System
├── Infrastructure
├── Member
└── Visualization

CMS
├── System
├── Infrastructure
├── Member
└── Visualization

BBS
├── System
├── Infrastructure
├── Member
└── Visualization

AI Agent
├── System
├── Infrastructure
└── Visualization
```

---

## Component Usage Frequency

### Most Used Base Components (by number of middle modules depending on them)

| Component | Usage Count | Used By |
|-----------|-------------|---------|
| **Web** | 6 | System, Infrastructure, BPM, Payment, Member, Visualization |
| **Security** | 6 | System, Infrastructure, BPM, Payment, Member, Visualization |
| **Redis** | 6 | System, Infrastructure, BPM, Payment, Member, Visualization |
| **Operate Log** | 4 | System, BPM, Payment, Member |
| **MQ** | 3 | Infrastructure, Payment, Visualization |
| **SMS** | 2 | Infrastructure, Member |
| **Social** | 1 | Member |
| **Payment (Base)** | 1 | Payment (Middle) |
| **Flowable** | 1 | BPM |
| **Job** | 1 | Infrastructure |
| **Test** | 1 | Infrastructure |
| **Data Permission** | 1 | System |
| **Tenant** | 1 | System |

### Most Used Middle Modules (by number of business systems depending on them)

| Module | Usage Count | Used By |
|--------|-------------|---------|
| **System** | 7 | Mall, OA, ERP, CRM, CMS, BBS, AI Agent |
| **Infrastructure** | 7 | Mall, OA, ERP, CRM, CMS, BBS, AI Agent |
| **Visualization** | 7 | Mall, OA, ERP, CRM, CMS, BBS, AI Agent |
| **Member** | 6 | Mall, OA, ERP, CRM, CMS, BBS |
| **BPM** | 3 | Mall, OA, ERP |
| **Payment** | 3 | Mall, OA, ERP |

---

## Layer Interaction Summary

### Base Level
- **Purpose**: Foundation layer providing core technical capabilities
- **Responsibility**: Technical services (caching, security, messaging, etc.)
- **Depends On**: External infrastructure only
- **Used By**: Middle Level modules

### Middle Level
- **Purpose**: Common business modules shared across systems
- **Responsibility**: Reusable business logic and features
- **Depends On**: Base Level framework components
- **Used By**: Top Level business systems

### Top Level
- **Purpose**: Complete business systems for end users
- **Responsibility**: Domain-specific business processes
- **Depends On**: Middle Level common modules
- **Used By**: End users directly
