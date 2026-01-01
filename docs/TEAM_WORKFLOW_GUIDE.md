# 🌸 Hướng Dẫn Làm Việc Nhóm - Flower Shop
## 2 Chức năng: Đăng nhập Google + Thanh toán VNPay

---

##  Phân Công 3 Người

| Người | Vai trò | Phạm vi |
|-------|---------|---------|
| **Người 1** | Leader | Commit đầu tiên (full project) + Backend Google OAuth |
| **Người 2** | Frontend | Frontend Google Login + VNPay UI |
| **Người 3** | Backend | VNPay Backend |

---

## � NGƯỜI 1 - Leader (Commit đầu tiên)

### Commit 1: Initial Project Setup
```bash
git init
git add .
git commit -m "FS-1 Initial project setup - Full Flower Shop structure"
git push origin main
```

**Files commit (TRỪ files của Người 2, 3):**

```
📂 flower_shop/
├── .env
├── .gitignore
├── docker-compose.yml
├── README.md
│
├── 📂 flower-shop/ (Backend)
│   ├── pom.xml
│   ├── FlowerShopApplication.java
│   ├── config/
│   │   ├── CorsConfig.java ✅
│   │   ├── DataInitializer.java ✅
│   │   ├── DataSeeder.java ✅
│   │   ├── SecurityConfig.java ✅
│   │   └── WebConfig.java ✅
│   ├── controller/
│   │   ├── AdminController.java ✅
│   │   ├── AuthController.java ✅
│   │   ├── BlogController.java ✅
│   │   ├── CategoryController.java ✅
│   │   ├── OrderController.java ✅
│   │   └── ProductController.java ✅
│   ├── dto/ (ALL) ✅
│   ├── exception/ (ALL) ✅
│   ├── model/ (ALL) ✅
│   ├── repository/ (ALL) ✅
│   ├── security/ (ALL - trừ oauth2/) ✅
│   │   ├── CustomUserDetailsService.java ✅
│   │   ├── RestAuthenticationEntryPoint.java ✅
│   │   ├── TokenAuthenticationFilter.java ✅
│   │   ├── TokenProvider.java ✅
│   │   └── UserPrincipal.java ✅
│   └── service/
│       ├── AdminService.java ✅
│       ├── BlogService.java ✅
│       ├── CategoryService.java ✅
│       ├── ImageStorageService.java ✅
│       └── ProductService.java ✅
│
└── 📂 frontend/
    ├── package.json ✅
    ├── vite.config.js ✅
    └── src/
        ├── App.vue ✅
        ├── main.js ✅
        ├── router/index.js ✅
        └── components/
            ├── AdminDashboard.vue ✅
            ├── AdminLogin.vue ✅
            ├── AdminBlogManagement.vue ✅
            ├── BlogDetail.vue ✅
            ├── BlogList.vue ✅
            ├── HelloWorld.vue ✅
            ├── HomePage.vue ✅
            ├── ImageUploader.vue ✅
            ├── ProductDetail.vue ✅
            ├── ShoppingCart.vue ✅
            └── SiteNavbar.vue ✅
```

### Commit 2-4: Google OAuth Backend
```bash
git checkout -b feature/FS-2-google-oauth-backend

git commit -m "FS-2 Add OAuth2 security configuration"
# Files: security/oauth2/*

git commit -m "FS-2 Implement OAuth2 user services"
# Files: CustomOAuth2UserService.java, CustomOidcUserService.java

git commit -m "FS-2 Add OAuth2 handlers and utilities"
# Files: OAuth2AuthenticationSuccessHandler.java, etc.
```

**Files của Người 1 (Google OAuth Backend):**
```
📂 security/oauth2/
├── CustomOAuth2UserService.java ✅
├── CustomOidcUserService.java ✅
├── HttpCookieOAuth2AuthorizationRequestRepository.java ✅
├── OAuth2AuthenticationFailureHandler.java ✅
├── OAuth2AuthenticationProcessingException.java ✅
├── OAuth2AuthenticationSuccessHandler.java ✅
└── user/
    ├── GoogleOAuth2UserInfo.java ✅
    ├── OAuth2UserInfo.java ✅
    └── OAuth2UserInfoFactory.java ✅
```

---

## 🎨 NGƯỜI 2 - Frontend

### Branch & Commits
```bash
git checkout develop
git pull origin develop
git checkout -b feature/FS-3-frontend-auth-payment

# Commit 1: Google Login UI
git commit -m "FS-3 Create UserLogin component with Google button"

# Commit 2: OAuth Redirect Handler
git commit -m "FS-3 Add OAuth2RedirectHandler for callback"

# Commit 3: User Register
git commit -m "FS-3 Create UserRegister component"

# Commit 4: Checkout Page
git commit -m "FS-6 Create CheckoutPage with VNPay option"

# Commit 5: Payment Result
git commit -m "FS-6 Add PaymentResult page"

git push origin feature/FS-3-frontend-auth-payment
```

**Files của Người 2:**
```
📂 frontend/src/components/
├── UserLogin.vue ✅           (Login + nút Google)
├── UserRegister.vue ✅        (Đăng ký)
├── OAuth2RedirectHandler.vue ✅ (Xử lý callback OAuth)
├── CheckoutPage.vue ✅        (Thanh toán + nút VNPay)
└── PaymentResult.vue ✅       (Kết quả thanh toán)
```

---

## � NGƯỜI 3 - VNPay Backend

### Branch & Commits
```bash
git checkout develop
git pull origin develop
git checkout -b feature/FS-5-vnpay-backend

# Commit 1: VNPay Config
git commit -m "FS-5 Add VNPay configuration"

# Commit 2: VNPay Service
git commit -m "FS-5 Implement VNPayService with payment logic"

# Commit 3: Payment Controller
git commit -m "FS-5 Create PaymentController endpoints"

git push origin feature/FS-5-vnpay-backend
```

**Files của Người 3:**
```
📂 Backend:
├── config/VNPayConfig.java ✅
├── service/VNPayService.java ✅
└── controller/PaymentController.java ✅
```

---

## 📅 Timeline Commit

```
NGÀY 1: Người 1 commit Initial Project → Push lên main → Tạo develop branch
        ↓
NGÀY 2: Người 1 tạo branch feature/FS-2-google-oauth-backend
        Người 2 tạo branch feature/FS-3-frontend-auth-payment  
        Người 3 tạo branch feature/FS-5-vnpay-backend
        ↓
NGÀY 3-5: Mỗi người commit vào branch của mình
        ↓
NGÀY 6: Merge tất cả vào develop
        ↓
NGÀY 7: Test → Merge develop vào main
```

---

## � Tổng Hợp Files

| Người | Branch | Files |
|-------|--------|-------|
| **Người 1** | `main` + `feature/FS-2-google-oauth-backend` | Full project + `security/oauth2/*` |
| **Người 2** | `feature/FS-3-frontend-auth-payment` | `UserLogin.vue`, `UserRegister.vue`, `OAuth2RedirectHandler.vue`, `CheckoutPage.vue`, `PaymentResult.vue` |
| **Người 3** | `feature/FS-5-vnpay-backend` | `VNPayConfig.java`, `VNPayService.java`, `PaymentController.java` |

---

## ⚠️ Chú Ý Quan Trọng

> **Người 1** commit TRƯỚC, push lên main/develop
> 
> **Người 2, 3** clone về, tạo branch từ develop, CHỈ sửa files được phân công
>
> Không ai sửa files của người khác → **KHÔNG CONFLICT**
