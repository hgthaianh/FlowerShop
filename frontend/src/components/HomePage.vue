<template>
  <div class="home-page">
    <SiteNavbar
      @search="handleSearch"
      @select-category="handleCategorySelect"
    />

    <!-- Hero Section -->
    <section class="hero">
      <div class="hero-content">
        <h1 class="hero-title">
          Chào mừng đến với <span class="gradient-text">Ther Florist</span>
        </h1>
        <p class="hero-subtitle">
          Mang vẻ đẹp của thiên nhiên đến từng không gian sống
        </p>
        <button class="hero-btn" @click="scrollToProducts">
          Khám phá ngay
          <svg
            width="20"
            height="20"
            viewBox="0 0 20 20"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              d="M5 10H15M15 10L10 5M15 10L10 15"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
          </svg>
        </button>
      </div>
      <div class="hero-decoration">
        <div class="flower-decoration flower-1">🌸</div>
        <div class="flower-decoration flower-2">🌺</div>
        <div class="flower-decoration flower-3">🌷</div>
        <div class="flower-decoration flower-4">🌹</div>
      </div>
    </section>

    <!-- Filter Section -->
    <section class="filter-section" ref="productsSection">
      <div class="container">
        <div class="filter-header">
          <h2 class="section-title">Sản phẩm của chúng tôi</h2>
          <div class="filter-controls">
            <button
              v-if="selectedCategory"
              class="clear-filter"
              @click="clearCategory"
            >
              ✕ {{ selectedCategory.name }}
            </button>
            <select v-model="sortBy" class="sort-select">
              <option value="">Sắp xếp theo</option>
              <option value="price-asc">Giá: Thấp đến Cao</option>
              <option value="price-desc">Giá: Cao đến Thấp</option>
              <option value="name">Tên A-Z</option>
            </select>
          </div>
        </div>

        <!-- Products Grid -->
        <div v-if="loading" class="loading">
          <div class="spinner"></div>
          <p>Đang tải sản phẩm...</p>
        </div>

        <div v-else-if="error" class="error">
          <p>{{ error }}</p>
        </div>

        <div v-else-if="filteredProducts.length === 0" class="no-products">
          <svg
            width="100"
            height="100"
            viewBox="0 0 100 100"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <circle cx="50" cy="50" r="40" fill="var(--pink-100)" />
            <path
              d="M50 30V50M50 70V70.01"
              stroke="var(--pink-500)"
              stroke-width="4"
              stroke-linecap="round"
            />
          </svg>
          <p>Không tìm thấy sản phẩm nào</p>
        </div>

        <div v-else class="products-grid">
          <div
            v-for="product in paginatedProducts"
            :key="product.id"
            class="product-card"
            @click="goToProduct(product.id)"
          >
            <div class="product-image">
              <img
                :src="
                  product.imageUrl ||
                  'https://via.placeholder.com/300x300/FFE1F0/F36DA1?text=Flower'
                "
                :alt="product.name"
                @error="handleImageError"
              />
              <div class="product-badge" v-if="product.collection">
                {{ product.collection.name }}
              </div>
            </div>
            <div class="product-info">
              <h3 class="product-name">{{ product.name }}</h3>
              <p class="product-description">{{ product.description }}</p>
              <div class="product-footer">
                <span class="product-price">{{
                  formatPrice(product.price)
                }}</span>
                <button
                  class="product-btn"
                  @click.stop="goToProduct(product.id)"
                >
                  Xem chi tiết
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Pagination -->
        <div v-if="totalPages > 1" class="pagination">
          <button
            class="page-btn"
            @click="prevPage"
            :disabled="currentPage === 1"
          >
            ◀ Trước
          </button>

          <div class="page-numbers">
            <button
              v-for="page in displayPages"
              :key="page"
              class="page-number"
              :class="{ active: page === currentPage }"
              @click="goToPage(page)"
            >
              {{ page }}
            </button>
          </div>

          <button
            class="page-btn"
            @click="nextPage"
            :disabled="currentPage === totalPages"
          >
            Sau ▶
          </button>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-section">
            <h3>Ther Florist</h3>
            <p>Mang vẻ đẹp thiên nhiên đến từng không gian sống</p>
          </div>
          <div class="footer-section">
            <h4>Liên hệ</h4>
            <p>📞 0931 772 074</p>
            <p>✉️ contact@flowershop.vn</p>
          </div>
          <div class="footer-section">
            <h4>Giờ làm việc</h4>
            <p>Thứ 2 - Thứ 7: 8:00 - 20:00</p>
            <p>Chủ nhật: 9:00 - 18:00</p>
          </div>
        </div>
        <div class="footer-bottom">
          <p>&copy; 2025 Ther Florist. All rights reserved.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";
import SiteNavbar from "./SiteNavbar.vue";
import API from "../config/api";

const router = useRouter();

const products = ref([]);
const loading = ref(true);
const error = ref(null);
const searchQuery = ref("");
const selectedCategory = ref(null);
const sortBy = ref("");
const productsSection = ref(null);

