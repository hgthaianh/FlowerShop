<template>
  <div class="blog-detail-page">
    <SiteNavbar />
    
    <!-- Loading State -->
    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <p>Đang tải bài viết...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="error-container">
      <div class="error-card">
        <svg width="64" height="64" viewBox="0 0 64 64" fill="none">
          <circle cx="32" cy="32" r="30" fill="var(--pink-100)"/>
          <path d="M32 20V32M32 44V44.01" stroke="var(--pink-500)" stroke-width="4" stroke-linecap="round"/>
        </svg>
        <h2>Không tìm thấy bài viết</h2>
        <p>{{ error }}</p>
        <button @click="goBack" class="back-btn">
          Quay lại
        </button>
      </div>
    </div>

    <!-- Blog Content -->
    <article v-else class="blog-detail-container">
      <div class="blog-hero">
        <div class="blog-hero-content">
          <button @click="goBack" class="back-button">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
              <path d="M15 10H5M5 10L10 5M5 10L10 15" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
            Quay lại
          </button>
          
          <h1 class="blog-detail-title">{{ blog.title }}</h1>
          
          <div class="blog-detail-meta">
            <span class="meta-item">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                <circle cx="10" cy="6" r="4" stroke="currentColor" stroke-width="1.5"/>
                <path d="M3 18C3 14.134 6.13401 11 10 11H10C13.866 11 17 14.134 17 18" stroke="currentColor" stroke-width="1.5"/>
              </svg>
              {{ blog.authorUsername || 'Admin' }}
            </span>
            <span class="meta-separator">•</span>
            <span class="meta-item">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                <rect x="3" y="4" width="14" height="13" rx="2" stroke="currentColor" stroke-width="1.5"/>
                <path d="M3 8H17M7 2V4M13 2V4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
              </svg>
              {{ formatDate(blog.createdAt) }}
            </span>
            <span class="meta-separator" v-if="blog.updatedAt !== blog.createdAt">•</span>
            <span class="meta-item" v-if="blog.updatedAt !== blog.createdAt">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                <path d="M17 10C17 6.134 13.866 3 10 3C6.134 3 3 6.134 3 10C3 13.866 6.134 17 10 17" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                <path d="M10 6V10L13 13M15 17L17 19L19 17" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
              </svg>
              Cập nhật: {{ formatDate(blog.updatedAt) }}
            </span>
          </div>
        </div>
      </div>

      <div class="blog-image-container" v-if="blog.imageUrl">
        <img 
          :src="blog.imageUrl" 
          :alt="blog.title"
          class="blog-detail-image"
          @error="handleImageError"
        />
      </div>

      <div class="blog-body">
        <div class="blog-content" v-html="formatContent(blog.content)"></div>
      </div>

      <!-- Related Articles Section (Optional) -->
      <div class="blog-footer">
        <div class="share-section">
          <h3>Chia sẻ bài viết</h3>
          <div class="share-buttons">
            <button @click="shareToFacebook" class="share-btn facebook">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="currentColor">
                <path d="M20 10C20 4.477 15.523 0 10 0S0 4.477 0 10c0 4.991 3.657 9.128 8.438 9.878v-6.987h-2.54V10h2.54V7.797c0-2.506 1.492-3.89 3.777-3.89 1.094 0 2.238.195 2.238.195v2.46h-1.26c-1.243 0-1.63.771-1.63 1.562V10h2.773l-.443 2.89h-2.33v6.988C16.343 19.128 20 14.991 20 10z"/>
              </svg>
            </button>
            <button @click="shareToTwitter" class="share-btn twitter">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="currentColor">
                <path d="M6.29 18.251c7.547 0 11.675-6.253 11.675-11.675 0-.178 0-.355-.012-.53A8.348 8.348 0 0020 3.92a8.19 8.19 0 01-2.357.646 4.118 4.118 0 001.804-2.27 8.224 8.224 0 01-2.605.996 4.107 4.107 0 00-6.993 3.743 11.65 11.65 0 01-8.457-4.287 4.106 4.106 0 001.27 5.477A4.073 4.073 0 01.8 7.713v.052a4.105 4.105 0 003.292 4.022 4.095 4.095 0 01-1.853.07 4.108 4.108 0 003.834 2.85A8.233 8.233 0 010 16.407a11.616 11.616 0 006.29 1.84"/>
              </svg>
            </button>
            <button @click="copyLink" class="share-btn copy">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none" stroke="currentColor">
                <path d="M10 13a3 3 0 01-3-3V4a3 3 0 116 0v6a3 3 0 01-3 3zm0 0v4" stroke-width="2" stroke-linecap="round"/>
                <path d="M13 7h2a3 3 0 013 3v0a3 3 0 01-3 3h-2M7 7H5a3 3 0 00-3 3v0a3 3 0 003 3h2" stroke-width="2" stroke-linecap="round"/>
              </svg>
            </button>
          </div>
        </div>
      </div>
    </article>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'
