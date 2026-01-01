<template>
  <div class="admin-page">
    <aside class="sidebar">
      <div class="brand">
        <img src="@/assets/logo_2.jpg" alt="Logo" class="logo-image" />
        <div class="brand-text">
          <p>Quản lý toàn diện</p>
        </div>
      </div>

      <ul class="sidebar-stats">
        <li>
          <span>Sản phẩm</span>
          <strong>{{ products.length }}</strong>
        </li>
        <li>
          <span>Danh mục</span>
          <strong>{{ categories.length }}</strong>
        </li>
        <li>
          <span>Blog</span>
          <strong>📝</strong>
        </li>
      </ul>

      <button
        type="button"
        class="refresh"
        @click="refreshAll"
        :disabled="loading.categories || loading.products"
      >
        {{
          loading.categories || loading.products
            ? "Đang làm mới..."
            : "Làm mới dữ liệu"
        }}
      </button>

      <router-link to="/" class="home-link"> 🏠 Về trang chủ </router-link>
    </aside>

    <main class="main">
      <header class="page-header">
        <div class="header-meta">
          <h1>Trang quản trị</h1>
          <p>Quản lý sản phẩm và danh mục của cửa hàng hoa Quá Hoa.</p>
        </div>
        <div class="header-controls">
          <div class="user-chip" role="status" aria-live="polite">
            <span class="avatar" aria-hidden="true">{{ adminInitial }}</span>
            <div class="user-text">
              <strong>{{ adminDisplayName }}</strong>
              <small>Quản trị viên</small>
            </div>
            <button type="button" class="ghost logout" @click="handleLogout">
              Đăng xuất
            </button>
          </div>
          <div class="filters">
            <label class="search" aria-label="Tìm kiếm sản phẩm">
              <span aria-hidden="true">🔍</span>
              <input
                v-model="productSearch"
                type="search"
                placeholder="Tìm theo tên hoặc mô tả sản phẩm..."
              />
            </label>
            <select class="filter-select" v-model="categoryFilter">
              <option value="all">Tất cả danh mục</option>
              <option
                v-for="category in categories"
                :key="category.id"
                :value="String(category.id)"
              >
                {{ category.name }}
              </option>
            </select>
          </div>
        </div>
      </header>

      <!-- Toast Notifications -->
      <transition-group name="toast" tag="div" class="toast-container">
        <div
          v-for="toast in toasts"
          :key="toast.id"
          class="toast"
          :class="toast.type"
        >
          <div class="toast-icon">
            {{
              toast.type === "error"
                ? "❌"
                : toast.type === "warning"
                ? "⚠️"
                : "✅"
            }}
          </div>
          <div class="toast-content">
            <strong>{{
              toast.type === "error"
                ? "Lỗi"
                : toast.type === "warning"
                ? "Cảnh báo"
                : "Thành công"
            }}</strong>
            <p>{{ toast.text }}</p>
          </div>
          <button class="toast-close" @click="removeToast(toast.id)">✕</button>
        </div>
      </transition-group>

      <transition name="fade">
        <div v-if="feedback.text" class="feedback" :class="feedback.type">
          <strong
            >{{ feedback.type === "error" ? "Có lỗi" : "Thành công" }}:</strong
          >
          <span>{{ feedback.text }}</span>
        </div>
      </transition>

      <section id="products" class="panel">
        <div class="panel-title">
          <div>
            <h2>Sản phẩm</h2>
            <p>Thêm mới, chỉnh sửa và xoá sản phẩm của cửa hàng.</p>
          </div>
          <span class="pill">{{ filteredProducts.length }} sản phẩm</span>
        </div>

        <div class="grid two">
          <div class="card form-card">
            <form @submit.prevent="submitProduct">
              <h3>
                {{
                  editing.product ? "Chỉnh sửa sản phẩm" : "Thêm sản phẩm mới"
                }}
              </h3>
              <label>
                Mã sản phẩm
                <input
                  v-model.trim="productForm.productCode"
                  required
                  placeholder="Ví dụ: FL001"
                  :class="{ error: productCodeError }"
                />
                <small v-if="productCodeError" class="error-message"
                  >⚠️ {{ productCodeError }}</small
                >
              </label>

              <label>
                Tên sản phẩm
                <input
                  v-model.trim="productForm.name"
                  required
                  placeholder="Ví dụ: Pastel Dream Bouquet"
                  :class="{ error: productNameError }"
                />
                <small v-if="productNameError" class="error-message"
                  >⚠️ {{ productNameError }}</small
                >
              </label>

              <label>
                Mô tả
                <textarea
                  v-model.trim="productForm.description"
                  rows="6"
                  maxlength="2000"
                  placeholder="Mô tả chi tiết sản phẩm (tối đa 2000 ký tự)"
                ></textarea>
                <small class="char-count"
                  >{{ productForm.description?.length || 0 }}/2000 ký tự</small
                >
              </label>

              <div class="form-row">
                <label>
                  Giá bán (₫)
                  <input
                    v-model="productForm.price"
                    type="number"
                    min="0"
                    step="1000"
                    required
                  />
                </label>
                <label>
                  Danh mục
                  <select v-model="productForm.categoryId" required>
                    <option value="" disabled>Chọn danh mục</option>
                    <option
                      v-for="category in categories"
                      :key="category.id"
                      :value="String(category.id)"
                    >
                      {{ category.name }}
                    </option>
                  </select>
                </label>
              </div>

              <!-- Main Image Section with ImageUploader Component -->
              <div class="image-uploader-section">
                <ImageUploader
                  ref="mainImageUploader"
                  label="🖼️ Hình ảnh đại diện"
                  :existing-images="existingMainImage"
                  :allow-multiple="false"
                  :default-mode="'file'"
                  @update:images="handleMainImageUpdate"
                  @delete:image="handleDeleteMainImage"
                />
              </div>

              <!-- Additional Images Section with ImageUploader Component -->
              <div class="image-uploader-section">
                <ImageUploader
                  ref="additionalImagesUploader"
                  label="📸 Hình ảnh bổ sung"
                  :existing-images="existingAdditionalImages"
                  :allow-multiple="true"
                  :default-mode="'file'"
                  @update:images="handleAdditionalImagesUpdate"
                  @delete:image="handleDeleteAdditionalImage"
                />
              </div>

              <div class="form-actions">
                <button
                  type="submit"
                  class="primary"
                  :disabled="
                    loading.products || !!productCodeError || !!productNameError
                  "
                >
                  {{
                    loading.products
                      ? "Đang lưu..."
                      : editing.product
                      ? "Lưu thay đổi"
                      : "Thêm sản phẩm"
                  }}
                </button>
                <button
                  v-if="editing.product"
                  type="button"
                  class="ghost"
                  @click="resetProductForm"
                >
                  Hủy
                </button>
              </div>
            </form>
          </div>

          <div
            class="card table-card"
            role="table"
            aria-label="Danh sách sản phẩm"
          >
            <div class="table-head" role="row">
              <span role="columnheader">Mã SP</span>
              <span role="columnheader">Tên &amp; Mô tả</span>
              <span role="columnheader">Danh mục</span>
              <span role="columnheader">Giá</span>
              <span role="columnheader">Ảnh</span>
              <span role="columnheader">Thao tác</span>
            </div>

            <transition-group tag="div" name="list">
              <article
                v-for="product in paginatedProducts"
                :key="product.id"
                class="table-row"
                role="row"
              >
                <div role="cell" class="product-code">
                  <code>{{ product.productCode }}</code>
                </div>
                <div role="cell" class="product-info">
                  <strong>{{ product.name }}</strong>
                  <small>{{ product.description || "Chưa có mô tả" }}</small>
                </div>
                <div role="cell">{{ categoryName(product.categoryId) }}</div>
                <div role="cell">{{ formatCurrency(product.price) }}</div>
                <div role="cell" class="image-cell">
                  <img
                    v-if="product.imageUrl"
                    :src="product.imageUrl"
                    :alt="product.name"
                    loading="lazy"
                  />
                  <span v-else class="placeholder">Không có ảnh</span>
                </div>
                <div role="cell" class="row-actions">
                  <button
                    type="button"
                    class="ghost"
                    @click="startEditProduct(product)"
                  >
                    Sửa
                  </button>
                  <button
                    type="button"
                    class="danger"
                    @click="deleteProduct(product)"
                  >
                    Xoá
                  </button>
                </div>
              </article>
            </transition-group>

            <p v-if="!filteredProducts.length" class="empty">
              Không có sản phẩm phù hợp.
            </p>

            <!-- Pagination -->
            <div v-if="totalPages > 1" class="pagination">
              <button
                type="button"
                class="page-btn"
                @click="prevPage"
                :disabled="currentPage === 1"
              >
                ◀ Trước
              </button>

              <div class="page-numbers">
                <button
                  v-for="page in totalPages"
                  :key="page"
                  type="button"
                  class="page-number"
                  :class="{ active: page === currentPage }"
                  @click="goToPage(page)"
                >
                  {{ page }}
                </button>
              </div>

              <button
                type="button"
                class="page-btn"
                @click="nextPage"
                :disabled="currentPage === totalPages"
              >
                Sau ▶
              </button>
            </div>
          </div>
        </div>
      </section>

      <section id="categories" class="panel">
        <div class="panel-title">
          <div>
            <h2>Danh mục</h2>
            <p>Tổ chức sản phẩm theo nhóm rõ ràng và dễ tìm.</p>
          </div>
          <span class="pill"
            >{{ filteredCategories.length }} / {{ categories.length }} danh
            mục</span
          >
        </div>

        <!-- Category Search -->
        <div class="category-search-bar">
          <label class="search" aria-label="Tìm kiếm danh mục">
            <span aria-hidden="true">🔍</span>
            <input
              v-model="categorySearch"
              type="search"
              placeholder="Tìm kiếm danh mục theo tên..."
            />
          </label>
          <button
            v-if="categorySearch"
            type="button"
            class="clear-search"
            @click="categorySearch = ''"
          >
            ✕ Xóa bộ lọc
          </button>
        </div>

        <div class="grid two categories">
          <div class="card form-card">
            <h3>
              {{
                editing.category ? "Chỉnh sửa danh mục" : "Thêm danh mục mới"
              }}
            </h3>
            <form @submit.prevent="submitCategory">
              <label>
                Tên danh mục
                <input
                  v-model.trim="categoryForm.name"
                  required
                  placeholder="Ví dụ: Hoa cưới pastel"
                  :class="{ error: categoryNameError }"
                />
                <small v-if="categoryNameError" class="error-message"
                  >⚠️ {{ categoryNameError }}</small
                >
              </label>

              <div class="form-actions">
                <button
                  type="submit"
                  class="primary"
                  :disabled="loading.categories || !!categoryNameError"
                >
                  {{
                    loading.categories
                      ? "Đang lưu..."
                      : editing.category
                      ? "Lưu thay đổi"
                      : "Thêm danh mục"
                  }}
                </button>
                <button
                  v-if="editing.category"
                  type="button"
                  class="ghost"
                  @click="resetCategoryForm"
                >
                  Hủy
                </button>
              </div>
            </form>
          </div>

          <div class="card category-list">
            <transition-group tag="ul" name="list">
              <li v-for="category in paginatedCategories" :key="category.id">
                <div>
                  <strong>{{ category.name }}</strong>
                  <small>{{ productCount(category.id) }} sản phẩm</small>
                </div>
                <div class="row-actions">
                  <button
                    type="button"
                    class="ghost"
                    @click="startEditCategory(category)"
                  >
                    Sửa
                  </button>
                  <button
                    type="button"
                    class="danger"
                    @click="deleteCategory(category)"
                  >
                    Xoá
                  </button>
                </div>
              </li>
            </transition-group>

            <p
              v-if="!filteredCategories.length && !categorySearch"
              class="empty"
            >
              Chưa có danh mục nào.
            </p>
            <p
              v-if="!filteredCategories.length && categorySearch"
              class="empty"
            >
              Không tìm thấy danh mục phù hợp với "{{ categorySearch }}".
            </p>

            <!-- Pagination for Categories -->
            <div v-if="totalCategoryPages > 1" class="pagination">
              <button
                type="button"
                class="page-btn"
                @click="prevCategoryPage"
                :disabled="currentCategoryPage === 1"
              >
                ◀ Trước
              </button>

              <div class="page-numbers">
                <button
                  v-for="page in totalCategoryPages"
                  :key="page"
                  type="button"
                  class="page-number"
                  :class="{ active: page === currentCategoryPage }"
                  @click="goToCategoryPage(page)"
                >
                  {{ page }}
                </button>
              </div>

              <button
                type="button"
                class="page-btn"
                @click="nextCategoryPage"
                :disabled="currentCategoryPage === totalCategoryPages"
              >
                Sau ▶
              </button>
            </div>
          </div>
        </div>
      </section>

      <section id="blogs" class="panel">
        <AdminBlogManagement />
      </section>

      <section id="admins" class="panel">
        <div class="panel-title">
          <div>
            <h2>Quản lý tài khoản Admin</h2>
            <p>Thêm, chỉnh sửa và xóa tài khoản quản trị viên.</p>
          </div>
          <span class="pill">{{ admins.length }} tài khoản</span>
        </div>

        <div class="grid two">
          <div class="card form-card">
            <h3>
              {{
                editing.admin ? "Chỉnh sửa tài khoản" : "Thêm tài khoản admin"
              }}
            </h3>
            <form @submit.prevent="submitAdmin">
              <label>
                Tên đăng nhập
                <input
                  v-model.trim="adminForm.username"
                  required
                  placeholder="Ví dụ: admin123"
                  :disabled="editing.admin"
                  minlength="3"
                  :class="{ error: adminUsernameError }"
                />
                <small v-if="adminUsernameError" class="error-message"
                  >⚠️ {{ adminUsernameError }}</small
                >
              </label>

              <label>
                Mật khẩu {{ editing.admin ? "(để trống nếu không đổi)" : "" }}
                <input
                  v-model="adminForm.password"
                  type="password"
                  :required="!editing.admin"
                  placeholder="Nhập mật khẩu..."
                  minlength="6"
                />
              </label>

              <div class="form-actions">
                <button
                  type="submit"
                  class="primary"
                  :disabled="loading.admins || !!adminUsernameError"
                >
                  {{
                    loading.admins
                      ? "Đang lưu..."
                      : editing.admin
                      ? "Lưu thay đổi"
                      : "Thêm tài khoản"
                  }}
                </button>
                <button
                  v-if="editing.admin"
                  type="button"
                  class="ghost"
                  @click="resetAdminForm"
                >
                  Hủy
                </button>
              </div>
            </form>
          </div>

          <div class="card table-card">
            <div class="admin-list">
              <transition-group tag="div" name="list">
                <div
                  v-for="adminItem in admins"
                  :key="adminItem.id"
                  class="admin-item"
                  :class="{ 'current-admin': adminItem.id === admin?.id }"
                >
                  <div class="admin-info">
                    <span class="admin-avatar">{{
                      adminItem.username.charAt(0).toUpperCase()
                    }}</span>
                    <div>
                      <strong>{{ adminItem.username }}</strong>
                      <small v-if="adminItem.id === admin?.id"
                        >Đang đăng nhập</small
                      >
                    </div>
                  </div>
                  <div class="row-actions">
                    <button
                      type="button"
                      class="ghost"
                      @click="startEditAdmin(adminItem)"
                    >
                      Đổi mật khẩu
                    </button>
                    <button
                      type="button"
                      class="danger"
                      @click="deleteAdmin(adminItem)"
                      :disabled="
                        adminItem.id === admin?.id || admins.length === 1
                      "
                    >
                      Xoá
                    </button>
                  </div>
                </div>
              </transition-group>

              <p v-if="!admins.length" class="empty">
                Chưa có tài khoản admin nào.
              </p>
            </div>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";
