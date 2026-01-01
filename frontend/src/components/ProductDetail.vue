<template>
  <div class="product-detail-page">
    <SiteNavbar />

    <!-- Loading State -->
    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <p>Đang tải thông tin sản phẩm...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="error-container">
      <div class="error-icon">⚠️</div>
      <h2>Không thể tải sản phẩm</h2>
      <p>{{ error }}</p>
      <router-link to="/" class="back-btn">Quay về trang chủ</router-link>
    </div>

    <!-- Product Detail -->
    <div v-else-if="product" class="product-detail-container" ref="topOfPage">
      <div class="container">
        <!-- Breadcrumb -->
        <nav class="breadcrumb">
          <router-link to="/">Trang chủ</router-link>
          <span class="separator">/</span>
          <span v-if="product.category">
            <a href="#" @click.prevent="goToCategory">{{
              product.category.name
            }}</a>
            <span class="separator">/</span>
          </span>
          <span class="current">{{ product.name }}</span>
        </nav>

        <!-- Product Content -->
        <div class="product-content">
          <!-- Product Image Gallery -->
          <div class="product-image-section">
            <div class="image-gallery-wrapper">
              <!-- Thumbnails - Left Side -->
              <div
                v-if="allImages.length > 1"
                class="image-thumbnails-vertical"
              >
                <button
                  v-for="(image, index) in allImages"
                  :key="index"
                  class="thumbnail-vertical"
                  :class="{ active: currentImage === image }"
                  @click="currentImage = image"
                  :title="`Ảnh ${index + 1}`"
                >
                  <img
                    :src="image"
                    :alt="`${product.name} - ảnh ${index + 1}`"
                    @error="handleImageError"
                  />
                </button>
              </div>

              <!-- Main Image - Right Side -->
              <div class="main-image">
                <img
                  :src="currentImage"
                  :alt="product.name"
                  @error="handleImageError"
                />
                <div v-if="product.collection" class="collection-badge">
                  {{ product.collection.name }}
                </div>
              </div>
            </div>
          </div>

          <!-- Product Info -->
          <div class="product-info-section">
            <h1 class="product-title">{{ product.name }}</h1>

            <div class="product-meta">
              <span v-if="product.category" class="category-tag">
                📂 {{ product.category.name }}
              </span>
            </div>

            <div class="product-price">
              {{ formatPrice(product.price) }}
            </div>

            <div class="product-description">
              <h3>
                <svg
                  width="24"
                  height="24"
                  viewBox="0 0 24 24"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <path
                    d="M4 7h16M4 12h16M4 17h10"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                  />
                </svg>
                Mô tả sản phẩm
              </h3>
              <div class="description-content">
                <p class="description-text">
                  {{ product.description || "Chưa có mô tả cho sản phẩm này." }}
                </p>
              </div>
            </div>

            <div class="product-features">
              <h3>
                <svg
                  width="24"
                  height="24"
                  viewBox="0 0 24 24"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <path
                    d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                  />
                </svg>
                Đặc điểm nổi bật
              </h3>
              <ul class="features-list">
                <li>🌸 Hoa tươi 100% chất lượng cao</li>
                <li>🎨 Thiết kế độc đáo, sang trọng</li>
                <li>🚚 Giao hàng nhanh chóng, tận nơi</li>
                <li>💝 Tặng kèm thiệp chúc mừng</li>
                <li>✨ Bảo quản hoa tươi lâu</li>
              </ul>
            </div>

            <div class="contact-section">
              <h3>
                <svg
                  width="24"
                  height="24"
                  viewBox="0 0 24 24"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <path
                    d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                  />
                </svg>
                Liên hệ đặt hàng
              </h3>
              <div class="contact-buttons">
                <a
                  href="https://www.facebook.com/ngoccam.nguyenthi.96387"
                  target="_blank"
                  rel="noopener noreferrer"
                  class="contact-btn facebook"
                >
                  <svg
                    width="24"
                    height="24"
                    viewBox="0 0 24 24"
                    fill="currentColor"
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <path
                      d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"
                    />
                  </svg>
                  <span>Facebook</span>
                </a>
                <a
                  href="https://zalo.me/0931772074"
                  target="_blank"
                  rel="noopener noreferrer"
                  class="contact-btn zalo"
                >
                  <svg
                    width="24"
                    height="24"
                    viewBox="0 0 24 24"
                    fill="currentColor"
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <path
                      d="M12 0C5.373 0 0 4.975 0 11.111c0 3.497 1.745 6.616 4.472 8.652v4.237l4.086-2.242c1.039.287 2.141.442 3.442.442 6.627 0 12-4.974 12-11.11C24 4.974 18.627 0 12 0zm.5 14.951l-3.083-3.285-6.017 3.285L9.633 8.2l3.161 3.285L18.9 8.2l-6.4 6.751z"
                    />
                  </svg>
                  <span>Zalo</span>
                </a>
                <a
                  href="https://www.instagram.com/ther.florist/"
                  target="_blank"
                  rel="noopener noreferrer"
                  class="contact-btn instagram"
                >
                  <svg
                    width="24"
                    height="24"
                    viewBox="0 0 24 24"
                    fill="currentColor"
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <path
                      d="M12 0C8.74 0 8.333.015 7.053.072 5.775.132 4.905.333 4.14.63c-.789.306-1.459.717-2.126 1.384S.935 3.35.63 4.14C.333 4.905.131 5.775.072 7.053.012 8.333 0 8.74 0 12s.015 3.667.072 4.947c.06 1.277.261 2.148.558 2.913.306.788.717 1.459 1.384 2.126.667.666 1.336 1.079 2.126 1.384.766.296 1.636.499 2.913.558C8.333 23.988 8.74 24 12 24s3.667-.015 4.947-.072c1.277-.06 2.148-.262 2.913-.558.788-.306 1.459-.718 2.126-1.384.666-.667 1.079-1.335 1.384-2.126.296-.765.499-1.636.558-2.913.06-1.28.072-1.687.072-4.947s-.015-3.667-.072-4.947c-.06-1.277-.262-2.149-.558-2.913-.306-.789-.718-1.459-1.384-2.126C21.319 1.347 20.651.935 19.86.63c-.765-.297-1.636-.499-2.913-.558C15.667.012 15.26 0 12 0zm0 2.16c3.203 0 3.585.016 4.85.071 1.17.055 1.805.249 2.227.415.562.217.96.477 1.382.896.419.42.679.819.896 1.381.164.422.36 1.057.413 2.227.057 1.266.07 1.646.07 4.85s-.015 3.585-.074 4.85c-.061 1.17-.256 1.805-.421 2.227-.224.562-.479.96-.899 1.382-.419.419-.824.679-1.38.896-.42.164-1.065.36-2.235.413-1.274.057-1.649.07-4.859.07-3.211 0-3.586-.015-4.859-.074-1.171-.061-1.816-.256-2.236-.421-.569-.224-.96-.479-1.379-.899-.421-.419-.69-.824-.9-1.38-.165-.42-.359-1.065-.42-2.235-.045-1.26-.061-1.649-.061-4.844 0-3.196.016-3.586.061-4.861.061-1.17.255-1.814.42-2.234.21-.57.479-.96.9-1.381.419-.419.81-.689 1.379-.898.42-.166 1.051-.361 2.221-.421 1.275-.045 1.65-.06 4.859-.06l.045.03zm0 3.678c-3.405 0-6.162 2.76-6.162 6.162 0 3.405 2.76 6.162 6.162 6.162 3.405 0 6.162-2.76 6.162-6.162 0-3.405-2.76-6.162-6.162-6.162zM12 16c-2.21 0-4-1.79-4-4s1.79-4 4-4 4 1.79 4 4-1.79 4-4 4zm7.846-10.405c0 .795-.646 1.44-1.44 1.44-.795 0-1.44-.646-1.44-1.44 0-.794.646-1.439 1.44-1.439.793-.001 1.44.645 1.44 1.439z"
                    />
                  </svg>
                  <span>Instagram</span>
                </a>
              </div>
            </div>

            <div class="product-actions">
              <button class="add-to-cart-btn" @click="addToCart">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M9 20C9 21.1 8.1 22 7 22C5.9 22 5 21.1 5 20C5 18.9 5.9 18 7 18C8.1 18 9 18.9 9 20ZM17 20C17 21.1 16.1 22 15 22C13.9 22 13 21.1 13 20C13 18.9 13.9 18 15 18C16.1 18 17 18.9 17 20ZM7.17 14.75L7.2 14.63L8.1 13H15.55C16.3 13 16.96 12.59 17.3 11.97L21.16 4.96L19.42 4H19.41L18.31 6L15.55 11H8.53L8.4 10.73L6.16 6L5.21 4L4.27 2H1V4H3L6.6 11.59L5.25 14.03C5.09 14.32 5 14.65 5 15C5 16.1 5.9 17 7 17H19V15H7.42C7.29 15 7.17 14.89 7.17 14.75Z" fill="currentColor"/>
                </svg>
                Thêm vào giỏ
              </button>
              <button class="share-btn" @click="shareProduct">
                <svg
                  width="20"
                  height="20"
                  viewBox="0 0 20 20"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <circle
                    cx="15"
                    cy="5"
                    r="2"
                    stroke="currentColor"
                    stroke-width="2"
                  />
                  <circle
                    cx="5"
                    cy="10"
                    r="2"
                    stroke="currentColor"
                    stroke-width="2"
                  />
                  <circle
                    cx="15"
                    cy="15"
                    r="2"
                    stroke="currentColor"
                    stroke-width="2"
                  />
                  <path
                    d="M7 9L13 6M7 11L13 14"
                    stroke="currentColor"
                    stroke-width="2"
                  />
                </svg>
                Chia sẻ sản phẩm
              </button>
            </div>

            <div class="product-info-grid">
              <div class="info-item">
                <span class="info-label">Mã sản phẩm:</span>
                <span class="info-value"
                  ><code>{{
                    product.productCode || `#${product.id}`
                  }}</code></span
                >
              </div>
              <div v-if="product.category" class="info-item">
                <span class="info-label">Danh mục:</span>
                <span class="info-value">{{ product.category.name }}</span>
              </div>
              <div v-if="product.collection" class="info-item">
                <span class="info-label">Bộ sưu tập:</span>
                <span class="info-value">{{ product.collection.name }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Related Products -->
        <section v-if="relatedProducts.length > 0" class="related-products">
          <h2 class="section-title">
            <svg
              width="32"
              height="32"
              viewBox="0 0 24 24"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path
                d="M4 6h16M4 12h16M4 18h16"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
              />
            </svg>
            Sản phẩm tương tự
          </h2>
          <div class="products-slider-container">
            <button
              v-if="relatedProducts.length > 4"
              class="slider-btn prev"
              @click="scrollProducts('left')"
              :disabled="scrollPosition <= 0"
            >
              <svg
                width="24"
                height="24"
                viewBox="0 0 24 24"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  d="M15 18l-6-6 6-6"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                />
              </svg>
            </button>
            <div class="products-slider" ref="productsSlider">
              <div
                v-for="relatedProduct in relatedProducts"
                :key="relatedProduct.id"
                class="product-card"
                @click="goToProduct(relatedProduct.id)"
              >
                <div class="product-image">
                  <img
                    :src="
                      relatedProduct.imageUrl ||
                      'https://via.placeholder.com/300x300/FFE1F0/F36DA1?text=Flower'
                    "
                    :alt="relatedProduct.name"
                    @error="handleImageError"
                  />
                  <div class="product-overlay">
                    <span class="view-detail">Xem chi tiết</span>
                  </div>
                </div>
                <div class="product-info">
                  <h3 class="product-name">{{ relatedProduct.name }}</h3>
                  <div class="product-footer">
                    <span class="product-price">{{
                      formatPrice(relatedProduct.price)
                    }}</span>
                    <div class="product-icon">→</div>
                  </div>
                </div>
              </div>
            </div>
            <button
              v-if="relatedProducts.length > 4"
              class="slider-btn next"
              @click="scrollProducts('right')"
              :disabled="scrollPosition >= maxScroll"
            >
              <svg
                width="24"
                height="24"
                viewBox="0 0 24 24"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  d="M9 18l6-6-6-6"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                />
              </svg>
            </button>
          </div>
        </section>
      </div>
    </div>

    <!-- Footer -->
    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-section">
            <h3>Flower Shop</h3>
            <p>Mang vẻ đẹp thiên nhiên đến từng không gian sống</p>
          </div>
          <div class="footer-section">
            <h4>Liên hệ</h4>
            <p>📞 0123 456 789</p>
            <p>✉️ contact@flowershop.vn</p>
          </div>
          <div class="footer-section">
            <h4>Giờ làm việc</h4>
            <p>Thứ 2 - Thứ 7: 8:00 - 20:00</p>
            <p>Chủ nhật: 9:00 - 18:00</p>
          </div>
        </div>
        <div class="footer-bottom">
          <p>&copy; 2024 Flower Shop. All rights reserved.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch, nextTick } from "vue";
