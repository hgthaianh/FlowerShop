# 📸 Backend API - Quản lý Hình ảnh Sản phẩm

## ✅ Tổng quan

Backend đã được cập nhật đầy đủ với **CRUD** cho hình ảnh:
- ✅ **CREATE** - Upload ảnh từ URL hoặc File
- ✅ **READ** - Lấy thông tin ảnh (qua ProductResponse)
- ✅ **UPDATE** - Cập nhật/Thay thế ảnh
- ✅ **DELETE** - Xóa ảnh (main hoặc additional)

---

## 📋 API Endpoints

### 1. UPLOAD - Thêm ảnh mới

#### 1.1. Upload ảnh đại diện từ FILE
```http
POST /api/products/{id}/images/main
Content-Type: multipart/form-data

Body:
- file: MultipartFile (image file)

Response: 200 OK
"http://localhost:8081/uploads/products/{productId}/main/image.jpg"
```

#### 1.2. Upload ảnh đại diện từ URL
```http
POST /api/products/{id}/images/main-url
Content-Type: application/json

Body:
{
  "imageUrl": "https://example.com/image.jpg"
}

Response: 200 OK
"http://localhost:8081/uploads/products/{productId}/main/image.jpg"
```

#### 1.3. Upload ảnh bổ sung từ FILE
```http
POST /api/products/{id}/images
Content-Type: multipart/form-data

Body:
- file: MultipartFile (image file)

Response: 200 OK
"http://localhost:8081/uploads/products/{productId}/12345.jpg"
```

#### 1.4. Upload ảnh bổ sung từ URL
```http
POST /api/products/{id}/images/url
Content-Type: application/json

Body:
{
  "imageUrl": "https://example.com/image.jpg"
}

Response: 200 OK
"http://localhost:8081/uploads/products/{productId}/12345.jpg"
```

---

### 2. UPDATE - Cập nhật ảnh

#### 2.1. Cập nhật ảnh đại diện từ FILE
```http
PUT /api/products/{id}/images/main
Content-Type: multipart/form-data

Body:
- file: MultipartFile (new image file)

Response: 200 OK
"http://localhost:8081/uploads/products/{productId}/main/new-image.jpg"

Logic: Xóa ảnh cũ → Upload ảnh mới
```

#### 2.2. Cập nhật ảnh đại diện từ URL
```http
PUT /api/products/{id}/images/main-url
Content-Type: application/json

Body:
{
  "imageUrl": "https://example.com/new-image.jpg"
}

Response: 200 OK
"http://localhost:8081/uploads/products/{productId}/main/new-image.jpg"

Logic: Xóa ảnh cũ → Download và save ảnh mới
```

---

### 3. DELETE - Xóa ảnh

#### 3.1. Xóa ảnh đại diện
```http
DELETE /api/products/{id}/images/main

Response: 204 No Content

Logic: 
- Xóa file vật lý từ /uploads/
- Set imageUrl = null trong database
```

#### 3.2. Xóa 1 ảnh bổ sung cụ thể
```http
DELETE /api/products/{id}/images/{imageId}

Response: 204 No Content

Logic:
- Verify ảnh thuộc về product này
- Xóa file vật lý từ /uploads/
- Xóa record trong product_images table
```

#### 3.3. Xóa TẤT CẢ ảnh bổ sung
```http
DELETE /api/products/{id}/images

Response: 204 No Content

Logic:
- Loop qua tất cả ảnh của product
- Xóa từng file vật lý
- Xóa tất cả records trong product_images table
```

---

## 🗄️ Database Schema

### Table: `products`
```sql
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    product_code VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    image_url TEXT,              -- ⬅️ Ảnh đại diện
    category_id BIGINT NOT NULL,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);
```

### Table: `product_images`
```sql
CREATE TABLE product_images (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    image_url TEXT NOT NULL,      -- ⬅️ URL đầy đủ
    file_name VARCHAR(255),        -- ⬅️ Tên file gốc
    file_path TEXT,                -- ⬅️ Path vật lý
    display_order INTEGER,         -- ⬅️ Thứ tự hiển thị
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);
```

---

## 📁 File Storage Structure

```
/uploads/
└── products/
    ├── {productId}/
    │   ├── main/
    │   │   └── image.jpg         ⬅️ Ảnh đại diện
    │   ├── 12345.jpg             ⬅️ Ảnh bổ sung 1
    │   ├── 12346.jpg             ⬅️ Ảnh bổ sung 2
    │   └── 12347.jpg             ⬅️ Ảnh bổ sung 3
    └── {anotherProductId}/
        └── ...
```

---

## 🔧 Backend Components

### 1. ProductController
```java
@RestController
@RequestMapping("/api")
public class ProductController {
    
    // UPLOAD
    @PostMapping("/products/{id}/images/main")
    @PostMapping("/products/{id}/images/main-url")
    @PostMapping("/products/{id}/images")
    @PostMapping("/products/{id}/images/url")
    
    // UPDATE
    @PutMapping("/products/{id}/images/main")
    @PutMapping("/products/{id}/images/main-url")
    
    // DELETE
    @DeleteMapping("/products/{id}/images/main")
    @DeleteMapping("/products/{id}/images/{imageId}")
    @DeleteMapping("/products/{id}/images")
}
```