import API from "../config/api";
import AdminBlogManagement from "./AdminBlogManagement.vue";
import ImageUploader from "./ImageUploader.vue";

const router = useRouter();

// Get admin from localStorage
const admin = ref(null);

onMounted(() => {
  const stored = localStorage.getItem("admin");
  if (stored) {
    admin.value = JSON.parse(stored);
  }
});

const adminDisplayName = computed(
  () => admin.value?.username || "Quản trị viên"
);

const adminInitial = computed(() => {
  const name = adminDisplayName.value;
  return name ? name.charAt(0).toUpperCase() : "A";
});

const handleLogout = () => {
  localStorage.removeItem("admin");
  router.push("/");
};

const api = axios.create({
  baseURL: API.baseURL,
  headers: {
    "Content-Type": "application/json",
  },
});

// Add auth interceptor
api.interceptors.request.use(
  (config) => {
    if (admin.value && admin.value.accessToken) {
      config.headers.Authorization = `Bearer ${admin.value.accessToken}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

const categories = ref([]);
const products = ref([]);
const admins = ref([]);

const productForm = reactive({
  productCode: "",
  name: "",
  description: "",
  price: "",
  imageUrl: "",
  imageUrls: [],
  categoryId: "",
  pendingFiles: [], // Files to upload after product creation
  pendingMainImageFile: null, // Main image file to upload after product creation
});

const newImageUrl = ref("");

// Image Uploader Refs
const mainImageUploader = ref(null);
const additionalImagesUploader = ref(null);

// Image States for ImageUploader component
const existingMainImage = ref([]);
const newMainImages = ref([]);
const existingAdditionalImages = ref([]);
const newAdditionalImages = ref([]);

const categoryForm = reactive({
  name: "",
});

const adminForm = reactive({
  username: "",
  password: "",
});

const editing = reactive({
  product: null,
  category: null,
  admin: null,
});

const loading = reactive({
  products: false,
  categories: false,
  admins: false,
});

const feedback = reactive({
  type: "success",
  text: "",
});

const productSearch = ref("");
const categoryFilter = ref("all");
const categorySearch = ref("");

// Toast notifications
const toasts = ref([]);
let toastIdCounter = 0;

const showToast = (type, text) => {
  const id = ++toastIdCounter;
  toasts.value.push({ id, type, text });

  // Auto remove after 5 seconds
  setTimeout(() => {
    removeToast(id);
  }, 5000);
};

const removeToast = (id) => {
  const index = toasts.value.findIndex((t) => t.id === id);
  if (index > -1) {
    toasts.value.splice(index, 1);
  }
};

// Pagination for products
const currentPage = ref(1);
const itemsPerPage = 10;

// Pagination for categories
const currentCategoryPage = ref(1);
const categoriesPerPage = 8;

// Filtered categories based on search
const filteredCategories = computed(() => {
  if (!categorySearch.value) {
    return categories.value;
  }
  return categories.value.filter((category) =>
    category.name.toLowerCase().includes(categorySearch.value.toLowerCase())
  );
});

const filteredProducts = computed(() =>
  products.value
    .filter((product) => {
      const matchCategory =
        categoryFilter.value === "all" ||
        String(product.categoryId) === categoryFilter.value;
      const matchSearch =
        !productSearch.value ||
        product.name
          .toLowerCase()
          .includes(productSearch.value.toLowerCase()) ||
        (product.description || "")
          .toLowerCase()
          .includes(productSearch.value.toLowerCase());
      return matchCategory && matchSearch;
    })
    .sort((a, b) => a.name.localeCompare(b.name, "vi"))
);

const totalPages = computed(() =>
  Math.ceil(filteredProducts.value.length / itemsPerPage)
);

const paginatedProducts = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return filteredProducts.value.slice(start, end);
});

const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
  }
};

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
  }
};

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--;
  }
};

// Category pagination functions
const totalCategoryPages = computed(() =>
  Math.ceil(filteredCategories.value.length / categoriesPerPage)
);

const paginatedCategories = computed(() => {
  const start = (currentCategoryPage.value - 1) * categoriesPerPage;
  const end = start + categoriesPerPage;
  return filteredCategories.value.slice(start, end);
});

const goToCategoryPage = (page) => {
  if (page >= 1 && page <= totalCategoryPages.value) {
    currentCategoryPage.value = page;
  }
};

const nextCategoryPage = () => {
  if (currentCategoryPage.value < totalCategoryPages.value) {
    currentCategoryPage.value++;
  }
};

const prevCategoryPage = () => {
  if (currentCategoryPage.value > 1) {
    currentCategoryPage.value--;
  }
};

// Watch category search to reset pagination
watch(categorySearch, () => {
  currentCategoryPage.value = 1;
});

// Validation computed properties
const productCodeError = computed(() => {
  if (!productForm.productCode || productForm.productCode.trim() === "") {
    return "";
  }

  const exists = products.value.some(
    (p) =>
      p.productCode.toLowerCase() === productForm.productCode.toLowerCase() &&
      (!editing.product || p.id !== editing.product.id)
  );

  if (exists) {
    return `Mã sản phẩm "${productForm.productCode}" đã tồn tại`;
  }
  return "";
});

const productNameError = computed(() => {
  if (!productForm.name || productForm.name.trim() === "") {
    return "";
  }

  const exists = products.value.some(
    (p) =>
      p.name.toLowerCase() === productForm.name.toLowerCase() &&
      (!editing.product || p.id !== editing.product.id)
  );

  if (exists) {
    return `Tên sản phẩm "${productForm.name}" đã tồn tại`;
  }
  return "";
});

const categoryNameError = computed(() => {
  if (!categoryForm.name || categoryForm.name.trim() === "") {
    return "";
  }

  const exists = categories.value.some(
    (c) =>
      c.name.toLowerCase() === categoryForm.name.toLowerCase() &&
      (!editing.category || c.id !== editing.category.id)
  );

  if (exists) {
    return `Danh mục "${categoryForm.name}" đã tồn tại`;
  }
  return "";
});

const adminUsernameError = computed(() => {
  if (!adminForm.username || adminForm.username.trim() === "") {
    return "";
  }

  const exists = admins.value.some(
    (a) =>
      a.username.toLowerCase() === adminForm.username.toLowerCase() &&
      (!editing.admin || a.id !== editing.admin.id)
  );

  if (exists) {
    return `Tài khoản "${adminForm.username}" đã tồn tại`;
  }
  return "";
});

const showFeedback = (type, text) => {
  // Show toast notification
  showToast(type, text);

  // Keep old feedback for backward compatibility (can be removed later)
  feedback.type = type;
  feedback.text = text;
  if (text) {
    window.clearTimeout(showFeedback.timer);
    showFeedback.timer = window.setTimeout(() => {
      feedback.text = "";
    }, 5000);
  }
};

const getErrorMessage = (error, context) => {
  // Check for network errors
  if (!error.response) {
    return `❌ Không thể kết nối với server. Vui lòng kiểm tra kết nối mạng.`;
  }

  const status = error.response.status;
  const data = error.response.data;
  const message = data.message || data.error || "";

  // Handle specific HTTP status codes
  switch (status) {
    case 400: // Bad Request
      if (message) {
        // Parse validation errors chi tiết
        if (message.toLowerCase().includes("productcode")) {
          if (
            message.toLowerCase().includes("exist") ||
            message.toLowerCase().includes("duplicate")
          ) {
            return `❌ Mã sản phẩm "${productForm.productCode}" đã tồn tại. Vui lòng chọn mã khác.`;
          }
          return "❌ Mã sản phẩm không hợp lệ. Vui lòng kiểm tra lại.";
        }
        if (message.toLowerCase().includes("name")) {
          if (
            message.toLowerCase().includes("empty") ||
            message.toLowerCase().includes("blank")
          ) {
            return `❌ Tên ${context} không được để trống.`;
          }
          if (
            message.toLowerCase().includes("length") ||
            message.toLowerCase().includes("long")
          ) {
            return `❌ Tên ${context} quá dài. Vui lòng rút ngắn lại.`;
          }
          if (
            message.toLowerCase().includes("exist") ||
            message.toLowerCase().includes("duplicate")
          ) {
            return `❌ Tên ${context} đã tồn tại. Vui lòng đổi tên khác.`;
          }
          return `❌ Tên ${context} không hợp lệ.`;
        }
        if (message.toLowerCase().includes("price")) {
          if (
            message.toLowerCase().includes("negative") ||
            message.toLowerCase().includes("zero")
          ) {
            return "❌ Giá sản phẩm phải lớn hơn 0.";
          }
          return "❌ Giá sản phẩm không hợp lệ. Vui lòng nhập số dương.";
        }
        if (message.toLowerCase().includes("description")) {
          if (
            message.toLowerCase().includes("long") ||
            message.toLowerCase().includes("2000")
          ) {
            return "❌ Mô tả sản phẩm quá dài (tối đa 2000 ký tự).";
          }
          return "❌ Mô tả sản phẩm không hợp lệ.";
        }
        if (message.toLowerCase().includes("category")) {
          if (
            message.toLowerCase().includes("not found") ||
            message.toLowerCase().includes("exist")
          ) {
            return "❌ Danh mục không tồn tại. Vui lòng chọn danh mục khác.";
          }
          if (message.toLowerCase().includes("product")) {
            return "❌ Không thể xóa danh mục vì còn sản phẩm. Vui lòng xóa hoặc chuyển sản phẩm trước.";
          }
          return "❌ Danh mục không hợp lệ.";
        }
        if (
          message.toLowerCase().includes("imageurl") ||
          message.toLowerCase().includes("image")
        ) {
          return "❌ URL hình ảnh không hợp lệ. Vui lòng nhập URL đúng định dạng.";
        }
        // Trả về message từ server nếu có
        return `❌ Dữ liệu không hợp lệ: ${message}`;
      }
      return `❌ Dữ liệu nhập vào không đúng định dạng. Vui lòng kiểm tra lại.`;

    case 404: // Not Found
      if (message) {
        return `❌ Không tìm thấy ${context}: ${message}`;
      }
      return `❌ Không tìm thấy ${context}. Có thể đã bị xóa trước đó.`;

    case 409: // Conflict
      if (message) {
        if (message.toLowerCase().includes("productcode")) {
          return `❌ Mã sản phẩm "${productForm.productCode}" đã tồn tại. Vui lòng chọn mã khác.`;
        }
        if (message.toLowerCase().includes("name")) {
          return `❌ Tên ${context} "${
            categoryForm.name || productForm.name
          }" đã tồn tại. Vui lòng đổi tên khác.`;
        }
        if (message.toLowerCase().includes("product")) {
          return `❌ Không thể xóa vì ${context} đang được sử dụng bởi sản phẩm khác.`;
        }
        return `❌ Xung đột dữ liệu: ${message}`;
      }
      return `❌ ${context} đã tồn tại hoặc đang được sử dụng. Vui lòng kiểm tra lại.`;

    case 500: // Internal Server Error
      if (message) {
        return `❌ Lỗi server: ${message}. Vui lòng thử lại sau hoặc liên hệ quản trị viên.`;
      }
      return `❌ Lỗi server. Vui lòng thử lại sau hoặc liên hệ quản trị viên.`;

    case 403: // Forbidden
      return `❌ Bạn không có quyền thực hiện thao tác này.`;

    case 401: // Unauthorized
      return `❌ Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.`;

    default:
      if (message) {
        return `❌ Lỗi: ${message} (Mã lỗi: ${status})`;
      }
      return `❌ Có lỗi xảy ra (Mã lỗi: ${status}). Vui lòng thử lại.`;
  }
};

