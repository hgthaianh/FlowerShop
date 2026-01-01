# 📸 ImageUploader Component - Hướng dẫn sử dụng

## 🎯 Tổng quan

Component `ImageUploader.vue` là một component tái sử dụng để quản lý hình ảnh sản phẩm với đầy đủ tính năng:
- ✅ Upload từ URL
- ✅ Upload file từ máy
- ✅ Preview ảnh
- ✅ Xóa ảnh (existing và new)
- ✅ Validation (file type, size)
- ✅ Error handling
- ✅ Hỗ trợ single/multiple images

## 📦 Cài đặt

```javascript
import ImageUploader from './ImageUploader.vue';
```

## 🔧 Props

| Prop | Type | Default | Mô tả |
|------|------|---------|-------|
| `label` | String | `'Hình ảnh'` | Tiêu đề hiển thị |
| `existingImages` | Array | `[]` | Danh sách ảnh đã có (từ server) |
| `allowMultiple` | Boolean | `true` | Cho phép chọn nhiều ảnh |
| `acceptedTypes` | String | `'image/*'` | Loại file chấp nhận |
| `maxFileSize` | Number | `5242880` | Kích thước file tối đa (5MB) |
| `defaultMode` | String | `'file'` | Mode mặc định: `'file'` hoặc `'url'` |

## 📤 Events

| Event | Payload | Mô tả |
|-------|---------|-------|
| `update:images` | `Array` | Emit khi có ảnh mới được thêm |
| `delete:image` | `Number/String` | Emit khi xóa ảnh existing (truyền imageId) |

## 💡 Cách sử dụng

### 1️⃣ Ví dụ đơn giản - Single Image

```vue
<template>
  <ImageUploader
    label="Ảnh đại diện"
    :existing-images="mainImage"
    :allow-multiple="false"
    @update:images="handleMainImageUpdate"
    @delete:image="handleDeleteMainImage"
  />
</template>

<script setup>
import { ref } from 'vue';
import ImageUploader from './ImageUploader.vue';

const mainImage = ref([]);
const newMainImage = ref([]);

const handleMainImageUpdate = (images) => {
  newMainImage.value = images;
  console.log('New main image:', images[0]);
};

const handleDeleteMainImage = async (imageId) => {
  // Xóa ảnh từ server
  await deleteImageAPI(imageId);
  mainImage.value = [];
};
</script>
```

### 2️⃣ Ví dụ đầy đủ - Multiple Images

Xem file: `ProductFormExample.vue`

## 📝 Cấu trúc dữ liệu

### Existing Images (từ server)
```javascript
[
  {
    id: 123,                    // ID từ database
    imageUrl: 'https://...',   // URL đầy đủ
    fileName: 'product.jpg',   // Tên file
    displayOrder: 1            // Thứ tự hiển thị
  }
]
```

### New Images (chưa upload)
```javascript
[
  // Từ URL
  {
    id: 1678901234567,         // Timestamp
    imageUrl: 'https://...',   // URL người dùng nhập
    preview: 'https://...',    // Preview URL
    status: 'pending',         // Trạng thái
    source: 'url'              // Nguồn
  },
  
  // Từ File
  {
    id: 1678901234568,
    file: File,                // File object
    fileName: 'image.jpg',     // Tên file
    fileSize: 123456,          // Kích thước (bytes)
    preview: 'data:image/...',// Base64 preview
    status: 'pending',
    source: 'file'
  }
]
```

## 🚀 Quy trình Upload (Best Practice)

```javascript
const handleSubmit = async () => {
  // 1. Tạo/cập nhật sản phẩm trước
  const response = await axios.post('/api/products', productData);
  const productId = response.data.id;

  // 2. Upload ảnh đại diện (nếu có)
  if (newMainImages.value.length > 0) {
    const mainImg = newMainImages.value[0];
    
    if (mainImg.source === 'file') {
      const formData = new FormData();
      formData.append('image', mainImg.file);
      await axios.post(`/api/products/${productId}/images/main`, formData);
    } else if (mainImg.source === 'url') {
      await axios.post(`/api/products/${productId}/images/main-url`, {
        imageUrl: mainImg.imageUrl
      });
    }
  }

  // 3. Upload ảnh bổ sung (loop qua từng ảnh)
  for (const img of newAdditionalImages.value) {
    if (img.source === 'file') {
      const formData = new FormData();
      formData.append('image', img.file);
      await axios.post(`/api/products/${productId}/images`, formData);
    } else if (img.source === 'url') {
      await axios.post(`/api/products/${productId}/images/url`, {
        imageUrl: img.imageUrl
      });
    }
  }
};
```

## ⚠️ Lưu ý quan trọng

1. **Validation**: Component đã validate file type và size, nhưng backend cũng nên validate lại
2. **Error Handling**: Bắt lỗi từ API và hiển thị cho user
3. **Performance**: Với nhiều ảnh lớn, nên upload tuần tự (loop) thay vì parallel
4. **Memory**: Preview base64 tốn RAM, nên giới hạn `maxFileSize`
5. **Cleanup**: Nhớ gọi `clearNewImages()` sau khi submit thành công

## 🎨 Customization

### Thay đổi style
```vue
<ImageUploader
  class="my-custom-uploader"
  ...
/>

<style>
.my-custom-uploader .image-card {
  border-radius: 12px;
}
</style>
```

### Thay đổi validation
```vue
<ImageUploader
  accepted-types="image/jpeg,image/png"
  :max-file-size="10 * 1024 * 1024"
  ...
/>
```

## 🐛 Troubleshooting

### Ảnh không preview được
- Kiểm tra URL có hợp lệ không
- Kiểm tra CORS nếu ảnh từ domain khác
- Kiểm tra file type có đúng `image/*`

### Upload nhiều ảnh bị lỗi
- Đảm bảo loop tuần tự (await trong for loop)
- Check backend có nhận đúng FormData không
- Xem console log để debug

### Memory leak khi upload nhiều ảnh
- Giảm `maxFileSize`
- Limit số lượng ảnh được chọn
- Clear preview sau khi upload xong

## 📚 API Backend cần thiết

```javascript
// Upload file - Main Image
POST /api/products/:id/images/main
Content-Type: multipart/form-data
Body: { image: File }

// Upload URL - Main Image  
POST /api/products/:id/images/main-url
Content-Type: application/json
Body: { imageUrl: "https://..." }

// Upload file - Additional Image
POST /api/products/:id/images
Content-Type: multipart/form-data
Body: { image: File }

// Upload URL - Additional Image
POST /api/products/:id/images/url
Content-Type: application/json
Body: { imageUrl: "https://..." }

// Delete Image
DELETE /api/products/:productId/images/:imageId
```

## ✅ Checklist tích hợp vào AdminDashboard

- [ ] Import component `ImageUploader`
- [ ] Thay thế code cũ bằng component mới
- [ ] Setup `existingImages` và `newImages` refs
- [ ] Implement `@update:images` handler
- [ ] Implement `@delete:image` handler  
- [ ] Update `submitProduct()` logic để upload ảnh
- [ ] Test create product
- [ ] Test edit product
- [ ] Test delete image
- [ ] Test validation errors
- [ ] Cleanup code cũ không dùng

## 🎓 Demo

Xem file `ProductFormExample.vue` để xem ví dụ hoàn chỉnh về cách sử dụng component này trong form tạo/sửa sản phẩm.

---

**Tác giả**: GitHub Copilot  
**Phiên bản**: 1.0.0  
**Ngày cập nhật**: 11/11/2025
