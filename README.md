# 🌸 Flower Shop - Hệ thống Quản lý Cửa hàng Hoa

## 📋 Mô tả

Ứng dụng quản lý cửa hàng hoa với giao diện màu hồng pastel, bao gồm:

- **Backend**: Spring Boot + PostgreSQL
- **Frontend**: Vue.js 3
- **Database**: PostgreSQL

## 🚀 Cài đặt và Chạy

### Yêu cầu

- Docker Desktop
- Docker Compose

### Chạy toàn bộ hệ thống

```bash
# Clone repository và di chuyển vào thư mục
cd /home/hoangsonsdk/flower_shop

# Build và chạy tất cả services
docker-compose up --build

# Hoặc chạy ở background
docker-compose up -d --build
```

### Truy cập ứng dụng

- **Frontend (Trang chủ)**: http://localhost
- **Backend API**: http://localhost:8080
- **Admin Dashboard**: http://localhost/admin/login

### Dừng hệ thống

```bash
# Dừng tất cả containers
docker-compose down

# Dừng và xóa volumes (xóa database)
docker-compose down -v
```

## 🛠️ Phát triển Local

### Backend (Spring Boot)

```bash
cd flower-shop

# Chạy PostgreSQL riêng
docker run -d \
  --name postgres-dev \
  -e POSTGRES_DB=flowershop \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:16-alpine

# Chạy backend
mvn spring-boot:run
```

### Frontend (Vue.js)

```bash
cd frontend

# Install dependencies
npm install

# Run dev server
npm run serve
```

Backend sẽ chạy trên: http://localhost:8080
Frontend sẽ chạy trên: http://localhost:84

## 📦 Cấu trúc Project

```
flower_shop/
├── docker-compose.yml          # Docker Compose configuration
├── flower-shop/               # Spring Boot Backend
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
└── frontend/                  # Vue.js Frontend
    ├── Dockerfile
    ├── nginx.conf
    ├── package.json
    └── src/
```

## 🔧 Biến môi trường

### Backend Environment Variables

| Variable    | Default    | Description         |
| ----------- | ---------- | ------------------- |
| DB_HOST     | localhost  | PostgreSQL host     |
| DB_PORT     | 5432       | PostgreSQL port     |
| DB_NAME     | flowershop | Database name       |
| DB_USER     | postgres   | Database username   |
| DB_PASSWORD | postgres   | Database password   |
| SERVER_PORT | 8080       | Backend server port |

### Frontend Build Args

Frontend được build với `VUE_APP_API_BASE=http://localhost:8080/api`

## 📝 API Endpoints

### Products

- `GET /api/products` - Lấy tất cả sản phẩm
- `GET /api/products/{id}` - Lấy chi tiết sản phẩm
- `POST /api/products` - Tạo sản phẩm mới
- `PUT /api/products/{id}` - Cập nhật sản phẩm
- `DELETE /api/products/{id}` - Xóa sản phẩm

### Categories

- `GET /api/categories` - Lấy tất cả danh mục
- `GET /api/categories/{id}/products` - Lấy sản phẩm theo danh mục
- `POST /api/categories` - Tạo danh mục mới
- `PUT /api/categories/{id}` - Cập nhật danh mục
- `DELETE /api/categories/{id}` - Xóa danh mục

### Admin

- `POST /api/admins/login` - Đăng nhập admin

## 🗄️ Database Schema

### Products Table

- `id` (BIGINT, PK)
- `product_code` (VARCHAR, UNIQUE)
- `name` (VARCHAR)
- `description` (TEXT)
- `price` (DOUBLE)
- `image_url` (VARCHAR)
- `category_id` (BIGINT, FK)

### Categories Table

- `id` (BIGINT, PK)
- `name` (VARCHAR)

### Product Collections Table

- `id` (BIGINT, PK)
- `name` (VARCHAR)

## 🎨 Theme

Ứng dụng sử dụng bảng màu hồng pastel:

- Primary: `#F36DA1`
- Light: `#FFE1F0`
- Dark: `#D63675`

## 🔐 Default Admin Credentials

- Username: `admin`
- Password: `admin123`

## 📸 Screenshots

[Thêm screenshots sau]

## 🐛 Troubleshooting

### Port đã được sử dụng

```bash
# Thay đổi port trong docker-compose.yml
# Ví dụ: "8081:8080" thay vì "8080:8080"
```

### Database connection error

```bash
# Kiểm tra PostgreSQL đang chạy
docker-compose ps

# Xem logs
docker-compose logs postgres
```

### Frontend không kết nối được backend

```bash
# Kiểm tra backend logs
docker-compose logs backend

# Restart services
docker-compose restart
```

## 📄 License

MIT License

## 👥 Contributors

- [Your Name]
