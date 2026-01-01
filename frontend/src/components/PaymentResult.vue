<template>
  <div class="payment-result-page">
    <div class="container">
      <div v-if="loading" class="result-card loading">
        <div class="spinner"></div>
        <p>Đang kiểm tra kết quả thanh toán...</p>
      </div>

      <div v-else-if="status === 'success'" class="result-card success">
        <div class="icon-circle">
          <svg width="40" height="40" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M20 6L9 17L4 12" stroke="white" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <h1>Thanh toán thành công!</h1>
        <p class="message">Cảm ơn bạn đã mua sắm tại Flower Shop.</p>
        
        <div class="order-details">
           <div class="detail-row" v-if="totalPrice">
             <span>Tổng tiền:</span>
             <span class="value">{{ formatPrice(totalPrice) }}</span>
           </div>
           <div class="detail-row" v-if="orderInfo">
             <span>Nội dung:</span>
             <span class="value">{{ orderInfo }}</span>
           </div>
        </div>

        <router-link to="/" class="home-btn primary">Tiếp tục mua sắm</router-link>
      </div>

      <div v-else class="result-card error">
        <div class="icon-circle error-icon">
          <svg width="40" height="40" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M18 6L6 18M6 6L18 18" stroke="white" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <h1>Thanh toán thất bại</h1>
        <p class="message">Giao dịch không thành công hoặc đã bị hủy.</p>
        
        <div class="actions">
           <router-link to="/checkout" class="home-btn secondary">Thử lại</router-link>
           <router-link to="/" class="home-btn primary">Về trang chủ</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { cart } from '../utils/cart';

const route = useRoute();
const loading = ref(true);
const status = ref('');
const orderInfo = ref('');
const totalPrice = ref('');

onMounted(() => {
    // Artificial delay for better UX or verify with backend if needed
    setTimeout(() => {
        status.value = route.query.status;
        orderInfo.value = route.query.orderInfo;
        const amount = route.query.totalPrice;
        if (amount) {
            totalPrice.value = parseInt(amount) / 100; // VNPay amount is x100
        }
        
        if (status.value === 'success') {
            cart.clear();
        }
        loading.value = false;
    }, 1000);
});

const formatPrice = (price) => {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(price);
};
</script>

<style scoped>
.payment-result-page {
  min-height: 80vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--pink-50);
  padding: 20px;
}

.result-card {
  background: white;
  padding: 40px;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.05);
  text-align: center;
  max-width: 500px;
  width: 100%;
}

.icon-circle {
  width: 80px;
  height: 80px;
  background: #4cd137;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
}

.icon-circle.error-icon {
  background: #ff4757;
}

h1 {
  color: #333;
  margin-bottom: 10px;
}

.message {
  color: #666;
  margin-bottom: 30px;
}

.order-details {
  background: #f9f9f9;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 30px;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 0.95rem;
}

.detail-row:last-child {
  margin-bottom: 0;
}

.detail-row .value {
  font-weight: 600;
  color: #333;
}

.home-btn {
  display: inline-block;
  padding: 12px 30px;
  border-radius: 12px;
  text-decoration: none;
  font-weight: 600;
  transition: transform 0.2s;
}

.home-btn.primary {
  background: linear-gradient(135deg, var(--pink-500), var(--pink-400));
  color: white;
}

.home-btn.secondary {
  background: #f1f2f6;
  color: #57606f;
  margin-right: 15px;
}

.home-btn:hover {
  transform: translateY(-2px);
}

.spinner {
  width: 50px;
  height: 50px;
  border: 4px solid var(--pink-200);
  border-top-color: var(--pink-500);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.actions {
    display: flex;
    justify-content: center;
}
</style>