import { useRoute, useRouter } from "vue-router";
import axios from "axios";
import SiteNavbar from "./SiteNavbar.vue";
import API from "../config/api";
import { cart } from "../utils/cart";

const route = useRoute();
const router = useRouter();

const product = ref(null);
const loading = ref(true);
const error = ref(null);
const relatedProducts = ref([]);
const currentImage = ref("");
const topOfPage = ref(null);
const productsSlider = ref(null);
const scrollPosition = ref(0);
const maxScroll = ref(0);

const productId = computed(() => route.params.id);

// Computed: Get all images from product (imageUrl + imageUrls)
const allImages = computed(() => {
  if (!product.value) return [];
  const images = [];

  // Add main image first
  if (product.value.imageUrl) {
    images.push(product.value.imageUrl);
  }

  // Add additional images
  if (
    product.value.imageUrls &&
    Array.isArray(product.value.imageUrls) &&
    product.value.imageUrls.length > 0
  ) {
    images.push(...product.value.imageUrls);
  }

  // Return placeholder if no images
  return images.length > 0
    ? images
    : ["https://via.placeholder.com/600x600/FFE1F0/F36DA1?text=Flower"];
});

// Watch for route changes
watch(productId, async (newId) => {
  if (newId) {
    await fetchProduct();
    await fetchRelatedProducts();
    scrollToTop();
  }
});

