# Vue/JavaScript Coding Standards

## Overview

This document defines the Vue/JavaScript coding standards for the UniEnterprise project, including naming conventions, component structure, state management, event handling, and best practices.

---

## Naming Conventions

### Component Names

**Format:** PascalCase (multi-word components)

**Examples:**
```vue
UserList.vue
OrderForm.vue
UserProfile.vue
DashboardChart.vue
```

**Rules:**
- Use PascalCase for component files
- Use multi-word names to avoid conflicts with HTML elements
- Use descriptive, meaningful names

**Avoid:**
```
List.vue (use UserList.vue)
Form.vue (use OrderForm.vue)
```

### TypeScript Types

**Format:** PascalCase

**Examples:**
```typescript
interface User {
  id: number
  name: string
  email: string
}

type UserRole = 'admin' | 'user' | 'viewer'

class UserService {
  // ...
}
```

### Functions/Methods

**Format:** camelCase

**Examples:**
```typescript
function getUserById(id: number) {
  // ...
}

const handleSubmit = () => {
  // ...
}

const fetchData = async () => {
  // ...
}
```

### Variables

**Format:** camelCase

**Examples:**
```typescript
const userName = 'John'
const orderList = []
const isLoading = false
const totalCount = 100
```

### Constants

**Format:** UPPER_SNAKE_CASE

**Examples:**
```typescript
const API_BASE_URL = 'https://api.example.com'
const MAX_RETRY_COUNT = 3
const DEFAULT_PAGE_SIZE = 10
```

### CSS Classes

**Format:** kebab-case

**Examples:**
```css
.user-list {
  padding: 20px;
}

.order-form {
  margin: 20px;
}

.submit-button {
  background: #1890ff;
}
```

---

## Component Structure

### Single File Component Structure

```vue
<template>
  <!-- 1. Template -->
  <div class="user-list">
    <!-- Template code -->
  </div>
</template>

<script setup lang="ts">
// 2. Imports
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserList } from '@/api/user'
import type { User } from '@/types/user'

// 3. Props (if any)
interface Props {
  userId?: number
}
const props = withDefaults(defineProps<Props>(), {
  userId: undefined
})

// 4. Emits (if any)
interface Emits {
  (e: 'update', value: string): void
  (e: 'delete', id: number): void
}
const emit = defineEmits<Emits>()

// 5. Composables
const router = useRouter()

// 6. State
const userList = ref<User[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 7. Computed
const hasUsers = computed(() => userList.value.length > 0)

// 8. Methods
const fetchUsers = async () => {
  loading.value = true
  try {
    const response = await getUserList({
      page: currentPage.value,
      size: pageSize.value
    })
    userList.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    console.error('Failed to fetch users:', error)
  } finally {
    loading.value = false
  }
}

const handleEdit = (user: User) => {
  router.push(`/users/${user.id}/edit`)
}

const handleDelete = async (id: number) => {
  if (await confirm('Are you sure you want to delete this user?')) {
    emit('delete', id)
  }
}

// 9. Lifecycle hooks
onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
/* 10. Styles */
.user-list {
  padding: 20px;
}
</style>
```

---

## TypeScript Usage

### Type Definitions

**Interface Definition:**

```typescript
// types/user.ts
export interface User {
  id: number
  username: string
  realName: string
  email: string
  phone: string
  status: number
  createTime: string
}

export interface UserDTO {
  username: string
  password?: string
  realName: string
  email?: string
  phone?: string
  deptId?: number
}

export interface UserVO {
  id: number
  username: string
  realName: string
  email: string
  phone: string
  deptId: number
  deptName: string
  status: number
  createTime: string
}
```

**Type Guards:**

```typescript
function isUser(obj: any): obj is User {
  return (
    obj !== null &&
    typeof obj === 'object' &&
    typeof obj.id === 'number' &&
    typeof obj.username === 'string'
  )
}
```

### Generic Types

```typescript
interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

interface PageData<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// Usage
type UserListResponse = ApiResponse<PageData<UserVO>>
```

---

## State Management (Pinia)

### Store Definition

```typescript
// stores/user.ts
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, logout, getUserInfo } from '@/api/auth'
import type { UserInfo } from '@/types/user'

export const useUserStore = defineStore('user', () => {
  // State
  const token = ref<string>('')
  const userInfo = ref<UserInfo | null>(null)

  // Getters
  const isLogin = computed(() => !!token.value)
  const username = computed(() => userInfo.value?.username || '')
  const hasPermission = computed(() => (permission: string) => {
    return userInfo.value?.permissions?.includes(permission) || false
  })

  // Actions
  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const clearToken = () => {
    token.value = ''
    localStorage.removeItem('token')
  }

  const setUserInfo = (info: UserInfo) => {
    userInfo.value = info
  }

  const clearUserInfo = () => {
    userInfo.value = null
  }

  const loginAction = async (username: string, password: string) => {
    const response = await login({ username, password })
    setToken(response.data.token)
    await fetchUserInfo()
  }

  const logoutAction = async () => {
    try {
      await logout()
    } finally {
      clearToken()
      clearUserInfo()
    }
  }

  const fetchUserInfo = async () => {
    const response = await getUserInfo()
    setUserInfo(response.data)
  }

  return {
    // State
    token,
    userInfo,
    // Getters
    isLogin,
    username,
    hasPermission,
    // Actions
    setToken,
    clearToken,
    setUserInfo,
    clearUserInfo,
    loginAction,
    logoutAction,
    fetchUserInfo
  }
})
```

