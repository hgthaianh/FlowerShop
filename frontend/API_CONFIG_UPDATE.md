# API Configuration Update Summary

## ✅ Đã hoàn thành

### 1. Tạo API Config Module
- **File**: `/src/config/api.js`
- **Mục đích**: Tập trung quản lý tất cả API endpoints
- **Sử dụng**: `process.env.VUE_APP_API_BASE` từ file `.env`

### 2. Cập nhật các Components

#### HomePage.vue
- ✅ Import API config
- ✅ Sử dụng `API.products.getAll()`
- ✅ Sử dụng `API.categories.getProducts(categoryId)`

#### SiteNavbar.vue
- ✅ Import API config
- ✅ Sử dụng `API.categories.getAll()`

#### ProductDetail.vue
- ✅ Import API config
- ✅ Sử dụng `API.products.getById(id)`
- ✅ Sử dụng `API.categories.getProducts(categoryId)`

#### AdminLogin.vue
- ✅ Import API config
- ✅ Sử dụng `API.baseURL` cho axios instance

#### AdminDashboard.vue
- ✅ Import API config
- ✅ Sử dụng `API.baseURL` cho axios instance

## 📁 Environment Files

### `.env` (Active)
```env
NODE_ENV=development
VUE_APP_API_BASE=http://localhost:8080/api
```

### `.env.development`
```env
NODE_ENV=development
VUE_APP_API_BASE=http://localhost:8080/api
```

### `.env.production`
```env
NODE_ENV=production
VUE_APP_API_BASE=http://localhost:8080/api
```

## 🎯 Cách sử dụng

### Development (Local)
```bash
# Backend chạy trên port 8080
# Frontend chạy trên port 8081 (dev server)
npm run serve
```

### Production (Docker)
```bash
# Backend chạy trên port 8080
# Frontend chạy trên port 80 (nginx)
# API được proxy qua nginx
docker compose up -d --build
```

## 🔧 Cấu hình API Endpoint

### Trong Development
API URL được lấy từ `.env`:
- `VUE_APP_API_BASE=http://localhost:8080/api`

### Trong Production (Docker)
Nginx sẽ proxy `/api/*` đến backend container:
```nginx
location /api/ {
    proxy_pass http://backend:8080/api/;
}
```

Frontend có thể gọi API bằng relative path `/api/*`

## 📝 API Endpoints Available

### Products
- `GET /api/products` - Lấy tất cả sản phẩm
- `GET /api/products/:id` - Lấy chi tiết sản phẩm
- `POST /api/products` - Tạo sản phẩm
- `PUT /api/products/:id` - Cập nhật sản phẩm
- `DELETE /api/products/:id` - Xóa sản phẩm

### Categories
- `GET /api/categories` - Lấy tất cả danh mục
- `GET /api/categories/:id` - Lấy chi tiết danh mục
- `GET /api/categories/:id/products` - Lấy sản phẩm theo danh mục
- `POST /api/categories` - Tạo danh mục
- `PUT /api/categories/:id` - Cập nhật danh mục
- `DELETE /api/categories/:id` - Xóa danh mục

### Collections
- `GET /api/collections` - Lấy tất cả bộ sưu tập
- `GET /api/collections/:id/products` - Lấy sản phẩm theo bộ sưu tập

### Admin
- `POST /api/admins/login` - Đăng nhập admin

## ✨ Benefits

1. **Centralized Configuration**: Tất cả API URLs ở một chỗ
2. **Environment-based**: Dễ dàng thay đổi theo môi trường
3. **Type Safety**: Tránh lỗi typo trong URLs
4. **Maintainable**: Dễ maintain và update
5. **Flexible**: Dễ dàng thêm/sửa endpoints mới

## 🧪 Testing

```bash
# Kiểm tra env được load đúng
echo $VUE_APP_API_BASE

# Test API trong browser console
console.log(process.env.VUE_APP_API_BASE)
```

## 🐛 Troubleshooting

### API không hoạt động
1. Kiểm tra file `.env` có đúng format không
2. Restart dev server sau khi thay đổi `.env`
3. Clear browser cache
4. Kiểm tra Network tab trong DevTools

### CORS Error
1. Đảm bảo backend đã config CORS
2. Kiểm tra URL trong `.env` đúng chưa
3. Backend phải chạy trên đúng port

### Environment variable không load
1. File `.env` phải ở root của frontend
2. Variable phải bắt đầu với `VUE_APP_`
3. Phải restart dev server sau khi thay đổi
