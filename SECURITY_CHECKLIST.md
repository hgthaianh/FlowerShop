# 🔒 Security Checklist - Flower Shop

## Pre-Deployment Security

### ⚠️ CRITICAL - Must Do Before Deploy

#### 1. Environment Variables
- [ ] **Change default passwords**
  - Database password (currently: `postgres`)
  - Admin passwords
  - Any default credentials

- [ ] **Remove sensitive data from code**
  - No hardcoded passwords
  - No API keys in source code
  - No secrets in git history

- [ ] **Create strong .env.production**
  ```bash
  # Generate strong password
  openssl rand -base64 32
  
  # Update .env.production with strong passwords
  POSTGRES_PASSWORD=<strong-password-here>
  ```

- [ ] **Verify .gitignore**
  ```
  .env.production
  *.pem
  *.key
  backup/*.sql
  logs/
  ```

#### 2. Database Security
- [ ] **Change PostgreSQL default user**
  - Create new database user instead of `postgres`
  - Grant only necessary permissions

- [ ] **Restrict database access**
  - Change port from 5432 (optional)
  - Only allow localhost connections
  - Configure pg_hba.conf for IP whitelist

- [ ] **Enable SSL for database connections**
  ```properties
  spring.datasource.url=jdbc:postgresql://...?ssl=true&sslmode=require
  ```

#### 3. Application Configuration
- [ ] **Disable debug mode**
  ```properties
  spring.jpa.show-sql=false
  logging.level.root=INFO
  ```

- [ ] **Hide error details**
  ```properties
  server.error.include-message=never
  server.error.include-stacktrace=never
  ```

- [ ] **Configure CORS properly**
  - Only allow your domain
  - No wildcard (*) in production
  
  ```java
  @Configuration
  public class CorsConfig {
      @Bean
      public CorsConfigurationSource corsConfigurationSource() {
          CorsConfiguration configuration = new CorsConfiguration();
          configuration.setAllowedOrigins(Arrays.asList(
              "https://yourdomain.com",
              "https://www.yourdomain.com"
          ));
          configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
          configuration.setAllowedHeaders(Arrays.asList("*"));
          configuration.setAllowCredentials(true);
          
          UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
          source.registerCorsConfiguration("/**", configuration);
          return source;
      }
  }
  ```

#### 4. Admin Authentication (⚠️ VERY IMPORTANT)
**Current Status**: ❌ No authentication on admin endpoints!

- [ ] **Implement Spring Security**
  - Add spring-boot-starter-security
  - Implement JWT authentication
  - Protect `/api/admin/**` endpoints

- [ ] **Role-based access control**
  ```java
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/admin/blogs")
  public BlogResponse createBlog(...) { }
  ```

- [ ] **Password hashing**
  - BCrypt for admin passwords
  - Never store plain text passwords

#### 5. API Security
- [ ] **Rate limiting**
  - Implement rate limiting for public endpoints
  - Prevent brute force attacks

- [ ] **Input validation**
  - All DTOs have @Valid annotations ✅ (Done)
  - Sanitize user inputs
  - Prevent SQL injection (using JPA ✅)
  - Prevent XSS attacks

- [ ] **File upload security (if implemented later)**
  - Validate file types
  - Limit file sizes
  - Scan for malware
  - Store files outside web root

#### 6. Frontend Security
- [ ] **Environment variables**
  - Update VUE_APP_API_BASE to production domain
  - No sensitive data in frontend code

- [ ] **Content Security Policy**
  ```nginx
  add_header Content-Security-Policy "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline';";
  ```

- [ ] **HTTPS only**
  - Force HTTPS redirection
  - HSTS header enabled

---

## Infrastructure Security

### Server Setup

#### 1. Firewall Configuration
- [ ] **Enable UFW (Ubuntu)**
  ```bash
  sudo ufw allow 22/tcp    # SSH
  sudo ufw allow 80/tcp    # HTTP
  sudo ufw allow 443/tcp   # HTTPS
  sudo ufw enable
  ```

- [ ] **Block unnecessary ports**
  - Database port 5432 should NOT be exposed
  - Only allow localhost access to database

#### 2. SSH Security
- [ ] **Disable root login**
  ```bash
  # /etc/ssh/sshd_config
  PermitRootLogin no
  PasswordAuthentication no  # Use SSH keys only
  ```

- [ ] **Use SSH keys**
  - Generate SSH key pair
  - Disable password authentication

- [ ] **Change default SSH port (optional)**
  ```bash
  Port 2222  # in /etc/ssh/sshd_config
  ```

#### 3. SSL/TLS Certificate
- [ ] **Install SSL certificate**
  - Use Let's Encrypt (free)
  ```bash
  sudo apt install certbot python3-certbot-nginx
  sudo certbot --nginx -d yourdomain.com -d www.yourdomain.com
  ```

- [ ] **Auto-renewal**
  ```bash
  sudo systemctl enable certbot.timer
  ```

