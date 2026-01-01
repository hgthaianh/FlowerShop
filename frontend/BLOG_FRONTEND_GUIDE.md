# Blog Module - Frontend Integration Guide

## 🎯 Tổng quan

Module Blog đã được tích hợp hoàn chỉnh vào frontend Vue 3, bao gồm:
- **Giao diện người dùng**: Xem danh sách blog và chi tiết blog
- **Giao diện admin**: Quản lý blog (CRUD operations)

---

## 📁 Các file đã tạo/cập nhật

### 1. API Configuration
**File**: `src/config/api.js`

Đã thêm các endpoints cho Blog:
```javascript
blogs: {
  getAll: () => `${API_BASE_URL}/blogs`,
  getById: (id) => `${API_BASE_URL}/blogs/${id}`,
  search: (keyword) => `${API_BASE_URL}/blogs?search=${keyword}`,
  
  // Admin endpoints
  getAllAdmin: () => `${API_BASE_URL}/admin/blogs`,
  create: () => `${API_BASE_URL}/admin/blogs`,
  update: (id) => `${API_BASE_URL}/admin/blogs/${id}`,
  delete: (id) => `${API_BASE_URL}/admin/blogs/${id}`,
  publish: (id) => `${API_BASE_URL}/admin/blogs/${id}/publish`,
  unpublish: (id) => `${API_BASE_URL}/admin/blogs/${id}/unpublish`,
  getByAuthor: (authorId) => `${API_BASE_URL}/admin/blogs/author/${authorId}`,
}
```

### 2. Public Components

#### `src/components/BlogList.vue`
- Hiển thị danh sách blog đã publish
- Tìm kiếm blog theo title/content
- Card layout với hình ảnh, tác giả, ngày tạo
- Responsive design

**Features:**
- ✅ Search functionality
- ✅ Loading state
- ✅ Empty state
- ✅ Image fallback
- ✅ Format date (Vietnamese)
- ✅ Click to view detail

#### `src/components/BlogDetail.vue`
- Hiển thị chi tiết blog
- Responsive layout
- Share buttons (Facebook, Twitter, Copy link)
- Back navigation
- Show author và timestamps

**Features:**
- ✅ Full blog content display
- ✅ Image header
- ✅ Author information
- ✅ Created/Updated dates
- ✅ Social sharing
- ✅ Error handling

### 3. Admin Components

#### `src/components/AdminBlogManagement.vue`
Component quản lý blog cho admin với đầy đủ tính năng CRUD.

**Features:**
- ✅ Danh sách blog (bảng)
- ✅ Tìm kiếm blog
- ✅ Filter theo trạng thái (Published/Draft)
- ✅ Tạo blog mới (modal form)
- ✅ Chỉnh sửa blog (modal form)
- ✅ Xóa blog (confirmation)
- ✅ Publish/Unpublish blog
- ✅ Image preview
- ✅ Character counter
- ✅ Form validation

**Form Fields:**
- Title (required, max 255)
- Image URL
- Summary (optional, max 500)
- Content (required, textarea)
- Status (DRAFT/PUBLISHED)

### 4. Router Configuration
**File**: `src/router/index.js`

Đã thêm routes:
```javascript
{
  path: '/blogs',
  name: 'BlogList',
  component: BlogList
},
{
  path: '/blogs/:id',
  name: 'BlogDetail',
  component: BlogDetail
}
```

### 5. Navigation
**File**: `src/components/SiteNavbar.vue`

Đã thêm link Blog vào navbar:
```vue
<router-link to="/blogs" class="nav-link" active-class="active">
  <i class="icon">📝</i>
  <span>Blog</span>
</router-link>
```

### 6. Admin Dashboard Integration
**File**: `src/components/AdminDashboard.vue`

Đã tích hợp AdminBlogManagement vào dashboard:
```vue
<section id="blogs" class="panel">
  <AdminBlogManagement />
</section>
```

---

## 🚀 Cách sử dụng

### User (Public)