onMounted(async () => {
  await fetchProduct();
  await fetchRelatedProducts();
  scrollToTop();
});

const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: "smooth",
  });
};

const fetchProduct = async () => {
  try {
    loading.value = true;
    error.value = null;
    const response = await axios.get(API.products.getById(productId.value));
    product.value = response.data;

    // Set current image to first available image
    currentImage.value = allImages.value[0];
  } catch (err) {
    error.value = "Không thể tải thông tin sản phẩm. Vui lòng thử lại sau.";
    console.error("Lỗi khi tải sản phẩm:", err);
  } finally {
    loading.value = false;
  }
};

const fetchRelatedProducts = async () => {
  try {
    // Fetch products from the same category
    if (product.value?.category?.id) {
      const response = await axios.get(
        API.categories.getProducts(product.value.category.id)
      );
      // Filter out current product and limit to 5 items
      relatedProducts.value = response.data
        .filter((p) => p.id !== product.value.id)
        .slice(0, 5);

      // Update scroll max position
      await nextTick();
      updateMaxScroll();
    }
  } catch (err) {
    console.error("Lỗi khi tải sản phẩm liên quan:", err);
  }
};

const updateMaxScroll = () => {
  if (productsSlider.value) {
    maxScroll.value =
      productsSlider.value.scrollWidth - productsSlider.value.clientWidth;
  }
};