// Pagination
const currentPage = ref(1);
const itemsPerPage = 12;

onMounted(async () => {
  await fetchProducts();
});

const fetchProducts = async (categoryId = null) => {
  try {
    loading.value = true;
    error.value = null;

    let url = categoryId
      ? API.categories.getProducts(categoryId)
      : API.products.getAll();

    const response = await axios.get(url);
    products.value = response.data;
  } catch (err) {
    error.value = "Không thể tải sản phẩm. Vui lòng thử lại sau.";
    console.error("Lỗi khi tải sản phẩm:", err);
  } finally {
    loading.value = false;
  }
};

const handleSearch = (query) => {
  searchQuery.value = query;
};

const handleCategorySelect = async (category) => {
  selectedCategory.value = category;
  await fetchProducts(category.id);
  currentPage.value = 1;
  scrollToProducts();
};

const clearCategory = async () => {
  selectedCategory.value = null;
  currentPage.value = 1;
  await fetchProducts();
};

const scrollToProducts = () => {
  if (productsSection.value) {
    productsSection.value.scrollIntoView({ behavior: "smooth" });
  }
};

const filteredProducts = computed(() => {
  let result = [...products.value];

  // Filter by search query (client-side for now)
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(
      (product) =>
        product.name.toLowerCase().includes(query) ||
        product.description?.toLowerCase().includes(query)
    );
  }

  // Category filter is handled by API call, no need to filter here

  // Sort
  if (sortBy.value === "price-asc") {
    result.sort((a, b) => a.price - b.price);
  } else if (sortBy.value === "price-desc") {
    result.sort((a, b) => b.price - a.price);
  } else if (sortBy.value === "name") {
    result.sort((a, b) => a.name.localeCompare(b.name, "vi"));
  }

  return result;
});

const totalPages = computed(() =>
  Math.ceil(filteredProducts.value.length / itemsPerPage)
);

const paginatedProducts = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return filteredProducts.value.slice(start, end);
});

const displayPages = computed(() => {
  const pages = [];
  const total = totalPages.value;
  const current = currentPage.value;

  if (total <= 7) {
    for (let i = 1; i <= total; i++) {
      pages.push(i);
    }
  } else {
    if (current <= 4) {
      for (let i = 1; i <= 5; i++) pages.push(i);
      pages.push("...");
      pages.push(total);
    } else if (current >= total - 3) {
      pages.push(1);
      pages.push("...");
      for (let i = total - 4; i <= total; i++) pages.push(i);
    } else {
      pages.push(1);
      pages.push("...");
      for (let i = current - 1; i <= current + 1; i++) pages.push(i);
      pages.push("...");
      pages.push(total);
    }
  }

  return pages;
});

const goToPage = (page) => {
  if (typeof page === "number" && page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
    scrollToProducts();
  }
};

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
    scrollToProducts();
  }
};

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--;
    scrollToProducts();
  }
};

const formatPrice = (price) => {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(price);
};

const handleImageError = (e) => {
  e.target.src =
    "https://via.placeholder.com/300x300/FFE1F0/F36DA1?text=Flower";
};

const goToProduct = (productId) => {
  router.push(`/products/${productId}`);
};
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: linear-gradient(
    135deg,
    rgba(255, 246, 251, 1),
    rgba(255, 225, 240, 0.5)
  );
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 2rem;
}

/* Hero Section */
.hero {
  position: relative;
  padding: 5rem 2rem;
  text-align: center;
  overflow: hidden;
}

.hero-content {
  position: relative;
  z-index: 1;
  max-width: 800px;
  margin: 0 auto;
}

.hero-title {
  font-size: 3.5rem;
  font-weight: 700;
  color: var(--pink-700);
  margin-bottom: 1rem;
  line-height: 1.2;
}

.gradient-text {
  background: linear-gradient(135deg, var(--pink-600), var(--pink-400));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-subtitle {
  font-size: 1.3rem;
  color: var(--pink-600);
  margin-bottom: 2.5rem;
  font-weight: 400;
}

.hero-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.8rem;
  padding: 1rem 2.5rem;
  background: linear-gradient(135deg, var(--pink-500), var(--pink-400));
  color: white;
  border: none;
  border-radius: 50px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 20px rgba(243, 109, 161, 0.3);
}

.hero-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 30px rgba(243, 109, 161, 0.4);
}

.hero-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.flower-decoration {
  position: absolute;
  font-size: 3rem;
  animation: float 3s ease-in-out infinite;
  opacity: 0.6;
}

.flower-1 {
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.flower-2 {
  top: 20%;
  right: 15%;
  animation-delay: 0.5s;
}

.flower-3 {
  bottom: 20%;
  left: 15%;
  animation-delay: 1s;
}

.flower-4 {
  bottom: 10%;
  right: 10%;
  animation-delay: 1.5s;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(10deg);
  }
}

