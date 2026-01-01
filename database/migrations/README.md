# Database Migrations

## Overview
Các file migration SQL cho Flower Shop database.

## Migration Files

### 001_create_blogs_table.sql
- **Version**: 001
- **Date**: 2025-11-10
- **Description**: Tạo bảng blogs và các indexes liên quan
- **Tables**: blogs
- **Dependencies**: admins table (phải tồn tại trước)

## How to Run Migrations

### Manual Execution
```bash
# Connect to PostgreSQL
psql -h localhost -U postgres -d flowershop

# Run migration
\i database/migrations/001_create_blogs_table.sql
```

### Using Docker
```bash
# Copy migration file to container
docker cp database/migrations/001_create_blogs_table.sql flowershop-db:/tmp/

# Execute in container
docker exec -i flowershop-db psql -U postgres -d flowershop -f /tmp/001_create_blogs_table.sql
```

### During Deployment
Migration sẽ được chạy tự động khi:
1. Docker container khởi động lần đầu (nếu mount vào /docker-entrypoint-initdb.d/)
2. JPA với `spring.jpa.hibernate.ddl-auto=update` sẽ tự động tạo bảng

## Best Practices

1. **Backup trước khi migrate**
   ```bash
   pg_dump -U postgres -d flowershop > backup_before_migration.sql
   ```

2. **Test trên staging trước**
   - Chạy migration trên staging environment
   - Verify data integrity
   - Test application functionality

3. **Rollback plan**
   - Có script rollback cho mỗi migration
   - Test rollback procedure trước khi deploy

## Rollback

Để rollback migration 001:
```sql
-- Drop trigger first
DROP TRIGGER IF EXISTS trigger_blogs_updated_at ON blogs;
DROP FUNCTION IF EXISTS update_blogs_updated_at();

-- Drop indexes
DROP INDEX IF EXISTS idx_blogs_created_at;
DROP INDEX IF EXISTS idx_blogs_author_id;
DROP INDEX IF EXISTS idx_blogs_status;

-- Drop table
DROP TABLE IF EXISTS blogs CASCADE;
```

## Production Deployment Notes

### Before Deploy
- [ ] Backup database
- [ ] Review migration script
- [ ] Test on staging
- [ ] Schedule maintenance window (if needed)

### During Deploy
- [ ] Stop application (or enable maintenance mode)
- [ ] Run migration
- [ ] Verify migration success
- [ ] Deploy new application version
- [ ] Start application

### After Deploy
- [ ] Verify all features working
- [ ] Check logs for errors
- [ ] Monitor database performance
- [ ] Keep backup for 24h before cleanup