const handleError = (error, context = "dữ liệu") => {
  const errorMessage = getErrorMessage(error, context);
  showFeedback("error", errorMessage);
  console.error("Error details:", error);
};

const loadCategories = async () => {
  loading.categories = true;
  try {
    const { data } = await api.get("/categories");
    categories.value = data;
    if (!productForm.categoryId && data.length) {
      productForm.categoryId = String(data[0].id);
    }
  } catch (error) {
    handleError(error, "danh mục");
  } finally {
    loading.categories = false;
  }
};

const loadProducts = async () => {
  loading.products = true;
  try {
    const { data } = await api.get("/products");
    products.value = data;
  } catch (error) {
    handleError(error, "sản phẩm");
  } finally {
    loading.products = false;
  }
};

// ============================================
// IMAGE UPLOADER HANDLERS
// ============================================

// Handle main image updates from ImageUploader
const handleMainImageUpdate = (images) => {
  newMainImages.value = images;
  console.log("Main images updated:", images);
};

// Handle additional images updates from ImageUploader
const handleAdditionalImagesUpdate = (images) => {
  newAdditionalImages.value = images;
  console.log("Additional images updated:", images);
};

// Handle delete main image
const handleDeleteMainImage = async (imageId) => {
  try {
    if (editing.product && imageId) {
      // Delete from server if editing existing product
      await api.delete(`/products/${editing.product.id}/images/main`);
      existingMainImage.value = [];
      showToast("success", "Đã xóa ảnh đại diện");
    }
  } catch (error) {
    console.error("Failed to delete main image:", error);
    showToast("error", "Không thể xóa ảnh đại diện");
  }
};