const scrollProducts = (direction) => {
  if (!productsSlider.value) return;

  const scrollAmount = 320; // width of one card + gap
  const currentScroll = productsSlider.value.scrollLeft;

  if (direction === "left") {
    productsSlider.value.scrollTo({
      left: currentScroll - scrollAmount,
      behavior: "smooth",
    });
  } else {
    productsSlider.value.scrollTo({
      left: currentScroll + scrollAmount,
      behavior: "smooth",
    });
  }

  // Update scroll position
  setTimeout(() => {
    scrollPosition.value = productsSlider.value.scrollLeft;
  }, 300);
};

const formatPrice = (price) => {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(price);
};

const handleImageError = (e) => {
  e.target.src =
    "https://via.placeholder.com/600x600/FFE1F0/F36DA1?text=Flower";
};

const goToProduct = (id) => {
  router.push(`/products/${id}`);
};

const goToCategory = () => {
  router.push({
    path: "/",
    query: { categoryId: product.value.category.id },
  });
};

const shareProduct = () => {
  if (navigator.share) {
    navigator
      .share({
        title: product.value.name,
        text: product.value.description,
        url: window.location.href,
      })
      .catch((err) => console.log("Error sharing:", err));
  } else {
    // Fallback: copy to clipboard
    navigator.clipboard.writeText(window.location.href);
    alert("Đã sao chép link sản phẩm!");
  }
};