#### 1. Xem danh sách blog
```
URL: http://localhost:8080/blogs
```
- Hiển thị tất cả blog đã publish
- Tìm kiếm bài viết
- Click để xem chi tiết

#### 2. Xem chi tiết blog
```
URL: http://localhost:8080/blogs/:id
```
- Hiển thị nội dung đầy đủ
- Chia sẻ lên mạng xã hội
- Quay lại danh sách

### Admin

#### 1. Truy cập quản lý blog
```
URL: http://localhost:8080/admin/dashboard
```
Sau khi login, scroll xuống section "Blog Management"

#### 2. Tạo blog mới
1. Click nút "Tạo bài viết mới"
2. Điền form:
   - Tiêu đề (bắt buộc)
   - URL hình ảnh (tùy chọn)
   - Tóm tắt (tùy chọn, max 500 ký tự)
   - Nội dung (bắt buộc)
   - Trạng thái: Draft hoặc Published
3. Click "Tạo mới"

#### 3. Chỉnh sửa blog
1. Click icon ✏️ ở hàng blog cần sửa
2. Cập nhật thông tin trong form
3. Click "Cập nhật"

#### 4. Publish/Unpublish
- Click icon ⬆️ để publish blog (từ Draft → Published)
- Click icon ⬇️ để unpublish blog (từ Published → Draft)

#### 5. Xóa blog
- Click icon 🗑️
- Xác nhận xóa (không thể hoàn tác)

#### 6. Tìm kiếm và lọc
- Search box: Tìm theo title hoặc content
- Dropdown: Lọc theo trạng thái (All/Published/Draft)

---

## 🎨 UI/UX Features

### Design System
- **Theme**: Pink pastel (consistent với hệ thống)
- **Colors**:
  - Primary: `var(--pink-500)` - #f36da1
  - Secondary: `var(--peach-500)` - #ff9466
  - Background: Pink gradient
- **Typography**: Be Vietnam Pro font
- **Components**: Card-based design

### Responsive
- ✅ Desktop: Multi-column grid
- ✅ Tablet: 2 columns
- ✅ Mobile: Single column

### Animations
- ✅ Hover effects
- ✅ Loading spinners
- ✅ Smooth transitions
- ✅ Modal animations

### User Experience
- ✅ Loading states
- ✅ Empty states
- ✅ Error handling
- ✅ Image fallbacks
- ✅ Confirmation dialogs
- ✅ Success/Error alerts

---

## 🧪 Testing

### Test Scenarios

#### Public Pages
1. **BlogList.vue**
   ```bash
   # Test cases:
   - Load all published blogs
   - Search for blogs
   - Click on blog card → navigate to detail
   - Empty state when no blogs
   - Image fallback on error
   ```

2. **BlogDetail.vue**
   ```bash
   # Test cases:
   - View blog detail
   - Share buttons work
   - Back button navigates correctly
   - Error page when blog not found
   - Image fallback
   ```

#### Admin Pages
1. **AdminBlogManagement.vue**
   ```bash
   # Test cases:
   - Load all blogs (including drafts)
   - Create new blog
   - Edit existing blog
   - Delete blog
   - Publish/unpublish blog
   - Search blogs
   - Filter by status
   - Form validation
   - Image preview
   ```

### Manual Testing Steps

1. **Start backend**:
   ```bash
   cd flower-shop
   mvn spring-boot:run
   ```

2. **Start frontend**:
   ```bash
   cd frontend
   npm run serve
   ```

3. **Test public pages**:
   - Navigate to http://localhost:8080/blogs
   - Test search
   - Click on a blog
   - Test share buttons

4. **Test admin pages**:
   - Login as admin
   - Go to dashboard
   - Scroll to Blog section
   - Test CRUD operations

---

## 🔧 Customization

### Thay đổi theme colors
**File**: `src/App.vue` (root CSS variables)
```css
:root {
  --pink-500: #your-color;
  --peach-500: #your-color;
}
```

### Thay đổi pagination
Hiện tại BlogList không có pagination. Để thêm:

1. Add pagination state:
```javascript
const currentPage = ref(1)
const perPage = ref(9)
```

2. Add computed:
```javascript
const paginatedBlogs = computed(() => {
  const start = (currentPage.value - 1) * perPage.value
  return blogs.value.slice(start, start + perPage.value)
})
```

3. Add pagination UI (có thể tham khảo HomePage.vue)

### Thêm Rich Text Editor
Để có trải nghiệm soạn thảo tốt hơn, có thể tích hợp:
- **TinyMCE**: https://www.tiny.cloud/
- **Quill**: https://quilljs.com/
- **CKEditor**: https://ckeditor.com/

Example với TinyMCE:
```bash
npm install @tinymce/tinymce-vue
```

```vue
<template>
  <Editor v-model="formData.content" :init="editorConfig" />
</template>

<script setup>
import Editor from '@tinymce/tinymce-vue'

const editorConfig = {
  height: 500,
  menubar: false,
  plugins: ['lists', 'link', 'image', 'code'],
  toolbar: 'undo redo | bold italic | alignleft aligncenter alignright | bullist numlist'
}
</script>
```

---

## 📊 API Integration Flow

```
User Flow:
1. User visits /blogs
   → BlogList.vue
   → GET /api/blogs
   → Display published blogs

2. User clicks on a blog
   → BlogDetail.vue
   → GET /api/blogs/:id
   → Display blog detail

3. User searches "hoa"
   → BlogList.vue
   → GET /api/blogs?search=hoa
   → Display search results

Admin Flow:
1. Admin logs in
   → AdminDashboard.vue
   → Load AdminBlogManagement.vue

2. Admin creates blog
   → Fill form
   → POST /api/admin/blogs
   → Refresh list

3. Admin publishes blog
   → Click publish
   → PATCH /api/admin/blogs/:id/publish
   → Update status in list

4. Admin edits blog
   → Click edit icon
   → PUT /api/admin/blogs/:id
   → Refresh list

5. Admin deletes blog
   → Click delete
   → Confirm
   → DELETE /api/admin/blogs/:id
   → Remove from list
```

---

## 🚨 Troubleshooting

### Blog không hiển thị
1. Kiểm tra backend đang chạy
2. Kiểm tra API endpoint trong `api.js`
3. Kiểm tra console browser cho errors
4. Verify blog có status PUBLISHED

### Image không hiển thị
- Kiểm tra URL hợp lệ
- CORS có được config đúng không
- Image fallback sẽ tự động load

### Admin không thể tạo blog
1. Kiểm tra đã login admin
2. Kiểm tra form validation
3. Kiểm tra authorId có trong localStorage
4. Xem console cho error message

### Search không hoạt động
1. Kiểm tra backend có implement search endpoint
2. Verify query parameter đúng format
3. Xem network tab trong DevTools

---

## 📝 Next Steps

### Suggested Improvements

1. **Categories cho Blog**
   - Thêm BlogCategory entity
   - Filter blogs by category
   - Category tags

2. **Comments System**
   - User comments
   - Admin moderation
   - Nested replies

3. **Markdown Support**
   - Use markdown editor
   - Render markdown in detail

4. **SEO Optimization**
   - Meta tags
   - Open Graph tags
   - Schema.org markup

5. **Analytics**
   - View count
   - Popular posts
   - Reading time

6. **Featured Posts**
   - Highlight important posts
   - Pin to top
   - Featured badge

---

## ✅ Checklist

- [x] API endpoints configured
- [x] BlogList component created
- [x] BlogDetail component created
- [x] AdminBlogManagement component created
- [x] Router updated
- [x] Navbar updated
- [x] AdminDashboard integrated
- [x] Responsive design
- [x] Error handling
- [x] Loading states
- [x] Image fallbacks
- [x] Form validation

---

## 📞 Support

Nếu gặp vấn đề, kiểm tra:
1. Console browser (F12)
2. Network tab (API calls)
3. Backend logs
4. Database data

Module đã sẵn sàng sử dụng! 🎉