#### 4. Docker Security
- [ ] **Run containers as non-root**
  ```dockerfile
  USER 1000:1000
  ```

- [ ] **Limit container resources**
  ```yaml
  deploy:
    resources:
      limits:
        cpus: '0.5'
        memory: 512M
  ```

- [ ] **Use official images only**
  - postgres:16-alpine ✅
  - eclipse-temurin:17-jre-alpine ✅
  - node:18-alpine ✅
  - nginx:alpine ✅

- [ ] **Scan images for vulnerabilities**
  ```bash
  docker scan flowershop-backend-prod
  ```

---

## Monitoring & Logging

### 1. Logging
- [ ] **Enable application logs**
  - Log all authentication attempts
  - Log admin actions
  - Rotate logs regularly

- [ ] **Centralized logging (optional)**
  - ELK Stack
  - CloudWatch
  - Datadog

### 2. Monitoring
- [ ] **Setup health checks**
  - Database health
  - Application health
  - Disk space monitoring

- [ ] **Alerting**
  - Email alerts for errors
  - Slack notifications
  - SMS for critical issues

### 3. Backup Strategy
- [ ] **Automated daily backups**
  ```bash
  # Add to crontab
  0 2 * * * /path/to/scripts/backup.sh
  ```

- [ ] **Backup retention policy**
  - Keep daily backups for 7 days
  - Keep weekly backups for 1 month
  - Keep monthly backups for 1 year

- [ ] **Test restore procedure**
  - Test backup restoration monthly
  - Document restore steps

---

## Post-Deployment Security

### 1. Security Scanning
- [ ] **Run security scan**
  ```bash
  # OWASP Dependency Check
  mvn org.owasp:dependency-check-maven:check
  
  # npm audit
  npm audit
  ```

- [ ] **SQL Injection testing**
  - Test all input fields
  - Use sqlmap for testing

- [ ] **XSS testing**
  - Test all user inputs
  - Test blog content rendering

### 2. Penetration Testing
- [ ] **Run penetration tests**
  - Use tools like OWASP ZAP
  - Burp Suite
  - Nikto

### 3. Regular Updates
- [ ] **Keep dependencies updated**
  ```bash
  # Check for updates
  mvn versions:display-dependency-updates
  npm outdated
  ```

- [ ] **Update base images**
  ```bash
  docker pull postgres:16-alpine
  docker pull eclipse-temurin:17-jre-alpine
  ```

---

## Compliance & Legal

### 1. Privacy Policy
- [ ] **Create Privacy Policy**
  - Data collection disclosure
  - Cookie policy
  - GDPR compliance (if EU users)

### 2. Terms of Service
- [ ] **Create Terms of Service**
  - Usage terms
  - Liability disclaimer

### 3. Data Protection
- [ ] **GDPR compliance (if applicable)**
  - Right to be forgotten
  - Data export
  - Consent management

---

## Emergency Procedures

### 1. Security Incident Response Plan
- [ ] **Document response plan**
  - Contact information
  - Escalation procedures
  - Communication plan

### 2. Backup Admin Access
- [ ] **Create emergency admin account**
  - Store credentials securely
  - Test access quarterly

### 3. Rollback Procedure
- [ ] **Test rollback**
  - Test rollback script
  - Document rollback steps
  - Set RTO (Recovery Time Objective)

---

## Security Audit Checklist

Run this before every deployment:

```bash
# 1. Check for exposed secrets
git secrets --scan

# 2. Check dependencies
npm audit
mvn dependency-check:check

# 3. Check Docker images
docker scan <image>

# 4. Check SSL certificate
openssl s_client -connect yourdomain.com:443

# 5. Check ports
nmap -p- localhost

# 6. Check logs for errors
docker-compose logs --tail=100 | grep -i error
```

---

## Current Security Status

### ✅ Implemented
- Input validation with @Valid
- JPA prevents SQL injection
- Error handling
- Logging configuration
- Docker containerization
- Database migrations

### ⚠️ CRITICAL - Not Implemented Yet
- ❌ **Authentication/Authorization** (HIGHEST PRIORITY!)
- ❌ Spring Security
- ❌ JWT tokens
- ❌ Role-based access control
- ❌ Password hashing for admin
- ❌ Rate limiting
- ❌ CORS configuration
- ❌ HTTPS/SSL

### 🔄 Recommended for Production
- API rate limiting
- Request throttling
- DDoS protection
- Web Application Firewall (WAF)
- Security headers
- Automated security scanning
- Intrusion detection system

---

## Quick Security Improvements

### Priority 1: Authentication (DO THIS FIRST!)

Add to `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.1</version>
</dependency>
```

### Priority 2: CORS Configuration

Create `CorsConfig.java` (see section 3 above)

### Priority 3: Strong Passwords

Update all default passwords in `.env.production`

---

## Resources

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [Docker Security Best Practices](https://docs.docker.com/engine/security/)
- [Let's Encrypt](https://letsencrypt.org/)

---

**⚠️ IMPORTANT**: Do not deploy to production without implementing authentication!