// Handle delete additional image
const handleDeleteAdditionalImage = async (imageId) => {
  try {
    if (editing.product && imageId) {
      // Delete from server
      await api.delete(`/products/${editing.product.id}/images/${imageId}`);
      existingAdditionalImages.value = existingAdditionalImages.value.filter(
        (img) => img.id !== imageId
      );
      showToast("success", "Đã xóa ảnh");
    }
  } catch (error) {
    console.error("Failed to delete image:", error);
    showToast("error", "Không thể xóa ảnh");
  }
};

const submitProduct = async () => {
  // Validation trước khi submit
  if (!productForm.name || productForm.name.trim() === "") {
    showFeedback("error", "❌ Vui lòng nhập tên sản phẩm.");
    return;
  }

  if (!productForm.productCode || productForm.productCode.trim() === "") {
    showFeedback("error", "❌ Vui lòng nhập mã sản phẩm.");
    return;
  }

  // Check for duplicate product code
  if (productCodeError.value) {
    showFeedback(
      "error",
      `❌ ${productCodeError.value}. Vui lòng chọn mã khác.`
    );
    return;
  }

  // Check for duplicate product name
  if (productNameError.value) {
    showFeedback(
      "error",
      `❌ ${productNameError.value}. Vui lòng đổi tên khác.`
    );
    return;
  }

  if (!productForm.categoryId) {
    showFeedback("error", "❌ Vui lòng chọn danh mục cho sản phẩm.");
    return;
  }

  if (!productForm.price || Number(productForm.price) <= 0) {
    showFeedback("error", "❌ Giá sản phẩm phải lớn hơn 0.");
    return;
  }

  if (productForm.description && productForm.description.length > 2000) {
    showFeedback("error", "❌ Mô tả sản phẩm không được vượt quá 2000 ký tự.");
    return;
  }

  loading.products = true;

  const payload = {
    productCode: productForm.productCode.trim(),
    name: productForm.name.trim(),
    description: productForm.description?.trim() || "",
    price: Number(productForm.price),
    imageUrl: null, // Will be set by image upload
    imageUrls: null, // Will be set by image upload
    categoryId: Number(productForm.categoryId),
  };

  try {
    let productId = null;

    if (editing.product) {
      await api.put(`/products/${editing.product.id}`, payload);
      productId = editing.product.id;
      showFeedback(
        "success",
        `✅ Đã cập nhật sản phẩm "${productForm.name}" thành công!`
      );
    } else {
      const response = await api.post("/products", payload);
      productId = response.data.id;
      showFeedback(
        "success",
        `✅ Đã thêm sản phẩm "${productForm.name}" thành công!`
      );
    }

    // Upload images using ImageUploader data
    let totalUploads = 0;
    let successUploads = 0;

    // 1. Upload main image (from ImageUploader)
    if (newMainImages.value.length > 0) {
      const mainImage = newMainImages.value[0];
      totalUploads++;

      try {
        if (mainImage.source === "file" && mainImage.file) {
          // Upload file
          const formData = new FormData();
          formData.append("file", mainImage.file); // Backend expects 'file'
          await api.post(`/products/${productId}/images/main`, formData, {
            headers: { "Content-Type": "multipart/form-data" },
          });
          successUploads++;
          console.log("✅ Main image file uploaded");
        } else if (mainImage.source === "url" && mainImage.imageUrl) {
          // Upload from URL
          await api.post(`/products/${productId}/images/main-url`, {
            imageUrl: mainImage.imageUrl,
          });
          successUploads++;
          console.log("✅ Main image URL processed");
        }
      } catch (error) {
        console.error("❌ Error uploading main image:", error);
        showToast("error", "Không thể tải lên ảnh đại diện");
      }
    }

    // 2. Upload additional images (from ImageUploader)
    for (const image of newAdditionalImages.value) {
      totalUploads++;

      try {
        if (image.source === "file" && image.file) {
          // Upload file
          const formData = new FormData();
          formData.append("file", image.file); // Backend expects 'file'
          await api.post(`/products/${productId}/images`, formData, {
            headers: { "Content-Type": "multipart/form-data" },
          });
          successUploads++;
          console.log(`✅ Additional image file uploaded: ${image.fileName}`);
        } else if (image.source === "url" && image.imageUrl) {
          // Upload from URL
          await api.post(`/products/${productId}/images/url`, {
            imageUrl: image.imageUrl,
          });
          successUploads++;
          console.log(`✅ Additional image URL processed`);
        }
      } catch (error) {
        console.error("❌ Error uploading additional image:", error);
      }
    }

    if (totalUploads > 0) {
      showToast(
        "success",
        `✅ Đã tải lên ${successUploads}/${totalUploads} hình ảnh!`
      );
    }

    // Reload products
    await loadProducts();
    resetProductForm();
  } catch (error) {
    handleError(error, "sản phẩm");
  } finally {
    loading.products = false;
  }
};