### Store Usage

```vue
<script setup lang="ts">
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'

const userStore = useUserStore()
const { username, isLogin } = storeToRefs(userStore)

const handleLogout = () => {
  userStore.logoutAction()
}
</script>

<template>
  <div>
    <p v-if="isLogin">Welcome, {{ username }}</p>
    <button v-if="isLogin" @click="handleLogout">Logout</button>
  </div>
</template>
```

---

## API Calls

### API Service

```typescript
// api/user.ts
import request from '@/utils/request'
import type { UserDTO, UserVO, ApiResponse, PageData } from '@/types'

export const getUserList = (params: {
  page?: number
  size?: number
  username?: string
}): Promise<ApiResponse<PageData<UserVO>>> => {
  return request.get('/api/v1/users', { params })
}

export const getUserById = (id: number): Promise<ApiResponse<UserVO>> => {
  return request.get(`/api/v1/users/${id}`)
}

export const createUser = (data: UserDTO): Promise<ApiResponse<number>> => {
  return request.post('/api/v1/users', data)
}

export const updateUser = (
  id: number,
  data: UserDTO
): Promise<ApiResponse<void>> => {
  return request.put(`/api/v1/users/${id}`, data)
}

export const deleteUser = (id: number): Promise<ApiResponse<void>> => {
  return request.delete(`/api/v1/users/${id}`)
}
```

### Request Interceptor

```typescript
// utils/request.ts
import axios, { type AxiosInstance, type AxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const request: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor
request.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    const token = userStore.token

    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }

    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor
request.interceptors.response.use(
  (response) => {
    const { code, message, data } = response.data

    if (code === 200) {
      return response.data
    } else if (code === 401) {
      ElMessage.error('Login expired, please login again')
      const userStore = useUserStore()
      userStore.logoutAction()
      window.location.href = '/login'
      return Promise.reject(new Error(message))
    } else {
      ElMessage.error(message || 'Request failed')
      return Promise.reject(new Error(message))
    }
  },
  (error) => {
    ElMessage.error(error.message || 'Network error')
    return Promise.reject(error)
  }
)

export default request
```

---

## Event Handling

### Event Naming

```vue
<template>
  <!-- Use kebab-case in template -->
  <ChildComponent @user-update="handleUserUpdate" />
</template>

<script setup lang="ts">
// Use camelCase in script
const emit = defineEmits<{
  (e: 'user-update', user: User): void
}>()
</script>
```

### Event Modifiers

```vue
<template>
  <!-- Prevent default -->
  <form @submit.prevent="handleSubmit">
    <!-- Stop propagation -->
    <div @click.stop="handleDivClick">
      <button @click="handleButtonClick">Click me</button>
    </div>
    <!-- Execute once -->
    <button @click.once="handleOnceClick">Click once</button>
  </form>
</template>
```

---

## Best Practices

### Composables

```typescript
// composables/useTable.ts
import { ref, computed } from 'vue'

export function useTable<T>(fetchDataFn: () => Promise<T[]>) {
  const data = ref<T[]>([])
  const loading = ref(false)
  const total = ref(0)

  const fetch = async () => {
    loading.value = true
    try {
      const result = await fetchDataFn()
      data.value = result
    } catch (error) {
      console.error('Fetch error:', error)
    } finally {
      loading.value = false
    }
  }

  const hasData = computed(() => data.value.length > 0)

  return {
    data,
    loading,
    total,
    fetch,
    hasData
  }
}
```

### Performance Optimization

**v-if vs v-show:**

```vue
<!-- Use v-if for conditional rendering (expensive initialization) -->
<HeavyComponent v-if="showComponent" />

<!-- Use v-show for frequent toggling (cheap initialization) -->
<LightComponent v-show="showComponent" />
```

**Computed Properties:**

```typescript
// Good: Use computed for derived data
const fullName = computed(() => {
  return `${firstName.value} ${lastName.value}`
})

// Bad: Computed in template
<template>
  <p>{{ firstName }} {{ lastName }}</p>
</template>
```

**Keys in v-for:**

```vue
<!-- Good: Unique key -->
<li v-for="user in users" :key="user.id">
  {{ user.name }}
</li>

<!-- Bad: Index as key -->
<li v-for="(user, index) in users" :key="index">
  {{ user.name }}
</li>
```

---

## Code Quality

### ESLint Rules

```javascript
// .eslintrc.js
module.exports = {
  rules: {
    // Enforce single quotes
    quotes: ['error', 'single'],

    // Enforce semicolons
    semi: ['error', 'never'],

    // Max line length
    'max-len': [
      'error',
      {
        code: 100,
        ignoreStrings: true,
        ignoreTemplateLiterals: true
      }
    ],

    // No console in production
    'no-console': process.env.NODE_ENV === 'production' ? 'error' : 'warn',

    // No unused variables
    'no-unused-vars': 'error',

    // Vue-specific rules
    'vue/multi-word-component-names': 'off',
    'vue/no-v-html': 'warn',
    'vue/require-default-prop': 'error'
  }
}
```

---

## Version History

| Version | Date | Author | Changes |
|---------|-------|---------|----------|
| 1.0.0 | 2025-01-11 | Initial Vue/JavaScript coding standards |
