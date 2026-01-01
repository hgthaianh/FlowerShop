<template>
  <div class="blog-management">
    <div class="management-header">
      <h2>
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
          <path
            d="M19 3H5C3.89543 3 3 3.89543 3 5V19C3 20.1046 3.89543 21 5 21H19C20.1046 21 21 20.1046 21 19V5C21 3.89543 20.1046 3 19 3Z"
            stroke="currentColor"
            stroke-width="2"
          />
          <path
            d="M7 7H17M7 11H17M7 15H13"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
          />
        </svg>
        Quản lý Blog
      </h2>
      <button @click="openCreateModal" class="add-btn">
        <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
          <path
            d="M10 5V15M5 10H15"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
          />
        </svg>
        Tạo bài viết mới
      </button>
    </div>

    <!-- Search and Filter -->
    <div class="filter-bar">
      <div class="search-box">
        <svg
          width="18"
          height="18"
          viewBox="0 0 18 18"
          fill="none"
          class="search-icon"
        >
          <circle cx="8" cy="8" r="6" stroke="currentColor" stroke-width="2" />
          <path
            d="M17 17L13 13"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
          />
        </svg>
        <input
          v-model="searchQuery"
          @input="filterBlogs"
          type="text"
          placeholder="Tìm kiếm bài viết..."
        />
      </div>
      <select
        v-model="statusFilter"
        @change="filterBlogs"
        class="status-filter"
      >
        <option value="">Tất cả trạng thái</option>
        <option value="PUBLISHED">Đã xuất bản</option>
        <option value="DRAFT">Bản nháp</option>
      </select>
    </div>

    <!-- Blogs Table -->
    <div class="blogs-table-container">
      <table class="blogs-table">
        <thead>
          <tr>
            <th>Tiêu đề</th>
            <th>Tác giả</th>
            <th>Trạng thái</th>
            <th>Ngày tạo</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="5" class="loading-cell">
              <div class="spinner"></div>
              Đang tải...
            </td>
          </tr>
          <tr v-else-if="filteredBlogs.length === 0">
            <td colspan="5" class="empty-cell">Không có bài viết nào</td>
          </tr>
          <tr v-else v-for="blog in filteredBlogs" :key="blog.id">
            <td class="title-cell">
              <div class="blog-title-wrapper">
                <span class="blog-title">{{ blog.title }}</span>
                <span class="blog-summary">{{
                  truncate(blog.summary || blog.content, 60)
                }}</span>
              </div>
            </td>
            <td>{{ blog.authorUsername || "N/A" }}</td>
            <td>
              <span :class="['status-badge', blog.status.toLowerCase()]">
                {{ blog.status === "PUBLISHED" ? "Đã xuất bản" : "Bản nháp" }}
              </span>
            </td>
            <td>{{ formatDate(blog.createdAt) }}</td>
            <td class="actions-cell">
              <div class="action-buttons">
                <button
                  v-if="blog.status === 'DRAFT'"
                  @click="publishBlog(blog.id)"
                  class="action-btn publish"
                  title="Xuất bản"
                >
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                    <path
                      d="M8 2V8M8 8L11 5M8 8L5 5M3 14H13"
                      stroke="currentColor"
                      stroke-width="1.5"
                      stroke-linecap="round"
                    />
                  </svg>
                </button>
                <button
                  v-else
                  @click="unpublishBlog(blog.id)"
                  class="action-btn unpublish"
                  title="Gỡ xuất bản"
                >
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                    <path
                      d="M8 14V8M8 8L5 11M8 8L11 11M3 2H13"
                      stroke="currentColor"
                      stroke-width="1.5"
                      stroke-linecap="round"
                    />
                  </svg>
                </button>
                <button
                  @click="openEditModal(blog)"
                  class="action-btn edit"
                  title="Sửa"
                >
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                    <path
                      d="M11.333 2A1.886 1.886 0 0114 4.667l-9 9-3.667 1 1-3.667 9-9z"
                      stroke="currentColor"
                      stroke-width="1.5"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                    />
                  </svg>
                </button>
                <button
                  @click="deleteBlog(blog.id)"
                  class="action-btn delete"
                  title="Xóa"
                >
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                    <path
                      d="M2 4h12M5.333 4V2.667a1.333 1.333 0 011.334-1.334h2.666a1.333 1.333 0 011.334 1.334V4m2 0v9.333a1.333 1.333 0 01-1.334 1.334H4.667a1.333 1.333 0 01-1.334-1.334V4h9.334z"
                      stroke="currentColor"
                      stroke-width="1.5"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                    />
                  </svg>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Create/Edit Modal -->
    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ isEditing ? "Chỉnh sửa bài viết" : "Tạo bài viết mới" }}</h3>
          <button @click="closeModal" class="close-btn">✕</button>
        </div>

        <form @submit.prevent="saveBlog" class="blog-form">
          <div class="form-group">
            <label>Tiêu đề <span class="required">*</span></label>
            <input
              v-model="formData.title"
              type="text"
              placeholder="Nhập tiêu đề bài viết"
              required
            />
          </div>

          <div class="form-group">
            <label>URL Hình ảnh</label>
            <input
              v-model="formData.imageUrl"
              type="url"
              placeholder="https://example.com/image.jpg"
            />
            <img
              v-if="formData.imageUrl"
              :src="formData.imageUrl"
              class="image-preview"
              @error="handleImageError"
            />
          </div>

          <div class="form-group">
            <label>Tóm tắt</label>
            <textarea
              v-model="formData.summary"
              placeholder="Tóm tắt ngắn gọn về bài viết (tùy chọn)"
              rows="2"
              maxlength="500"
            ></textarea>
            <span class="char-count"
              >{{ formData.summary?.length || 0 }}/500</span
            >
          </div>

          <div class="form-group">
            <label>Nội dung <span class="required">*</span></label>
            <textarea
              v-model="formData.content"
              placeholder="Nhập nội dung bài viết"
              rows="12"
              required
            ></textarea>
          </div>

          <div class="form-group">
            <label>Trạng thái</label>
            <select v-model="formData.status">
              <option value="DRAFT">Bản nháp</option>
              <option value="PUBLISHED">Xuất bản ngay</option>
            </select>
          </div>

          <div class="form-actions">
            <button type="button" @click="closeModal" class="cancel-btn">
              Hủy
            </button>
            <button type="submit" class="save-btn" :disabled="saving">
              {{ saving ? "Đang lưu..." : isEditing ? "Cập nhật" : "Tạo mới" }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import apiConfig from "../config/api";

// Create authenticated API instance
const authApi = axios.create();

// Add request interceptor
authApi.interceptors.request.use(
  (config) => {
    const adminData = localStorage.getItem("admin");
    if (adminData) {
      const admin = JSON.parse(adminData);
      if (admin.accessToken) {
        config.headers.Authorization = `Bearer ${admin.accessToken}`;
      }
    }
    return config;
  },
  (error) => Promise.reject(error)
);

const blogs = ref([]);
const filteredBlogs = ref([]);
const loading = ref(true);
const saving = ref(false);
const showModal = ref(false);
const isEditing = ref(false);
const searchQuery = ref("");
const statusFilter = ref("");

const formData = ref({
  title: "",
  content: "",
  imageUrl: "",
  summary: "",
  status: "DRAFT",
});

const currentBlogId = ref(null);

const fetchBlogs = async () => {
  try {
    loading.value = true;
    const response = await authApi.get(apiConfig.blogs.getAllAdmin());
    blogs.value = response.data;
    filteredBlogs.value = response.data;
  } catch (error) {
    console.error("Error fetching blogs:", error);
    alert("Không thể tải danh sách bài viết");
  } finally {
    loading.value = false;
  }
};

const filterBlogs = () => {
  let result = [...blogs.value];

  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(
      (blog) =>
        blog.title.toLowerCase().includes(query) ||
        blog.content.toLowerCase().includes(query)
    );
  }

  if (statusFilter.value) {
    result = result.filter((blog) => blog.status === statusFilter.value);
  }

  filteredBlogs.value = result;
};