const submitCategory = async () => {
  // Validation
  if (!categoryForm.name || categoryForm.name.trim() === "") {
    showFeedback("error", "❌ Vui lòng nhập tên danh mục.");
    return;
  }

  // Check for duplicate category name
  if (categoryNameError.value) {
    showFeedback(
      "error",
      `❌ ${categoryNameError.value}. Vui lòng đổi tên khác.`
    );
    return;
  }

  if (categoryForm.name.length > 100) {
    showFeedback("error", "❌ Tên danh mục không được vượt quá 100 ký tự.");
    return;
  }

  loading.categories = true;
  const payload = { name: categoryForm.name.trim() };

  try {
    if (editing.category) {
      await api.put(`/categories/${editing.category.id}`, payload);
      showFeedback(
        "success",
        `✅ Đã cập nhật danh mục "${categoryForm.name}" thành công!`
      );
    } else {
      await api.post("/categories", payload);
      showFeedback(
        "success",
        `✅ Đã thêm danh mục "${categoryForm.name}" thành công!`
      );
    }
    await loadCategories();
    await loadProducts();
    resetCategoryForm();
  } catch (error) {
    handleError(error, "danh mục");
  } finally {
    loading.categories = false;
  }
};

const deleteProduct = async (product) => {
  if (
    !window.confirm(
      `⚠️ Bạn có chắc muốn xoá sản phẩm "${product.name}"?\n\nThao tác này không thể hoàn tác!`
    )
  ) {
    return;
  }
  loading.products = true;
  try {
    await api.delete(`/products/${product.id}`);
    showFeedback("success", `✅ Đã xoá sản phẩm "${product.name}" thành công!`);
    await loadProducts();
  } catch (error) {
    if (error.response?.status === 404) {
      showFeedback(
        "error",
        `❌ Sản phẩm "${product.name}" không tồn tại hoặc đã bị xóa trước đó.`
      );
    } else if (error.response?.status === 409) {
      showFeedback(
        "error",
        `❌ Không thể xóa sản phẩm "${product.name}" vì đang được sử dụng trong đơn hàng.`
      );
    } else {
      handleError(error, "sản phẩm");
    }
  } finally {
    loading.products = false;
  }
};

const deleteCategory = async (category) => {
  const productInCategory = productCount(category.id);

  if (productInCategory > 0) {
    showFeedback(
      "error",
      `❌ Không thể xóa danh mục "${category.name}" vì còn ${productInCategory} sản phẩm. Vui lòng xóa hoặc chuyển sản phẩm sang danh mục khác trước.`
    );
    return;
  }

  if (
    !window.confirm(
      `⚠️ Bạn có chắc muốn xoá danh mục "${category.name}"?\n\nThao tác này không thể hoàn tác!`
    )
  ) {
    return;
  }

  loading.categories = true;
  try {
    await api.delete(`/categories/${category.id}`);
    showFeedback(
      "success",
      `✅ Đã xoá danh mục "${category.name}" thành công!`
    );
    if (productForm.categoryId === String(category.id)) {
      productForm.categoryId = "";
    }
    await loadCategories();
    await loadProducts();
  } catch (error) {
    if (error.response?.status === 404) {
      showFeedback(
        "error",
        `❌ Danh mục "${category.name}" không tồn tại hoặc đã bị xóa trước đó.`
      );
    } else if (error.response?.status === 409) {
      showFeedback(
        "error",
        `❌ Không thể xóa danh mục "${category.name}" vì còn sản phẩm phụ thuộc. Vui lòng xóa hoặc chuyển các sản phẩm trước.`
      );
    } else {
      handleError(error, "danh mục");
    }
  } finally {
    loading.categories = false;
  }
};

const startEditProduct = (product) => {
  editing.product = product;
  productForm.productCode = product.productCode || "";
  productForm.name = product.name;
  productForm.description = product.description || "";
  productForm.price = product.price;
  productForm.imageUrl = product.imageUrl || "";
  productForm.imageUrls = product.imageUrls ? [...product.imageUrls] : [];
  productForm.categoryId = String(product.categoryId);

  // Load existing images into ImageUploader
  // Main image
  if (product.imageUrl) {
    existingMainImage.value = [
      {
        id: "main",
        imageUrl: product.imageUrl,
        fileName: "Main Image",
      },
    ];
  } else {
    existingMainImage.value = [];
  }

  // Additional images
  if (product.images && product.images.length > 0) {
    existingAdditionalImages.value = product.images.map((img) => ({
      id: img.id,
      imageUrl: img.imageUrl,
      fileName: img.fileName || `Image ${img.id}`,
      displayOrder: img.displayOrder,
    }));
  } else {
    existingAdditionalImages.value = [];
  }

  // Clear new images
  newMainImages.value = [];
  newAdditionalImages.value = [];
};

const startEditCategory = (category) => {
  editing.category = category;
  categoryForm.name = category.name;
};

const resetProductForm = () => {
  editing.product = null;
  productForm.productCode = "";
  productForm.name = "";
  productForm.description = "";
  productForm.price = "";
  productForm.imageUrl = "";
  productForm.imageUrls = [];
  productForm.pendingFiles = [];
  productForm.pendingMainImageFile = null;
  newImageUrl.value = "";
  productForm.categoryId = categories.value[0]
    ? String(categories.value[0].id)
    : "";

  // Reset ImageUploader states
  existingMainImage.value = [];
  newMainImages.value = [];
  existingAdditionalImages.value = [];
  newAdditionalImages.value = [];

  // Clear ImageUploader components
  if (mainImageUploader.value) {
    mainImageUploader.value.clearNewImages();
  }
  if (additionalImagesUploader.value) {
    additionalImagesUploader.value.clearNewImages();
  }
};

const resetCategoryForm = () => {
  editing.category = null;
  categoryForm.name = "";
};

const categoryName = (categoryId) => {
  const category = categories.value.find((item) => item.id === categoryId);
  return category ? category.name : "Chưa rõ";
};

const productCount = (categoryId) =>
  products.value.filter((product) => product.categoryId === categoryId).length;

const formatCurrency = (value) => {
  if (value === null || value === undefined || Number.isNaN(Number(value))) {
    return "—";
  }
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
    maximumFractionDigits: 0,
  }).format(value);
};

const refreshAll = async () => {
  try {
    await Promise.all([loadCategories(), loadProducts()]);
    showFeedback("success", "✅ Đã làm mới dữ liệu thành công!");
  } catch (error) {
    showFeedback(
      "error",
      "❌ Không thể làm mới dữ liệu. Vui lòng kiểm tra kết nối."
    );
  }
};

// Admin management functions
const loadAdmins = async () => {
  loading.admins = true;
  try {
    const { data } = await api.get("/admins");
    admins.value = data;
  } catch (error) {
    handleError(error, "tài khoản admin");
  } finally {
    loading.admins = false;
  }
};

const submitAdmin = async () => {
  // Validation
  if (!adminForm.username || adminForm.username.trim() === "") {
    showFeedback("error", "❌ Vui lòng nhập tên đăng nhập.");
    return;
  }

  if (adminForm.username.length < 3) {
    showFeedback("error", "❌ Tên đăng nhập phải có ít nhất 3 ký tự.");
    return;
  }

  // Check for duplicate username
  if (adminUsernameError.value) {
    showFeedback(
      "error",
      `❌ ${adminUsernameError.value}. Vui lòng chọn tên khác.`
    );
    return;
  }

  if (
    !editing.admin &&
    (!adminForm.password || adminForm.password.length < 6)
  ) {
    showFeedback("error", "❌ Mật khẩu phải có ít nhất 6 ký tự.");
    return;
  }

  if (editing.admin && adminForm.password && adminForm.password.length < 6) {
    showFeedback("error", "❌ Mật khẩu mới phải có ít nhất 6 ký tự.");
    return;
  }

  loading.admins = true;
  const payload = {
    username: adminForm.username.trim(),
    ...(adminForm.password && { password: adminForm.password }),
  };

  try {
    if (editing.admin) {
      await api.put(`/admins/${editing.admin.id}`, payload);
      showFeedback(
        "success",
        `✅ Đã cập nhật tài khoản "${adminForm.username}" thành công!`
      );
    } else {
      await api.post("/admins", payload);
      showFeedback(
        "success",
        `✅ Đã thêm tài khoản "${adminForm.username}" thành công!`
      );
    }
    await loadAdmins();
    resetAdminForm();
  } catch (error) {
    handleError(error, "tài khoản admin");
  } finally {
    loading.admins = false;
  }
};

