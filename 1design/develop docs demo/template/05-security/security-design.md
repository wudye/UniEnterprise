# Security Design Solution

## Overview

This document describes the comprehensive security design for the UniEnterprise project, including authentication, authorization, data encryption, protection mechanisms, and security best practices.

---

## Security Architecture

### Security Layers

```
┌─────────────────────────────────────────────────────────┐
│                   Network Layer                         │
│  - Firewall                                            │
│  - DDoS Protection                                     │
│  - Network Segmentation                                │
└─────────────────────────────────────────────────────────┘
                        │
┌─────────────────────────────────────────────────────────┐
│                   Application Layer                    │
│  - Authentication (JWT/OAuth2)                         │
│  - Authorization (RBAC)                                │
│  - Rate Limiting                                       │
│  - Input Validation                                    │
└─────────────────────────────────────────────────────────┘
                        │
┌─────────────────────────────────────────────────────────┐
│                     Data Layer                         │
│  - Encryption at Rest (AES)                            │
│  - Encryption in Transit (TLS)                         │
│  - Data Masking                                        │
│  - Audit Logging                                       │
└─────────────────────────────────────────────────────────┘
```

---

## Authentication & Authorization

### Authentication Strategy

**Primary Method:** JWT (JSON Web Token)

**Flow:**
1. User submits credentials (username/password)
2. Server validates credentials
3. Server generates JWT token
4. Client stores token and includes in Authorization header
5. Server validates token on each request

**Configuration:**

```yaml
security:
  jwt:
    secret: ${JWT_SECRET:change-this-secret-in-production}
    expiration: 7200  # 2 hours
    refresh-expiration: 604800  # 7 days
  oauth2:
    enabled: true
    providers:
      - google
      - github
```

### Authorization Strategy

**Model:** RBAC (Role-Based Access Control)

**Components:**
- **User**: System user
- **Role**: Collection of permissions
- **Permission**: Granular access right
- **Menu**: UI element with permission

**Permission Hierarchy:**
```
Admin (Super Admin)
├── User Management
│   ├── user:view
│   ├── user:create
│   ├── user:update
│   └── user:delete
├── Role Management
│   ├── role:view
│   ├── role:create
│   ├── role:update
│   └── role:delete
└── ...

User (Regular User)
├── My Profile
│   ├── profile:view
│   └── profile:update
└── ...
```

### Security Configuration

```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
                .antMatchers("/api/v1/auth/**").permitAll()
                .antMatchers("/api/v1/public/**").permitAll()
                .antMatchers("/swagger-ui/**").permitAll()
                .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
}
```

---

## Data Encryption

### Encryption in Transit

**Protocol:** TLS 1.2/1.3

**Configuration:**

```yaml
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${SSL_KEYSTORE_PASSWORD}
    key-store-type: PKCS12
    key-alias: uni-enterprise
    protocol: TLSv1.3
    ciphers:
      - TLS_AES_256_GCM_SHA384
      - TLS_CHACHA20_POLY1305_SHA256
```

### Encryption at Rest

**Algorithm:** AES-256-GCM

**Sensitive Fields:**
- Passwords (BCrypt)
- Phone numbers (AES)
- Email addresses (AES)
- ID numbers (AES)
- Credit card numbers (AES)

**Implementation:**

```java
@Service
public class EncryptionService {

    private static final String SECRET_KEY = "your-256-bit-secret";
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;

    private final SecretKeySpec keySpec;
    private final SecureRandom secureRandom;

    public EncryptionService() {
        byte[] key = SECRET_KEY.getBytes();
        this.keySpec = new SecretKeySpec(Arrays.copyOf(key, 32), "AES");
        this.secureRandom = new SecureRandom();
    }

    public String encrypt(String plaintext) throws Exception {
        byte[] iv = new byte[12];
        secureRandom.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

        byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        byte[] encrypted = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, encrypted, 0, iv.length);
        System.arraycopy(ciphertext, 0, encrypted, iv.length, ciphertext.length);

        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decrypt(String ciphertext) throws Exception {
        byte[] encrypted = Base64.getDecoder().decode(ciphertext);

        byte[] iv = new byte[12];
        System.arraycopy(encrypted, 0, iv, 0, iv.length);

        byte[] cipherBytes = new byte[encrypted.length - iv.length];
        System.arraycopy(encrypted, iv.length, cipherBytes, 0, cipherBytes.length);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

        byte[] plaintext = cipher.doFinal(cipherBytes);
        return new String(plaintext, StandardCharsets.UTF_8);
    }
}
```

### Password Hashing

**Algorithm:** BCrypt

**Configuration:**

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);  // Work factor 12
}
```

**Usage:**

```java
@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void createUser(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        // ...
    }

    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
```

---

## Protection Mechanisms

### SQL Injection Protection

**Strategy:** Parameterized Queries

**Best Practices:**
1. Use MyBatis parameter binding
2. Use @Param annotation
3. Avoid string concatenation in SQL
4. Validate and sanitize user input

**Example:**

❌ **Vulnerable:**
```java
@Select("SELECT * FROM user WHERE username = '${username}'")
User selectByUsername(String username);
```

✅ **Secure:**
```java
@Select("SELECT * FROM user WHERE username = #{username}")
User selectByUsername(@Param("username") String username);
```

### XSS Protection

**Strategy:** Input Sanitization + Output Encoding

**Implementation:**

```java
@Component
public class XSSFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                        ServletResponse response,
                        FilterChain chain)
            throws IOException, ServletException {
        XSSRequestWrapper wrappedRequest = new XSSRequestWrapper(
            (HttpServletRequest) request
        );
        chain.doFilter(wrappedRequest, response);
    }
}

