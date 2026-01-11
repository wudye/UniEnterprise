# IDE Configuration Guide

## Overview

This document provides IDE configuration guidelines for the UniEnterprise project, including IntelliJ IDEA and VSCode configurations, recommended plugins, and code formatting settings.

---

## IntelliJ IDEA Configuration

### Recommended Plugins

| Plugin | Description | Purpose |
|--------|-------------|---------|
| Lombok | Lombok support | Simplify Java code |
| MyBatisX | MyBatis support | Enhanced MyBatis development |
| Alibaba Java Coding Guidelines | Alibaba coding standards | Code quality checks |
| SonarLint | Code quality analysis | Static code analysis |
| Rainbow Brackets | Color-coded brackets | Better code readability |
| Translation | Translation plugin | English translation |
| GitToolBox | Git enhancement | Git workflow improvements |
| Maven Helper | Maven dependency management | Dependency analysis |
| JSON Viewer | JSON format viewer | JSON formatting |

### Plugin Installation

1. Open IntelliJ IDEA
2. Go to **File** → **Settings** → **Plugins**
3. Search for plugin name
4. Click **Install**
5. Restart IDE

### Code Formatting

#### Code Style Configuration

**Java Code Style:**

1. Go to **File** → **Settings** → **Editor** → **Code Style** → **Java**
2. Click **Set from...** → **GoogleStyleGuide**
3. Customize if needed

**Recommended Settings:**

```java
// Indentation
- Indent: 4 spaces
- Continuation indent: 8 spaces

// Tabs and Indents
- Use tab character: No
- Smart tabs: Yes

// Wrapping and Braces
- Keep when reformatting: Line breaks
- Right margin (columns): 120

// Imports
- Use single class import: Yes
- Use fully qualified class names: No
- Import count to use wildcard: 999
```

#### Import Optimization

**Configuration:**

1. Go to **Editor** → **Code Style** → **Java** → **Imports**
2. Set import order:

```java
// Static imports
// Java standard library
// Third-party libraries
// Project imports
```

#### Save Actions

**Configuration:**

1. Go to **Tools** → **Actions on Save**
2. Enable:
   - Optimize imports
   - Reformat code
   - Rearrange code
   - Run inspection

### Code Templates

#### File Templates

**Class Template:**

```java
#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME};

#end
/**
 * ${NAME}
 *
 * @author ${USER}
 * @since ${DATE}
 */
public class ${NAME} {

}
```

**Interface Template:**

```java
#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME};

#end
/**
 * ${NAME}
 *
 * @author ${USER}
 * @since ${DATE}
 */
public interface ${NAME} {

}
```

#### Live Templates

**Controller Template (Abbreviation: ctrl):**

```java
@RestController
@RequestMapping("/api/v1/$path$")
@Tag(name = "$name$", description = "$description$")
public class $ClassName$Controller {

    @Autowired
    private $Service$ $service$;

    @GetMapping
    @Operation(summary = "Get $resource$ list")
    public Result<IPage<$VO$>> getPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        IPage<$VO$> pageResult = $service$.getPage(page, size);
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get $resource$ by ID")
    public Result<$VO$> getById(@PathVariable $IdType$ id) {
        $VO$ vo = $service$.getById(id);
        return Result.success(vo);
    }

    @PostMapping
    @Operation(summary = "Create $resource$")
    public Result<$IdType$> create(@RequestBody @Validated $DTO$ dto) {
        $IdType$ id = $service$.create(dto);
        return Result.success(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update $resource$")
    public Result<Void> update(@PathVariable $IdType$ id,
                               @RequestBody @Validated $DTO$ dto) {
        $service$.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete $resource$")
    public Result<Void> delete(@PathVariable $IdType$ id) {
        $service$.delete(id);
        return Result.success();
    }
}
```

### Lombok Configuration

**Enable Lombok:**

1. Go to **Build, Execution, Deployment** → **Compiler** → **Annotation Processors**
2. Enable **Enable annotation processing**

### Run Configuration