const deleteAdmin = async (adminItem) => {
  if (adminItem.id === admin.value?.id) {
    showFeedback("error", "❌ Không thể xóa tài khoản đang đăng nhập.");
    return;
  }

  if (admins.value.length === 1) {
    showFeedback("error", "❌ Không thể xóa tài khoản admin cuối cùng.");
    return;
  }

  if (
    !window.confirm(
      `⚠️ Bạn có chắc muốn xoá tài khoản "${adminItem.username}"?\n\nThao tác này không thể hoàn tác!`
    )
  ) {
    return;
  }

  loading.admins = true;
  try {
    await api.delete(`/admins/${adminItem.id}`);
    showFeedback(
      "success",
      `✅ Đã xoá tài khoản "${adminItem.username}" thành công!`
    );
    await loadAdmins();
  } catch (error) {
    if (error.response?.status === 404) {
      showFeedback(
        "error",
        `❌ Tài khoản "${adminItem.username}" không tồn tại hoặc đã bị xóa trước đó.`
      );
    } else if (error.response?.status === 409) {
      showFeedback(
        "error",
        `❌ Không thể xóa tài khoản "${adminItem.username}" vì đang được sử dụng.`
      );
    } else {
      handleError(error, "tài khoản admin");
    }
  } finally {
    loading.admins = false;
  }
};

const startEditAdmin = (adminItem) => {
  editing.admin = adminItem;
  adminForm.username = adminItem.username;
  adminForm.password = "";
};

const resetAdminForm = () => {
  editing.admin = null;
  adminForm.username = "";
  adminForm.password = "";
};

onMounted(async () => {
  await Promise.all([loadCategories(), loadProducts(), loadAdmins()]);
});
</script>

<style scoped>
.admin-page {
  display: grid;
  grid-template-columns: 280px 1fr;
  min-height: 100vh;
  background: linear-gradient(
    135deg,
    rgba(255, 240, 247, 0.9),
    rgba(255, 226, 239, 0.9)
  );
}

.sidebar {
  padding: 36px 28px;
  display: flex;
  flex-direction: column;
  gap: 28px;
  background: rgba(255, 255, 255, 0.65);
  backdrop-filter: blur(18px);
  border-right: 1px solid rgba(243, 109, 161, 0.18);
}

.brand {
  display: flex;
  align-items: center;
  gap: 18px;
  flex-direction: column;
  padding-bottom: 12px;
}

.logo-image {
  width: 100%;
  max-width: 280px;
  height: 100px;
  object-fit: cover;
  border-radius: 0;
  box-shadow: none;
  background-color: white;
  padding: 12px;
}

.brand-text {
  text-align: center;
  width: 100%;
}

.brand-text p {
  margin: 0;
  color: var(--pink-600);
  font-weight: 600;
  font-size: 0.95rem;
}

.sidebar-stats {
  list-style: none;
  margin: 0;
  padding: 18px;
  display: grid;
  gap: 12px;
  border-radius: 18px;
  background: linear-gradient(
    155deg,
    rgba(255, 210, 227, 0.45),
    rgba(255, 235, 244, 0.8)
  );
  box-shadow: 0 22px 48px -42px rgba(243, 109, 161, 0.75);
}

.sidebar-stats li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: var(--pink-600);
}

.sidebar-stats strong {
  font-size: 1.3rem;
  color: var(--pink-800);
}

.refresh {
  border: none;
  border-radius: 14px;
  padding: 12px 16px;
  font-weight: 600;
  cursor: pointer;
  color: var(--pink-600);
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(243, 109, 161, 0.18);
  transition: transform 0.2s ease;
}

.refresh:hover:enabled {
  transform: translateY(-2px);
}

.refresh:disabled {
  opacity: 0.65;
  cursor: progress;
}

.home-link {
  display: block;
  text-align: center;
  padding: 12px 16px;
  border-radius: 14px;
  text-decoration: none;
  color: var(--pink-600);
  background: rgba(255, 255, 255, 0.6);
  border: 1px solid rgba(243, 109, 161, 0.18);
  font-weight: 600;
  transition: all 0.3s ease;
  margin-top: 12px;
}

.home-link:hover {
  background: rgba(255, 255, 255, 0.9);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(243, 109, 161, 0.15);
}

.sidebar-tip {
  margin: 0;
  color: var(--pink-500);
  line-height: 1.6;
}

.main {
  padding: 40px 48px 64px;
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.page-header {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  gap: 24px;
}

.page-header h1 {
  margin: 0;
  font-size: 2rem;
  color: var(--pink-700);
}

.page-header p {
  margin: 8px 0 0;
  color: var(--pink-400);
}

.header-meta {
  display: grid;
  gap: 8px;
}

.header-controls {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 16px;
}

.user-chip {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 16px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(243, 109, 161, 0.18);
  color: var(--pink-600);
  box-shadow: 0 20px 42px -32px rgba(243, 109, 161, 0.55);
}

.avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  font-weight: 700;
  color: white;
  background: linear-gradient(135deg, var(--pink-400), var(--pink-600));
  box-shadow: 0 18px 36px -28px rgba(243, 109, 161, 0.55);
}

.user-text strong {
  display: block;
  color: var(--pink-700);
}

.user-text small {
  display: block;
  color: var(--pink-400);
  font-weight: 500;
}

.logout {
  margin-left: 8px;
  white-space: nowrap;
}

.filters {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
  width: 100%;
}

.search {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.75);
  color: var(--pink-400);
  box-shadow: 0 20px 36px -30px rgba(243, 109, 161, 0.65);
  width: 100%;
  box-sizing: border-box;
}

.search input {
  border: none;
  outline: none;
  background: transparent;
  color: var(--pink-700);
  min-width: 220px;
  width: 100%;
}

.filter-select {
  border: 1px solid rgba(243, 109, 161, 0.22);
  border-radius: 14px;
  padding: 10px 14px;
  background: rgba(255, 255, 255, 0.75);
  color: var(--pink-600);
  font-weight: 600;
  width: 100%;
  box-sizing: border-box;
}