const openCreateModal = () => {
  isEditing.value = false;
  formData.value = {
    title: "",
    content: "",
    imageUrl: "",
    summary: "",
    status: "DRAFT",
  };

  const admin = JSON.parse(localStorage.getItem("admin") || "{}");
  if (admin.id) {
    formData.value.authorId = admin.id;
  }

  showModal.value = true;
};

const openEditModal = (blog) => {
  isEditing.value = true;
  currentBlogId.value = blog.id;
  formData.value = {
    title: blog.title,
    content: blog.content,
    imageUrl: blog.imageUrl || "",
    summary: blog.summary || "",
    status: blog.status,
  };
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
  formData.value = {
    title: "",
    content: "",
    imageUrl: "",
    summary: "",
    status: "DRAFT",
  };
  currentBlogId.value = null;
};

const saveBlog = async () => {
  try {
    saving.value = true;

    if (isEditing.value) {
      await authApi.put(
        apiConfig.blogs.update(currentBlogId.value),
        formData.value
      );
      alert("Cập nhật bài viết thành công!");
    } else {
      await authApi.post(apiConfig.blogs.create(), formData.value);
      alert("Tạo bài viết mới thành công!");
    }

    closeModal();
    await fetchBlogs();
  } catch (error) {
    console.error("Error saving blog:", error);
    alert(error.response?.data?.message || "Không thể lưu bài viết");
  } finally {
    saving.value = false;
  }
};

const publishBlog = async (id) => {
  if (!confirm("Bạn có chắc muốn xuất bản bài viết này?")) return;

  try {
    await authApi.patch(apiConfig.blogs.publish(id));
    alert("Xuất bản bài viết thành công!");
    await fetchBlogs();
  } catch (error) {
    console.error("Error publishing blog:", error);
    alert("Không thể xuất bản bài viết");
  }
};