#### Spring Boot Run Configuration

1. Go to **Run** → **Edit Configurations**
2. Click **+** → **Spring Boot**
3. Configure:

```
Name: UniEnterpriseApplication
Main class: com.uni.admin.UniEnterpriseApplication
VM options: -Xms512m -Xmx1024m
Environment: SPRING_PROFILES_ACTIVE=dev
```

---

## VSCode Configuration

### Recommended Extensions

| Extension | Description | Purpose |
|-----------|-------------|---------|
| ESLint | JavaScript/TypeScript linting | Code quality |
| Prettier | Code formatter | Code formatting |
| Volar | Vue 3 support | Vue development |
| TypeScript Vue Plugin (Volar) | TypeScript support for Vue | TypeScript |
| GitLens | Git enhancement | Git workflow |
| Prettier ESLint | Format on save with ESLint | Code formatting |
| Path Intellisense | Path autocompletion | File paths |
| CSS Peek | CSS source navigation | CSS/SCSS |
| Auto Close Tag | Auto close HTML/XML tags | HTML/XML |
| Auto Rename Tag | Auto rename paired tags | HTML/XML |
| Highlight Matching Tag | Highlight matching tags | HTML/XML |

### Extension Installation

1. Open VSCode
2. Go to **Extensions** (Ctrl+Shift+X)
3. Search for extension name
4. Click **Install**

### Settings.json Configuration

```json
{
  "editor.formatOnSave": true,
  "editor.defaultFormatter": "esbenp.prettier-vscode",
  "editor.codeActionsOnSave": {
    "source.fixAll.eslint": true
  },
  "editor.tabSize": 2,
  "editor.insertSpaces": true,
  "editor.wordWrap": "on",
  "editor.minimap.enabled": false,
  "files.associations": {
    "*.vue": "vue"
  },
  "files.exclude": {
    "**/.git": true,
    "**/.DS_Store": true,
    "**/node_modules": true,
    "**/dist": true,
    "**/target": true
  },
  "eslint.validate": [
    "javascript",
    "javascriptreact",
    "typescript",
    "typescriptreact",
    "vue"
  ],
  "eslint.workingDirectories": ["./frontend"],
  "typescript.tsdk": "./frontend/node_modules/typescript/lib",
  "volar.takeOverMode.enabled": true
}
```

### Prettier Configuration

**.prettierrc:**

```json
{
  "semi": false,
  "singleQuote": true,
  "trailingComma": "none",
  "printWidth": 100,
  "tabWidth": 2,
  "useTabs": false,
  "arrowParens": "always",
  "endOfLine": "lf"
}
```

**.prettierignore:**

```
node_modules
dist
build
coverage
*.min.js
*.min.css
```

### ESLint Configuration

**.eslintrc.js:**

```javascript
module.exports = {
  root: true,
  env: {
    node: true,
    browser: true,
    es2021: true
  },
  extends: [
    'plugin:vue/vue3-recommended',
    'eslint:recommended',
    'plugin:@typescript-eslint/recommended',
    'prettier'
  ],
  parser: 'vue-eslint-parser',
  parserOptions: {
    ecmaVersion: 2021,
    parser: '@typescript-eslint/parser',
    sourceType: 'module'
  },
  plugins: ['@typescript-eslint'],
  rules: {
    'vue/multi-word-component-names': 'off',
    '@typescript-eslint/no-explicit-any': 'warn',
    'no-console': process.env.NODE_ENV === 'production' ? 'error' : 'warn',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'error' : 'warn'
  }
}
```

### VSCode Tasks

**.vscode/tasks.json:**

