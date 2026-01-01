<template>
  <div class="image-uploader">
    <label class="uploader-label">{{ label }}</label>
    
    <!-- Upload Mode Selector -->
    <div class="upload-mode-selector">
      <button
        type="button"
        class="mode-btn"
        :class="{ active: uploadMode === 'url' }"
        @click="uploadMode = 'url'"
      >
        🔗 Từ URL
      </button>
      <button
        type="button"
        class="mode-btn"
        :class="{ active: uploadMode === 'file' }"
        @click="uploadMode = 'file'"
      >
        📁 Tải lên từ máy
      </button>
    </div>

    <!-- URL Input Mode -->
    <div v-if="uploadMode === 'url'" class="url-input-section">
      <input
        v-model="imageUrl"
        type="url"
        placeholder="Nhập URL hình ảnh..."
        class="url-input"
        @keyup.enter="addImageFromUrl"
      />
      <button
        type="button"
        class="add-url-btn"
        @click="addImageFromUrl"
        :disabled="!imageUrl.trim()"
      >
        Thêm URL
      </button>
    </div>

    <!-- File Upload Mode -->
    <div v-if="uploadMode === 'file'" class="file-upload-section">
      <input
        ref="fileInput"
        type="file"
        accept="image/*"
        :multiple="allowMultiple"
        @change="handleFileSelect"
        class="file-input"
      />
      <div class="file-info">
        <small>Chấp nhận: image/*. Kích thước tối đa: 5 MB</small>
      </div>
    </div>

    <!-- Existing Images Display -->
    <div v-if="existingImages.length > 0" class="images-section">
      <h4 class="section-title">Chưa có hình ảnh nào</h4>
      <div class="images-grid">
        <div
          v-for="img in existingImages"
          :key="img.id"
          class="image-item existing"
        >
          <div class="image-preview">
            <img :src="img.imageUrl" :alt="img.fileName || 'Image'" />
          </div>
          <div class="image-info">
            <span class="image-name">{{ img.fileName || 'Existing Image' }}</span>
            <span class="image-badge existing-badge">Đã lưu</span>
          </div>
          <button
            type="button"
            class="delete-btn"
            @click="deleteExistingImage(img.id)"
            title="Xóa hình ảnh"
          >
            🗑️
          </button>
        </div>
      </div>
    </div>

    <!-- New Images to Upload -->
    <div v-if="newImages.length > 0" class="images-section">
      <h4 class="section-title">📤 Hình ảnh mới (chưa lưu)</h4>
      <div class="images-grid">
        <div
          v-for="(img, index) in newImages"
          :key="index"
          class="image-item new"
        >
          <div class="image-preview">
            <img :src="img.preview || img.url" :alt="img.name || 'New Image'" />
          </div>
          <div class="image-info">
            <span class="image-name">{{ img.name || img.url }}</span>
            <span class="image-badge new-badge">Mới</span>
          </div>
          <button
            type="button"
            class="delete-btn"
            @click="removeNewImage(index)"
            title="Xóa hình ảnh"
          >
            ✖️
          </button>
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div
      v-if="existingImages.length === 0 && newImages.length === 0"
      class="empty-state"
    >
      Chưa có hình ảnh nào
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';

const props = defineProps({
  label: {
    type: String,
    default: 'Hình ảnh'
  },
  existingImages: {
    type: Array,
    default: () => []
  },
  allowMultiple: {
    type: Boolean,
    default: false
  },
  defaultMode: {
    type: String,
    default: 'file',
    validator: (value) => ['url', 'file'].includes(value)
  }
});

const emit = defineEmits(['update:images', 'delete:image']);

// State
const uploadMode = ref(props.defaultMode);
const imageUrl = ref('');
const fileInput = ref(null);
const newImages = ref([]);

// Watch for existing images changes
watch(() => props.existingImages, () => {
  // Just react to prop changes, don't modify
}, { deep: true });

// Add image from URL
const addImageFromUrl = () => {
  if (!imageUrl.value.trim()) return;
  
  const newImage = {
    url: imageUrl.value,
    name: imageUrl.value,
    source: 'url'
  };
  
  if (props.allowMultiple) {
    newImages.value.push(newImage);
  } else {
    newImages.value = [newImage];
  }
  
  imageUrl.value = '';
  emitUpdate();
};

// Handle file selection
const handleFileSelect = (event) => {
  const files = Array.from(event.target.files);
  
  if (!props.allowMultiple && files.length > 1) {
    alert('Chỉ được chọn 1 hình ảnh');
    return;
  }
  
  // Validate file size (5MB max)
  const maxSize = 5 * 1024 * 1024; // 5MB in bytes
  const invalidFiles = files.filter(file => file.size > maxSize);
  
  if (invalidFiles.length > 0) {
    alert('Một số file vượt quá 5MB. Vui lòng chọn file nhỏ hơn.');
    return;
  }
  
  // Process files
  files.forEach(file => {
    const reader = new FileReader();
    reader.onload = (e) => {
      const newImage = {
        file: file,
        name: file.name,
        preview: e.target.result,
        source: 'file'
      };
      
      if (props.allowMultiple) {
        newImages.value.push(newImage);
      } else {
        newImages.value = [newImage];
      }
      
      emitUpdate();
    };
    reader.readAsDataURL(file);
  });
  
  // Reset input
  event.target.value = '';
};

// Remove new image
const removeNewImage = (index) => {
  newImages.value.splice(index, 1);
  emitUpdate();
};

// Delete existing image
const deleteExistingImage = (imageId) => {
  if (confirm('Bạn có chắc muốn xóa hình ảnh này?')) {
    emit('delete:image', imageId);
  }
};

// Emit update to parent
const emitUpdate = () => {
  emit('update:images', newImages.value);
};

// Expose method to clear new images (called from parent)
const clearNewImages = () => {
  newImages.value = [];
  if (fileInput.value) {
    fileInput.value.value = '';
  }
};

defineExpose({
  clearNewImages
});
</script>

<style scoped>
.image-uploader {
  width: 100%;
}

.uploader-label {
  display: block;
  font-weight: 600;
  margin-bottom: 12px;
  color: var(--pink-600);
  font-size: 1rem;
}

.upload-mode-selector {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  background: white;
  border-radius: 8px;
  padding: 4px;
  border: 1px solid var(--pink-200);
}

.mode-btn {
  flex: 1;
  padding: 8px 16px;
  border: none;
  background: transparent;
  color: var(--pink-600);
  font-weight: 500;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.mode-btn:hover {
  background: rgba(244, 114, 182, 0.1);
}

.mode-btn.active {
  background: var(--pink-500);
  color: white;
}

.url-input-section {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.url-input {
  flex: 1;
  padding: 10px 14px;
  border: 2px solid var(--pink-200);
  border-radius: 8px;
  font-size: 0.95rem;
  transition: all 0.2s;
}

.url-input:focus {
  outline: none;
  border-color: var(--pink-500);
  box-shadow: 0 0 0 3px rgba(244, 114, 182, 0.1);
}

.add-url-btn {
  padding: 10px 20px;
  background: var(--pink-500);
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.add-url-btn:hover:not(:disabled) {
  background: var(--pink-600);
  transform: translateY(-1px);
}

.add-url-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.file-upload-section {
  margin-bottom: 16px;
}

.file-input {
  width: 100%;
  padding: 10px;
  border: 2px dashed var(--pink-300);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.file-input:hover {
  border-color: var(--pink-500);
  background: rgba(244, 114, 182, 0.05);
}

.file-info {
  margin-top: 8px;
}

.file-info small {
  color: var(--pink-400);
  font-size: 0.85rem;
}

.images-section {
  margin-top: 20px;
}

.section-title {
  font-size: 0.9rem;
  font-weight: 600;
  color: var(--pink-600);
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.images-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 12px;
}

.image-item {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid var(--pink-200);
  transition: all 0.2s;
}

.image-item:hover {
  border-color: var(--pink-400);
  box-shadow: 0 4px 12px rgba(244, 114, 182, 0.2);
  transform: translateY(-2px);
}

.image-item.existing {
  border-color: var(--green-300);
}

.image-item.new {
  border-color: var(--blue-300);
}

.image-preview {
  width: 100%;
  height: 120px;
  overflow: hidden;
  background: var(--gray-100);
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-info {
  padding: 8px;
  background: white;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.image-name {
  font-size: 0.8rem;
  color: var(--gray-700);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.image-badge {
  font-size: 0.75rem;
  padding: 2px 8px;
  border-radius: 4px;
  width: fit-content;
  font-weight: 500;
}

.existing-badge {
  background: var(--green-100);
  color: var(--green-700);
}

.new-badge {
  background: var(--blue-100);
  color: var(--blue-700);
}

.delete-btn {
  position: absolute;
  top: 6px;
  right: 6px;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  border: none;
  background: rgba(239, 68, 68, 0.9);
  color: white;
  font-size: 0.85rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  opacity: 0;
}

.image-item:hover .delete-btn {
  opacity: 1;
}

.delete-btn:hover {
  background: rgba(220, 38, 38, 1);
  transform: scale(1.1);
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: var(--pink-400);
  font-size: 0.95rem;
  background: rgba(244, 114, 182, 0.05);
  border: 2px dashed var(--pink-200);
  border-radius: 8px;
}

/* CSS Variables */
:root {
  --pink-100: #fce7f3;
  --pink-200: #fbcfe8;
  --pink-300: #f9a8d4;
  --pink-400: #f472b6;
  --pink-500: #ec4899;
  --pink-600: #db2777;
  --green-100: #d1fae5;
  --green-300: #6ee7b7;
  --green-700: #047857;
  --blue-100: #dbeafe;
  --blue-300: #93c5fd;
  --blue-700: #1d4ed8;
  --gray-100: #f3f4f6;
  --gray-700: #374151;
}
</style>
