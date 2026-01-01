<template>
  <div class="blog-page">
    <SiteNavbar />

    <div class="blog-container">
      <div class="blog-header">
        <h1 class="page-title">
          <span class="gradient-text">Blog</span>
        </h1>
        <p class="page-subtitle">Chia sẻ kiến thức và câu chuyện về hoa</p>
      </div>

      <!-- Search Section -->
      <div class="search-section">
        <div class="search-box">
          <svg
            width="20"
            height="20"
            viewBox="0 0 20 20"
            fill="none"
            class="search-icon"
          >
            <path
              d="M9 17C13.4183 17 17 13.4183 17 9C17 4.58172 13.4183 1 9 1C4.58172 1 1 4.58172 1 9C1 13.4183 4.58172 17 9 17Z"
              stroke="currentColor"
              stroke-width="2"
            />
            <path
              d="M19 19L14.65 14.65"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
            />
          </svg>
          <input
            v-model="searchQuery"
            @input="handleSearch"
            type="text"
            placeholder="Tìm kiếm bài viết..."
            class="search-input"
          />
          <button v-if="searchQuery" @click="clearSearch" class="clear-btn">
            ✕
          </button>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="loading">
        <div class="spinner"></div>
        <p>Đang tải bài viết...</p>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="error">
        <p>{{ error }}</p>
      </div>

      <!-- Empty State -->
      <div v-else-if="blogs.length === 0" class="empty-state">
        <svg width="100" height="100" viewBox="0 0 100 100" fill="none">
          <circle cx="50" cy="50" r="40" fill="var(--pink-100)" />
          <path
            d="M35 45H65M35 55H55"
            stroke="var(--pink-500)"
            stroke-width="4"
            stroke-linecap="round"
          />
        </svg>
        <p>Chưa có bài viết nào</p>
      </div>

      <!-- Blog Grid -->
      <div v-else class="blogs-grid">
        <article
          v-for="blog in blogs"
          :key="blog.id"
          class="blog-card"
          @click="goToBlog(blog.id)"
        >
          <div class="blog-image">
            <img
              :src="
                blog.imageUrl ||
                'https://via.placeholder.com/400x250/FFE1F0/F36DA1?text=Blog+Image'
              "
              :alt="blog.title"
              @error="handleImageError"
            />
          </div>
          <div class="blog-content">
            <div class="blog-meta">
              <span class="blog-author">
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                  <circle
                    cx="8"
                    cy="5"
                    r="3"
                    stroke="currentColor"
                    stroke-width="1.5"
                  />
                  <path
                    d="M2 14C2 11.2386 4.23858 9 7 9H9C11.7614 9 14 11.2386 14 14"
                    stroke="currentColor"
                    stroke-width="1.5"
                  />
                </svg>
                {{ blog.authorUsername || "Admin" }}
              </span>
              <span class="blog-date">
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                  <rect
                    x="2"
                    y="3"
                    width="12"
                    height="11"
                    rx="2"
                    stroke="currentColor"
                    stroke-width="1.5"
                  />
                  <path
                    d="M2 6H14M5 1V3M11 1V3"
                    stroke="currentColor"
                    stroke-width="1.5"
                    stroke-linecap="round"
                  />
                </svg>
                {{ formatDate(blog.createdAt) }}
              </span>
            </div>
            <h2 class="blog-title">{{ blog.title }}</h2>
            <p class="blog-summary">
              {{ blog.summary || truncateContent(blog.content) }}
            </p>
            <button class="read-more-btn">
              Đọc tiếp
              <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                <path
                  d="M3 8H13M13 8L8 3M13 8L8 13"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                />
              </svg>
            </button>
          </div>
        </article>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";
import SiteNavbar from "./SiteNavbar.vue";
import apiConfig from "../config/api";

const router = useRouter();
const blogs = ref([]);
const loading = ref(true);
const error = ref(null);
const searchQuery = ref("");

const fetchBlogs = async () => {
  try {
    loading.value = true;
    error.value = null;
    const response = await axios.get(apiConfig.blogs.getAll());
    blogs.value = response.data;
  } catch (err) {
    error.value = "Không thể tải danh sách bài viết";
    console.error("Error fetching blogs:", err);
  } finally {
    loading.value = false;
  }
};

