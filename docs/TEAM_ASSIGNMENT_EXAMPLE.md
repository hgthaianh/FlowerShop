# 👥 Phân Chia Công Việc Nhóm 3 Người - Flower Shop

## 📋 Danh Sách Thành Viên

| Thành viên | Vai trò | Chuyên môn |
|------------|---------|------------|
| **Người A** | Backend Lead | Spring Boot, Security, Database |
| **Người B** | Frontend Lead | Vue.js, UI/UX, CSS |
| **Người C** | Full-stack + DevOps | Integration, Testing, Docker |

---

## 🗂️ Phân Chia Module (Tránh Conflict)

```
📁 flower_shop/
├── 📂 flower-shop/ (Backend - Spring Boot)
│   └── src/main/java/vn/quahoa/flowershop/
│       ├── 🔴 controller/     ← Người A + C chia ra
│       ├── 🔴 service/        ← Người A + C chia ra  
│       ├── 🔴 security/       ← Người A (RIÊNG)
│       ├── ⚪ model/          ← Ai tạo entity người đó quản lý
│       ├── ⚪ repository/     ← Ai tạo entity người đó quản lý
│       └── ⚪ config/         ← Người A (RIÊNG)
│
└── 📂 frontend/src/ (Frontend - Vue.js)
    ├── 🔵 components/         ← Người B + C chia ra
    ├── 🔵 router/             ← Người B (RIÊNG)
    └── 🔵 assets/             ← Người B (RIÊNG)
```

---

## 📋 Chi Tiết Phân Chia Theo File Thực Tế

### 🔴 NGƯỜI A - Backend Lead

**Branch prefix:** `feature/FS-X-backend-*`

| Jira Task | Branch | Files phụ trách |
|-----------|--------|-----------------|
| FS-1 | `feature/FS-1-auth-system` | `AuthController.java`, `security/*` |
| FS-2 | `feature/FS-2-vnpay-service` | `VNPayService.java`, `PaymentController.java` |
| FS-5 | `feature/FS-5-admin-api` | `AdminController.java`, `AdminService.java` |

```
📂 Người A quản lý:
├── controller/
│   ├── AuthController.java ✅
│   ├── PaymentController.java ✅
│   └── AdminController.java ✅
├── service/
│   ├── VNPayService.java ✅
│   └── AdminService.java ✅
├── security/ (TOÀN BỘ) ✅
│   ├── oauth2/*
│   ├── jwt/*
│   └── SecurityConfig.java
└── config/ (TOÀN BỘ) ✅
    ├── WebSecurityConfig.java
    └── AppProperties.java
```

---

### 🔵 NGƯỜI B - Frontend Lead

**Branch prefix:** `feature/FS-X-frontend-*`

| Jira Task | Branch | Files phụ trách |
|-----------|--------|-----------------|
| FS-3 | `feature/FS-3-payment-ui` | `CheckoutPage.vue`, `PaymentResult.vue` |
| FS-4 | `feature/FS-4-homepage` | `HomePage.vue`, `SiteNavbar.vue` |
| FS-6 | `feature/FS-6-blog-ui` | `BlogList.vue`, `BlogDetail.vue` |

```
📂 Người B quản lý:
├── components/
│   ├── HomePage.vue ✅
│   ├── SiteNavbar.vue ✅
│   ├── CheckoutPage.vue ✅
│   ├── PaymentResult.vue ✅
│   ├── BlogList.vue ✅
│   ├── BlogDetail.vue ✅
│   ├── UserLogin.vue ✅
│   └── UserRegister.vue ✅
├── router/index.js ✅
├── assets/ ✅
└── App.vue ✅
```

---

### 🟢 NGƯỜI C - Full-stack + DevOps

**Branch prefix:** `feature/FS-X-product-*` hoặc `feature/FS-X-devops-*`

| Jira Task | Branch | Files phụ trách |
|-----------|--------|-----------------|
| FS-7 | `feature/FS-7-product-crud` | `ProductController.java`, `ProductService.java`, `ProductDetail.vue` |
| FS-8 | `feature/FS-8-category` | `CategoryController.java`, `CategoryService.java` |
| FS-9 | `feature/FS-9-docker-setup` | `docker-compose.yml`, `Dockerfile`, scripts |

```
📂 Người C quản lý:
├── Backend:
│   ├── controller/
│   │   ├── ProductController.java ✅
│   │   ├── CategoryController.java ✅
│   │   ├── BlogController.java ✅
│   │   └── OrderController.java ✅
│   ├── service/
│   │   ├── ProductService.java ✅
│   │   ├── CategoryService.java ✅
│   │   ├── BlogService.java ✅
│   │   └── ImageStorageService.java ✅
│   └── model/ + repository/ (cho Product, Category, Blog, Order)
│
├── Frontend:
│   ├── components/
│   │   ├── ProductDetail.vue ✅
│   │   ├── ShoppingCart.vue ✅
│   │   ├── AdminDashboard.vue ✅
│   │   ├── AdminBlogManagement.vue ✅
│   │   └── ImageUploader.vue ✅
│
└── DevOps:
    ├── docker-compose.yml ✅
    ├── docker-compose.prod.yml ✅
    ├── scripts/* ✅
    └── database/* ✅
```

