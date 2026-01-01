# BÁO CÁO KIỂM THỬ PHẦN MỀM
## Hệ Thống Quản Lý Cửa Hàng Hoa (Flower Shop)

---

**Sinh viên thực hiện:** [Họ và tên]  
**MSSV:** [Mã số sinh viên]  
**Lớp:** [Tên lớp]  
**Môn học:** Kiểm thử phần mềm  
**Ngày nộp:** 25/12/2024  

---

## MỤC LỤC

1. [Chương 1: Giới thiệu hệ thống](#chương-1-giới-thiệu-hệ-thống)
2. [Chương 2: Mô tả và kiểm thử các chức năng](#chương-2-mô-tả-và-kiểm-thử-các-chức-năng)
   - [2.1 Chức năng 1: Quản lý Sản phẩm (ProductService)](#21-chức-năng-1-quản-lý-sản-phẩm-productservice)
   - [2.2 Chức năng 2: Xác thực Admin (AdminService)](#22-chức-năng-2-xác-thực-admin-adminservice)
   - [2.3 Chức năng 3: Thanh toán VNPay (VNPayService)](#23-chức-năng-3-thanh-toán-vnpay-vnpayservice)
   - [2.4 Chức năng 4: Quản lý Danh mục (CategoryService)](#24-chức-năng-4-quản-lý-danh-mục-categoryservice)
3. [Chương 3: Công cụ kiểm thử](#chương-3-công-cụ-kiểm-thử)

---

# Chương 1: Giới Thiệu Hệ Thống

## 1.1 Tổng quan

Hệ thống **Flower Shop** là một ứng dụng web thương mại điện tử cho phép người dùng mua bán các sản phẩm hoa. Hệ thống được xây dựng trên nền tảng:

- **Backend:** Spring Boot (Java)
- **Frontend:** Vue.js
- **Database:** MySQL
- **Payment Gateway:** VNPay

## 1.2 Các chức năng chính

| STT | Chức năng | Mô tả |
|-----|-----------|-------|
| 1 | Quản lý Sản phẩm | CRUD sản phẩm, upload hình ảnh, tìm kiếm |
| 2 | Xác thực Admin | Đăng nhập, quản lý tài khoản admin |
| 3 | Thanh toán VNPay | Tạo URL thanh toán, xử lý callback |
| 4 | Quản lý Danh mục | CRUD danh mục sản phẩm |

---

# Chương 2: Mô Tả và Kiểm Thử Các Chức Năng

---

## 2.1 Chức năng 1: Quản Lý Sản Phẩm (ProductService)

### 2.1.1 Mô tả yêu cầu

**Yêu cầu chức năng:** Hệ thống cho phép quản trị viên thực hiện các thao tác:
- Tạo mới sản phẩm với thông tin: mã sản phẩm, tên, mô tả, giá, hình ảnh, danh mục
- Cập nhật thông tin sản phẩm
- Xóa sản phẩm
- Tìm kiếm sản phẩm theo từ khóa
- Lấy sản phẩm theo danh mục

**Ràng buộc:**
- Mã sản phẩm (productCode) phải duy nhất
- Tên sản phẩm phải duy nhất
- Danh mục (categoryId) phải tồn tại trong hệ thống

### 2.1.2 Phân tích yêu cầu

#### Hàm `validateUniqueProductCode(String productCode, Long currentId)`

**Mục đích:** Kiểm tra mã sản phẩm có bị trùng hay không

**Đầu vào:**
- `productCode`: Mã sản phẩm cần kiểm tra (String)
- `currentId`: ID sản phẩm hiện tại (Long, có thể null nếu tạo mới)

**Đầu ra:**
- Không có giá trị trả về
- Ném exception `ValidationException` nếu mã sản phẩm đã tồn tại

**Logic xử lý:**
1. Tìm sản phẩm theo productCode (không phân biệt hoa thường)
2. Nếu tìm thấy sản phẩm:
   - Nếu `currentId == null` (tạo mới) → ném exception
   - Nếu `currentId != null` và ID tìm thấy khác currentId → ném exception
   - Nếu `currentId != null` và ID tìm thấy bằng currentId → OK (đang cập nhật chính nó)

### 2.1.3 Kiểm thử White-Box: Hàm `validateUniqueProductCode`

#### Đoạn mã nguồn đánh số:

```java
private void validateUniqueProductCode(String productCode, Long currentId) {
    /* 1 */ productRepository.findByProductCodeIgnoreCase(productCode).ifPresent(existing -> {
    /* 2 */     if (currentId == null || !existing.getId().equals(currentId)) {
    /* 3 */         throw new ValidationException("productCode", "Product code already exists");
    /*   */     }
    /* 4 */ });
    /* 5 */ // End of method
}
```

#### Sơ đồ điều khiển dòng (CFG):

```
                    ┌─────────────────┐
                    │   START (1)     │
                    │ findByProductCode│
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │  Product found? │
                    │     (A)         │
                    └────────┬────────┘
                    ┌────────┴────────┐
              True  │                 │ False
                    ▼                 ▼
           ┌────────────────┐  ┌─────────────┐
           │  Check (2)     │  │   END (5)   │
           │currentId==null │  └─────────────┘
           │    OR          │        ▲
           │ID != currentId │        │
           │     (B)        │        │
           └────────┬───────┘        │
                    │                │
           ┌────────┴────────┐       │
     True  │                 │ False │
           ▼                 ▼       │
    ┌──────────────┐  ┌─────────────┐│
    │   Throw (3)  │  │   Skip (4)  │┘
    │  Exception   │  │             ├─┐
    └──────────────┘  └─────────────┘ │
                             │        │
                             └────────┘
```

#### Ký hiệu:
- **A:** Điểm quyết định - Có tìm thấy sản phẩm với mã trùng không?
- **B:** Điểm quyết định - currentId == null HOẶC existing.getId() != currentId?

#### Tính độ phức tạp Cyclomatic V(G):

**Công thức:** V(G) = E - N + 2P

Trong đó:
- E = Số cạnh (edges) = 6
- N = Số nút (nodes) = 5
- P = Số thành phần liên thông = 1

**V(G) = 6 - 5 + 2(1) = 3**

**Hoặc theo công thức:** V(G) = Số điểm quyết định + 1 = 2 + 1 = 3

#### Xác định các đường dẫn độc lập (V(G) = 3):

| Đường dẫn | Mô tả | Điều kiện |
|-----------|-------|-----------|
| **P1** | 1 → A(False) → 5 | Không tìm thấy product với code trùng |
| **P2** | 1 → A(True) → 2 → B(True) → 3 | Tìm thấy product trùng và currentId=null hoặc ID khác |
| **P3** | 1 → A(True) → 2 → B(False) → 4 → 5 | Tìm thấy product nhưng chính là sản phẩm đang cập nhật |

### 2.1.4 Phủ kiểm thử

#### A. Phủ câu lệnh (Statement Coverage)

Để phủ tất cả các câu lệnh, cần ít nhất 2 test case:

| TC | Đầu vào | Kỳ vọng | Câu lệnh phủ |
|----|---------|---------|--------------|
| SC1 | productCode="ABC", currentId=null, *không tồn tại* | Không throw | 1, 5 |
| SC2 | productCode="ABC", currentId=null, *đã tồn tại* | Throw exception | 1, 2, 3 |
| SC3 | productCode="ABC", currentId=1, *tồn tại với ID=1* | Không throw | 1, 2, 4, 5 |

#### B. Phủ nhánh (Branch Coverage)

| TC | Điều kiện A | Điều kiện B | Kết quả |
|----|-------------|-------------|---------|
| BC1 | False | - | Không throw |
| BC2 | True | True | Throw exception |
| BC3 | True | False | Không throw |

#### C. Phủ điều kiện (Condition Coverage)

Điều kiện tại B: `currentId == null || !existing.getId().equals(currentId)`

| TC | currentId == null | !existing.getId().equals(currentId) | Kết quả B |
|----|-------------------|-------------------------------------|-----------|
| CC1 | True | - | True |
| CC2 | False | True | True |
| CC3 | False | False | False |

#### D. Phủ đa điều kiện (Multiple Condition Coverage)

| TC | currentId == null | !existing.getId().equals(currentId) | Kết quả |
|----|-------------------|-------------------------------------|---------|
| MC1 | True | True | True |
| MC2 | True | False | True |
| MC3 | False | True | True |
| MC4 | False | False | False |

### 2.1.5 Bảng Test Case đầy đủ (10 trường hợp)

| TC ID | Mô tả | productCode | currentId | DB State | Kết quả mong đợi | Loại phủ |
|-------|-------|-------------|-----------|----------|------------------|----------|
| TC1.1 | Tạo mới với mã không trùng | "HOA001" | null | Không tồn tại "HOA001" | ✓ Thành công | P1, Statement |
| TC1.2 | Tạo mới với mã trùng | "HOA001" | null | Tồn tại "HOA001" | ✗ ValidationException | P2, Branch |
| TC1.3 | Cập nhật với mã không đổi | "HOA001" | 1 | Tồn tại "HOA001" với ID=1 | ✓ Thành công | P3, Condition |
| TC1.4 | Cập nhật với mã mới không trùng | "HOA002" | 1 | Không tồn tại "HOA002" | ✓ Thành công | P1 |
| TC1.5 | Cập nhật với mã mới bị trùng | "HOA002" | 1 | Tồn tại "HOA002" với ID=2 | ✗ ValidationException | P2 |
| TC1.6 | Mã sản phẩm rỗng | "" | null | - | ✓ Thành công (không tìm thấy) | P1 |
| TC1.7 | Mã sản phẩm null | null | null | - | ✗ NullPointerException | Exception |
| TC1.8 | Mã có chữ hoa/thường khác | "hoa001" | null | Tồn tại "HOA001" | ✗ ValidationException | P2, Case-insensitive |
| TC1.9 | Mã với ký tự đặc biệt | "HOA@#$" | null | Không tồn tại | ✓ Thành công | P1 |
| TC1.10 | Mã rất dài (255 ký tự) | "A" * 255 | null | Không tồn tại | ✓ Thành công | P1, Boundary |

---

## 2.2 Chức năng 2: Xác Thực Admin (AdminService)

### 2.2.1 Mô tả yêu cầu

**Yêu cầu chức năng:** Hệ thống cho phép:
- Admin đăng nhập bằng username và password
- Xác thực thông tin đăng nhập
- Mã hóa password sử dụng BCrypt

**Ràng buộc:**
- Username phải tồn tại trong hệ thống
- Password phải khớp với password đã mã hóa

### 2.2.2 Phân tích yêu cầu

#### Hàm `authenticate(AdminLoginRequest request)`

**Mục đích:** Xác thực thông tin đăng nhập của admin

**Đầu vào:**
- `request.username`: Tên đăng nhập (String)
- `request.password`: Mật khẩu (String)

**Đầu ra:**
- Trả về đối tượng `Admin` nếu xác thực thành công
- Ném exception `ValidationException` nếu thất bại

### 2.2.3 Kiểm thử White-Box: Hàm `authenticate`

#### Đoạn mã nguồn đánh số:

```java
public Admin authenticate(AdminLoginRequest request) {
    /* 1 */ Admin admin = adminRepository.findByUsername(request.getUsername())
    /* 2 */     .orElseThrow(() -> new ValidationException("credentials", "Invalid username or password"));
    
    /* 3 */ if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
    /* 4 */     throw new ValidationException("credentials", "Invalid username or password");
    /*   */ }
    
    /* 5 */ return admin;
}
```

#### Sơ đồ điều khiển dòng (CFG):

```
              ┌───────────────────┐
              │    START (1)      │
              │ findByUsername    │
              └─────────┬─────────┘
                        │
              ┌─────────▼─────────┐
              │  User found? (A)  │
              └─────────┬─────────┘
              ┌─────────┴─────────┐
        True  │                   │ False
              ▼                   ▼
     ┌────────────────┐  ┌────────────────┐
     │  Check (3)     │  │   Throw (2)    │
     │  Password      │  │   Exception    │
     │  Match? (B)    │  └────────────────┘
     └────────┬───────┘
              │
     ┌────────┴────────┐
False│                 │ True
     ▼                 ▼
┌──────────────┐  ┌──────────────┐
│   Throw (4)  │  │  Return (5)  │
│   Exception  │  │    admin     │
└──────────────┘  └──────────────┘
```

#### Tính V(G):

**V(G) = E - N + 2P = 5 - 5 + 2(1) = 2**

**Hoặc:** V(G) = 2 điểm quyết định + 1 = 3

> **Lưu ý:** Do điểm quyết định A dẫn đến kết thúc ngay, nên V(G) thực tế = 3

#### Xác định các đường dẫn độc lập (V(G) = 3):

| Đường | Mô tả | Điều kiện |
|-------|-------|-----------|
| **P1** | 1 → A(False) → 2 | Username không tồn tại |
| **P2** | 1 → A(True) → 3 → B(False) → 4 | Username đúng, password sai |
| **P3** | 1 → A(True) → 3 → B(True) → 5 | Xác thực thành công |

### 2.2.4 Phủ kiểm thử

#### A. Phủ nhánh (Branch Coverage)

| TC | Điều kiện A (User found) | Điều kiện B (Password match) | Kết quả |
|----|--------------------------|------------------------------|---------|
| BC1 | False | - | Exception: Invalid username |
| BC2 | True | False | Exception: Invalid password |
| BC3 | True | True | Return Admin |

#### B. Phủ điều kiện (Condition Coverage)

Điều kiện B: `!passwordEncoder.matches(request.getPassword(), admin.getPassword())`

| TC | matches() | !matches() | Kết quả |
|----|-----------|------------|---------|
| CC1 | True | False | Return admin |
| CC2 | False | True | Throw exception |

### 2.2.5 Bảng Test Case đầy đủ (10 trường hợp)

| TC ID | Mô tả | username | password | DB State | Kết quả mong đợi | Loại phủ |
|-------|-------|----------|----------|----------|------------------|----------|
| TC2.1 | Đăng nhập thành công | "admin" | "password123" | Tồn tại admin với password đúng | ✓ Return Admin | P3 |
| TC2.2 | Username không tồn tại | "nonexist" | "password" | Không tồn tại user | ✗ ValidationException | P1, Branch |
| TC2.3 | Password sai | "admin" | "wrongpass" | Tồn tại admin | ✗ ValidationException | P2, Branch |
| TC2.4 | Username rỗng | "" | "password" | - | ✗ ValidationException | P1 |
| TC2.5 | Password rỗng | "admin" | "" | Tồn tại admin | ✗ ValidationException | P2 |
| TC2.6 | Username null | null | "password" | - | ✗ NullPointerException | Exception |
| TC2.7 | Password null | "admin" | null | Tồn tại admin | ✗ NullPointerException | Exception |
| TC2.8 | Username có khoảng trắng | "  admin  " | "password123" | Không tồn tại | ✗ ValidationException | P1 |
| TC2.9 | Password phân biệt hoa/thường | "admin" | "PASSWORD123" | Tồn tại với "password123" | ✗ ValidationException | P2 |
| TC2.10 | Username và password đúng (SQL Injection) | "admin' OR '1'='1" | "anything" | - | ✗ ValidationException | P1, Security |

---

## 2.3 Chức năng 3: Thanh Toán VNPay (VNPayService)

### 2.3.1 Mô tả yêu cầu

**Yêu cầu chức năng:** Hệ thống tích hợp cổng thanh toán VNPay:
- Tạo URL thanh toán với các tham số cần thiết
- Xác thực callback từ VNPay sau khi thanh toán
- Kiểm tra chữ ký số (secure hash)

**Ràng buộc:**
- Số tiền phải > 0
- Chữ ký số phải hợp lệ
- Mã phản hồi phải là "00" để thanh toán thành công

### 2.3.2 Phân tích yêu cầu

#### Hàm `orderReturn(HttpServletRequest request)`

**Mục đích:** Xử lý kết quả thanh toán từ VNPay

**Đầu vào:**
- `request`: HttpServletRequest chứa các tham số VNPay trả về

**Đầu ra:**
- `1`: Thanh toán thành công
- `0`: Thanh toán thất bại
- `-1`: Chữ ký không hợp lệ

### 2.3.3 Kiểm thử White-Box: Hàm `orderReturn`

#### Đoạn mã nguồn đánh số:

```java
public int orderReturn(HttpServletRequest request) {
    /* 1 */ Map<String, String> fields = new HashMap<>();
    /* 2 */ for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
    /* 3 */     String fieldName = params.nextElement();
    /* 4 */     String fieldValue = request.getParameter(fieldName);
    /* 5 */     if ((fieldValue != null) && (fieldValue.length() > 0)) {
    /* 6 */         fields.put(fieldName, fieldValue);
    /*   */     }
    /* 7 */ }
    
    /* 8 */ String vnp_SecureHash = request.getParameter("vnp_SecureHash");
    /* 9 */ if (fields.containsKey("vnp_SecureHashType")) {
    /* 10 */    fields.remove("vnp_SecureHashType");
    /*   */ }
    /* 11 */ if (fields.containsKey("vnp_SecureHash")) {
    /* 12 */    fields.remove("vnp_SecureHash");
    /*   */ }
    
    /* 13 */ String signValue = hashAllFields(fields);
    /* 14 */ if (signValue.equals(vnp_SecureHash)) {
    /* 15 */    if ("00".equals(request.getParameter("vnp_ResponseCode"))) {
    /* 16 */        return 1; // Success
    /*    */    } else {
    /* 17 */        return 0; // Error
    /*    */    }
    /* 18 */ } else {
    /* 19 */    return -1; // Invalid signature
    /*    */ }
}
```

#### Sơ đồ điều khiển dòng (CFG):

```
                    ┌─────────────────┐
                    │   START (1-7)   │
                    │  Build fields   │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │ Remove hash     │
                    │ fields (8-12)   │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │ signValue =     │
                    │ hashAllFields   │
                    │     (13)        │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │ signValue ==    │
                    │ vnp_SecureHash? │
                    │      (A)        │
                    └────────┬────────┘
                    ┌────────┴────────┐
              True  │                 │ False
                    ▼                 ▼
           ┌────────────────┐  ┌─────────────┐
           │ ResponseCode   │  │ return -1   │
           │   == "00"?     │  │    (19)     │
           │     (B)        │  └─────────────┘
           └────────┬───────┘
                    │
           ┌────────┴────────┐
     True  │                 │ False
           ▼                 ▼
    ┌──────────────┐  ┌──────────────┐
    │  return 1    │  │  return 0    │
    │    (16)      │  │    (17)      │
    └──────────────┘  └──────────────┘
```

#### Tính V(G):

**V(G) = E - N + 2P = 7 - 6 + 2(1) = 3**

**Hoặc:** V(G) = 2 điểm quyết định + 1 = 3

#### Xác định các đường dẫn độc lập (V(G) = 3):

| Đường | Điều kiện | Kết quả |
|-------|-----------|---------|
| **P1** | A(True) → B(True) | return 1 (Success) |
| **P2** | A(True) → B(False) | return 0 (Error) |
| **P3** | A(False) | return -1 (Invalid signature) |

### 2.3.4 Phủ kiểm thử

#### A. Phủ nhánh (Branch Coverage)

| TC | Điều kiện A (Signature valid) | Điều kiện B (Response=00) | Kết quả |
|----|-------------------------------|---------------------------|---------|
| BC1 | True | True | return 1 |
| BC2 | True | False | return 0 |
| BC3 | False | - | return -1 |

#### B. Phủ điều kiện (Condition Coverage)

| TC | signValue.equals(vnp_SecureHash) | "00".equals(vnp_ResponseCode) | Kết quả |
|----|----------------------------------|-------------------------------|---------|
| CC1 | True | True | 1 |
| CC2 | True | False | 0 |
| CC3 | False | - | -1 |

### 2.3.5 Bảng Test Case đầy đủ (10 trường hợp)

| TC ID | Mô tả | vnp_SecureHash | vnp_ResponseCode | Kết quả mong đợi | Loại phủ |
|-------|-------|----------------|------------------|------------------|----------|
| TC3.1 | Thanh toán thành công | Valid hash | "00" | return 1 | P1, Branch |
| TC3.2 | Thanh toán thất bại (lỗi bank) | Valid hash | "01" | return 0 | P2, Branch |
| TC3.3 | Chữ ký không hợp lệ | Invalid hash | "00" | return -1 | P3, Branch |
| TC3.4 | Giao dịch bị từ chối | Valid hash | "07" | return 0 | P2 |
| TC3.5 | Thẻ hết hạn | Valid hash | "09" | return 0 | P2 |
| TC3.6 | SecureHash null | null | "00" | return -1 | P3 |
| TC3.7 | ResponseCode null | Valid hash | null | return 0 | P2 |
| TC3.8 | Không có tham số | - | - | return -1 | P3 |
| TC3.9 | Hash đúng, code "99" (unknown) | Valid hash | "99" | return 0 | P2 |
| TC3.10 | Hash bị tampering | Tampered hash | "00" | return -1 | P3, Security |

---

## 2.4 Chức năng 4: Quản Lý Danh Mục (CategoryService)

### 2.4.1 Mô tả yêu cầu

**Yêu cầu chức năng:** Hệ thống cho phép quản trị viên:
- Tạo mới danh mục sản phẩm
- Cập nhật tên danh mục
- Xóa danh mục
- Lấy danh sách tất cả danh mục

**Ràng buộc:**
- Tên danh mục phải duy nhất (không phân biệt hoa thường)

### 2.4.2 Phân tích yêu cầu

#### Hàm `validateUniqueName(String name, Long currentId)`

**Mục đích:** Kiểm tra tên danh mục có bị trùng hay không

**Đầu vào:**
- `name`: Tên danh mục cần kiểm tra (String)
- `currentId`: ID danh mục hiện tại (Long, có thể null nếu tạo mới)

**Đầu ra:**
- Không có giá trị trả về
- Ném exception `ValidationException` nếu tên đã tồn tại

### 2.4.3 Kiểm thử White-Box: Hàm `validateUniqueName`

#### Đoạn mã nguồn đánh số:

```java
private void validateUniqueName(String name, Long currentId) {
    /* 1 */ categoryRepository.findByNameIgnoreCase(name).ifPresent(existing -> {
    /* 2 */     if (currentId == null || !existing.getId().equals(currentId)) {
    /* 3 */         throw new ValidationException("name", "Category name already exists");
    /*   */     }
    /* 4 */ });
    /* 5 */ // End of method
}
```

#### Sơ đồ điều khiển dòng (CFG):

```
                    ┌─────────────────┐
                    │   START (1)     │
                    │ findByNameIgnoreCase│
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │ Category found? │
                    │     (A)         │
                    └────────┬────────┘
                    ┌────────┴────────┐
              True  │                 │ False
                    ▼                 ▼
           ┌────────────────┐  ┌─────────────┐
           │  Check (2)     │  │   END (5)   │
           │currentId==null │  └─────────────┘
           │    OR          │        ▲
           │ID != currentId │        │
           │     (B)        │        │
           └────────┬───────┘        │
                    │                │
           ┌────────┴────────┐       │
     True  │                 │ False │
           ▼                 ▼       │
    ┌──────────────┐  ┌─────────────┐│
    │   Throw (3)  │  │   Skip (4)  │┘
    │  Exception   │  │             ├─┐
    └──────────────┘  └─────────────┘ │
                             │        │
                             └────────┘
```

#### Tính V(G):

**V(G) = E - N + 2P = 6 - 5 + 2(1) = 3**

#### Xác định các đường dẫn độc lập (V(G) = 3):

| Đường | Mô tả | Điều kiện |
|-------|-------|-----------|
| **P1** | 1 → A(False) → 5 | Tên không tồn tại |
| **P2** | 1 → A(True) → 2 → B(True) → 3 | Tên trùng và cần ném exception |
| **P3** | 1 → A(True) → 2 → B(False) → 4 → 5 | Tên trùng nhưng là chính nó |

### 2.4.4 Phủ kiểm thử

#### A. Phủ nhánh (Branch Coverage)

| TC | Điều kiện A | Điều kiện B | Kết quả |
|----|-------------|-------------|---------|
| BC1 | False | - | Pass |
| BC2 | True | True | Exception |
| BC3 | True | False | Pass |

#### B. Phủ điều kiện (Condition Coverage)

Điều kiện B: `currentId == null || !existing.getId().equals(currentId)`

| TC | currentId == null | !existing.getId().equals(currentId) | Kết quả |
|----|-------------------|-------------------------------------|---------|
| CC1 | True | - | True → Exception |
| CC2 | False | True | True → Exception |
| CC3 | False | False | False → Pass |

#### C. Phủ đa điều kiện (Multiple Condition Coverage)

| TC | C1: currentId==null | C2: ID!=currentId | Kết quả B |
|----|---------------------|-------------------|-----------|
| MC1 | T | T | T |
| MC2 | T | F | T |
| MC3 | F | T | T |
| MC4 | F | F | F |

### 2.4.5 Bảng Test Case đầy đủ (10 trường hợp)

| TC ID | Mô tả | name | currentId | DB State | Kết quả mong đợi | Loại phủ |
|-------|-------|------|-----------|----------|------------------|----------|
| TC4.1 | Tạo mới với tên không trùng | "Hoa Tươi" | null | Không có "Hoa Tươi" | ✓ Thành công | P1 |
| TC4.2 | Tạo mới với tên trùng | "Hoa Tươi" | null | Tồn tại "Hoa Tươi" | ✗ ValidationException | P2, Branch |
| TC4.3 | Cập nhật với tên không đổi | "Hoa Tươi" | 1 | Tồn tại ID=1, name="Hoa Tươi" | ✓ Thành công | P3, Condition |
| TC4.4 | Cập nhật với tên mới không trùng | "Hoa Khô" | 1 | Không có "Hoa Khô" | ✓ Thành công | P1 |
| TC4.5 | Cập nhật với tên bị trùng | "Hoa Khô" | 1 | Tồn tại "Hoa Khô" với ID=2 | ✗ ValidationException | P2 |
| TC4.6 | Tên rỗng | "" | null | - | ✓ Thành công | P1 |
| TC4.7 | Tên null | null | null | - | Có thể NullPointerException | Exception |
| TC4.8 | Tên hoa/thường khác nhau | "HOA TƯƠI" | null | Tồn tại "hoa tươi" | ✗ ValidationException | P2, Case-insensitive |
| TC4.9 | Tên với ký tự đặc biệt | "Hoa @#$%" | null | Không tồn tại | ✓ Thành công | P1 |
| TC4.10 | Tên rất dài (255 ký tự) | "A" * 255 | null | Không tồn tại | ✓ Thành công | P1, Boundary |

---

# Chương 3: Công Cụ Kiểm Thử

## 3.1 Tổng quan công cụ

Trong quá trình kiểm thử phần mềm hệ thống Flower Shop, các công cụ sau được sử dụng:

### 3.1.1 JUnit 5 (Jupiter)

**Mô tả:** JUnit 5 là framework kiểm thử đơn vị phổ biến nhất cho Java. Nó cung cấp:

- **Annotations:** `@Test`, `@BeforeEach`, `@AfterEach`, `@DisplayName`
- **Assertions:** `assertEquals`, `assertTrue`, `assertThrows`
- **Parameterized Tests:** Chạy test với nhiều bộ dữ liệu

**Ví dụ sử dụng:**
```java
@Test
@DisplayName("TC1.1: Tạo mới với mã không trùng")
void testCreateProductWithUniqueCode() {
    ProductCreateRequest request = new ProductCreateRequest();
    request.setProductCode("HOA001");
    request.setName("Hoa Hồng");
    
    Product result = productService.createProduct(request);
    
    assertNotNull(result);
    assertEquals("HOA001", result.getProductCode());
}
```

### 3.1.2 Mockito

**Mô tả:** Mockito là framework mocking cho Java, cho phép:

- **Mock objects:** Tạo đối tượng giả lập
- **Stubbing:** Định nghĩa hành vi cho mock
- **Verification:** Xác minh các phương thức được gọi

**Ví dụ sử dụng:**
```java
@Mock
private ProductRepository productRepository;

@Test
void testValidateUniqueProductCode_WhenCodeExists_ThrowsException() {
    when(productRepository.findByProductCodeIgnoreCase("HOA001"))
        .thenReturn(Optional.of(existingProduct));
    
    assertThrows(ValidationException.class, () -> 
        productService.validateUniqueProductCode("HOA001", null));
}
```

### 3.1.3 Spring Boot Test

**Mô tả:** Spring Boot Test cung cấp các annotation và utility để kiểm thử:

- `@SpringBootTest`: Tải toàn bộ context
- `@WebMvcTest`: Kiểm thử controller layer
- `@DataJpaTest`: Kiểm thử repository layer
- `@MockBean`: Inject mock vào Spring context

### 3.1.4 Postman

**Mô tả:** Postman là công cụ kiểm thử API, cho phép:

- **Gửi HTTP requests:** GET, POST, PUT, DELETE
- **Kiểm tra response:** Status code, body, headers
- **Tự động hóa:** Collection Runner, Newman CLI
- **Environment variables:** Quản lý biến môi trường

**Sử dụng cho:**
- Kiểm thử API endpoint
- Kiểm thử Authentication
- Kiểm thử VNPay integration

### 3.1.5 JaCoCo (Java Code Coverage)

**Mô tả:** JaCoCo là công cụ đo code coverage:

- **Line coverage:** Phần trăm dòng code được thực thi
- **Branch coverage:** Phần trăm nhánh được phủ
- **Instruction coverage:** Phần trăm bytecode instructions
- **Report:** HTML, XML, CSV

**Cấu hình Maven:**
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.10</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## 3.2 Môi trường kiểm thử

| Thành phần | Phiên bản |
|------------|-----------|
| Java | 17 |
| Spring Boot | 3.x |
| JUnit | 5.9.x |
| Mockito | 5.x |
| MySQL | 8.0 |
| Docker | 24.x |

## 3.3 Quy trình kiểm thử

```
┌─────────────────┐
│  1. Unit Test   │
│    (JUnit +     │
│    Mockito)     │
└────────┬────────┘
         │
┌────────▼────────┐
│ 2. Integration  │
│    Test         │
│ (SpringBootTest)│
└────────┬────────┘
         │
┌────────▼────────┐
│   3. API Test   │
│   (Postman)     │
└────────┬────────┘
         │
┌────────▼────────┐
│  4. Coverage    │
│    Report       │
│   (JaCoCo)      │
└─────────────────┘
```

---

# PHỤ LỤC

## A. Tổng hợp các đường dẫn độc lập

| Chức năng | V(G) | Số đường dẫn | Phủ |
|-----------|------|--------------|-----|
| validateUniqueProductCode | 3 | 3 | Statement, Branch, Condition, Multi-Condition |
| authenticate | 3 | 3 | Statement, Branch, Condition |
| orderReturn | 3 | 3 | Statement, Branch, Condition |
| validateUniqueName | 3 | 3 | Statement, Branch, Condition, Multi-Condition |

## B. Ma trận truy xuất yêu cầu

| Yêu cầu | Test Case |
|---------|-----------|
| REQ-1: Mã sản phẩm duy nhất | TC1.1 - TC1.10 |
| REQ-2: Admin authentication | TC2.1 - TC2.10 |
| REQ-3: VNPay integration | TC3.1 - TC3.10 |
| REQ-4: Category name unique | TC4.1 - TC4.10 |

## C. Kết luận

Báo cáo này đã trình bày chi tiết:
- **4 chức năng** được kiểm thử với kỹ thuật White-Box
- **40 test case** (10 test case mỗi chức năng)
- **Phân tích CFG** và tính toán **V(G)** cho mỗi hàm
- Các loại phủ kiểm thử: **Statement, Branch, Condition, Multi-Condition Coverage**

---

*Kết thúc báo cáo*