const addToCart = () => {
  cart.addItem(product.value);
  // Optional: show toast
  alert(`Đã thêm "${product.value.name}" vào giỏ hàng!`);
};
</script>

<style scoped>
.product-detail-page {
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

/* Loading & Error States */
.loading-container,
.error-container {
  min-height: 60vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 3rem;
}

.spinner {
  width: 60px;
  height: 60px;
  border: 4px solid var(--pink-200);
  border-top-color: var(--pink-500);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 1.5rem;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.error-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.error-container h2 {
  color: var(--pink-700);
  margin-bottom: 1rem;
}

.error-container p {
  color: var(--pink-600);
  margin-bottom: 2rem;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem 2rem;
  background: linear-gradient(135deg, var(--pink-500), var(--pink-400));
  color: white;
  border: none;
  border-radius: 12px;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s ease;
}

.back-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(243, 109, 161, 0.3);
}

/* Product Detail */
.product-detail-container {
  padding: 2rem 0 4rem;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 2rem;
  padding: 1rem;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 12px;
  font-size: 0.95rem;
}

.breadcrumb a {
  color: var(--pink-600);
  text-decoration: none;
  transition: color 0.3s ease;
}

.breadcrumb a:hover {
  color: var(--pink-500);
}

.separator {
  color: var(--pink-400);
}

.current {
  color: var(--pink-700);
  font-weight: 600;
}

.product-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 3rem;
  margin-bottom: 4rem;
}

/* Product Image */
.product-image-section {
  position: sticky;
  top: 100px;
  height: fit-content;
}

.image-gallery-wrapper {
  display: flex;
  gap: 1rem;
  align-items: flex-start;
}

/* Vertical Thumbnails - Left Side */
.image-thumbnails-vertical {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  max-height: 500px;
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: var(--pink-400) var(--pink-100);
  padding-right: 0.5rem;
}

.image-thumbnails-vertical::-webkit-scrollbar {
  width: 4px;
}

.image-thumbnails-vertical::-webkit-scrollbar-track {
  background: var(--pink-100);
  border-radius: 2px;
}

.image-thumbnails-vertical::-webkit-scrollbar-thumb {
  background: var(--pink-400);
  border-radius: 2px;
}

.thumbnail-vertical {
  flex-shrink: 0;
  width: 80px;
  height: 80px;
  border-radius: 10px;
  overflow: hidden;
  border: 3px solid transparent;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
  padding: 0;
}

.thumbnail-vertical img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.thumbnail-vertical.active {
  border-color: var(--pink-500);
  box-shadow: 0 4px 12px rgba(243, 109, 161, 0.3);
  transform: scale(1.05);
}

.thumbnail-vertical:hover {
  border-color: var(--pink-400);
  transform: scale(1.05);
}

.main-image {
  position: relative;
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 30px rgba(243, 109, 161, 0.15);
  flex: 1;
}

.main-image img {
  width: 100%;
  height: auto;
  display: block;
  transition: transform 0.5s ease;
}

.main-image:hover img {
  transform: scale(1.05);
}

/* Image Thumbnails - Horizontal (for mobile fallback) */
.image-thumbnails {
  display: flex;
  gap: 1rem;
  overflow-x: auto;
  padding: 0.5rem 0;
  scrollbar-width: thin;
  scrollbar-color: var(--pink-400) var(--pink-100);
}

.image-thumbnails::-webkit-scrollbar {
  height: 6px;
}

.image-thumbnails::-webkit-scrollbar-track {
  background: var(--pink-100);
  border-radius: 3px;
}

.image-thumbnails::-webkit-scrollbar-thumb {
  background: var(--pink-400);
  border-radius: 3px;
}

.thumbnail {
  flex-shrink: 0;
  width: 100px;
  height: 100px;
  border-radius: 12px;
  overflow: hidden;
  border: 3px solid transparent;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
  padding: 0;
}

.thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.thumbnail.active {
  border-color: var(--pink-500);
  box-shadow: 0 4px 12px rgba(243, 109, 161, 0.3);
  transform: scale(1.05);
}

.thumbnail:hover {
  border-color: var(--pink-400);
  transform: scale(1.05);
}