const unpublishBlog = async (id) => {
  if (!confirm("Bạn có chắc muốn gỡ xuất bản bài viết này?")) return;

  try {
    await authApi.patch(apiConfig.blogs.unpublish(id));
    alert("Gỡ xuất bản bài viết thành công!");
    await fetchBlogs();
  } catch (error) {
    console.error("Error unpublishing blog:", error);
    alert("Không thể gỡ xuất bản bài viết");
  }
};

const deleteBlog = async (id) => {
  if (
    !confirm(
      "Bạn có chắc muốn xóa bài viết này? Hành động này không thể hoàn tác!"
    )
  )
    return;

  try {
    await authApi.delete(apiConfig.blogs.delete(id));
    alert("Xóa bài viết thành công!");
    await fetchBlogs();
  } catch (error) {
    console.error("Error deleting blog:", error);
    alert("Không thể xóa bài viết");
  }
};

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return new Intl.DateTimeFormat("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  }).format(date);
};

const truncate = (text, length) => {
  if (!text) return "";
  return text.length > length ? text.substring(0, length) + "..." : text;
};

const handleImageError = (e) => {
  e.target.src =
    "https://via.placeholder.com/400x250/FFE1F0/F36DA1?text=Invalid+Image";
};

onMounted(() => {
  fetchBlogs();
});
</script>

<style scoped>
.blog-management {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.management-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.management-header h2 {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 0;
  font-size: 1.5rem;
  color: #333;
}

.add-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  background: var(--pink-500);
  color: white;
  border: none;
  padding: 12px 20px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.add-btn:hover {
  background: var(--pink-600);
  transform: translateY(-2px);
}

.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.search-box {
  width: 50%;
  max-width: 500px;
}

.search-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
}

.search-box input {
  width: 100%;
  padding: 10px 10px 10px 40px;
  border: 2px solid #e5e5e5;
  border-radius: 8px;
  font-size: 0.95rem;
}

.status-filter {
  padding: 10px 16px;
  border: 2px solid #e5e5e5;
  border-radius: 8px;
  font-size: 0.95rem;
  cursor: pointer;
}

.blogs-table-container {
  overflow-x: auto;
}

.blogs-table {
  width: 100%;
  border-collapse: collapse;
}

.blogs-table th {
  background: var(--pink-50);
  padding: 12px;
  text-align: left;
  font-weight: 600;
  color: #666;
  border-bottom: 2px solid var(--pink-200);
}

.blogs-table td {
  padding: 16px 12px;
  border-bottom: 1px solid #f0f0f0;
}

.title-cell {
  max-width: 300px;
}

.blog-title-wrapper {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.blog-title {
  font-weight: 600;
  color: #333;
}

.blog-summary {
  font-size: 0.875rem;
  color: #999;
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.875rem;
  font-weight: 600;
}

.status-badge.published {
  background: #d4edda;
  color: #155724;
}

.status-badge.draft {
  background: #fff3cd;
  color: #856404;
}

.actions-cell {
  white-space: nowrap;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 8px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-btn.publish {
  background: #d4edda;
  color: #155724;
}

.action-btn.unpublish {
  background: #fff3cd;
  color: #856404;
}

.action-btn.edit {
  background: #cfe2ff;
  color: #084298;
}

.action-btn.delete {
  background: #f8d7da;
  color: #842029;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.loading-cell,
.empty-cell {
  text-align: center;
  padding: 48px 20px;
  color: #999;
}

.spinner {
  width: 32px;
  height: 32px;
  border: 3px solid var(--pink-100);
  border-top-color: var(--pink-500);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 12px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  background: white;
  border-radius: 16px;
  width: 100%;
  max-width: 700px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.25rem;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #999;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  transition: all 0.2s;
}

.close-btn:hover {
  background: #f0f0f0;
  color: #333;
}

.blog-form {
  padding: 24px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #333;
}

.required {
  color: #e74c3c;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 10px 12px;
  border: 2px solid #e5e5e5;
  border-radius: 8px;
  font-size: 0.95rem;
  font-family: inherit;
  transition: border-color 0.3s;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  outline: none;
  border-color: var(--pink-500);
}

.image-preview {
  margin-top: 12px;
  width: 100%;
  max-height: 200px;
  object-fit: cover;
  border-radius: 8px;
}

.char-count {
  display: block;
  text-align: right;
  font-size: 0.875rem;
  color: #999;
  margin-top: 4px;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
}

.cancel-btn,
.save-btn {
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.cancel-btn {
  background: #f0f0f0;
  color: #666;
  border: none;
}

.cancel-btn:hover {
  background: #e5e5e5;
}

.save-btn {
  background: var(--pink-500);
  color: white;
  border: none;
}

.save-btn:hover:not(:disabled) {
  background: var(--pink-600);
}

.save-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