const handleSearch = async () => {
  if (!searchQuery.value.trim()) {
    fetchBlogs();
    return;
  }

  try {
    loading.value = true;
    error.value = null;
    const response = await axios.get(apiConfig.blogs.search(searchQuery.value));
    blogs.value = response.data;
  } catch (err) {
    error.value = "Không thể tìm kiếm bài viết";
    console.error("Error searching blogs:", err);
  } finally {
    loading.value = false;
  }
};

const clearSearch = () => {
  searchQuery.value = "";
  fetchBlogs();
};

const goToBlog = (id) => {
  router.push(`/blogs/${id}`);
};

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return new Intl.DateTimeFormat("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  }).format(date);
};

const truncateContent = (content) => {
  return content.length > 150 ? content.substring(0, 150) + "..." : content;
};

const handleImageError = (e) => {
  e.target.src =
    "https://via.placeholder.com/400x250/FFE1F0/F36DA1?text=Blog+Image";
};

onMounted(() => {
  fetchBlogs();
});
</script>

<style scoped>
.blog-page {
  min-height: 100vh;
  background: linear-gradient(
    135deg,
    rgba(255, 231, 241, 0.8),
    rgba(255, 214, 232, 0.5)
  );
}

.blog-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 100px 24px 60px;
}

.blog-header {
  text-align: center;
  margin-bottom: 48px;
}

.page-title {
  font-size: 3.5rem;
  font-weight: 700;
  margin: 0 0 16px;
  color: #333;
}

.gradient-text {
  background: linear-gradient(135deg, var(--pink-600), var(--peach-500));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-subtitle {
  font-size: 1.25rem;
  color: #666;
  margin: 0;
}

.search-section {
  margin-bottom: 48px;
  display: flex;
  justify-content: center;
}

.search-box {
  position: relative;
  width: 100%;
  max-width: 600px;
}

.search-icon {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--pink-400);
}

.search-input {
  width: 100%;
  padding: 14px 48px 14px 48px;
  border: 2px solid var(--pink-200);
  border-radius: 12px;
  font-size: 1rem;
  transition: all 0.3s ease;
  background: white;
}

.search-input:focus {
  outline: none;
  border-color: var(--pink-500);
  box-shadow: 0 0 0 4px rgba(243, 109, 161, 0.1);
}

.clear-btn {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #999;
  cursor: pointer;
  font-size: 1.25rem;
  padding: 4px 8px;
  transition: color 0.3s;
}

.clear-btn:hover {
  color: var(--pink-600);
}

.loading,
.error,
.empty-state {
  text-align: center;
  padding: 80px 20px;
}

.spinner {
  width: 48px;
  height: 48px;
  border: 4px solid var(--pink-100);
  border-top-color: var(--pink-500);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.blogs-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 32px;
}

.blog-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  cursor: pointer;
}

.blog-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 32px rgba(243, 109, 161, 0.2);
}

.blog-image {
  width: 100%;
  height: 220px;
  overflow: hidden;
  background: var(--pink-50);
}

.blog-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.blog-card:hover .blog-image img {
  transform: scale(1.05);
}

.blog-content {
  padding: 24px;
}

.blog-meta {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
  font-size: 0.875rem;
  color: #666;
}

.blog-author,
.blog-date {
  display: flex;
  align-items: center;
  gap: 6px;
}

.blog-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
  margin: 0 0 12px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.blog-summary {
  color: #666;
  line-height: 1.6;
  margin: 0 0 16px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.read-more-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: var(--pink-600);
  font-weight: 600;
  border: none;
  background: none;
  cursor: pointer;
  padding: 0;
  transition: all 0.3s ease;
}

.read-more-btn:hover {
  color: var(--pink-700);
  gap: 12px;
}

@media (max-width: 768px) {
  .blog-container {
    padding-top: 80px;
  }

  .page-title {
    font-size: 2.5rem;
  }

  .blogs-grid {
    grid-template-columns: 1fr;
    gap: 24px;
  }
}
</style>
