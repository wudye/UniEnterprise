# Prototype Design (UI/UX Wireframes)

## Page Wireframes

### Authentication Pages

#### Login Page
```
┌─────────────────────────────────────────────┐
│           UniEnterprise Logo            │
├─────────────────────────────────────────────┤
│                                     │
│  Email/Phone: [_______________]         │
│  Password:     [_______________]         │
│  ☐ Remember Me                       │
│                                     │
│  [        Login Button      ]           │
│                                     │
│  Forgot Password? | Register           │
│                                     │
│  ─────── Or ────────                │
│                                     │
│  [WeChat] [DingTalk] [GitHub]        │
│                                     │
└─────────────────────────────────────────────┘
```

#### Registration Page
```
┌─────────────────────────────────────────────┐
│           User Registration            │
├─────────────────────────────────────────────┤
│  Username:      [_______________]         │
│  Email:         [_______________]         │
│  Phone:         [_______________]         │
│  Verification:  [_______] [Send Code]  │
│  Password:      [_______________]         │
│  Confirm Pass:  [_______________]         │
│  ☐ I agree to Terms & Conditions      │
│                                     │
│  [      Register Button      ]          │
│                                     │
└─────────────────────────────────────────────┘
```

### Admin Dashboard Pages

#### Dashboard Home
```
┌──────────┬────────────────────────────────────────┐
│   Menu   │  Welcome, Administrator          │
├──────────┤  Quick Actions:                 │
│ Dashboard│  [Create User] [New Order]      │
│ System   │                                │
│  ├ Users│  Statistics:                   │
│  ├ Roles│  ┌──────┬──────┬──────┐   │
│  ├ Menus│  │Users │Orders│ Sales│   │
│  ├ Depts│  │  123 │  456 │ $12K │   │
│  └ Dict │  └──────┴──────┴──────┘   │
│ Business │                                │
│  ├ Mall │  Recent Activities:             │
│  ├ CRM  │  • User registered: John       │
│  ├ ERP  │  • Order placed: #ORD-001    │
│  └ OA   │  • Payment received: $500     │
│ Reports │                                │
│  ├ Sales│  Charts:                       │
│  └ Visul│  [Sales Trend Line Chart]      │
│ Settings│                                │
│  └ Logs │                                │
└──────────┴────────────────────────────────────────┘
```

#### User Management Page
```
┌──────────┬────────────────────────────────────────┐
│   Menu   │  User Management              │
├──────────┤  [Add User] [Batch Import] [Export] │
│ ...      │                                │
│ Users    │  Search: [__________] [Search]     │
│          │                                │
│          │  ┌─────┬──────┬─────┬─────┬───┐│
│          │  │Name │Email │Role │Dept │Act││
│          │  ├─────┼──────┼─────┼─────┼───┤│
│          │  │John │john@│Admin│IT  │ ✓ ││
│          │  │     │email│     │     │   ││
│          │  ├─────┼──────┼─────┼─────┼───┤│
│          │  │Jane │jane@│User │Sales│ ✓ ││
│          │  │     │email│     │     │   ││
│          │  ├─────┼──────┼─────┼─────┼───┤│
│          │  │Bob  │bob@ │User │HR  │ ✓ ││
│          │  │     │email│     │     │   ││
│          │  └─────┴──────┴─────┴─────┴───┘│
│          │                                │
│          │  Pagination: < 1 2 3 4 5 >     │
└──────────┴────────────────────────────────────────┘
```

#### Role Management Page
```
┌──────────┬────────────────────────────────────────┐
│   Menu   │  Role Management               │
├──────────┤  [Add Role]                     │
│ ...      │                                │
│ Roles    │  ┌──────────┬──────────┬───────┐│
│          │  │Role Name │Created   │Actions││
│          │  ├──────────┼──────────┼───────┤│
│          │  │Administrator│2025-01-01│[Edit]││
│          │  │          │          │[Del] ││
│          │  ├──────────┼──────────┼───────┤│
│          │  │Manager   │2025-01-05│[Edit]││
│          │  │          │          │[Del] ││
│          │  ├──────────┼──────────┼───────┤│
│          │  │User      │2025-01-10│[Edit]││
│          │  │          │          │[Del] ││
│          │  └──────────┴──────────┴───────┘│
│          │                                │
│          │  Selected Role: [Administrator ▼]   │
│          │  Assign Permissions:              │
│          │  ☐ System Management            │
│          │    ☐ User Management            │
│          │    ☐ Role Management            │
│          │  ☐ Business Modules             │
│          │    ☐ Mall Module               │
│          │    ☐ CRM Module                │
│          │  [Save] [Cancel]                │
└──────────┴────────────────────────────────────────┘
```

### Business Module Pages

#### E-commerce Product List
```
┌──────────┬────────────────────────────────────────┐
│   Menu   │  Product Management              │
├──────────┤  [Add Product] [Batch Import] [Export]│
│ Mall     │                                │
│  ├ Products│  Category: [All ▼] Brand: [All ▼]   │
│  ├ Orders │  Search: [________________] [Search]  │
│  └ Cart  │                                │
│ CRM      │  ┌────┬────────┬──────┬──────┬────┐│
│          │  │Img │  Name  │Price │Stock │Act││
│          │  ├────┼────────┼──────┼──────┼────┤│
│          │  │[img]│iPhone 15│$999  │100   │ ✓ ││
│          │  ├────┼────────┼──────┼──────┼────┤│
│          │  │[img]│MacBook Pro│$1999 │ 50   │ ✓ ││
│          │  ├────┼────────┼──────┼──────┼────┤│
│          │  │[img]│AirPods  │$199  │ 200  │ ✓ ││
│          │  └────┴────────┴──────┴──────┴────┘│
│          │                                │
│          │  Pagination: < 1 2 3 4 5 >     │
└──────────┴────────────────────────────────────────┘
```

