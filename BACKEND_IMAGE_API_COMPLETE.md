# 📸 API Endpoints - Quản lý Hình ảnh Sản phẩm

## 📋 Tổng quan

Backend đã implement đầy đủ **CRUD** cho cả **Main Image** (ảnh đại diện) và **Additional Images** (ảnh bổ sung).

---

## 🖼 MAIN IMAGE (Ảnh đại diện)

### ➕ CREATE/UPLOAD Main Image

#### 1. Upload từ File
```http
POST /api/products/{productId}/images/main
Content-Type: multipart/form-data

Params:
- file: MultipartFile (image file)

Response: 200 OK
Body: "http://localhost:8081/uploads/products/{productId}/main/filename.jpg"
```

**Example:**
```bash
curl -X POST \
  -F "file=@image.jpg" \
  http://localhost:8081/api/products/1/images/main
```

#### 2. Upload từ URL
```http
POST /api/products/{productId}/images/main-url
Content-Type: application/json

Body:
{
  "imageUrl": "https://example.com/image.jpg"
}

Response: 200 OK
Body: "http://localhost:8081/uploads/products/{productId}/main/downloaded_image.jpg"
```

**Example:**
```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"imageUrl":"https://example.com/image.jpg"}' \
  http://localhost:8081/api/products/1/images/main-url
```

---

### 🔄 UPDATE Main Image

#### 1. Update từ File (thay thế ảnh cũ)
```http
PUT /api/products/{productId}/images/main
Content-Type: multipart/form-data

Params:
- file: MultipartFile (new image file)

Response: 200 OK
Body: "http://localhost:8081/uploads/products/{productId}/main/new_filename.jpg"

Note: Ảnh cũ sẽ bị XÓA khỏi server
```

**Example:**
```bash
curl -X PUT \
  -F "file=@new_image.jpg" \
  http://localhost:8081/api/products/1/images/main
```

#### 2. Update từ URL (thay thế ảnh cũ)
```http
PUT /api/products/{productId}/images/main-url
Content-Type: application/json

Body:
{
  "imageUrl": "https://example.com/new_image.jpg"
}

Response: 200 OK
Body: "http://localhost:8081/uploads/products/{productId}/main/new_downloaded.jpg"

Note: Ảnh cũ sẽ bị XÓA khỏi server
```

**Example:**
```bash
curl -X PUT \
  -H "Content-Type: application/json" \
  -d '{"imageUrl":"https://example.com/new_image.jpg"}' \
  http://localhost:8081/api/products/1/images/main-url
```

---

### ❌ DELETE Main Image

```http
DELETE /api/products/{productId}/images/main

Response: 204 No Content

Note: 
- Xóa file vật lý khỏi server
- Set product.imageUrl = null trong database
```

**Example:**
```bash
curl -X DELETE http://localhost:8081/api/products/1/images/main
```

---

## 📸 ADDITIONAL IMAGES (Ảnh bổ sung)

### ➕ CREATE/UPLOAD Additional Image

#### 1. Upload từ File
```http
POST /api/products/{productId}/images
Content-Type: multipart/form-data

Params:
- file: MultipartFile (image file)

Response: 200 OK
Body: "http://localhost:8081/uploads/products/{productId}/image_{timestamp}.jpg"

Note: Tạo ProductImage entity mới trong database
```

**Example:**
```bash
curl -X POST \
  -F "file=@additional_image.jpg" \
  http://localhost:8081/api/products/1/images
```

#### 2. Upload từ URL
```http
POST /api/products/{productId}/images/url
Content-Type: application/json

Body:
{
  "imageUrl": "https://example.com/additional_image.jpg"
}

Response: 200 OK
Body: "http://localhost:8081/uploads/products/{productId}/downloaded_{timestamp}.jpg"

Note: Tạo ProductImage entity mới trong database
```

**Example:**
```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"imageUrl":"https://example.com/additional.jpg"}' \
  http://localhost:8081/api/products/1/images/url
```

