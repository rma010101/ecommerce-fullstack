import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add auth token to requests
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Authentication API endpoints
export const authAPI = {
  // Login user
  login: (credentials) => api.post('/auth/signin', credentials),
  
  // Register user
  register: (userData) => api.post('/auth/signup', userData),
  
  // Get current user profile
  getCurrentUser: () => api.get('/auth/me'),
  
  // Logout user
  logout: () => api.post('/auth/logout'),
};

// Product API endpoints
export const productAPI = {
  // Get all products
  getAllProducts: () => api.get('/products'),
  
  // Get product by ID
  getProductById: (id) => api.get(`/products/${id}`),
  
  // Create single product
  createProduct: (product) => api.post('/products', product),
  
  // Create multiple products (bulk)
  createBulkProducts: (products) => api.post('/products/bulk', products),
  
  // Update product
  updateProduct: (id, product) => api.put(`/products/${id}`, product),
  
  // Delete product
  deleteProduct: (id) => api.delete(`/products/${id}`),
  
  // Search products
  searchProducts: (params) => api.get('/products/search', { params }),
  
  // Get products by category
  getProductsByCategory: (category) => api.get(`/products/category/${category}`),
};

// Error interceptor
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expired or invalid
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    console.error('API Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

export default api;