.feedback {
  padding: 16px 20px;
  border-radius: 16px;
  display: flex;
  gap: 12px;
  align-items: flex-start;
  font-weight: 600;
  background: rgba(255, 255, 255, 0.95);
  border: 2px solid var(--pink-300);
  box-shadow: 0 8px 24px rgba(243, 109, 161, 0.2);
  animation: slideDown 0.3s ease;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.feedback strong {
  font-size: 1rem;
  display: block;
  margin-bottom: 4px;
}

.feedback span {
  line-height: 1.6;
  font-size: 0.95rem;
  font-weight: 500;
}

.feedback.success {
  background: linear-gradient(
    135deg,
    rgba(16, 185, 129, 0.1),
    rgba(16, 185, 129, 0.05)
  );
  border-color: #10b981;
  color: #047857;
}

.feedback.success strong {
  color: #065f46;
}

.feedback.error {
  background: linear-gradient(
    135deg,
    rgba(239, 68, 68, 0.1),
    rgba(239, 68, 68, 0.05)
  );
  border-color: #ef4444;
  color: #dc2626;
}

.feedback.error strong {
  color: #991b1b;
}

.panel {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 26px;
  padding: 28px 30px;
  display: grid;
  gap: 24px;
  box-shadow: 0 36px 80px -55px rgba(243, 109, 161, 0.45);
}

.panel-title {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.panel-title h2 {
  margin: 0;
  color: var(--pink-700);
}

.panel-title p {
  margin: 6px 0 0;
  color: var(--pink-400);
}

.pill {
  align-self: center;
  padding: 8px 14px;
  border-radius: 999px;
  background: rgba(255, 214, 232, 0.65);
  color: var(--pink-600);
  font-weight: 600;
}

.grid.two {
  display: grid;
  gap: 24px;
  grid-template-columns: minmax(320px, 420px) minmax(0, 1fr);
  align-items: start;
}

.grid.two.categories {
  grid-template-columns: minmax(280px, 360px) minmax(0, 1fr);
}

.category-search-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.category-search-bar .search {
  flex: 1;
  min-width: 300px;
}

.clear-search {
  border: none;
  border-radius: 10px;
  padding: 10px 16px;
  font-weight: 600;
  cursor: pointer;
  color: var(--pink-600);
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(243, 109, 161, 0.25);
  transition: all 0.2s ease;
  white-space: nowrap;
}

.clear-search:hover {
  background: var(--pink-100);
  border-color: var(--pink-400);
  transform: translateY(-1px);
}

.card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 22px;
  padding: 24px 26px;
  border: 1px solid rgba(243, 109, 161, 0.12);
  box-shadow: 0 24px 55px -48px rgba(243, 109, 161, 0.55);
  display: grid;
  gap: 18px;
  overflow: hidden;
}

.form-card {
  overflow-y: visible;
}

.form-card h3 {
  margin: 0;
  color: var(--pink-600);
  position: sticky;
  top: 0;
  background: rgba(255, 255, 255, 0.95);
  padding-bottom: 12px;
  z-index: 1;
}

label {
  display: grid;
  gap: 8px;
  font-weight: 600;
  color: var(--pink-500);
  font-size: 0.95rem;
}

input,
textarea,
select {
  border: 1px solid rgba(243, 109, 161, 0.22);
  border-radius: 14px;
  padding: 12px 14px;
  font: inherit;
  color: var(--pink-700);
  background: rgba(255, 255, 255, 0.85);
  box-shadow: inset 0 6px 18px -18px rgba(243, 109, 161, 0.6);
  width: 100%;
  box-sizing: border-box;
}

input:focus,
textarea:focus,
select:focus {
  outline: none;
  border-color: var(--pink-500);
  box-shadow: 0 0 0 3px rgba(243, 109, 161, 0.1);
}

textarea {
  resize: vertical;
  min-height: 96px;
}

.form-row {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
}

.form-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  width: 100%;
}

.form-actions button {
  flex: 1;
  min-width: 120px;
}

button {
  border: none;
  cursor: pointer;
  font-weight: 600;
}

.primary {
  border-radius: 14px;
  padding: 12px 20px;
  color: white;
  background: linear-gradient(135deg, var(--pink-400), var(--pink-500));
  box-shadow: 0 20px 42px -28px rgba(243, 109, 161, 0.75);
  transition: transform 0.2s ease;
}

.primary:hover:enabled {
  transform: translateY(-2px);
}

.ghost {
  border-radius: 14px;
  padding: 10px 16px;
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(243, 109, 161, 0.22);
  color: var(--pink-600);
}

.danger {
  border-radius: 14px;
  padding: 10px 16px;
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(214, 69, 107, 0.25);
  color: #d6456b;
}

.table-card {
  gap: 12px;
  padding: 18px 0;
  overflow: hidden;
}

.table-head,
.table-row {
  display: grid;
  grid-template-columns:
    minmax(100px, 0.8fr) minmax(180px, 2fr) minmax(120px, 1fr)
    minmax(100px, 1fr) minmax(100px, 1fr) minmax(140px, 1fr);
  align-items: center;
  gap: 16px;
  padding: 0 26px;
}

.table-head {
  font-size: 0.8rem;
  text-transform: uppercase;
  font-weight: 600;
  color: var(--pink-400);
}

.table-row {
  padding: 18px 26px;
  margin: 0 12px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.88);
  color: var(--pink-600);
  box-shadow: 0 16px 36px -30px rgba(243, 109, 161, 0.6);
}

.product-code code {
  padding: 4px 8px;
  background: var(--pink-100);
  border-radius: 6px;
  font-family: "Courier New", monospace;
  font-size: 0.9rem;
  color: var(--pink-700);
  font-weight: 600;
}

.product-info strong {
  color: var(--pink-700);
}

.product-info small {
  display: block;
  margin-top: 6px;
  color: var(--pink-300);
}

.image-cell {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.image-cell img {
  width: 64px;
  height: 64px;
  object-fit: cover;
  border-radius: 18px;
  box-shadow: 0 12px 24px -20px rgba(243, 109, 161, 0.55);
}

.placeholder {
  padding: 6px 10px;
  border-radius: 12px;
  background: rgba(255, 214, 232, 0.55);
  color: var(--pink-500);
}

.row-actions {
  display: flex;
  gap: 10px;
}

.empty {
  margin: 0;
  padding: 18px;
  text-align: center;
  color: var(--pink-400);
  font-weight: 600;
}

.category-list ul {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 12px;
}

.category-list li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.88);
  color: var(--pink-600);
  box-shadow: 0 14px 32px -28px rgba(243, 109, 161, 0.55);
}

.category-list strong {
  color: var(--pink-700);
}

.category-list small {
  display: block;
  margin-top: 4px;
  color: var(--pink-300);
}

/* Admin Management Styles */
.admin-list {
  display: grid;
  gap: 12px;
}

.admin-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 18px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.88);
  color: var(--pink-600);
  box-shadow: 0 14px 32px -28px rgba(243, 109, 161, 0.55);
  transition: all 0.3s ease;
}

.admin-item.current-admin {
  background: linear-gradient(
    135deg,
    rgba(255, 225, 240, 0.9),
    rgba(255, 240, 247, 0.9)
  );
  border: 2px solid var(--pink-400);
  box-shadow: 0 8px 24px -12px rgba(243, 109, 161, 0.45);
}

.admin-info {
  display: flex;
  align-items: center;
  gap: 14px;
}

.admin-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 1.2rem;
  color: white;
  background: linear-gradient(135deg, var(--pink-400), var(--pink-600));
  box-shadow: 0 8px 20px -12px rgba(243, 109, 161, 0.6);
  flex-shrink: 0;
}

.admin-info strong {
  color: var(--pink-700);
  font-size: 1.05rem;
}

.admin-info small {
  display: block;
  margin-top: 4px;
  color: var(--pink-500);
  font-weight: 600;
  font-size: 0.85rem;
}

.char-count {
  display: block;
  margin-top: 6px;
  font-size: 0.85rem;
  color: var(--pink-400);
  text-align: right;
}

/* Main Image Section */
.main-image-section {
  margin-bottom: 1rem;
}

.file-upload-main {
  margin-top: 0.5rem;
}

.upload-btn-main {
  width: 100%;
  padding: 0.75rem 1rem;
  background: var(--pink-100);
  color: var(--pink-700);
  border: 2px dashed var(--pink-400);
  border-radius: 10px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 600;
  transition: all 0.2s ease;
}

.upload-btn-main:hover {
  background: var(--pink-200);
  border-color: var(--pink-500);
}

.main-image-preview {
  margin-top: 1rem;
  text-align: center;
}

.main-image-preview img {
  max-width: 100%;
  max-height: 200px;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* Multiple Images Section */
.multiple-images-section {
  background: rgba(255, 225, 240, 0.3);
  padding: 1rem;
  border-radius: 14px;
  border: 2px dashed var(--pink-300);
  margin-top: 0.5rem;
  box-sizing: border-box;
  width: 100%;
}

.label-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.label-header span {
  font-weight: 600;
  color: var(--pink-700);
  font-size: 0.95rem;
}

.label-header small {
  background: var(--pink-500);
  color: white;
  padding: 0.2rem 0.6rem;
  border-radius: 12px;
  font-size: 0.8rem;
}

.image-input-group {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 0.75rem;
  width: 100%;
}

.image-input-group input {
  flex: 1;
  padding: 0.6rem 0.8rem;
  border-radius: 10px;
  border: 1px solid rgba(243, 109, 161, 0.22);
  font-size: 0.9rem;
  min-width: 0;
}

.add-image-btn {
  padding: 0.6rem 1.2rem;
  background: var(--pink-500);
  color: white;
  border: none;
  border-radius: 10px;
  font-weight: 600;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
}

.add-image-btn:hover {
  background: var(--pink-600);
  transform: translateY(-1px);
}

.file-upload-group {
  margin-bottom: 1rem;
  padding: 1rem;
  border: 2px dashed rgba(243, 109, 161, 0.3);
  border-radius: 12px;
  background: rgba(243, 109, 161, 0.05);
  text-align: center;
}

.file-upload-label {
  display: block;
  cursor: pointer;
}

.upload-btn {
  padding: 0.6rem 1.5rem;
  background: linear-gradient(135deg, var(--pink-500), var(--pink-600));
  color: white;
  border: none;
  border-radius: 10px;
  font-weight: 600;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(243, 109, 161, 0.3);
}

.upload-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(243, 109, 161, 0.4);
}

