#!/bin/bash

# Flower Shop Docker Management Script

case "$1" in
  start)
    echo "🌸 Starting Flower Shop..."
    docker compose up -d --build
    echo "✅ Done! Access at http://localhost"
    ;;
  
  stop)
    echo "🛑 Stopping Flower Shop..."
    docker compose down
    echo "✅ Stopped!"
    ;;
  
  restart)
    echo "🔄 Restarting Flower Shop..."
    docker compose restart
    echo "✅ Restarted!"
    ;;
  
  logs)
    docker compose logs -f
    ;;
  
  clean)
    echo "🧹 Cleaning up (this will delete database)..."
    docker compose down -v
    echo "✅ Cleaned!"
    ;;
  
  status)
    docker compose ps
    ;;
  
  rebuild)
    echo "🔨 Rebuilding all services..."
    docker compose down
    docker compose up -d --build --force-recreate
    echo "✅ Rebuilt!"
    ;;
  
  db)
    echo "📊 Accessing PostgreSQL..."
    docker compose exec postgres psql -U postgres -d flowershop
    ;;
  
  *)
    echo "🌸 Flower Shop Management"
    echo ""
    echo "Usage: ./manage.sh [command]"
    echo ""
    echo "Commands:"
    echo "  start     - Start all services"
    echo "  stop      - Stop all services"
    echo "  restart   - Restart all services"
    echo "  logs      - View logs (follow mode)"
    echo "  clean     - Stop and remove all data"
    echo "  status    - Show service status"
    echo "  rebuild   - Rebuild all services"
    echo "  db        - Access PostgreSQL CLI"
    echo ""
    ;;
esac
