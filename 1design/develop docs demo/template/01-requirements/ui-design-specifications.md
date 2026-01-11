# UI Design Specifications

## Overview

This document defines the visual design and user experience standards for the UniEnterprise project, including color schemes, typography, component specifications, and responsive design guidelines.

---

## Design System

### Color Palette

#### Primary Colors

| Color Name | Hex Code | Usage |
|------------|----------|-------|
| Primary Blue | #1890FF | Primary buttons, links, active states |
| Primary Dark | #096DD9 | Hover states |
| Primary Light | #40A9FF | Light backgrounds, disabled states |

#### Secondary Colors

| Color Name | Hex Code | Usage |
|------------|----------|-------|
| Success Green | #52C41A | Success messages, positive actions |
| Warning Orange | #FAAD14 | Warning messages, caution states |
| Error Red | #F5222D | Error messages, destructive actions |
| Info Cyan | #13C2C2 | Information messages |

#### Neutral Colors

| Color Name | Hex Code | Usage |
|------------|----------|-------|
| Text Primary | #262626 | Primary text, headings |
| Text Secondary | #595959 | Secondary text, body copy |
| Text Tertiary | #8C8C8C | Helper text, labels |
| Border Color | #D9D9D9 | Borders, dividers |
| Background | #F0F2F5 | Page background |
| White | #FFFFFF | Component backgrounds |

### Typography

#### Font Family

```css
font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto,
             'Helvetica Neue', Arial, 'Noto Sans', sans-serif;
```

#### Font Sizes

| Type | Size | Line Height | Usage |
|------|------|-------------|-------|
| H1 | 38px | 1.2 | Page titles |
| H2 | 30px | 1.3 | Section titles |
| H3 | 24px | 1.4 | Subsection titles |
| H4 | 20px | 1.5 | Card titles |
| H5 | 16px | 1.5 | Table headers |
| Body | 14px | 1.5 | Body text |
| Small | 12px | 1.5 | Helper text |

#### Font Weights

| Weight | Value | Usage |
|--------|-------|-------|
| Regular | 400 | Body text |
| Medium | 500 | Emphasized text |
| Semibold | 600 | Headings |
| Bold | 700 | Strong emphasis |

---

## Component Specifications

### Buttons

#### Primary Button

```css
.btn-primary {
  background-color: #1890FF;
  color: #FFFFFF;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-primary:hover {
  background-color: #40A9FF;
}

.btn-primary:disabled {
  background-color: #D9D9D9;
  cursor: not-allowed;
}
```

#### Secondary Button

```css
.btn-secondary {
  background-color: transparent;
  color: #1890FF;
  border: 1px solid #1890FF;
  padding: 8px 16px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
}
```

#### Danger Button

```css
.btn-danger {
  background-color: #F5222D;
  color: #FFFFFF;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
}
```

#### Button Sizes

| Size | Padding | Font Size | Icon Size |
|------|---------|-----------|-----------|
| Large | 12px 24px | 16px | 20px |
| Medium (default) | 8px 16px | 14px | 16px |
| Small | 4px 12px | 12px | 14px |

### Form Inputs

#### Input Field

```css
.form-input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #D9D9D9;
  border-radius: 4px;
  font-size: 14px;
  transition: all 0.3s;
}

.form-input:focus {
  border-color: #1890FF;
  outline: none;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.form-input:disabled {
  background-color: #F5F5F5;
  cursor: not-allowed;
}
```

#### Input States

| State | Border Color | Background |
|-------|--------------|------------|
| Default | #D9D9D9 | #FFFFFF |
| Focus | #1890FF | #FFFFFF |
| Error | #F5222D | #FFF1F0 |
| Disabled | #D9D9D9 | #F5F5F5 |

#### Input Sizes

| Size | Padding | Font Size |
|------|---------|-----------|
| Large | 12px 16px | 16px |
| Medium (default) | 8px 12px | 14px |
| Small | 4px 8px | 12px |

### Tables

#### Standard Table

```css
.table {
  width: 100%;
  border-collapse: collapse;
  border: 1px solid #F0F0F0;
}

.table thead th {
  background-color: #FAFAFA;
  border-bottom: 1px solid #F0F0F0;
  padding: 12px 16px;
  font-weight: 600;
  text-align: left;
}

.table tbody td {
  padding: 12px 16px;
  border-bottom: 1px solid #F0F0F0;
}

.table tbody tr:hover {
  background-color: #FAFAFA;
}
```

#### Table Actions Column

| Action | Icon | Color |
|--------|------|-------|
| View | Eye icon | #1890FF |
| Edit | Pencil icon | #1890FF |
| Delete | Trash icon | #F5222D |