.file-upload-group .hint {
  display: block;
  margin-top: 0.5rem;
  color: #999;
  font-size: 0.85rem;
}

.image-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  max-height: 300px;
  overflow-y: auto;
  padding: 0.25rem;
  margin-top: 0.5rem;
}

.image-list::-webkit-scrollbar {
  width: 6px;
}

.image-list::-webkit-scrollbar-track {
  background: var(--pink-100);
  border-radius: 3px;
}

.image-list::-webkit-scrollbar-thumb {
  background: var(--pink-400);
  border-radius: 3px;
}

.image-item {
  display: flex;
  gap: 0.75rem;
  align-items: center;
  background: white;
  padding: 0.5rem;
  border-radius: 10px;
  box-shadow: 0 2px 6px rgba(243, 109, 161, 0.1);
  width: 100%;
  box-sizing: border-box;
  min-width: 0;
}

.image-item img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 6px;
  background: var(--pink-50);
  flex-shrink: 0;
}

.image-item-info {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 0.85rem;
  color: var(--pink-600);
}

.image-actions {
  display: flex;
  gap: 0.25rem;
  margin-left: auto;
  flex-shrink: 0;
}

.image-actions button {
  padding: 0.4rem 0.6rem;
  background: rgba(243, 109, 161, 0.1);
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 0.9rem;
  line-height: 1;
}

.image-actions button:hover:not(:disabled) {
  background: rgba(243, 109, 161, 0.2);
  transform: scale(1.1);
}

.image-actions button:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.image-actions .delete-btn:hover:not(:disabled) {
  background: rgba(214, 69, 107, 0.2);
}

.hint {
  display: block;
  color: var(--pink-500);
  font-style: italic;
  text-align: center;
  padding: 0.75rem;
  font-size: 0.9rem;
}

/* Pagination */
.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  margin-top: 1.5rem;
  padding-top: 1.5rem;
  border-top: 2px solid var(--pink-200);
}

.page-btn {
  padding: 0.6rem 1rem;
  background: var(--pink-100);
  border: 2px solid var(--pink-300);
  border-radius: 12px;
  color: var(--pink-700);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 0.95rem;
}

.page-btn:hover:not(:disabled) {
  background: var(--pink-200);
  border-color: var(--pink-500);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(243, 109, 161, 0.2);
}

.page-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
  background: var(--pink-50);
}

.page-numbers {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
  justify-content: center;
}

.page-number {
  width: 40px;
  height: 40px;
  padding: 0;
  background: white;
  border: 2px solid var(--pink-300);
  border-radius: 10px;
  color: var(--pink-700);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 0.95rem;
}

.page-number:hover {
  background: var(--pink-100);
  border-color: var(--pink-500);
  transform: scale(1.1);
}

.page-number.active {
  background: var(--pink-500);
  border-color: var(--pink-600);
  color: white;
  box-shadow: 0 4px 12px rgba(243, 109, 161, 0.3);
}

.fade-enter-active,
.fade-leave-active,
.list-enter-active,
.list-leave-active {
  transition: opacity 0.25s ease, transform 0.25s ease;
}

.fade-enter-from,
.fade-leave-to,
.list-enter-from,
.list-leave-to {
  opacity: 0;
  transform: translateY(6px);
}

@media (max-width: 1280px) {
  .admin-page {
    grid-template-columns: 240px 1fr;
  }

  .grid.two,
  .grid.two.categories {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 960px) {
  .admin-page {
    grid-template-columns: 1fr;
  }

  .sidebar {
    flex-direction: row;
    align-items: flex-start;
    overflow-x: auto;
    border-right: none;
    border-bottom: 1px solid rgba(243, 109, 161, 0.18);
  }

  .sidebar-stats {
    min-width: 220px;
  }

  .main {
    padding: 32px 24px 48px;
  }

  .header-controls {
    width: 100%;
    align-items: stretch;
  }

  .user-chip {
    justify-content: space-between;
  }

  .table-head,
  .table-row {
    grid-template-columns: minmax(200px, 2fr) minmax(140px, 1fr) minmax(
        1px,
        1fr
      );
    grid-template-areas:
      "info category price"
      "image image actions";
    row-gap: 12px;
  }

  .table-row .product-info {
    grid-area: info;
  }

  .table-row div:nth-child(2) {
    grid-area: category;
  }

  .table-row div:nth-child(3) {
    grid-area: price;
  }

  .table-row .image-cell {
    grid-area: image;
  }

  .table-row .row-actions {
    grid-area: actions;
  }
}

@media (max-width: 640px) {
  .page-header {
    flex-direction: column;
  }

  .header-controls {
    align-items: stretch;
  }

  .user-chip {
    flex-wrap: wrap;
    gap: 10px;
  }

  .logout {
    width: 100%;
    text-align: center;
  }

  .filters {
    width: 100%;
    flex-direction: column;
    align-items: stretch;
  }

  .search,
  .filter-select,
  .form-actions button {
    width: 100%;
  }

  .sidebar {
    padding: 24px;
  }

  .logo-image {
    max-width: 200px;
    height: 80px;
  }
}

/* Toast Notifications */
.toast-container {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-width: 420px;
  pointer-events: none;
}

.toast {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  border-left: 4px solid var(--pink-500);
  pointer-events: auto;
  animation: slideInRight 0.3s ease-out;
  min-width: 320px;
}

.toast.success {
  border-left-color: #10b981;
}

.toast.error {
  border-left-color: #ef4444;
}

.toast.warning {
  border-left-color: #f59e0b;
}

.toast-icon {
  font-size: 24px;
  flex-shrink: 0;
}

.toast-content {
  flex: 1;
}

.toast-content strong {
  display: block;
  font-size: 15px;
  color: #1f2937;
  margin-bottom: 4px;
}

.toast-content p {
  margin: 0;
  font-size: 14px;
  color: #6b7280;
  line-height: 1.5;
}

.toast-close {
  background: none;
  border: none;
  cursor: pointer;
  color: #9ca3af;
  font-size: 20px;
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: all 0.2s;
  flex-shrink: 0;
}

.toast-close:hover {
  background: #f3f4f6;
  color: #374151;
}

/* Toast Animations */
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from {
  opacity: 0;
  transform: translateX(100px);
}

.toast-leave-to {
  opacity: 0;
  transform: translateX(50px);
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(100px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@media (max-width: 640px) {
  .toast-container {
    left: 10px;
    right: 10px;
    top: 10px;
    max-width: none;
  }

  .toast {
    min-width: auto;
  }
}

/* Error states for inputs */
input.error,
textarea.error,
select.error {
  border-color: #ef4444;
  background: rgba(254, 242, 242, 0.5);
}

input.error:focus,
textarea.error:focus,
select.error:focus {
  border-color: #dc2626;
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1);
}

.error-message {
  color: #dc2626;
  font-size: 0.85rem;
  font-weight: 500;
  margin-top: -4px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.char-count {
  font-size: 0.85rem;
  color: var(--pink-400);
  text-align: right;
  margin-top: -4px;
}

/* Image Uploader Section Styling */
.image-uploader-section {
  margin-bottom: 20px;
  padding: 16px;
  background: linear-gradient(
    135deg,
    rgba(251, 207, 232, 0.1) 0%,
    rgba(244, 114, 182, 0.05) 100%
  );
  border: 2px dashed var(--pink-200);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.image-uploader-section:hover {
  border-color: var(--pink-400);
  background: linear-gradient(
    135deg,
    rgba(251, 207, 232, 0.15) 0%,
    rgba(244, 114, 182, 0.08) 100%
  );
  box-shadow: 0 4px 12px rgba(244, 114, 182, 0.1);
}

.image-uploader-section:focus-within {
  border-color: var(--pink-500);
  border-style: solid;
  background: linear-gradient(
    135deg,
    rgba(251, 207, 232, 0.2) 0%,
    rgba(244, 114, 182, 0.1) 100%
  );
  box-shadow: 0 0 0 4px rgba(244, 114, 182, 0.15);
}
</style>