import SiteNavbar from './SiteNavbar.vue'
import apiConfig from '../config/api'

const router = useRouter()
const route = useRoute()

const blog = ref({})
const loading = ref(true)
const error = ref(null)

const fetchBlog = async () => {
  try {
    loading.value = true
    error.value = null
    const blogId = route.params.id
    const response = await axios.get(apiConfig.blogs.getById(blogId))
    blog.value = response.data
  } catch (err) {
    error.value = err.response?.data?.message || 'Không thể tải bài viết'
    console.error('Error fetching blog:', err)
  } finally {
    loading.value = false
  }
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return new Intl.DateTimeFormat('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date)
}

const formatContent = (content) => {
  return content.replace(/\n/g, '<br>')
}

const handleImageError = (e) => {
  e.target.src = 'https://via.placeholder.com/1200x600/FFE1F0/F36DA1?text=Blog+Image'
}

const goBack = () => {
  router.push('/blogs')
}

const shareToFacebook = () => {
  const url = window.location.href
  window.open(`https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(url)}`, '_blank')
}

const shareToTwitter = () => {
  const url = window.location.href
  const text = blog.value.title
  window.open(`https://twitter.com/intent/tweet?url=${encodeURIComponent(url)}&text=${encodeURIComponent(text)}`, '_blank')
}

const copyLink = async () => {
  try {
    await navigator.clipboard.writeText(window.location.href)
    alert('Đã sao chép link!')
  } catch (err) {
    console.error('Failed to copy:', err)
  }
}

onMounted(() => {
  fetchBlog()
})
</script>

<style scoped>
.blog-detail-page {
  min-height: 100vh;
  background: linear-gradient(135deg, rgba(255, 231, 241, 0.8), rgba(255, 214, 232, 0.5));
}

.loading-container, .error-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
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
  to { transform: rotate(360deg); }
}

.error-card {
  background: white;
  padding: 48px;
  border-radius: 16px;
  text-align: center;
  max-width: 400px;
}

.error-card h2 {
  color: #333;
  margin: 16px 0 8px;
}

.error-card p {
  color: #666;
  margin-bottom: 24px;
}

.back-btn {
  background: var(--pink-500);
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.back-btn:hover {
  background: var(--pink-600);
  transform: translateY(-2px);
}

.blog-detail-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 100px 24px 60px;
}

.blog-hero {
  margin-bottom: 32px;
}

.back-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: white;
  border: 2px solid var(--pink-200);
  color: var(--pink-600);
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 24px;
}

.back-button:hover {
  background: var(--pink-50);
  border-color: var(--pink-400);
  transform: translateX(-4px);
}

.blog-detail-title {
  font-size: 3rem;
  font-weight: 700;
  color: #333;
  line-height: 1.2;
  margin: 0 0 24px;
}

.blog-detail-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  color: #666;
  font-size: 0.95rem;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.meta-separator {
  color: var(--pink-300);
}

.blog-image-container {
  width: 100%;
  margin-bottom: 48px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.blog-detail-image {
  width: 100%;
  height: auto;
  display: block;
}

.blog-body {
  background: white;
  padding: 48px;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  margin-bottom: 48px;
}

.blog-content {
  font-size: 1.125rem;
  line-height: 1.8;
  color: #333;
}

.blog-content :deep(p) {
  margin-bottom: 1.5em;
}

.blog-footer {
  background: white;
  padding: 32px;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.share-section h3 {
  font-size: 1.25rem;
  color: #333;
  margin: 0 0 16px;
}

.share-buttons {
  display: flex;
  gap: 12px;
}

.share-btn {
  width: 48px;
  height: 48px;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.share-btn.facebook {
  background: #1877f2;
}

.share-btn.twitter {
  background: #1da1f2;
}

.share-btn.copy {
  background: var(--pink-500);
}

.share-btn:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
}

@media (max-width: 768px) {
  .blog-detail-container {
    padding: 80px 16px 40px;
  }

  .blog-detail-title {
    font-size: 2rem;
  }

  .blog-body {
    padding: 24px;
  }

  .blog-content {
    font-size: 1rem;
  }
}
</style>
