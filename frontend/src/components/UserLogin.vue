<template>
  <div class="login-page">
    <div class="login-card" role="form" aria-labelledby="user-login-title">
      <div class="card-header">
        <span class="logo" aria-hidden="true">🌸</span>
        <div>
          <h1 id="user-login-title">Đăng nhập</h1>
          <p>Chào mừng bạn quay trở lại</p>
        </div>
      </div>

      <transition name="fade">
        <p v-if="error" class="feedback error" role="alert">
          {{ error }}
        </p>
      </transition>

      <form @submit.prevent="handleSubmit">
        <label>
          Email
          <input
            v-model.trim="form.email"
            type="email"
            name="email"
            autocomplete="email"
            required
            placeholder="example@email.com"
          />
        </label>

        <label>
          Mật khẩu
          <input
            v-model="form.password"
            :type="showPassword ? 'text' : 'password'"
            name="password"
            autocomplete="current-password"
            required
            placeholder="••••••••"
            @keyup.enter="handleSubmit"
          />
        </label>

        <div class="form-extras">
          <label class="toggle">
            <input type="checkbox" v-model="showPassword" />
            <span>Hiển thị mật khẩu</span>
          </label>
          <router-link to="/forgot-password" class="forgot-link">Quên mật khẩu?</router-link>
        </div>

        <button type="submit" class="primary" :disabled="loading">
          {{ loading ? "Đang xử lý..." : "Đăng nhập" }}
        </button>

        <!-- Google Login -->
        <a href="http://localhost:8080/oauth2/authorize/google?redirect_uri=http://localhost:84/oauth2/redirect" class="google-btn">
           <svg width="18" height="18" viewBox="0 0 18 18" xmlns="http://www.w3.org/2000/svg">
              <path d="M17.64 9.20455C17.64 8.56636 17.5827 7.95273 17.4764 7.36364H9V10.845H13.8436C13.635 11.97 13.0009 12.9232 12.0477 13.5614V15.8195H14.9564C16.6582 14.2527 17.64 11.9455 17.64 9.20455Z" fill="#4285F4"/>
              <path d="M9 18C11.43 18 13.4673 17.1941 14.9564 15.8195L12.0477 13.5614C11.2418 14.1014 10.2109 14.4205 9 14.4205C6.65591 14.4205 4.67182 12.8373 3.96409 10.71H0.957275V13.0418C2.43818 15.9832 5.48182 18 9 18Z" fill="#34A853"/>
              <path d="M3.96409 10.71C3.78409 10.17 3.68182 9.59318 3.68182 9C3.68182 8.40682 3.78409 7.83 3.96409 7.29V4.95818H0.957275C0.347727 6.17318 0 7.54773 0 9C0 10.4523 0.347727 11.8268 0.957275 13.0418L3.96409 10.71Z" fill="#FBBC05"/>
              <path d="M9 3.57955C10.3214 3.57955 11.5077 4.03364 12.4405 4.92545L15.0218 2.34409C13.4673 0.891818 11.43 0 9 0 5.48182 0 2.43818 2.01682 0.957275 4.95818L3.96409 7.29C4.67182 5.16273 6.65591 3.57955 9 3.57955Z" fill="#EA4335"/>
           </svg>
           Đăng nhập bằng Google
        </a>
      </form>

      <p class="hint">
        Chưa có tài khoản? <router-link to="/register">Đăng ký ngay</router-link>
      </p>
      <p class="hint admin-hint">
        <router-link to="/admin/login">Đăng nhập Quản trị viên</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";
import API from "../config/api";

const router = useRouter();

const api = axios.create({
  baseURL: API.baseURL || "http://localhost:8080/api", // Fallback if API.baseURL not visible
  headers: {
    "Content-Type": "application/json",
  },
});

const form = reactive({
  email: "",
  password: "",
});

const loading = ref(false);
const error = ref("");
const showPassword = ref(false);

const handleSubmit = async () => {
  if (loading.value) return;
  error.value = "";
  loading.value = true;

  try {
    const { data } = await api.post("/auth/login", {
      email: form.email,
      password: form.password,
    });

    // Save user data and token
    localStorage.setItem("token", data.accessToken);
    // For simplicity, we decode token or fetch user me endpoint.
    // But login response currently only has token.
    // We should probably fetch user details or update LoginResponse to include user info.
    // For now, let's just assume we are logged in.
    // Better: let's fetch user profile immediately.
    
    // Attempt to get user info if endpoint exists, or just redirect.
    // Current plan didn't create /api/users/me. 
    // I should create that later. 
    // For now, save a flag.
    localStorage.setItem("user_token", data.accessToken);
    
    // Redirect
    router.push("/");
  } catch (err) {
    if (err.response?.data?.message) {
      error.value = err.response.data.message;
    } else {
      error.value = "Đăng nhập thất bại. Kiểm tra lại email/mật khẩu.";
    }
    console.error(err);
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
/* Reusing styles from AdminLogin */
.login-page {
  min-height: 80vh; /* Adjustment */
  display: grid;
  place-items: center;
  padding: 32px 16px;
}

.login-card {
  width: min(420px, 100%);
  padding: 36px 34px 42px;
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(243, 109, 161, 0.14);
  box-shadow: 0 46px 120px -64px rgba(243, 109, 161, 0.55);
  display: grid;
  gap: 22px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 18px;
}

.logo {
  width: 64px;
  height: 64px;
  border-radius: 20px;
  display: grid;
  place-items: center;
  font-size: 30px;
  background: linear-gradient(135deg, var(--pink-500), var(--pink-300));
  box-shadow: 0 26px 60px -40px rgba(243, 109, 161, 0.5);
}

.card-header h1 {
  margin: 0;
  font-size: 1.85rem;
  color: var(--pink-700);
}

.card-header p {
  margin: 6px 0 0;
  color: var(--pink-400);
  font-weight: 500;
}

form {
  display: grid;
  gap: 18px;
}

label {
  display: grid;
  gap: 8px;
  font-weight: 600;
  color: var(--pink-500);
}

input {
  border: 1px solid rgba(243, 109, 161, 0.22);
  border-radius: 14px;
  padding: 12px 14px;
  font: inherit;
  color: var(--pink-700);
  background: rgba(255, 255, 255, 0.9);
}

.toggle {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 0.95rem;
  color: var(--pink-400);
}

.form-extras {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.forgot-link {
    color: var(--pink-500);
    text-decoration: none;
    font-size: 0.9rem;
}

.primary {
  width: 100%;
  border-radius: 16px;
  padding: 14px 18px;
  color: white;
  font-size: 1.05rem;
  background: linear-gradient(135deg, var(--pink-400), var(--pink-500));
  border: none;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.primary:hover:enabled {
  transform: translateY(-2px);
}

.google-btn {
    width: 100%;
    border-radius: 16px;
    padding: 12px 18px;
    background: white;
    border: 1px solid #ddd;
    color: #555;
    font-weight: 600;
    cursor: pointer;
    text-decoration: none;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    transition: background 0.2s;
}

.google-btn:hover {
    background: #f9f9f9;
}

.feedback {
  margin: 0;
  padding: 12px 16px;
  border-radius: 14px;
  background: rgba(255, 237, 244, 0.95);
  color: #d6456b;
  border: 1px solid rgba(214, 69, 107, 0.2);
}

.hint {
  margin: 0;
  text-align: center;
  color: var(--pink-400);
  font-size: 0.95rem;
}

.hint a {
    color: var(--pink-600);
    font-weight: 600;
    text-decoration: none;
}
.admin-hint {
    margin-top: 10px;
    font-size: 0.9rem;
    opacity: 0.8;
}
</style>
