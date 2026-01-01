<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
// Router-based navigation - no need for local admin state here
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700&display=swap');

:root {
  --pink-50: #f0f9ff;
  --pink-75: #e0f2fe;
  --pink-100: #bae6fd;
  --pink-200: #7dd3fc;
  --pink-300: #38bdf8;
  --pink-400: #0ea5e9;
  --pink-500: #0284c7;
  --pink-600: #0369a1;
  --pink-700: #075985;
  --pink-800: #0c4a6e;
  --peach-500: #38bdf8; /* Accent also blue */
  font-family: 'Be Vietnam Pro', 'Poppins', Arial, sans-serif;
}

body {
  margin: 0;
  background: linear-gradient(135deg, rgba(224, 242, 254, 0.8), rgba(186, 230, 253, 0.5));
  min-height: 100vh;
}

#app {
  min-height: 100vh;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.28s ease, transform 0.28s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(12px);
}
</style>