---

## 📅 Ví Dụ Sprint 2 Tuần

### 🗓️ TUẦN 1

```
┌─────────────────────────────────────────────────────────────────────┐
│ THỨ 2 (Ngày 1) - Sprint Planning                                    │
├─────────────────────────────────────────────────────────────────────┤
│ 🔴 Người A: git checkout -b feature/FS-1-auth-system                │
│ 🔵 Người B: git checkout -b feature/FS-3-payment-ui                 │
│ 🟢 Người C: git checkout -b feature/FS-7-product-crud               │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│ THỨ 3-4 (Ngày 2-3) - Development                                    │
├─────────────────────────────────────────────────────────────────────┤
│ 🔴 Người A commits:                                                 │
│    git commit -m "FS-1 Add JWT token generation"                    │
│    git commit -m "FS-1 Implement login endpoint"                    │
│    git commit -m "FS-1 Add Google OAuth2 config"                    │
│                                                                     │
│ 🔵 Người B commits:                                                 │
│    git commit -m "FS-3 Create CheckoutPage component"               │
│    git commit -m "FS-3 Add payment form validation"                 │
│    git commit -m "FS-3 Style payment buttons"                       │
│                                                                     │
│ 🟢 Người C commits:                                                 │
│    git commit -m "FS-7 Add ProductController endpoints"             │
│    git commit -m "FS-7 Implement ProductService CRUD"               │
│    git commit -m "FS-7 Create ProductDetail.vue"                    │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│ THỨ 5 (Ngày 4) - First Merge                                        │
├─────────────────────────────────────────────────────────────────────┤
│ 🟢 Người C: Merge FS-7 → develop (hoàn thành đầu tiên)              │
│    1. Create Pull Request                                           │
│    2. Người A review                                                 │
│    3. Merge vào develop                                             │
│                                                                     │
│ 🔴🔵 Người A, B: Pull develop mới                                   │
│    git checkout develop && git pull                                 │
│    git checkout feature/FS-X && git merge develop                   │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│ THỨ 6 (Ngày 5) - Continue & Second Merge                            │
├─────────────────────────────────────────────────────────────────────┤
│ 🔴 Người A: Merge FS-1 → develop                                    │
│ 🔵 Người B: Still working on FS-3                                   │
│ 🟢 Người C: Start new task FS-8-category                            │
└─────────────────────────────────────────────────────────────────────┘
```

### 🗓️ TUẦN 2

```
┌─────────────────────────────────────────────────────────────────────┐
│ THỨ 2 (Ngày 6) - Tiếp tục phát triển                                │
├─────────────────────────────────────────────────────────────────────┤
│ 🔴 Người A: git checkout -b feature/FS-2-vnpay-service              │
│ 🔵 Người B: Merge FS-3 → develop, start FS-4-homepage               │
│ 🟢 Người C: Continue FS-8-category                                  │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│ THỨ 3-4 (Ngày 7-8) - Development                                    │
├─────────────────────────────────────────────────────────────────────┤
│ 🔴 Người A commits:                                                 │
│    git commit -m "FS-2 Create VNPayService"                         │
│    git commit -m "FS-2 Add payment callback handler"                │
│    git commit -m "FS-2 Write unit tests for VNPay"                  │
│                                                                     │
│ 🔵 Người B commits:                                                 │
│    git commit -m "FS-4 Design HomePage layout"                      │
│    git commit -m "FS-4 Add product carousel"                        │
│    git commit -m "FS-4 Improve SiteNavbar responsive"               │
│                                                                     │
│ 🟢 Người C commits:                                                 │
│    git commit -m "FS-8 Add CategoryController"                      │
│    git commit -m "FS-8 Create category management API"              │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│ THỨ 5-6 (Ngày 9-10) - Final Merge & Testing                         │
├─────────────────────────────────────────────────────────────────────┤
│ ALL: Merge tất cả branches vào develop                              │
│ 🟢 Người C: Test integration, fix bugs                              │
│ 🔴🔵 Người A, B: Code review cross-team                             │
│                                                                     │
│ Final: Merge develop → main (release)                               │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 🔄 Workflow Từng Ngày

### 🌅 Buổi Sáng (15 phút)

```bash
# Mỗi người đều làm:

# 1. Update develop
git checkout develop
git pull origin develop

# 2. Merge develop vào feature branch của mình
git checkout feature/FS-X-my-task
git merge develop

# 3. Update Jira status nếu chưa
# Task: TODO → IN PROGRESS
```

### 💻 Trong Ngày

```bash
# Commit thường xuyên (mỗi 1-2 tiếng)

# Người A example:
git add src/main/java/vn/quahoa/flowershop/security/
git commit -m "FS-1 Add JWT token validation filter"

# Người B example:
git add frontend/src/components/CheckoutPage.vue
git commit -m "FS-3 Add payment form with VNPay button"

