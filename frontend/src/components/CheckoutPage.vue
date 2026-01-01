<template>
  <div class="checkout-page">
    <div class="container">
      <h1>Thanh toán 🛍️</h1>

      <div class="checkout-content">
        <!-- Billing Details -->
        <div class="form-section">
          <h2>Thông tin giao hàng</h2>
          <form @submit.prevent="placeOrder" id="checkout-form">
            <div class="form-group">
               <label>Họ và tên người nhận</label>
               <input v-model="form.customerName" required placeholder="Nhập họ tên đầy đủ">
            </div>
            
            <div class="form-group">
               <label>Số điện thoại</label>
               <input v-model="form.phoneNumber" required placeholder="Ví dụ: 0987..." type="tel">
            </div>
            
            <div class="form-group">
               <label>Địa chỉ nhận hàng</label>
               <textarea v-model="form.shippingAddress" required placeholder="Số nhà, đường, phường/xã..." rows="3"></textarea>
            </div>
            
            <div class="form-group">
               <label>Ghi chú đơn hàng (Tùy chọn)</label>
               <textarea v-model="form.notes" placeholder="Lời nhắn, thời gian giao hàng..." rows="2"></textarea>
            </div>
          </form>
        </div>

        <!-- Order Summary & Payment -->
        <div class="summary-section">
           <h2>Đơn hàng của bạn</h2>
           <div class="order-items">
              <div v-for="item in cart.items" :key="item.id" class="order-item">
                 <span>{{ item.name }} x {{ item.quantity }}</span>
                 <span>{{ formatPrice(item.price * item.quantity) }}</span>
              </div>
           </div>
           
           <div class="total-row">
              <span>Tổng cộng:</span>
              <span>{{ formatPrice(cart.totalPrice) }}</span>
           </div>
           
           <div class="payment-methods">
              <h3>Phương thức thanh toán</h3>
              <label class="payment-option">
                 <input type="radio" v-model="form.paymentMethod" value="COD">
                 <span class="radio-label">
                    <span>💵 Thanh toán khi nhận hàng (COD)</span>
                 </span>
              </label>
              
              <label class="payment-option">
                 <input type="radio" v-model="form.paymentMethod" value="VNPAY">
                 <span class="radio-label">
                    <span>💳 Thanh toán qua VNPAY</span>
                    <img src="https://vnpay.vn/s1/statics.vnpay.vn/2023/6/0oxhzjmxbksr1686814746087.png" alt="VNPay" height="24">
                 </span>
              </label>
           </div>
           
           <button 
             type="submit" 
             form="checkout-form" 
             class="checkout-btn" 
             :disabled="loading || cart.items.length === 0"
           >
             {{ loading ? "Đang xử lý..." : "Đặt hàng ngay" }}
           </button>
           
           <p v-if="error" class="error-msg">{{ error }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue';
import { cart } from '../utils/cart';
import { useRouter } from 'vue-router';
import axios from 'axios';
import API from "../config/api";

const router = useRouter();
const loading = ref(false);
const error = ref("");

const api = axios.create({
  baseURL: API.baseURL || "http://localhost:8080/api",
  headers: { "Content-Type": "application/json" },
});

// Load user info if available
// user variable removed because it was unused

const form = reactive({
  customerName: "",
  phoneNumber: "",
  shippingAddress: "",
  notes: "",
  paymentMethod: "COD"
});

onMounted(() => {
    if (cart.items.length === 0) {
        router.push('/cart');
    }
});

const formatPrice = (price) => {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(price);
};

const placeOrder = async () => {
    if (cart.items.length === 0) return;
    loading.value = true;
    error.value = "";
    
    try {
        const token = localStorage.getItem('token');
        const headers = token ? { Authorization: `Bearer ${token}` } : {};

        const orderData = {
           customerName: form.customerName,
           shippingAddress: form.shippingAddress,
           phoneNumber: form.phoneNumber,
           notes: form.notes,
           items: cart.items.map(item => ({
               productId: item.id,
               quantity: item.quantity
           }))
        };

        const { data: order } = await api.post('/orders', orderData, { headers });
        
        // Order created successfully
        
        if (form.paymentMethod === 'VNPAY') {
            const vnpayResponse = await api.post('/payment/create_payment_url', null, {
                params: {
                    amount: Math.round(cart.totalPrice),
                    orderInfo: `Thanh toan don hang ${order.id}`
                },
                headers
            });
            
            if (vnpayResponse.data && vnpayResponse.data.url) {
                window.location.href = vnpayResponse.data.url;
            } else {
                 alert("Không thể tạo liên kết thanh toán VNPay.");
            }
        } else {
             alert("Đặt hàng thành công! Mã đơn hàng: " + order.id);
             cart.clear();
             router.push('/');
        }
        
    } catch (err) {
        console.error(err);
        error.value = "Đặt hàng thất bại. Vui lòng thử lại.";
    } finally {
        loading.value = false;
    }
};
</script>

<style scoped>
.checkout-page {
  padding: 40px 16px;
  min-height: 80vh;
  background: var(--pink-50);
}

.container {
  max-width: 1000px;
  margin: 0 auto;
}

h1 {
  text-align: center;
  margin-bottom: 30px;
}

.checkout-content {
  display: grid;
  grid-template-columns: 1fr 350px;
  gap: 30px;
}

.form-section, .summary-section {
  background: white;
  padding: 30px;
  border-radius: 20px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.05);
}

h2 {
    font-size: 1.3rem;
    margin-bottom: 20px;
    color: var(--pink-700);
    border-bottom: 1px solid #eee;
    padding-bottom: 10px;
}

.form-group {
    margin-bottom: 15px;
}

label {
    display: block;
    margin-bottom: 5px;
    font-weight: 500;
}

input, textarea {
    width: 100%;
    padding: 10px 15px;
    border: 1px solid #ddd;
    border-radius: 10px;
    font: inherit;
    transition: border 0.3s;
}

input:focus, textarea:focus {
    border-color: var(--pink-400);
    outline: none;
}

.order-items {
    max-height: 200px;
    overflow-y: auto;
    margin-bottom: 15px;
}

.order-item {
    display: flex;
    justify-content: space-between;
    padding: 10px 0;
    border-bottom: 1px solid #f9f9f9;
}

.total-row {
    display: flex;
    justify-content: space-between;
    font-size: 1.2rem;
    font-weight: 700;
    color: var(--pink-700);
    margin: 20px 0;
    padding-top: 10px;
    border-top: 2px dashed #eee;
}

.payment-methods {
    margin-bottom: 20px;
}

.payment-methods h3 {
    font-size: 1rem;
    margin-bottom: 10px;
    color: #555;
}

.payment-option {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 12px;
    border: 1px solid #eee;
    border-radius: 12px;
    margin-bottom: 8px;
    cursor: pointer;
    transition: all 0.2s;
}

.payment-option:hover {
    background: #fdfdfd;
    border-color: var(--pink-200);
}

.radio-label {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
}

.checkout-btn {
  display: block;
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, var(--pink-500), var(--pink-400));
  color: white;
  border: none;
  border-radius: 16px;
  font-weight: 600;
  cursor: pointer;
  font-size: 1.05rem;
}

.checkout-btn:disabled {
    opacity: 0.7;
    cursor: not-allowed;
}

.error-msg {
    color: red;
    margin-top: 10px;
    text-align: center;
}

@media (max-width: 768px) {
  .checkout-content {
    grid-template-columns: 1fr;
  }
}
</style>
