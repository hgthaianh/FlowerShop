#!/bin/bash

# Flower Shop Database Backup Script
# Usage: ./scripts/backup.sh

set -e

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

print_header() {
    echo -e "\n${BLUE}================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}================================${NC}\n"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

print_header "Database Backup"

# Create backup directory
mkdir -p backup

# Backup filename with timestamp
BACKUP_FILE="backup/flowershop_$(date +%Y%m%d_%H%M%S).sql"

# Check if database container is running
if ! docker ps | grep -q flowershop-db-prod; then
    print_warning "Production database not running. Checking development..."
    if ! docker ps | grep -q flowershop-db; then
        print_warning "No database container running!"
        exit 1
    fi
    CONTAINER="flowershop-db"
else
    CONTAINER="flowershop-db-prod"
fi

print_warning "Creating backup from container: $CONTAINER"
docker exec $CONTAINER pg_dump -U postgres flowershop > $BACKUP_FILE

# Compress backup
print_warning "Compressing backup..."
gzip $BACKUP_FILE
BACKUP_FILE="${BACKUP_FILE}.gz"

print_success "Backup created: $BACKUP_FILE"

# Show backup size
SIZE=$(du -h $BACKUP_FILE | cut -f1)
echo "Backup size: $SIZE"

# Keep only last 7 backups
print_warning "Cleaning old backups (keeping last 7)..."
cd backup
ls -t flowershop_*.sql.gz | tail -n +8 | xargs -r rm
cd ..

print_success "Backup complete!"
echo ""
echo "To restore this backup:"
echo "  gunzip $BACKUP_FILE"
echo "  ./scripts/rollback.sh ${BACKUP_FILE%.gz}"
echo ""