---

### ❌ DELETE Additional Images

#### 1. Xóa 1 ảnh cụ thể
```http
DELETE /api/products/{productId}/images/{imageId}

Response: 204 No Content

Note: 
- Xóa file vật lý khỏi server
- Xóa ProductImage entity khỏi database
- Kiểm tra imageId có thuộc productId không
```

**Example:**
```bash
curl -X DELETE http://localhost:8081/api/products/1/images/5
```

#### 2. Xóa TẤT CẢ ảnh bổ sung
```http
DELETE /api/products/{productId}/images

Response: 204 No Content

Note: 
- Xóa tất cả files vật lý khỏi server
- Xóa tất cả ProductImage entities của product này
- Chỉ xóa additional images, KHÔNG xóa main image
```

**Example:**
```bash
curl -X DELETE http://localhost:8081/api/products/1/images
```

---

## 📊 Bảng tổng hợp CRUD

### Main Image (Ảnh đại diện)

| Action | Method | Endpoint | Body/Params | Notes |
|--------|--------|----------|-------------|-------|
| **Upload File** | POST | `/products/{id}/images/main` | `file` (multipart) | Tạo mới hoặc ghi đè |
| **Upload URL** | POST | `/products/{id}/images/main-url` | `{"imageUrl":"..."}` | Download từ URL |
| **Update File** | PUT | `/products/{id}/images/main` | `file` (multipart) | Xóa cũ, upload mới |
| **Update URL** | PUT | `/products/{id}/images/main-url` | `{"imageUrl":"..."}` | Xóa cũ, download mới |
| **Delete** | DELETE | `/products/{id}/images/main` | - | Xóa khỏi server & DB |

### Additional Images (Ảnh bổ sung)

| Action | Method | Endpoint | Body/Params | Notes |
|--------|--------|----------|-------------|-------|
| **Upload File** | POST | `/products/{id}/images` | `file` (multipart) | Thêm ảnh mới |
| **Upload URL** | POST | `/products/{id}/images/url` | `{"imageUrl":"..."}` | Download & thêm mới |
| **Delete 1 ảnh** | DELETE | `/products/{id}/images/{imageId}` | - | Xóa ảnh cụ thể |
| **Delete ALL** | DELETE | `/products/{id}/images` | - | Xóa tất cả ảnh bổ sung |
| **Update** | - | ❌ Not implemented | - | Dùng DELETE + POST |

> **Lưu ý:** Để update 1 additional image, frontend nên:
> 1. DELETE ảnh cũ: `DELETE /products/{id}/images/{oldImageId}`
> 2. UPLOAD ảnh mới: `POST /products/{id}/images` (file) hoặc `POST /products/{id}/images/url` (URL)

---

## 🔍 Chi tiết Implementation

### ProductService Methods

```java
// MAIN IMAGE - Create/Upload
public String uploadMainProductImage(Long productId, MultipartFile file)
public String uploadMainProductImageFromUrl(Long productId, String imageUrl)

// MAIN IMAGE - Update (Replace)
public String updateMainImage(Long productId, MultipartFile file)
public String updateMainImageFromUrl(Long productId, String imageUrl)

// MAIN IMAGE - Delete
public void deleteMainImage(Long productId)

// ADDITIONAL IMAGES - Create/Upload
public String uploadProductImage(Long productId, MultipartFile file)
public String uploadProductImageFromUrl(Long productId, String imageUrl)

// ADDITIONAL IMAGES - Delete
public void deleteProductImage(Long productId, Long imageId)
public void deleteAllProductImages(Long productId)
```

---

## 🗂 Cấu trúc Database

### Table: `products`
```sql
- id BIGINT (PK)
- image_url TEXT (nullable) -- Main image URL
- name VARCHAR(255)
- price DECIMAL
- ...
```