#### CRM Lead Management
```
┌──────────┬────────────────────────────────────────┐
│   Menu   │  Lead Management                 │
├──────────┤  [Add Lead] [Import Leads]         │
│ ...      │                                │
│ CRM      │  Stage: [All ▼] Status: [All ▼]     │
│  ├ Leads │  Search: [________________] [Search]  │
│  ├ Custom│                                │
│  └ Opprt.│  ┌──────┬────────┬──────────┬─────┐│
│          │  │Name  │Company  │Status    │Act.││
│          │  ├──────┼────────┼──────────┼─────┤│
│          │  │John  │ABC Corp  │New       │[Edit]││
│          │  │Smith │         │          │[Conv]││
│          │  ├──────┼────────┼──────────┼─────┤│
│          │  │Jane  │XYZ Ltd  │Contacted │[Edit]││
│          │  │Doe   │         │          │[Conv]││
│          │  ├──────┼────────┼──────────┼─────┤│
│          │  │Mike  │Inc. Co  │Qualified │[Edit]││
│          │  │Brown │         │          │[Conv]││
│          │  └──────┴────────┴──────────┴─────┘│
│          │                                │
│          │  Pipeline:                       │
│          │  [New: 10] → [Contacted: 15] →   │
│          │  [Qualified: 8] → [Closed: 20]    │
└──────────┴────────────────────────────────────────┘
```

#### Workflow Design
```
┌─────────────────────────────────────────────────────┐
│          Workflow Designer                    │
├─────────────────────────────────────────────────────┤
│                                             │
│  ┌───────────────────────────────────────┐    │
│  │  Components Palette                 │    │
│  │  ┌─────┐ ┌─────┐ ┌───────┐    │    │
│  │  │Start│ │ Task│ │Gateway│    │    │
│  │  └─────┘ └─────┘ └───────┘    │    │
│  │  ┌─────┐ ┌─────┐ ┌───────┐    │    │
│  │  │ End │ │ User│ │System │    │    │
│  │  └─────┘ └─────┘ └───────┘    │    │
│  └───────────────────────────────────────┘    │
│                                             │
│  ┌───────────────────────────────────────────┐│
│  │         Canvas                       ││
│  │                                       ││
│  │  [Start] → [Submit Task]              ││
│  │    ↓                                 ││
│  │  [Manager Approval]                    ││
│  │    ↓                                 ││
│  │  [Finance Approval]                    ││
│  │    ↓                                 ││
│  │  [End]                              ││
│  │                                       ││
│  └───────────────────────────────────────────┘│
│                                             │
│  Properties Panel:                          │
│  Selected: [Submit Task]                    │
│  ┌───────────────────────────────────────┐    │
│  │ Task Name: [Submit Task________]   │    │
│  │ Assignee:  [Manager ▼]         │    │
│  │ Due Time:  [2025-01-20________]  │    │
│  │ Form:     [Application Form ▼]  │    │
│  └───────────────────────────────────────┘    │
│                                             │
│  [Save] [Deploy] [Clear]                      │
└─────────────────────────────────────────────────────┘
```

---

## Interaction Flow Diagrams

### User Registration Flow
```
User → Registration Page
   ↓
Enter Email/Phone
   ↓
[Click Send Verification Code]
   ↓
System → Send Code to Email/Phone
   ↓
User → Enter Verification Code
   ↓
Enter Password
   ↓
[Click Register]
   ↓
System → Validate Input
   ↓
Create Account
   ↓
Send Confirmation Email
   ↓
Redirect to Login Page
```

### Order Placement Flow
```
User → Browse Products
   ↓
Add to Cart
   ↓
View Cart
   ↓
[Click Checkout]
   ↓
Enter Shipping Address
   ↓
Select Payment Method
   ↓
[Click Place Order]
   ↓
System → Create Order
   ↓
Redirect to Payment Gateway
   ↓
User → Complete Payment
   ↓
Payment Gateway → Callback
   ↓
System → Update Order Status
   ↓
Send Confirmation Email
   ↓
Redirect to Order Success Page
```

### Approval Workflow Flow
```
User → Submit Request
   ↓
System → Create Workflow Instance
   ↓
Assign to First Approver
   ↓
Approver 1 → Approve/Reject
   ↓
If Approved → Assign to Approver 2
If Rejected → Notify Submitter
   ↓
Approver 2 → Approve/Reject
   ↓
If Approved → Complete Workflow
If Rejected → Notify Submitter
   ↓
System → Send Notification
   ↓
Submitter → View Result
```

---

## UI Design Drafts

### Color Scheme
- **Primary Color**: #1890ff (Blue)
- **Success Color**: #52c41a (Green)
- **Warning Color**: #faad14 (Orange)
- **Error Color**: #f5222d (Red)
- **Text Color**: #000000 (Primary), #666666 (Secondary)
- **Background Color**: #ffffff (Primary), #f5f5f5 (Secondary)

### Typography
- **Heading Font**: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial
- **Body Font**: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial
- **Monospace Font**: Consolas, Monaco, "Courier New", monospace

### Component Specifications
- **Button**: Height 32px, Border Radius 4px, Padding 8px 16px
- **Input**: Height 32px, Border Radius 4px, Padding 4px 12px
- **Card**: Border Radius 8px, Shadow: 0 2px 8px rgba(0,0,0,0.08)
- **Table**: Row Height 56px, Border Bottom 1px solid #f0f0f0

---

## Version History

| Version | Date | Author | Changes |
|---------|-------|---------|----------|
| 1.0.0 | 2025-01-11 | Initial version |
