<template>
  <div class="redirect-handler">
    <div class="spinner"></div>
    <p>Đang xử lý đăng nhập...</p>
  </div>
</template>

<script setup>
import { onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";

const router = useRouter();
const route = useRoute();

onMounted(() => {
  const token = route.query.token;
  const error = route.query.error;

  if (token) {
    localStorage.setItem("token", token);
    localStorage.setItem("user_token", token); // Flag for logged in
    
    // Ideally fetch user info here or let Navbar do it.
    router.push("/");
  } else {
    alert(error || "Đăng nhập thất bại");
    router.push("/login");
  }
});
</script>

<style scoped>
.redirect-handler {
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--pink-600);
}

.spinner {
  width: 50px;
  height: 50px;
  border: 4px solid var(--pink-200);
  border-top-color: var(--pink-500);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