### Table: `product_images`
```sql
- id BIGINT (PK)
- product_id BIGINT (FK -> products.id)
- image_url TEXT -- Additional image URL
- file_name VARCHAR(255) (nullable)
- file_path TEXT (nullable)
- display_order INTEGER (nullable)
```

---

## 📁 File Storage Structure

```
uploads/
└── products/
    ├── 1/                      # Product ID
    │   ├── main/               # Main image folder
    │   │   └── image.jpg
    │   └── image_1699999.jpg   # Additional images
    ├── 2/
    │   ├── main/
    │   │   └── product2.jpg
    │   ├── add1.jpg
    │   └── add2.jpg
```

---

## ✅ Validation

1. **File Type**: Phải là image (MIME type check)
2. **File Size**: Max 5MB (có thể config)
3. **Product Exists**: productId phải tồn tại
4. **Image Ownership**: imageId phải thuộc productId khi delete
5. **URL Format**: URL phải valid khi upload từ URL

---

## 🧪 Test Scenarios

### Scenario 1: Tạo sản phẩm với ảnh
```bash
# 1. Tạo product (không có ảnh)
POST /api/products
{"name":"Product 1","price":100,"categoryId":1}
→ Response: {"id":1,...}

# 2. Upload main image từ file
POST /api/products/1/images/main
file=@main.jpg
→ Response: "http://localhost:8081/uploads/products/1/main/main.jpg"

# 3. Upload additional images từ file
POST /api/products/1/images
file=@add1.jpg
→ Response: "http://localhost:8081/uploads/products/1/add1_123.jpg"

POST /api/products/1/images
file=@add2.jpg
→ Response: "http://localhost:8081/uploads/products/1/add2_124.jpg"

# 4. Upload additional image từ URL
POST /api/products/1/images/url
{"imageUrl":"https://example.com/image.jpg"}
→ Response: "http://localhost:8081/uploads/products/1/downloaded_125.jpg"
```

### Scenario 2: Sửa ảnh
```bash
# 1. Update main image
PUT /api/products/1/images/main
file=@new_main.jpg
→ Ảnh cũ bị xóa, ảnh mới được upload

# 2. Xóa 1 additional image
DELETE /api/products/1/images/5

# 3. Upload ảnh mới thay thế
POST /api/products/1/images
file=@replacement.jpg
```

### Scenario 3: Xóa ảnh
```bash
# Xóa main image
DELETE /api/products/1/images/main

# Xóa 1 additional image
DELETE /api/products/1/images/5

# Xóa tất cả additional images
DELETE /api/products/1/images
```

---

## 🚨 Error Handling

```java
// Product not found
404 Not Found
{"message":"Product not found with id: 1"}

// Image not found
404 Not Found
{"message":"ProductImage not found with id: 5"}

// Image ownership validation
400 Bad Request
{"message":"Image does not belong to this product"}

// File upload error
500 Internal Server Error
{"message":"Failed to upload image"}

// Invalid URL
400 Bad Request
{"message":"Failed to download image from URL"}
```

---

## 📝 Lưu ý khi sử dụng

1. **Main Image**: Mỗi product chỉ có 1 main image
   - Upload mới → Ghi đè lên ảnh cũ
   - Update → Xóa cũ + Upload mới
   - Delete → Set null

2. **Additional Images**: Mỗi product có thể có nhiều ảnh
   - Upload mới → Thêm vào danh sách
   - Không có UPDATE trực tiếp → Dùng DELETE + POST
   - Delete → Xóa từng ảnh hoặc xóa all

3. **File Storage**: 
   - Tất cả files lưu trong `uploads/products/{productId}/`
   - Main image lưu trong subfolder `main/`
   - Additional images lưu ở root của product folder

4. **Cascade Delete**:
   - Khi xóa product → Xóa tất cả ảnh (main + additional)
   - Khi xóa ảnh → Xóa cả file vật lý

---

**Status**: ✅ **COMPLETE**  
**Date**: 11/11/2025  
**Version**: Backend v2.0 (Full CRUD for Images)