# Người C example:
git add src/main/java/vn/quahoa/flowershop/controller/ProductController.java
git commit -m "FS-7 Add create product endpoint"
```

### 🌆 Cuối Ngày (15 phút)

```bash
# Push code lên remote
git push origin feature/FS-X-my-task

# Update Jira:
# - Log time spent (2h, 4h, etc.)
# - Add comment về progress
# - Nếu hoàn thành: IN PROGRESS → TEST
```

---

## 📊 Bảng Theo Dõi Jira

### Sprint Board View

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           SPRINT 1 BOARD                                     │
├───────────────┬───────────────┬───────────────┬───────────────┬─────────────┤
│     TODO      │      DEV      │  IN PROGRESS  │     TEST      │    DONE     │
├───────────────┼───────────────┼───────────────┼───────────────┼─────────────┤
│               │               │ 🔴 FS-1       │               │             │
│               │               │ Auth System   │               │             │
│               │               │ @PersonA      │               │             │
├───────────────┼───────────────┼───────────────┼───────────────┼─────────────┤
│               │               │ 🔵 FS-3       │               │             │
│               │               │ Payment UI    │               │             │
│               │               │ @PersonB      │               │             │
├───────────────┼───────────────┼───────────────┼───────────────┼─────────────┤
│               │               │ 🟢 FS-7       │               │             │
│               │               │ Product CRUD  │               │             │
│               │               │ @PersonC      │               │             │
├───────────────┼───────────────┼───────────────┼───────────────┼─────────────┤
│ FS-2 VNPay    │               │               │               │             │
│ FS-4 Homepage │               │               │               │             │
│ FS-5 Admin    │               │               │               │             │
│ FS-6 Blog     │               │               │               │             │
│ FS-8 Category │               │               │               │             │
└───────────────┴───────────────┴───────────────┴───────────────┴─────────────┘
```

---

## 🚨 Quy Tắc Tránh Conflict

### ✅ Nguyên Tắc Vàng

| Rule | Mô tả |
|------|-------|
| **1 File = 1 Người** | Không ai sửa file đã assign cho người khác |
| **Merge Daily** | Pull develop mỗi ngày để tránh diverge |
| **Small Commits** | Commit nhỏ, thường xuyên, dễ merge |
| **Communicate** | Comment trên Jira nếu cần sửa file chung |

### ⚠️ Khi PHẢI Sửa Cùng File

Nếu 2 người cần sửa cùng 1 file (ví dụ: `App.vue`):

```bash
# 1. Thông báo trước trên Jira/Slack
"@PersonB Tôi cần sửa App.vue để thêm route, bạn commit trước đi"

# 2. Người đầu tiên hoàn thành → Merge → Thông báo
"@PersonC Done! Đã merge, bạn pull về và tiếp tục"

# 3. Người sau pull và tiếp tục
git checkout develop && git pull
git checkout my-branch && git merge develop
# Tiếp tục sửa App.vue
```

---

## 📱 Daily Standup (15 phút)

Mỗi người trả lời 3 câu:

```
👤 Người A:
- Hôm qua: Hoàn thành JWT validation (FS-1)
- Hôm nay: Tiếp tục Google OAuth (FS-1)
- Blockers: Cần API key Google từ PM

👤 Người B:
- Hôm qua: Xong CheckoutPage layout (FS-3)
- Hôm nay: Integrate với backend payment API (FS-3)
- Blockers: Chờ Người A xong endpoint /api/payment

👤 Người C:
- Hôm qua: Hoàn thành ProductController (FS-7)
- Hôm nay: Test và tạo PR để merge (FS-7)
- Blockers: Không có
```

---

## 📝 Template Commit Message

```bash
# Format chuẩn:
# FS-[number] [type]: [short description]

# Types:
# - feat: Tính năng mới
# - fix: Sửa bug
# - refactor: Tái cấu trúc
# - test: Thêm test
# - docs: Documentation
# - style: UI/CSS changes

# Examples:
git commit -m "FS-1 feat: Add JWT authentication filter"
git commit -m "FS-3 style: Improve payment button hover effect"
git commit -m "FS-7 fix: Handle null product image"
git commit -m "FS-2 test: Add unit tests for VNPayService"
git commit -m "FS-8 refactor: Extract category validation logic"
```

---

## 🎯 Checklist Hoàn Thành Task

```bash
# Trước khi tạo PR, kiểm tra:

□ Code chạy được locally
□ Không có lỗi compile
□ Đã test manual các chức năng liên quan
□ Đã merge develop mới nhất vào branch
□ Không có conflict
□ Commit messages có Jira key (FS-X)
□ Đã update Jira status → TEST

# Khi tạo PR:
□ Title có Jira key: "FS-7 Add Product CRUD functionality"
□ Description có link Jira và summary
□ Assign reviewer (team member khác)
□ Link PR vào Jira task
```

---

> [!IMPORTANT]
> **Nguyên tắc quan trọng nhất**: Mỗi người có folder/file riêng của mình. Nếu cần sửa file của người khác, phải **trao đổi trước** để tránh conflict!