### 2. ProductService
```java
@Service
public class ProductService {
    
    // UPLOAD methods
    String uploadMainProductImage(Long productId, MultipartFile file)
    String uploadMainProductImageFromUrl(Long productId, String imageUrl)
    String uploadProductImage(Long productId, MultipartFile file)
    String uploadProductImageFromUrl(Long productId, String imageUrl)
    
    // UPDATE methods
    String updateMainImage(Long productId, MultipartFile file)
    String updateMainImageFromUrl(Long productId, String imageUrl)
    
    // DELETE methods
    void deleteMainImage(Long productId)
    void deleteProductImage(Long productId, Long imageId)
    void deleteAllProductImages(Long productId)
}
```

### 3. ImageStorageService
```java
@Service
public class ImageStorageService {
    
    String saveImageFromFile(MultipartFile file, Long productId, boolean isMain)
    String saveImageFromUrl(String imageUrl, Long productId, boolean isMain)
    void deleteImage(String imageUrl)
}
```

### 4. Repositories
```java
ProductRepository extends JpaRepository<Product, Long>
ProductImageRepository extends JpaRepository<ProductImage, Long>
CategoryRepository extends JpaRepository<Category, Long>
```

---

## 🎯 Logic Flow

### Upload Main Image from FILE
```
1. Client gửi file → POST /api/products/{id}/images/main
2. ProductController nhận MultipartFile
3. ProductService.uploadMainProductImage()
4. ImageStorageService.saveImageFromFile(file, productId, true)
   - Save file → /uploads/products/{productId}/main/
   - Return URL: http://localhost:8081/uploads/...
5. Update product.imageUrl = URL
6. Save product to database
7. Return URL to client
```

### Upload Main Image from URL
```
1. Client gửi URL → POST /api/products/{id}/images/main-url
2. ProductController nhận ImageUrlRequest
3. ProductService.uploadMainProductImageFromUrl()
4. ImageStorageService.saveImageFromUrl(url, productId, true)
   - Download image từ URL
   - Save file → /uploads/products/{productId}/main/
   - Return local URL
5. Update product.imageUrl = local URL
6. Save product to database
7. Return local URL to client
```

### Upload Additional Image from FILE
```
1. Client gửi file → POST /api/products/{id}/images
2. ProductController nhận MultipartFile
3. ProductService.uploadProductImage()
4. ImageStorageService.saveImageFromFile(file, productId, false)
   - Save file → /uploads/products/{productId}/
   - Return URL
5. Create new ProductImage entity
   - imageUrl = URL
   - fileName = original filename
   - filePath = URL
   - product = current product
6. Add to product.images
7. Save product to database
8. Return URL to client
```

### Delete Main Image
```
1. Client gửi → DELETE /api/products/{id}/images/main
2. ProductService.deleteMainImage()
3. Get product by ID
4. If imageUrl exists:
   - ImageStorageService.deleteImage(imageUrl)
     → Delete physical file
   - Set product.imageUrl = null
   - Save product
5. Return 204 No Content
```

### Delete Additional Image
```
1. Client gửi → DELETE /api/products/{id}/images/{imageId}
2. ProductService.deleteProductImage()
3. Get product by ID
4. Get ProductImage by imageId
5. Verify image belongs to product
6. ImageStorageService.deleteImage(imageUrl)
   → Delete physical file
7. Remove from product.images list
8. Delete ProductImage entity
9. Return 204 No Content
```

---

## ✅ Features Implemented

- ✅ Upload ảnh từ 2 nguồn: URL + File
- ✅ Lưu file vật lý vào /uploads/products/
- ✅ Lưu metadata vào database (imageUrl, fileName, filePath)
- ✅ CRUD đầy đủ: Create, Read, Update, Delete
- ✅ Verify ownership (ảnh phải thuộc về product)
- ✅ Auto delete file khi xóa record
- ✅ Hỗ trợ cả main image và additional images
- ✅ Static file serving qua Spring Boot
- ✅ Error handling cơ bản

---

## 🧪 Test với cURL

```bash
# 1. Upload main image từ file
curl -X POST http://localhost:8081/api/products/1/images/main \
  -F "file=@/path/to/image.jpg"

# 2. Upload main image từ URL
curl -X POST http://localhost:8081/api/products/1/images/main-url \
  -H "Content-Type: application/json" \
  -d '{"imageUrl":"https://example.com/image.jpg"}'

# 3. Upload additional image từ file
curl -X POST http://localhost:8081/api/products/1/images \
  -F "file=@/path/to/image.jpg"

# 4. Upload additional image từ URL
curl -X POST http://localhost:8081/api/products/1/images/url \
  -H "Content-Type: application/json" \
  -d '{"imageUrl":"https://example.com/image.jpg"}'

# 5. Delete main image
curl -X DELETE http://localhost:8081/api/products/1/images/main

# 6. Delete specific additional image
curl -X DELETE http://localhost:8081/api/products/1/images/5

# 7. Update main image
curl -X PUT http://localhost:8081/api/products/1/images/main \
  -F "file=@/path/to/new-image.jpg"
```

---

**Status**: ✅ **BACKEND READY**  
**Build**: SUCCESS  
**Containers**: RUNNING  
**Date**: 11/11/2025
