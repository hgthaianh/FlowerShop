# 🚀 Deployment Guide - Flower Shop

## 📋 Table of Contents
1. [Pre-requisites](#pre-requisites)
2. [Server Setup](#server-setup)
3. [Deployment Methods](#deployment-methods)
4. [Post-Deployment](#post-deployment)
5. [Monitoring](#monitoring)
6. [Troubleshooting](#troubleshooting)

---

## 🔧 Pre-requisites

### Local Machine
- [x] Git installed
- [x] Docker & Docker Compose installed
- [x] Maven 3.9+ (for backend build)
- [x] Node.js 18+ & npm (for frontend build)

### Server Requirements
- **OS**: Ubuntu 20.04+ / CentOS 8+ / Debian 11+
- **RAM**: Minimum 2GB, Recommended 4GB
- **Disk**: Minimum 20GB free space
- **CPU**: 2+ cores recommended
- **Network**: Public IP with ports 80, 443 accessible

### Domain Setup
- [ ] Domain name registered
- [ ] DNS A record pointing to server IP
- [ ] (Optional) CDN setup (Cloudflare, etc.)

---

## 🖥️ Server Setup

### Step 1: Connect to Server
```bash
ssh your-user@your-server-ip
```

### Step 2: Update System
```bash
sudo apt update && sudo apt upgrade -y
```

### Step 3: Install Docker
```bash
# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Add user to docker group
sudo usermod -aG docker $USER

# Install Docker Compose
sudo apt install docker-compose -y

# Verify installation
docker --version
docker-compose --version
```

### Step 4: Install Additional Tools
```bash
# Install git
sudo apt install git -y

# Install certbot for SSL (Let's Encrypt)
sudo apt install certbot python3-certbot-nginx -y

# Install nginx (if using reverse proxy)
sudo apt install nginx -y
```

### Step 5: Configure Firewall
```bash
# Enable UFW
sudo ufw allow OpenSSH
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable

# Verify firewall status
sudo ufw status
```

### Step 6: Clone Repository
```bash
# Create app directory
sudo mkdir -p /opt/flower-shop
sudo chown $USER:$USER /opt/flower-shop
cd /opt/flower-shop

# Clone repository
git clone <your-repo-url> .

# Or upload files via SCP
# scp -r /local/path/* user@server:/opt/flower-shop/
```

---

## 🚀 Deployment Methods

### Method 1: Docker Compose (Recommended)

#### Step 1: Create Production Environment File
```bash
cd /opt/flower-shop

# Copy example and edit
cp .env.production .env.prod

# Edit with your values
nano .env.prod
```

**Important variables to change:**
```bash
# Generate strong password
openssl rand -base64 32

# Update these in .env.prod:
POSTGRES_PASSWORD=<strong-password>
DB_PASSWORD=<strong-password>
VUE_APP_API_BASE=https://yourdomain.com/api
```

#### Step 2: Build and Start Services
```bash
# Using deployment script (recommended)
chmod +x scripts/deploy.sh
./scripts/deploy.sh production

# Or manual deployment
docker-compose -f docker-compose.prod.yml --env-file .env.prod up -d --build
```

#### Step 3: Verify Services
```bash
# Check running containers
docker-compose -f docker-compose.prod.yml ps

# Check logs
docker-compose -f docker-compose.prod.yml logs -f

# Test health
curl http://localhost:8080/actuator/health
curl http://localhost:80
```

---

### Method 2: Manual Deployment (Without Docker)

#### Backend Setup

```bash
# Install Java 17
sudo apt install openjdk-17-jdk -y

# Build backend
cd flower-shop
./mvnw clean package -DskipTests

# Run as service (create systemd service)
sudo nano /etc/systemd/system/flower-shop-backend.service
```

Service file content:
```ini
[Unit]
Description=Flower Shop Backend
After=postgresql.service

[Service]
Type=simple
User=flowershop
WorkingDirectory=/opt/flower-shop/flower-shop
ExecStart=/usr/bin/java -jar target/flower-shop-0.0.1-SNAPSHOT.jar
Restart=always
Environment="DB_HOST=localhost"
Environment="DB_PORT=5432"
Environment="DB_NAME=flowershop"
Environment="DB_USER=postgres"
Environment="DB_PASSWORD=your-password"

[Install]
WantedBy=multi-user.target
```

```bash
# Start service
sudo systemctl daemon-reload
sudo systemctl enable flower-shop-backend
sudo systemctl start flower-shop-backend
sudo systemctl status flower-shop-backend
```

#### Frontend Setup

```bash
# Build frontend
cd ../frontend
npm ci
npm run build

# Copy to nginx
sudo cp -r dist/* /var/www/flower-shop/

# Configure nginx
sudo nano /etc/nginx/sites-available/flower-shop
```

Nginx config:
```nginx
server {
    listen 80;
    server_name yourdomain.com www.yourdomain.com;
    
    root /var/www/flower-shop;
    index index.html;
    
    # Frontend
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    # Backend API
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

```bash
# Enable site
sudo ln -s /etc/nginx/sites-available/flower-shop /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

---

## 🔐 SSL Certificate Setup

### Using Let's Encrypt (Free)

```bash
# For Docker deployment (nginx container)
# Stop containers first
docker-compose -f docker-compose.prod.yml down

# Install certbot and get certificate
sudo certbot certonly --standalone -d yourdomain.com -d www.yourdomain.com

# Certificate will be at: /etc/letsencrypt/live/yourdomain.com/

# Update docker-compose.prod.yml to mount certificates
# Then restart containers

# For manual nginx deployment
sudo certbot --nginx -d yourdomain.com -d www.yourdomain.com

# Test auto-renewal
sudo certbot renew --dry-run

# Setup auto-renewal (cron)
sudo systemctl enable certbot.timer
```

### Update Nginx for HTTPS
```nginx
server {
    listen 443 ssl http2;
    server_name yourdomain.com www.yourdomain.com;
    
    ssl_certificate /etc/letsencrypt/live/yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/yourdomain.com/privkey.pem;
    
    # SSL configuration
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_prefer_server_ciphers on;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512;
    
    # ... rest of config
}

# Redirect HTTP to HTTPS
server {
    listen 80;
    server_name yourdomain.com www.yourdomain.com;
    return 301 https://$server_name$request_uri;
}
```

---

## 📊 Post-Deployment

### Step 1: Database Migration
```bash
# Check if blogs table exists
docker exec -it flowershop-db-prod psql -U postgres -d flowershop -c "\dt"

# If not exists, run migration
docker exec -i flowershop-db-prod psql -U postgres -d flowershop < database/migrations/001_create_blogs_table.sql

# Verify
docker exec -it flowershop-db-prod psql -U postgres -d flowershop -c "SELECT * FROM blogs;"
```

### Step 2: Create Initial Admin User
```bash
# Connect to database
docker exec -it flowershop-db-prod psql -U postgres -d flowershop

# Insert admin (if not exists)
INSERT INTO admins (username, password) 
VALUES ('admin', '$2a$10$hashed_password_here')
ON CONFLICT (username) DO NOTHING;
```

**Note**: Use BCrypt to hash password. Online tool or:
```java
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String hashed = encoder.encode("your-password");
```

### Step 3: Test All Features
- [ ] Homepage loads
- [ ] Product listing works
- [ ] Category filtering works
- [ ] Blog listing (/blogs) loads
- [ ] Blog detail page works
- [ ] Blog search works
- [ ] Admin login works
- [ ] Admin dashboard loads
- [ ] Admin can create/edit/delete products
- [ ] Admin can create/edit/delete blogs
- [ ] Admin can publish/unpublish blogs

### Step 4: Performance Testing
```bash
# Install Apache Bench
sudo apt install apache2-utils -y

# Test homepage
ab -n 1000 -c 10 https://yourdomain.com/

# Test API
ab -n 1000 -c 10 https://yourdomain.com/api/products
```

### Step 5: Setup Monitoring
```bash
# Install monitoring tools
docker run -d --name=portainer --restart=always \
  -p 9000:9000 \
  -v /var/run/docker.sock:/var/run/docker.sock \
  portainer/portainer-ce

# Access at: http://your-server:9000
```

---

## 🔄 Continuous Deployment

### Setup Git Webhook (Optional)

Create webhook script:
```bash
# /opt/flower-shop/scripts/webhook-deploy.sh
#!/bin/bash
cd /opt/flower-shop
git pull origin main
./scripts/deploy.sh production
```

Setup webhook listener:
```bash
# Install webhook
sudo apt install webhook -y

# Configure webhook
sudo nano /etc/webhook.conf
```

```json
[
  {
    "id": "flower-shop-deploy",
    "execute-command": "/opt/flower-shop/scripts/webhook-deploy.sh",
    "command-working-directory": "/opt/flower-shop",
    "response-message": "Deploying Flower Shop...",
    "trigger-rule": {
      "match": {
        "type": "value",
        "value": "refs/heads/main",
        "parameter": {
          "source": "payload",
          "name": "ref"
        }
      }
    }
  }
]
```

```bash
# Start webhook service
sudo systemctl enable webhook
sudo systemctl start webhook
```

### CI/CD with GitHub Actions (Example)

`.github/workflows/deploy.yml`:
```yaml
name: Deploy to Production

on:
  push:
    branches: [ main ]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - name: Deploy via SSH
        uses: appleboy/ssh-action@master
        with:
          host: ${{ secrets.SERVER_HOST }}
          username: ${{ secrets.SERVER_USER }}
          key: ${{ secrets.SSH_PRIVATE_KEY }}
          script: |
            cd /opt/flower-shop
            git pull origin main
            ./scripts/deploy.sh production
```

---

## 📈 Monitoring

### Setup Logging
```bash
# View logs
docker-compose -f docker-compose.prod.yml logs -f

# Specific service
docker-compose -f docker-compose.prod.yml logs -f backend

# Save logs to file
docker-compose -f docker-compose.prod.yml logs --no-color > logs.txt
```

### Setup Automated Backups
```bash
# Add to crontab
crontab -e

# Daily backup at 2 AM
0 2 * * * /opt/flower-shop/scripts/backup.sh

# Weekly cleanup
0 3 * * 0 find /opt/flower-shop/backup -name "*.sql.gz" -mtime +30 -delete
```

### Health Check Script
```bash
# Create health check script
nano /opt/flower-shop/scripts/health-check.sh
```

```bash
#!/bin/bash
# Check if services are running
if ! curl -f http://localhost:8080/actuator/health &> /dev/null; then
    echo "Backend is down!" | mail -s "Alert: Backend Down" admin@yourdomain.com
    docker-compose -f /opt/flower-shop/docker-compose.prod.yml restart backend
fi
```

```bash
chmod +x scripts/health-check.sh

# Add to crontab (every 5 minutes)
*/5 * * * * /opt/flower-shop/scripts/health-check.sh
```

---

## 🔧 Troubleshooting

### Common Issues

#### 1. Database Connection Failed
```bash
# Check if database is running
docker ps | grep postgres

# Check logs
docker logs flowershop-db-prod

# Verify credentials
docker exec -it flowershop-db-prod psql -U postgres -d flowershop
```

#### 2. Backend Not Starting
```bash
# Check logs
docker logs flowershop-backend-prod

# Common issues:
# - Database not ready (wait 10-20 seconds)
# - Port 8080 already in use
# - Invalid environment variables

# Test database connection
docker exec flowershop-backend-prod \
  java -jar app.jar --spring.jpa.hibernate.ddl-auto=validate
```

#### 3. Frontend Not Loading
```bash
# Check nginx logs
docker logs flowershop-frontend-prod

# Check if port is accessible
curl http://localhost:80

# Check API configuration
# Make sure VUE_APP_API_BASE matches your domain
```

#### 4. SSL Certificate Issues
```bash
# Check certificate
sudo certbot certificates

# Renew certificate
sudo certbot renew

# Test renewal
sudo certbot renew --dry-run
```

#### 5. Out of Disk Space
```bash
# Check disk usage
df -h

# Clean Docker
docker system prune -a --volumes

# Clean old backups
find backup/ -name "*.sql.gz" -mtime +7 -delete
```

### Debug Mode
```bash
# Enable debug logging
# Edit application-prod.properties
logging.level.vn.quahoa.flowershop=DEBUG

# Restart backend
docker-compose -f docker-compose.prod.yml restart backend
```

---

## 🔄 Update Procedure

### Update Application
```bash
cd /opt/flower-shop

# Backup first
./scripts/backup.sh

# Pull latest code
git pull origin main

# Deploy
./scripts/deploy.sh production
```

### Update Dependencies
```bash
# Update backend
cd flower-shop
./mvnw versions:display-dependency-updates

# Update frontend
cd ../frontend
npm outdated
npm update

# Rebuild
cd ..
./scripts/deploy.sh production
```

---

## ↩️ Rollback Procedure

```bash
# List available backups
ls -lh backup/

# Rollback to backup
./scripts/rollback.sh backup/backup_YYYYMMDD_HHMMSS.sql

# Rollback code (git)
git log --oneline
git checkout <commit-hash>
./scripts/deploy.sh production
```

---

## 📊 Performance Optimization

### Database Optimization
```sql
-- Create additional indexes
CREATE INDEX idx_products_name ON products(name);
CREATE INDEX idx_blogs_title ON blogs(title);

-- Analyze tables
ANALYZE products;
ANALYZE blogs;

-- Vacuum
VACUUM ANALYZE;
```

### Application Optimization
```properties
# application-prod.properties
# Connection pool
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=10

# Enable caching
spring.cache.type=caffeine
spring.cache.cache-names=products,categories,blogs
```

### Frontend Optimization
- Enable gzip compression in nginx
- Use CDN for static assets
- Implement lazy loading
- Enable browser caching

```nginx
# Gzip compression
gzip on;
gzip_types text/plain text/css application/json application/javascript text/xml application/xml;

# Browser caching
location ~* \.(jpg|jpeg|png|gif|ico|css|js)$ {
    expires 1y;
    add_header Cache-Control "public, immutable";
}
```

---

## 📝 Checklist Before Go-Live

### Security
- [ ] All passwords changed from defaults
- [ ] SSL certificate installed and working
- [ ] Firewall configured
- [ ] Admin authentication implemented
- [ ] CORS configured for production domain only
- [ ] Security headers added (CSP, HSTS, etc.)
- [ ] Rate limiting implemented

### Configuration
- [ ] Environment variables set correctly
- [ ] Database connection working
- [ ] Backup strategy in place
- [ ] Monitoring configured
- [ ] Health checks working
- [ ] Logs rotation configured

### Testing
- [ ] All features tested on production
- [ ] Load testing completed
- [ ] Security scan completed
- [ ] Mobile responsiveness tested
- [ ] Cross-browser compatibility tested

### Documentation
- [ ] Deployment documented
- [ ] Credentials stored securely
- [ ] Emergency contacts documented
- [ ] Rollback procedure tested

---

## 🆘 Emergency Contacts

**Server Admin**: your-email@domain.com  
**Database Admin**: db-admin@domain.com  
**DNS Provider**: provider-support@domain.com  
**Hosting Provider**: hosting-support@domain.com

---

## 📚 Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [Spring Boot Deployment Guide](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html)
- [Vue.js Deployment Guide](https://vuejs.org/guide/best-practices/production-deployment.html)
- [Nginx Documentation](https://nginx.org/en/docs/)
- [Let's Encrypt](https://letsencrypt.org/getting-started/)

---

## 🎉 Post-Deployment Success

Your Flower Shop is now live! 🚀

**Access URLs:**
- Frontend: https://yourdomain.com
- Backend API: https://yourdomain.com/api
- Admin Panel: https://yourdomain.com/admin/dashboard

**Next Steps:**
1. Monitor logs for first 24 hours
2. Test all features thoroughly
3. Create marketing materials
4. Announce launch on social media
5. Setup analytics (Google Analytics, etc.)

**Remember:**
- Keep backups for at least 30 days
- Monitor server resources
- Update dependencies regularly
- Review logs weekly
- Test rollback quarterly

Good luck! 🌸
