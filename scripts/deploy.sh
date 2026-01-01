#!/bin/bash

# Flower Shop Deployment Script
# Usage: ./scripts/deploy.sh [environment]
# Example: ./scripts/deploy.sh production

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Functions
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

# Check if environment parameter is provided
ENV=${1:-production}

print_header "Flower Shop Deployment - $ENV Environment"

# Step 1: Pre-deployment checks
print_header "Step 1: Pre-deployment Checks"

if [ ! -f ".env.$ENV" ]; then
    print_error ".env.$ENV file not found!"
    echo "Please create .env.$ENV from .env.production.example"
    exit 1
fi
print_success ".env.$ENV file found"

if ! command -v docker &> /dev/null; then
    print_error "Docker is not installed!"
    exit 1
fi
print_success "Docker is installed"

if ! command -v docker-compose &> /dev/null; then
    print_error "Docker Compose is not installed!"
    exit 1
fi
print_success "Docker Compose is installed"

# Step 2: Backup current database
print_header "Step 2: Database Backup"

if docker ps | grep -q flowershop-db-prod; then
    BACKUP_FILE="backup/backup_$(date +%Y%m%d_%H%M%S).sql"
    mkdir -p backup
    print_warning "Creating database backup: $BACKUP_FILE"
    docker exec flowershop-db-prod pg_dump -U postgres flowershop > $BACKUP_FILE
    print_success "Database backup created successfully"
else
    print_warning "Database container not running. Skipping backup."
fi

# Step 3: Pull latest code (if using git)
print_header "Step 3: Code Update"
if [ -d ".git" ]; then
    print_warning "Pulling latest code from git..."
    git pull origin main || print_warning "Git pull skipped (not a git repo or no remote)"
    print_success "Code updated"
else
    print_warning "Not a git repository. Skipping git pull."
fi

# Step 4: Build Backend
print_header "Step 4: Building Backend"
cd flower-shop
print_warning "Running Maven build..."
./mvnw clean package -DskipTests
print_success "Backend build completed"
cd ..

# Step 5: Build Frontend
print_header "Step 5: Building Frontend"
cd frontend
print_warning "Installing dependencies..."
npm ci
print_warning "Building production bundle..."
npm run build
print_success "Frontend build completed"
cd ..

# Step 6: Stop running containers
print_header "Step 6: Stopping Current Services"
if [ -f "docker-compose.prod.yml" ]; then
    docker-compose -f docker-compose.prod.yml --env-file .env.$ENV down || true
    print_success "Services stopped"
else
    print_warning "docker-compose.prod.yml not found. Using default docker-compose.yml"
    docker-compose --env-file .env.$ENV down || true
fi

# Step 7: Start new containers
print_header "Step 7: Starting Services"
if [ -f "docker-compose.prod.yml" ]; then
    docker-compose -f docker-compose.prod.yml --env-file .env.$ENV up -d --build
else
    docker-compose --env-file .env.$ENV up -d --build
fi
print_success "Services started"

# Step 8: Wait for services to be healthy
print_header "Step 8: Health Check"
print_warning "Waiting for services to be healthy..."
sleep 10

# Check database
if docker ps | grep -q flowershop-db-prod; then
    print_success "Database is running"
else
    print_error "Database failed to start!"
    exit 1
fi

# Check backend
if docker ps | grep -q flowershop-backend-prod; then
    print_success "Backend is running"
    
    # Wait for backend to be ready
    echo "Waiting for backend to be ready (max 60s)..."
    for i in {1..12}; do
        if curl -f http://localhost:8080/actuator/health &> /dev/null; then
            print_success "Backend is healthy"
            break
        fi
        echo "Attempt $i/12..."
        sleep 5
    done
else
    print_error "Backend failed to start!"
    exit 1
fi

# Check frontend
if docker ps | grep -q flowershop-frontend-prod; then
    print_success "Frontend is running"
else
    print_error "Frontend failed to start!"
    exit 1
fi

# Step 9: Show running containers
print_header "Step 9: Deployment Summary"
docker-compose -f docker-compose.prod.yml ps

# Step 10: Show logs
print_header "Recent Logs"
docker-compose -f docker-compose.prod.yml logs --tail=20

# Final message
print_header "Deployment Complete!"
echo -e "${GREEN}✓ Application deployed successfully!${NC}"
echo -e "\nAccess your application at:"
echo -e "  Frontend: ${BLUE}http://localhost${NC}"
echo -e "  Backend:  ${BLUE}http://localhost:8080${NC}"
echo -e "\nTo view logs:"
echo -e "  ${YELLOW}docker-compose -f docker-compose.prod.yml logs -f${NC}"
echo -e "\nTo stop services:"
echo -e "  ${YELLOW}docker-compose -f docker-compose.prod.yml down${NC}"
echo ""
