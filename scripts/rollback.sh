#!/bin/bash

# Flower Shop Rollback Script
# Usage: ./scripts/rollback.sh [backup_file]
# Example: ./scripts/rollback.sh backup/backup_20251110_120000.sql

set -e

# Colors
RED='\033[0;31m'
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

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_header "Flower Shop Rollback Procedure"

# Check if backup file is provided
if [ -z "$1" ]; then
    print_error "Please provide backup file!"
    echo "Usage: ./scripts/rollback.sh [backup_file]"
    echo ""
    echo "Available backups:"
    ls -lh backup/*.sql 2>/dev/null || echo "No backups found"
    exit 1
fi

BACKUP_FILE=$1

if [ ! -f "$BACKUP_FILE" ]; then
    print_error "Backup file not found: $BACKUP_FILE"
    exit 1
fi

print_warning "This will restore database from: $BACKUP_FILE"
echo -n "Are you sure? (yes/no): "
read -r CONFIRM

if [ "$CONFIRM" != "yes" ]; then
    print_warning "Rollback cancelled"
    exit 0
fi

# Step 1: Stop services
print_header "Step 1: Stopping Services"
docker-compose -f docker-compose.prod.yml down
print_success "Services stopped"

# Step 2: Backup current state
print_header "Step 2: Creating Safety Backup"
SAFETY_BACKUP="backup/safety_backup_$(date +%Y%m%d_%H%M%S).sql"
mkdir -p backup
docker-compose -f docker-compose.prod.yml up -d postgres
sleep 5
docker exec flowershop-db-prod pg_dump -U postgres flowershop > $SAFETY_BACKUP
print_success "Safety backup created: $SAFETY_BACKUP"

# Step 3: Restore database
print_header "Step 3: Restoring Database"
docker exec -i flowershop-db-prod dropdb -U postgres flowershop --if-exists
docker exec -i flowershop-db-prod createdb -U postgres flowershop
docker exec -i flowershop-db-prod psql -U postgres flowershop < $BACKUP_FILE
print_success "Database restored from backup"

# Step 4: Restart all services
print_header "Step 4: Restarting All Services"
docker-compose -f docker-compose.prod.yml down
docker-compose -f docker-compose.prod.yml up -d
print_success "Services restarted"

# Step 5: Health check
print_header "Step 5: Health Check"
sleep 10

if docker ps | grep -q flowershop-db-prod; then
    print_success "Database is running"
fi

if docker ps | grep -q flowershop-backend-prod; then
    print_success "Backend is running"
fi

if docker ps | grep -q flowershop-frontend-prod; then
    print_success "Frontend is running"
fi

print_header "Rollback Complete!"
echo -e "${GREEN}✓ System restored from backup${NC}"
echo -e "\nSafety backup (before rollback): ${YELLOW}$SAFETY_BACKUP${NC}"
echo ""
