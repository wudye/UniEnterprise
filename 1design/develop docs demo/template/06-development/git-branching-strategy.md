# Git Branching Strategy

## Overview

This document defines the Git branching strategy for the UniEnterprise project, including branch types, naming conventions, merge processes, and release procedures.

---

## Branch Types

### Main Branches

#### main (or master)

**Purpose:** Production-ready code

**Rules:**
- Only production-ready code
- Tags for releases
- No direct commits
- Protected branch (requires pull requests)

#### develop

**Purpose:** Integration branch for features

**Rules:**
- All features merge into develop
- Preparation for next release
- No direct commits
- Protected branch (requires pull requests)

### Supporting Branches

#### feature

**Purpose:** New feature development

**Rules:**
- Branch from: develop
- Merge into: develop
- Naming: `feature/<feature-name>`
- Lifetime: Until feature is complete

#### hotfix

**Purpose:** Production bug fixes

**Rules:**
- Branch from: main
- Merge into: main and develop
- Naming: `hotfix/<issue-description>`
- Lifetime: Until fix is deployed

#### release

**Purpose:** Release preparation

**Rules:**
- Branch from: develop
- Merge into: main and develop
- Naming: `release/<version>`
- Lifetime: Until release is complete

#### bugfix

**Purpose:** Non-critical bug fixes

**Rules:**
- Branch from: develop
- Merge into: develop
- Naming: `bugfix/<issue-description>`
- Lifetime: Until fix is complete

---

## Branch Structure

```
main (production)
  ↑
  │   release/v1.0.0
  │
  │
  develop (integration)
  ↑
  │
  ├── feature/user-management
  │
  ├── feature/order-processing
  │
  ├── feature/payment-integration
  │
  ├── bugfix/login-issue
  │
  └── feature/report-dashboard
```

---

## Branch Naming Conventions

### Feature Branches

**Format:** `feature/<feature-name>`

**Examples:**
- `feature/user-management`
- `feature/order-processing`
- `feature/payment-integration`
- `feature/report-dashboard`
- `feature/multi-tenancy`

### Hotfix Branches

**Format:** `hotfix/<issue-description>`

**Examples:**
- `hotfix/login-error`
- `hotfix/payment-failure`
- `hotfix/security-vulnerability`
- `hotfix/performance-issue`

### Release Branches

**Format:** `release/<version>`

**Examples:**
- `release/v1.0.0`
- `release/v1.1.0`
- `release/v2.0.0`

### Bugfix Branches

**Format:** `bugfix/<issue-description>`

**Examples:**
- `bugfix/menu-display`
- `bugfix/export-error`
- `bugfix/notification-delay`

---

## Workflow

### Feature Development Workflow

1. **Create feature branch** from develop
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b feature/user-management
   ```

2. **Develop feature** on feature branch
   ```bash
   # Make changes
   git add .
   git commit -m "feat: add user management module"
   ```

3. **Pull latest changes** from develop
   ```bash
   git pull origin develop
   # Resolve conflicts if any
   ```

4. **Push feature branch** to remote
   ```bash
   git push origin feature/user-management
   ```

5. **Create pull request** to develop
   - Request code review
   - Run CI/CD checks
   - Merge into develop

### Hotfix Workflow

1. **Create hotfix branch** from main
   ```bash
   git checkout main
   git pull origin main
   git checkout -b hotfix/login-error
   ```

2. **Fix the bug** on hotfix branch
   ```bash
   # Make fixes
   git add .
   git commit -m "fix: resolve login authentication error"
   ```

3. **Push hotfix branch** to remote
   ```bash
   git push origin hotfix/login-error
   ```

4. **Create pull request** to main
   - Request code review
   - Run CI/CD checks
   - Merge into main

5. **Merge hotfix into develop**
   ```bash
   git checkout develop
   git merge hotfix/login-error
   git push origin develop
   ```

### Release Workflow

1. **Create release branch** from develop
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b release/v1.0.0
   ```

2. **Update version** in code
   - Update version numbers
   - Update CHANGELOG
   - Update documentation

3. **Commit version changes**
   ```bash
   git add .
   git commit -m "chore: bump version to v1.0.0"
   ```

4. **Merge release into main**
   ```bash
   git checkout main
   git merge release/v1.0.0
   git tag -a v1.0.0 -m "Release version 1.0.0"
   git push origin main --tags
   ```

5. **Merge release into develop**
   ```bash
   git checkout develop
   git merge release/v1.0.0
   git push origin develop
   ```

6. **Delete release branch**
   ```bash
   git branch -d release/v1.0.0
   git push origin --delete release/v1.0.0
   ```

---

## Commit Convention

### Commit Message Format

**Format:** `<type>(<scope>): <subject>`

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Test changes
- `chore`: Build process or auxiliary tool changes

### Commit Message Examples