public class XSSRequestWrapper extends HttpServletRequestWrapper {

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }

        String[] encodedValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            encodedValues[i] = sanitize(values[i]);
        }

        return encodedValues;
    }

    private String sanitize(String value) {
        if (value == null) {
            return null;
        }
        return value.replaceAll("<", "&lt;")
                   .replaceAll(">", "&gt;")
                   .replaceAll("\"", "&quot;")
                   .replaceAll("'", "&#x27;");
    }
}
```

### CSRF Protection

**Strategy:** Same-Site Cookies + CSRF Token

**Configuration:**

```java
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .and()
            // ... other configuration
        return http.build();
    }
}
```

### Clickjacking Protection

**Strategy:** X-Frame-Options Header

**Configuration:**

```java
@Configuration
public class SecurityHeadersConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.headers()
            .frameOptions().deny()
            .and()
            .headers()
            .xssProtection()
            .and()
            .contentSecurityPolicy("default-src 'self'")
            .and()
            .httpStrictTransportSecurity();
        return http.build();
    }
}
```

---

## Sensitive Data Protection

### Data Masking

**Strategy:** Mask sensitive data in logs and responses

**Implementation:**

```java
public class DataMaskingUtil {

    public static String maskEmail(String email) {
        if (StringUtils.isBlank(email)) {
            return email;
        }
        int atIndex = email.indexOf("@");
        if (atIndex <= 1) {
            return "****@****.com";
        }
        String prefix = email.substring(0, 1);
        String suffix = email.substring(atIndex);
        return prefix + "****" + suffix;
    }

    public static String maskPhone(String phone) {
        if (StringUtils.isBlank(phone) || phone.length() < 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    public static String maskIdNumber(String idNumber) {
        if (StringUtils.isBlank(idNumber) || idNumber.length() < 6) {
            return idNumber;
        }
        return idNumber.substring(0, 3) + "***********" +
               idNumber.substring(idNumber.length() - 4);
    }
}
```

### Audit Log for Sensitive Operations

**Sensitive Operations:**
- Login/Logout
- Password change
- User creation/deletion
- Role changes
- Data export
- System configuration changes

---

## Security Headers

### Required Headers

| Header | Value | Purpose |
|--------|-------|---------|
| X-Frame-Options | DENY | Prevent clickjacking |
| X-XSS-Protection | 1; mode=block | XSS protection |
| X-Content-Type-Options | nosniff | Prevent MIME sniffing |
| Content-Security-Policy | default-src 'self' | CSP |
| Strict-Transport-Security | max-age=31536000 | HSTS |
| Referrer-Policy | strict-origin-when-cross-origin | Referrer policy |

**Configuration:**

```java
@Component
public class SecurityHeadersFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                        ServletResponse response,
                        FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader("X-Frame-Options", "DENY");
        httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");
        httpResponse.setHeader("Content-Security-Policy", "default-src 'self'");
        httpResponse.setHeader("Strict-Transport-Security", "max-age=31536000");
        httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        chain.doFilter(request, response);
    }
}
```

---

## Session Security

### Session Management

**Strategy:** Stateless (JWT-based)

**Configuration:**

```yaml
server:
  servlet:
    session:
      timeout: 1800  # 30 minutes
      cookie:
        http-only: true
        secure: true
        same-site: strict
```

### Session Timeout

- Access token: 2 hours
- Refresh token: 7 days
- Idle timeout: 30 minutes

---

## Security Best Practices

### Code Security

1. **Input Validation**
   - Validate all user inputs
   - Use whitelist approach
   - Sanitize data before processing

2. **Output Encoding**
   - Encode output to prevent XSS
   - Use template engines with auto-escaping
   - Validate data before display

3. **Error Handling**
   - Don't expose sensitive information in errors
   - Use generic error messages
   - Log detailed errors server-side only

4. **Dependencies**
   - Keep dependencies updated
   - Use dependency scanning tools
   - Remove unused dependencies

### Configuration Security

1. **Secrets Management**
   - Use environment variables for secrets
   - Never hardcode credentials
   - Use secret management tools (e.g., HashiCorp Vault)

2. **Production Configuration**
   ```yaml
   # production.yml
   spring:
     datasource:
       url: ${DB_URL}
       username: ${DB_USERNAME}
       password: ${DB_PASSWORD}
   security:
     jwt:
       secret: ${JWT_SECRET}
   ```

### Operational Security

1. **Regular Updates**
   - Update operating system
   - Update application dependencies
   - Apply security patches

2. **Backup & Recovery**
   - Regular backups
   - Encrypted backups
   - Test recovery procedures

3. **Monitoring**
   - Security event logging
   - Intrusion detection
   - Anomaly monitoring

---

## Version History

| Version | Date | Author | Changes |
|---------|-------|---------|----------|
| 1.0.0 | 2025-01-11 | Initial security design solution |
