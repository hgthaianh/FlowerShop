# Deployment Scripts

## Available Scripts

### 🚀 deploy.sh
Automated deployment script for Flower Shop.

**Usage:**
```bash
./scripts/deploy.sh [environment]
```

**Examples:**
```bash
# Deploy to production
./scripts/deploy.sh production

# Default is production
./scripts/deploy.sh
```

**What it does:**
1. Validates environment files
2. Creates database backup
3. Pulls latest code (if git)
4. Builds backend with Maven
5. Builds frontend with npm
6. Stops running containers
7. Starts new containers with updated code
8. Performs health checks
9. Shows deployment summary

---

### 💾 backup.sh
Creates compressed backup of PostgreSQL database.

**Usage:**
```bash
./scripts/backup.sh
```

**Features:**
- Creates timestamped backup file
- Compresses with gzip
- Keeps only last 7 backups
- Works with both dev and prod databases

**Output:**
```
backup/flowershop_YYYYMMDD_HHMMSS.sql.gz
```

---

### ↩️ rollback.sh
Restores database from backup and restarts services.

**Usage:**
```bash
./scripts/rollback.sh [backup_file]
```

**Examples:**
```bash
# List available backups
ls -lh backup/

# Rollback to specific backup
./scripts/rollback.sh backup/flowershop_20251110_120000.sql.gz
```

**What it does:**
1. Stops all services
2. Creates safety backup of current state
3. Restores database from specified backup
4. Restarts all services
5. Performs health checks

---

## Prerequisites

### Required
- Docker and Docker Compose installed
- `.env.production` or `.env.prod` file configured
- Scripts have execute permission (`chmod +x scripts/*.sh`)

### For deploy.sh
- Maven installed (for backend build)
- Node.js & npm installed (for frontend build)

### For backup.sh / rollback.sh
- Database container must be running
- Gzip installed (for compression)

---

## First Time Setup

1. **Make scripts executable:**
   ```bash
   chmod +x scripts/*.sh
   ```

2. **Create production environment file:**
   ```bash
   cp .env.production .env.prod
   nano .env.prod  # Edit with your values
   ```

3. **Create backup directory:**
   ```bash
   mkdir -p backup
   ```

4. **Test deployment:**
   ```bash
   ./scripts/deploy.sh production
   ```

---

## Automated Backups

Add to crontab for daily backups:

```bash
crontab -e
```

Add these lines:
```cron
# Daily backup at 2 AM
0 2 * * * /opt/flower-shop/scripts/backup.sh

# Weekly cleanup (delete backups older than 30 days)
0 3 * * 0 find /opt/flower-shop/backup -name "*.sql.gz" -mtime +30 -delete
```

---

## Troubleshooting

### Script Permission Denied
```bash
chmod +x scripts/*.sh
```

### Environment File Not Found
```bash
# Create from template
cp .env.production .env.prod
# Then edit with your values
nano .env.prod
```

### Database Container Not Running
```bash
# Check containers
docker ps -a

# Start database
docker-compose -f docker-compose.prod.yml up -d postgres
```

### Build Failed
```bash
# Check Maven
mvn --version

# Check Node.js
node --version
npm --version

# Manual build
cd flower-shop && mvn clean package
cd ../frontend && npm run build
```

---

## Script Output

### Success Indicators
- ✓ Green checkmarks
- "Deployment Complete!" message
- All services showing as "running"
- Health checks passing

### Error Indicators
- ✗ Red errors
- "failed to start" messages
- Exit codes other than 0
- Health checks failing

---

## Additional Scripts (Optional)

### health-check.sh (create yourself)
Monitor services and restart if down:

```bash
#!/bin/bash
if ! curl -f http://localhost:8080/actuator/health &> /dev/null; then
    echo "Backend is down!" | mail -s "Alert" admin@domain.com
    docker-compose -f /path/to/docker-compose.prod.yml restart backend
fi
```

Add to crontab:
```cron
*/5 * * * * /opt/flower-shop/scripts/health-check.sh
```

---

## Quick Reference

```bash
# Deploy
./scripts/deploy.sh production

# Backup
./scripts/backup.sh

# List backups
ls -lh backup/

# Rollback
./scripts/rollback.sh backup/backup_file.sql.gz

# View deployment logs
docker-compose -f docker-compose.prod.yml logs -f

# Restart services
docker-compose -f docker-compose.prod.yml restart

# Stop services
docker-compose -f docker-compose.prod.yml down
```

---

## Safety Tips

1. **Always backup before deploy**
   ```bash
   ./scripts/backup.sh
   ./scripts/deploy.sh production
   ```

2. **Test rollback procedure**
   ```bash
   # Create test backup
   ./scripts/backup.sh
   
   # Test rollback
   ./scripts/rollback.sh backup/latest_backup.sql.gz
   ```

3. **Monitor first deployment closely**
   ```bash
   # Watch logs
   docker-compose -f docker-compose.prod.yml logs -f
   ```

4. **Keep backups for at least 30 days**

5. **Document any custom changes to scripts**

---

## More Information

- Full deployment guide: `../DEPLOYMENT_GUIDE.md`
- Security checklist: `../SECURITY_CHECKLIST.md`
- Deployment readiness: `../DEPLOYMENT_READY.md`
