<template>
  <div class="login-page">
    <div class="login-card" role="form">
      <div class="card-header">
        <span class="logo">🌸</span>
        <div>
          <h1>Đăng ký</h1>
          <p>Tạo tài khoản mới</p>
        </div>
      </div>

      <transition name="fade">
        <p v-if="error" class="feedback error">{{ error }}</p>
      </transition>

      <form @submit.prevent="handleSubmit">
        <label>
          Họ và tên
          <input
            v-model.trim="form.name"
            type="text"
            required
            placeholder="Nguyen Van A"
          />
        </label>

        <label>
          Email
          <input
            v-model.trim="form.email"
            type="email"
            required
            placeholder="example@email.com"
          />
        </label>

        <label>
          Mật khẩu
          <input
            v-model="form.password"
            :type="showPassword ? 'text' : 'password'"
            required
            placeholder="Min 6 characters"
          />
        </label>

        <div class="form-extras">
          <label class="toggle">
            <input type="checkbox" v-model="showPassword" />
            <span>Hiển thị mật khẩu</span>
          </label>
        </div>

        <button type="submit" class="primary" :disabled="loading">
          {{ loading ? "Đang đăng ký..." : "Đăng ký" }}
        </button>
      </form>

      <p class="hint">
        Đã có tài khoản? <router-link to="/login">Đăng nhập</router-link>
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
  baseURL: API.baseURL || "http://localhost:8080/api",
  headers: { "Content-Type": "application/json" },
});

const form = reactive({
  name: "",
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
    await api.post("/auth/signup", {
      name: form.name,
      email: form.email,
      password: form.password,
    });

    // Auto login or redirect to login?
    // Let's redirect to login with a message?
    // Or simpler: alert and push.
    alert("Đăng ký thành công! Vui lòng đăng nhập.");
    router.push("/login");

  } catch (err) {
    if (err.response?.data?.message) {
        // ValidationException often returns field errors
        // If it's a simple message:
       error.value = err.response.data.message;
    } else if (err.response?.data?.errors) {
       // Format validation errors
       const errors = err.response.data.errors;
       error.value = Object.values(errors).join(", ");
    } else {
       error.value = "Đăng ký thất bại. Vui lòng thử lại.";
    }
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
/* Same styles as Login */
.login-page {
  min-height: 80vh;
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
}

.hint a {
    color: var(--pink-600);
    font-weight: 600;
    text-decoration: none;
}
</style>