.collection-badge {
  position: absolute;
  top: 1.5rem;
  right: 1.5rem;
  background: linear-gradient(135deg, var(--pink-500), var(--pink-400));
  color: white;
  padding: 0.6rem 1.5rem;
  border-radius: 25px;
  font-weight: 600;
  font-size: 0.95rem;
  box-shadow: 0 4px 12px rgba(243, 109, 161, 0.3);
}

/* Product Info */
.product-info-section {
  background: rgba(255, 255, 255, 0.8);
  padding: 2.5rem;
  border-radius: 20px;
  box-shadow: 0 4px 30px rgba(243, 109, 161, 0.1);
}

.product-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: var(--pink-700);
  margin-bottom: 1rem;
  line-height: 1.2;
}

.product-meta {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.category-tag {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background: var(--pink-100);
  color: var(--pink-700);
  border-radius: 20px;
  font-weight: 500;
  font-size: 0.95rem;
}

.product-price {
  font-size: 2.5rem;
  font-weight: 700;
  color: var(--pink-600);
  margin-bottom: 2rem;
  padding-bottom: 2rem;
  border-bottom: 2px solid var(--pink-200);
}

.product-description {
  margin-bottom: 2rem;
}

.product-description h3,
.product-features h3,
.contact-section h3 {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  font-size: 1.3rem;
  font-weight: 600;
  color: var(--pink-700);
  margin-bottom: 1rem;
}

.product-description h3 svg,
.product-features h3 svg,
.contact-section h3 svg {
  color: var(--pink-500);
}

.description-content {
  padding: 1.5rem;
  background: var(--pink-50);
  border-radius: 12px;
  border-left: 4px solid var(--pink-500);
}

.description-content p {
  color: var(--pink-700);
  line-height: 1.8;
  font-size: 1.05rem;
  margin: 0;
}

.description-text {
  white-space: pre-wrap;
  word-wrap: break-word;
  overflow-wrap: break-word;
}

.product-features {
  margin-bottom: 2rem;
}

.features-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.features-list li {
  padding: 1rem 1.2rem;
  background: white;
  border-radius: 10px;
  margin-bottom: 0.8rem;
  color: var(--pink-700);
  font-size: 1rem;
  font-weight: 500;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.features-list li:hover {
  border-color: var(--pink-300);
  transform: translateX(8px);
  background: var(--pink-50);
}

.contact-section {
  margin-bottom: 2rem;
}

.contact-buttons {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
}

.contact-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.8rem;
  padding: 1.5rem 1rem;
  border: none;
  border-radius: 16px;
  font-weight: 600;
  font-size: 0.95rem;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.contact-btn:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.contact-btn.facebook {
  background: linear-gradient(135deg, #1877f2, #0d65d9);
  color: white;
}

.contact-btn.zalo {
  background: linear-gradient(135deg, #0068ff, #0052cc);
  color: white;
}

.contact-btn.instagram {
  background: linear-gradient(
    135deg,
    #f09433,
    #e6683c,
    #dc2743,
    #cc2366,
    #bc1888
  );
  color: white;
}

.contact-btn svg {
  width: 32px;
  height: 32px;
}

.contact-btn span {
  font-size: 1rem;
  font-weight: 600;
}

.product-actions {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
}

.share-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.8rem;
  padding: 1.2rem;
  border: none;
  border-radius: 12px;
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
  color: var(--pink-600);
  border: 2px solid var(--pink-300);
}

.share-btn:hover {
  background: var(--pink-100);
  border-color: var(--pink-400);
  transform: translateY(-2px);
}

.product-info-grid {
  display: grid;
  gap: 1rem;
  padding: 1.5rem;
  background: var(--pink-50);
  border-radius: 12px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-label {
  color: var(--pink-600);
  font-weight: 500;
}

.info-value {
  color: var(--pink-700);
  font-weight: 600;
}

.info-value code {
  padding: 4px 10px;
  background: var(--pink-100);
  border-radius: 6px;
  font-family: "Courier New", monospace;
  font-size: 0.95rem;
  color: var(--pink-700);
  font-weight: 700;
}

/* Related Products */
.related-products {
  margin-top: 4rem;
  padding-top: 3rem;
  border-top: 2px solid var(--pink-200);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 1rem;
  font-size: 2rem;
  font-weight: 700;
  color: var(--pink-700);
  margin-bottom: 2rem;
}

.section-title svg {
  color: var(--pink-500);
}

.products-slider-container {
  position: relative;
}

.products-slider {
  display: flex;
  gap: 1.5rem;
  overflow-x: auto;
  scroll-behavior: smooth;
  padding: 1rem 0;
  scrollbar-width: thin;
  scrollbar-color: var(--pink-300) var(--pink-100);
}

.products-slider::-webkit-scrollbar {
  height: 8px;
}

.products-slider::-webkit-scrollbar-track {
  background: var(--pink-100);
  border-radius: 10px;
}

.products-slider::-webkit-scrollbar-thumb {
  background: var(--pink-300);
  border-radius: 10px;
}

.products-slider::-webkit-scrollbar-thumb:hover {
  background: var(--pink-400);
}

.slider-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 50px;
  height: 50px;
  border: none;
  border-radius: 50%;
  background: white;
  color: var(--pink-600);
  box-shadow: 0 4px 20px rgba(243, 109, 161, 0.2);
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: center;
}

.slider-btn:hover:not(:disabled) {
  background: var(--pink-500);
  color: white;
  box-shadow: 0 6px 25px rgba(243, 109, 161, 0.3);
  transform: translateY(-50%) scale(1.1);
}

.slider-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.slider-btn.prev {
  left: -25px;
}

.slider-btn.next {
  right: -25px;
}

.product-card {
  min-width: 280px;
  max-width: 280px;
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

.product-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(243, 109, 161, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.product-card:hover .product-overlay {
  opacity: 1;
}

.view-detail {
  color: white;
  font-weight: 600;
  font-size: 1.1rem;
  padding: 0.8rem 1.5rem;
  border: 2px solid white;
  border-radius: 25px;
  transition: all 0.3s ease;
}

.product-card:hover .view-detail {
  background: white;
  color: var(--pink-600);
}

.product-info {
  padding: 1.5rem;
}

.product-name {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--pink-700);
  margin-bottom: 1rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 2.6em;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-price {
  font-size: 1.2rem;
  font-weight: 700;
  color: var(--pink-600);
}

.product-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--pink-100);
  color: var(--pink-600);
  border-radius: 50%;
  font-size: 1.5rem;
  font-weight: bold;
  transition: all 0.3s ease;
}

.product-card:hover .product-icon {
  background: var(--pink-500);
  color: white;
  transform: translateX(5px);
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

/* Responsive */
@media (max-width: 968px) {
  .product-content {
    grid-template-columns: 1fr;
    gap: 2rem;
  }

  .product-image-section {
    position: static;
  }

  /* Switch to horizontal thumbnails on mobile */
  .image-gallery-wrapper {
    flex-direction: column-reverse;
  }

  .image-thumbnails-vertical {
    flex-direction: row;
    max-height: none;
    overflow-x: auto;
    overflow-y: hidden;
    padding-right: 0;
    padding-bottom: 0.5rem;
  }

  .thumbnail-vertical {
    width: 80px;
    height: 80px;
  }

  .product-title {
    font-size: 2rem;
  }

  .product-price {
    font-size: 2rem;
  }

  .contact-buttons {
    grid-template-columns: 1fr;
  }

  .product-actions {
    flex-direction: column;
  }

  .slider-btn {
    display: none;
  }

  .product-card {
    min-width: 250px;
    max-width: 250px;
  }
}

@media (max-width: 640px) {
  .container {
    padding: 0 1rem;
  }

  .breadcrumb {
    font-size: 0.85rem;
    flex-wrap: wrap;
  }

  .product-info-section {
    padding: 1.5rem;
  }

  .product-title {
    font-size: 1.6rem;
  }

  .section-title {
    font-size: 1.5rem;
  }

  .contact-btn svg {
    width: 28px;
    height: 28px;
  }

  .contact-btn span {
    font-size: 0.9rem;
  }

  .product-card {
    min-width: 220px;
    max-width: 220px;
  }
}
/* New styles for buttons */
.product-actions {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
  align-items: center;
}

.add-to-cart-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.8rem 1.5rem;
  background: linear-gradient(135deg, var(--pink-500), var(--pink-400));
  color: white;
  border: none;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 1rem;
}

.add-to-cart-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(243, 109, 161, 0.3);
}

.share-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.8rem 1.5rem;
  background: white;
  color: var(--pink-600);
  border: 1px solid var(--pink-200);
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.share-btn:hover {
  background: var(--pink-50);
  border-color: var(--pink-400);
}
</style>
