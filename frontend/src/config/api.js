// API Configuration
const API_BASE_URL = process.env.VUE_APP_API_BASE || 'http://localhost:8080/api'

export default {
  baseURL: API_BASE_URL,
  
  // Endpoints
  products: {
    getAll: () => `${API_BASE_URL}/products`,
    getById: (id) => `${API_BASE_URL}/products/${id}`,
    create: () => `${API_BASE_URL}/products`,
    update: (id) => `${API_BASE_URL}/products/${id}`,
    delete: (id) => `${API_BASE_URL}/products/${id}`,
    uploadProductImage: (id) => `${API_BASE_URL}/products/${id}/images`,
  },
  
  categories: {
    getAll: () => `${API_BASE_URL}/categories`,
    getById: (id) => `${API_BASE_URL}/categories/${id}`,
    getProducts: (id) => `${API_BASE_URL}/categories/${id}/products`,
    create: () => `${API_BASE_URL}/categories`,
    update: (id) => `${API_BASE_URL}/categories/${id}`,
    delete: (id) => `${API_BASE_URL}/categories/${id}`,
  },
  
  collections: {
    getAll: () => `${API_BASE_URL}/collections`,
    getById: (id) => `${API_BASE_URL}/collections/${id}`,
    getProducts: (id) => `${API_BASE_URL}/collections/${id}/products`,
    create: () => `${API_BASE_URL}/collections`,
    update: (id) => `${API_BASE_URL}/collections/${id}`,
    delete: (id) => `${API_BASE_URL}/collections/${id}`,
  },
  
  admin: {
    login: () => `${API_BASE_URL}/admins/login`,
  },
  
  blogs: {
    getAll: () => `${API_BASE_URL}/blogs`,
    getById: (id) => `${API_BASE_URL}/blogs/${id}`,
    search: (keyword) => `${API_BASE_URL}/blogs?search=${encodeURIComponent(keyword)}`,
    
    // Admin endpoints
    getAllAdmin: () => `${API_BASE_URL}/admin/blogs`,
    create: () => `${API_BASE_URL}/admin/blogs`,
    update: (id) => `${API_BASE_URL}/admin/blogs/${id}`,
    delete: (id) => `${API_BASE_URL}/admin/blogs/${id}`,
    publish: (id) => `${API_BASE_URL}/admin/blogs/${id}/publish`,
    unpublish: (id) => `${API_BASE_URL}/admin/blogs/${id}/unpublish`,
    getByAuthor: (authorId) => `${API_BASE_URL}/admin/blogs/author/${authorId}`,
  }
}