```
feat(user): add user management module
fix(auth): resolve login authentication error
docs(readme): update installation instructions
style(format): apply code formatting
refactor(api): simplify API response structure
test(unit): add unit tests for user service
chore(deps): upgrade Spring Boot to 3.2.0
```

### Conventional Commits

**Format:**

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Example:**

```
feat(user): add user management module

Add comprehensive user management features including:
- User CRUD operations
- Role management
- Permission management
- Department management

Closes #123
```

---

## Merge Strategy

### Merge vs Rebase

**Strategy:** Use merge for feature branches

**Reasoning:**
- Preserve history
- Easier to understand
- Better for collaboration

**When to use rebase:**
- Small personal branches
- Local cleanup before PR

### Merge Example

```bash
# Fast-forward merge (if possible)
git checkout develop
git merge feature/user-management

# Three-way merge (if diverged)
git checkout develop
git merge --no-ff feature/user-management
```

### Rebase Example

```bash
# Rebase feature onto latest develop
git checkout feature/user-management
git fetch origin
git rebase origin/develop
```

---

## Pull Request Process

### PR Requirements

1. **Branch naming:** Follow naming conventions
2. **Clean history:** Squash commits if needed
3. **Tests:** All tests passing
4. **Documentation:** Update relevant docs
5. **Description:** Clear PR description

### PR Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] Manual testing completed

## Checklist
- [ ] Code follows project style guidelines
- [ ] Self-review completed
- [ ] Commented complex code
- [ ] Documentation updated
- [ ] No new warnings generated
```

### PR Review Process

1. **Submit PR** to target branch
2. **Automated checks** (CI/CD) must pass
3. **Code review** by at least one reviewer
4. **Address feedback** from reviewers
5. **Approval** from reviewers
6. **Merge** PR

---

## Protected Branches

### Branch Protection Rules

**main branch:**
- Require pull requests
- Require at least 1 approval
- Dismiss stale approvals
- Require status checks to pass
- Include administrators

**develop branch:**
- Require pull requests
- Require at least 1 approval
- Dismiss stale approvals
- Require status checks to pass
- Include administrators

### GitHub Configuration

```yaml
# .github/branch-protection.yml
branches:
  - name: main
    protection:
      required_pull_request_reviews:
        required_approving_review_count: 1
        dismiss_stale_reviews: true
      required_status_checks:
        strict: true
        contexts:
          - CI
      enforce_admins: false

  - name: develop
    protection:
      required_pull_request_reviews:
        required_approving_review_count: 1
        dismiss_stale_reviews: true
      required_status_checks:
        strict: true
        contexts:
          - CI
      enforce_admins: false
```

---

## Tagging Strategy

### Tag Naming

**Format:** `v<major>.<minor>.<patch>`

**Examples:**
- `v1.0.0` - First release
- `v1.1.0` - Feature update
- `v1.1.1` - Bug fix
- `v2.0.0` - Major version

### Creating Tags

```bash
# Create annotated tag
git tag -a v1.0.0 -m "Release version 1.0.0"

# Push tags to remote
git push origin v1.0.0
git push origin --tags
```

### Tagging for Releases

```bash
# Tag main branch for release
git checkout main
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0

# Tag develop branch for beta
git checkout develop
git tag -a v1.1.0-beta.1 -m "Beta release 1.1.0"
git push origin v1.1.0-beta.1
```

---

## Release Process

### Release Checklist

1. **Code quality**
   - [ ] All tests passing
   - [ ] Code review completed
   - [ ] No critical bugs

2. **Documentation**
   - [ ] CHANGELOG updated
   - [ ] README updated
   - [ ] API docs updated

3. **Testing**
   - [ ] Integration tests passed
   - [ ] Manual testing completed
   - [ ] Performance tests passed

4. **Deployment**
   - [ ] Staging deployment successful
   - [ ] Production backup created
   - [ ] Production deployment plan ready

### Release Steps

1. **Create release branch**
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b release/v1.0.0
   ```

2. **Update version numbers**
   - Update pom.xml
   - Update package.json
   - Update version constants

3. **Update CHANGELOG**
   ```markdown
   ## [1.0.0] - 2025-01-11
   ### Added
   - User management module
   - Order processing
   - Payment integration
   ### Fixed
   - Login authentication error
   ### Changed
   - API response structure
   ```

4. **Commit changes**
   ```bash
   git add .
   git commit -m "chore: prepare release v1.0.0"
   ```

5. **Merge to main and tag**
   ```bash
   git checkout main
   git merge release/v1.0.0
   git tag -a v1.0.0 -m "Release version 1.0.0"
   git push origin main --tags
   ```

6. **Merge to develop**
   ```bash
   git checkout develop
   git merge release/v1.0.0
   git push origin develop
   ```

7. **Deploy to production**
   ```bash
   # CI/CD will handle deployment
   ```

---

## Version History

| Version | Date | Author | Changes |
|---------|-------|---------|----------|
| 1.0.0 | 2025-01-11 | Initial Git branching strategy |