/* Filter Section */
.filter-section {
  padding: 3rem 0;
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2.5rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.section-title {
  font-size: 2rem;
  font-weight: 700;
  color: var(--pink-700);
}

.filter-controls {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.clear-filter {
  padding: 0.6rem 1.2rem;
  background: var(--pink-200);
  color: var(--pink-700);
  border: none;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.clear-filter:hover {
  background: var(--pink-300);
}

.sort-select {
  padding: 0.6rem 1.2rem;
  border: 2px solid var(--pink-200);
  border-radius: 8px;
  font-family: "Be Vietnam Pro", sans-serif;
  font-weight: 500;
  color: var(--pink-700);
  cursor: pointer;
  outline: none;
  transition: all 0.3s ease;
}

.sort-select:focus {
  border-color: var(--pink-400);
}

/* Loading & Error States */
.loading,
.error,
.no-products {
  text-align: center;
  padding: 4rem 2rem;
  color: var(--pink-600);
}

.spinner {
  width: 50px;
  height: 50px;
  border: 4px solid var(--pink-200);
  border-top-color: var(--pink-500);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.no-products svg {
  margin-bottom: 1rem;
}

/* Products Grid */
.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 2rem;
  margin-bottom: 3rem;
}

.product-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 2px 12px rgba(243, 109, 161, 0.08);
  cursor: pointer;
}

.product-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 30px rgba(243, 109, 161, 0.2);
}

.product-image {
  position: relative;
  width: 100%;
  padding-top: 100%;
  overflow: hidden;
  background: var(--pink-100);
}

.product-image img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-card:hover .product-image img {
  transform: scale(1.1);
}

.product-badge {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: linear-gradient(135deg, var(--pink-500), var(--pink-400));
  color: white;
  padding: 0.4rem 1rem;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
}

.product-info {
  padding: 1.5rem;
}

.product-name {
  font-size: 1.2rem;
  font-weight: 600;
  color: var(--pink-700);
  margin-bottom: 0.5rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-description {
  font-size: 0.95rem;
  color: var(--pink-600);
  margin-bottom: 1rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 1rem;
}

.product-price {
  font-size: 1.3rem;
  font-weight: 700;
  color: var(--pink-600);
}

.product-btn {
  padding: 0.6rem 1.2rem;
  background: var(--pink-100);
  color: var(--pink-600);
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.product-btn:hover {
  background: var(--pink-500);
  color: white;
}

/* Pagination */
.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  margin-top: 3rem;
  padding: 2rem 0;
}

.page-btn {
  padding: 0.75rem 1.5rem;
  background: linear-gradient(135deg, var(--pink-100), var(--pink-200));
  border: 2px solid var(--pink-300);
  border-radius: 12px;
  color: var(--pink-700);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 1rem;
  box-shadow: 0 2px 8px rgba(243, 109, 161, 0.1);
}

.page-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, var(--pink-200), var(--pink-300));
  border-color: var(--pink-500);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(243, 109, 161, 0.2);
}

.page-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
  background: var(--pink-50);
  box-shadow: none;
}

.page-numbers {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
  justify-content: center;
}

.page-number {
  min-width: 45px;
  height: 45px;
  padding: 0 0.5rem;
  background: white;
  border: 2px solid var(--pink-300);
  border-radius: 12px;
  color: var(--pink-700);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 1rem;
  box-shadow: 0 2px 8px rgba(243, 109, 161, 0.1);
}

.page-number:hover {
  background: var(--pink-100);
  border-color: var(--pink-500);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(243, 109, 161, 0.2);
}

.page-number.active {
  background: linear-gradient(135deg, var(--pink-500), var(--pink-600));
  border-color: var(--pink-700);
  color: white;
  box-shadow: 0 4px 16px rgba(243, 109, 161, 0.4);
  transform: scale(1.1);
}

/* Footer */
.footer {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-top: 1px solid var(--pink-200);
  padding: 3rem 0 1.5rem;
  margin-top: 4rem;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  margin-bottom: 2rem;
}

.footer-section h3 {
  color: var(--pink-600);
  font-size: 1.5rem;
  margin-bottom: 1rem;
}

.footer-section h4 {
  color: var(--pink-600);
  font-size: 1.2rem;
  margin-bottom: 1rem;
}

.footer-section p {
  color: var(--pink-700);
  margin-bottom: 0.5rem;
}

.footer-bottom {
  text-align: center;
  padding-top: 1.5rem;
  border-top: 1px solid var(--pink-200);
  color: var(--pink-600);
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 2.5rem;
  }

  .hero-subtitle {
    font-size: 1.1rem;
  }

  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 1.5rem;
  }

  .pagination {
    gap: 0.5rem;
    padding: 1.5rem 0;
  }

  .page-btn {
    padding: 0.5rem 1rem;
    font-size: 0.9rem;
  }

  .page-number {
    min-width: 38px;
    height: 38px;
    font-size: 0.9rem;
  }

  .page-numbers {
    gap: 0.35rem;
  }
}
</style>