### Cards

#### Standard Card

```css
.card {
  background-color: #FFFFFF;
  border: 1px solid #F0F0F0;
  border-radius: 4px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.card-header {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #F0F0F0;
}
```

#### Card Variants

| Variant | Description | Use Case |
|---------|-------------|----------|
| Standard | White background with shadow | General content display |
| Bordered | White background with border | Form containers |
| Hover | Hover effect on mouseover | Interactive cards |

---

## Layout Guidelines

### Page Layout

```
┌────────────────────────────────────────────┐
│                Header Bar                  │
├────────┬───────────────────────────────────┤
│        │                                   │
│        │         Main Content              │
│ Sidebar│                                   │
│        │                                   │
│        │                                   │
└────────┴───────────────────────────────────┘
```

### Header Bar

- Height: 64px
- Background: #001529 (dark theme)
- Logo: Left aligned
- Navigation: Left aligned
- User menu: Right aligned

### Sidebar

- Width: 256px
- Background: #001529 (dark theme)
- Menu items: Left aligned with icons
- Active state: #1890FF background

### Main Content

- Background: #F0F2F5
- Padding: 24px
- Maximum content width: 1200px
- Center aligned

---

## Responsive Design

### Breakpoints

| Breakpoint | Screen Width | Columns |
|------------|--------------|---------|
| Mobile | < 576px | 1 column |
| Tablet | 576px - 768px | 2 columns |
| Desktop | 768px - 992px | 3 columns |
| Large Desktop | > 992px | 4 columns |

### Mobile Adaptations

#### Mobile Menu

- Sidebar collapses to hamburger menu
- Menu slides from left on toggle

#### Mobile Tables

- Tables switch to card view on mobile
- Horizontal scroll for complex tables

#### Mobile Forms

- Single column layout
- Full-width input fields
- Larger touch targets (min 44px)

---

## Icon System

### Icon Library

- Primary: Ant Design Icons
- Backup: Material Design Icons

### Icon Sizes

| Size | Value | Usage |
|------|-------|-------|
| Extra Small | 12px | Inline text icons |
| Small | 16px | Form labels, buttons |
| Medium | 20px | Table actions |
| Large | 24px | Page headers |
| Extra Large | 32px | Dashboard widgets |

### Icon Colors

| Usage | Color |
|-------|-------|
| Information | #1890FF |
| Success | #52C41A |
| Warning | #FAAD14 |
| Error | #F5222D |
| Neutral | #8C8C8C |

---

## Animation Guidelines

### Duration

| Type | Duration |
|------|----------|
| Micro interactions | 0.1s - 0.2s |
| Standard transitions | 0.3s |
| Complex animations | 0.5s |
| Page transitions | 0.3s - 0.5s |

### Easing

| Type | Function |
|------|----------|
| Enter | cubic-bezier(0.25, 0.1, 0.25, 1) |
| Exit | cubic-bezier(0.4, 0, 0.2, 1) |
| Bounce | cubic-bezier(0.68, -0.55, 0.265, 1.55) |

### Common Animations

| Animation | Duration | Usage |
|-----------|----------|-------|
| Fade In | 0.3s | Modal, dropdown appearance |
| Fade Out | 0.2s | Modal, dropdown disappearance |
| Slide Down | 0.3s | Dropdown menus |
| Slide Up | 0.3s | Bottom sheets |
| Scale | 0.2s | Button press, hover effects |

---

## Accessibility

### Color Contrast

- Text to background: Minimum 4.5:1 (WCAG AA)
- Large text: Minimum 3:1 (WCAG AA)
- UI components: Minimum 3:1 (WCAG AA)

### Keyboard Navigation

- Tab order: Logical and consistent
- Focus indicator: Visible 2px border
- Keyboard shortcuts: Documented in help

### Screen Reader Support

- ARIA labels for all interactive elements
- Alt text for all images
- Semantic HTML structure

---

## Browser Support

| Browser | Version | Status |
|---------|---------|--------|
| Chrome | Latest 2 versions | Supported |
| Firefox | Latest 2 versions | Supported |
| Safari | Latest 2 versions | Supported |
| Edge | Latest 2 versions | Supported |
| IE 11 | - | Not supported |

---

## Design Assets

### Figma Files

- Main design file: `uni-enterprise-design.fig`
- Component library: `uni-enterprise-components.fig`

### Image Assets

- Logo: `/assets/images/logo.svg`
- Icons: `/assets/icons/`
- Illustrations: `/assets/illustrations/`

---

## Version History

| Version | Date | Author | Changes |
|---------|-------|---------|----------|
| 1.0.0 | 2025-01-11 | Initial UI design specifications |