```json
{
  "version": "2.0.0",
  "tasks": [
    {
      "label": "dev:frontend",
      "type": "shell",
      "command": "npm run dev",
      "options": {
        "cwd": "${workspaceFolder}/frontend"
      },
      "group": "build",
      "presentation": {
        "reveal": "always",
        "panel": "new"
      }
    },
    {
      "label": "build:frontend",
      "type": "shell",
      "command": "npm run build",
      "options": {
        "cwd": "${workspaceFolder}/frontend"
      },
      "group": {
        "kind": "build",
        "isDefault": true
      }
    },
    {
      "label": "dev:backend",
      "type": "shell",
      "command": "mvn spring-boot:run",
      "options": {
        "cwd": "${workspaceFolder}"
      },
      "group": "build"
    }
  ]
}
```

### VSCode Launch Configuration

**.vscode/launch.json:**

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "chrome",
      "request": "launch",
      "name": "vue: chrome",
      "url": "http://localhost:5173",
      "webRoot": "${workspaceFolder}/frontend/src",
      "breakOnLoad": true,
      "sourceMapPathOverrides": {
        "webpack:///src/*": "${webRoot}/*"
      }
    },
    {
      "type": "java",
      "name": "Spring Boot: Backend",
      "request": "launch",
      "mainClass": "com.uni.admin.UniEnterpriseApplication",
      "projectName": "uni-enterprise-admin"
    }
  ]
}
```

---

## Code Formatting Standards

### Java Code Standards

#### Naming Conventions

| Type | Convention | Example |
|------|------------|---------|
| Class | PascalCase | `UserService`, `OrderController` |
| Interface | PascalCase | `IUserService`, `IOrderService` |
| Method | camelCase | `getUserById()`, `createOrder()` |
| Variable | camelCase | `userName`, `orderCount` |
| Constant | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT`, `DEFAULT_PAGE_SIZE` |
| Package | lowercase with dots | `com.uni.admin.service` |

#### Code Organization

```java
package com.uni.admin.service;

import org.springframework.stereotype.Service;
import com.uni.common.domain.Result;
import java.util.List;

/**
 * User service implementation
 *
 * @author John Doe
 * @since 1.0.0
 */
@Service
public class UserServiceImpl implements IUserService {

    // Constants
    private static final int MAX_RETRY_COUNT = 3;

    // Dependencies
    @Autowired
    private UserMapper userMapper;

    // Public methods
    @Override
    public Result<UserVO> getUserById(Long id) {
        // Implementation
        return Result.success(user);
    }

    // Private methods
    private UserVO convertToVO(User user) {
        // Implementation
        return userVO;
    }
}
```

### Vue/TypeScript Code Standards

#### Naming Conventions

| Type | Convention | Example |
|------|------------|---------|
| Component | PascalCase | `UserList.vue`, `OrderForm.vue` |
| Interface/Type | PascalCase | `User`, `Order`, `ApiResponse` |
| Function/Method | camelCase | `getUserById()`, `handleSubmit()` |
| Variable | camelCase | `userName`, `orderList` |
| Constant | UPPER_SNAKE_CASE | `API_BASE_URL`, `MAX_RETRY` |
| CSS Class | kebab-case | `.user-list`, `.order-form` |

#### Component Structure

```vue
<template>
  <!-- Template -->
  <div class="user-list">
    <h1>User List</h1>
    <ul>
      <li v-for="user in users" :key="user.id">
        {{ user.name }}
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
// Imports
import { ref, onMounted } from 'vue';
import { getUserList } from '@/api/user';
import type { User } from '@/types/user';

// State
const users = ref<User[]>([]);
const loading = ref(false);

// Methods
const fetchUsers = async () => {
  loading.value = true;
  try {
    const response = await getUserList();
    users.value = response.data;
  } catch (error) {
    console.error('Failed to fetch users:', error);
  } finally {
    loading.value = false;
  }
};

// Lifecycle
onMounted(() => {
  fetchUsers();
});
</script>

<style scoped>
.user-list {
  padding: 20px;
}

h1 {
  font-size: 24px;
  color: #333;
}

ul {
  list-style: none;
  padding: 0;
}

li {
  padding: 8px 0;
}
</style>
```

---

## Version History

| Version | Date | Author | Changes |
|---------|-------|---------|----------|
| 1.0.0 | 2025-01-11 | Initial IDE configuration guide |
