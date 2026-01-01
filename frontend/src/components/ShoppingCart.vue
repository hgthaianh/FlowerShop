<template>
  <div class="cart-page">
    <div class="container">
      <h1>Giỏ hàng của bạn 🌸</h1>
      
      <div v-if="cart.items.length === 0" class="empty-cart">
        <div class="empty-icon">🛒</div>
        <p>Giỏ hàng đang trống</p>
        <router-link to="/" class="continue-btn">Tiếp tục mua sắm</router-link>
      </div>
      
      <div v-else class="cart-content">
        <div class="cart-items">
          <div v-for="item in cart.items" :key="item.id" class="cart-item">
            <img :src="item.imageUrl" :alt="item.name" class="item-image" />
            
            <div class="item-details">
              <h3>{{ item.name }}</h3>
              <p class="price">{{ formatPrice(item.price) }}</p>
            </div>
            
            <div class="item-quantity">
              <button @click="decreaseQty(item)" class="qty-btn">-</button>
              <input 
                type="number" 
                v-model.number="item.quantity" 
                min="1" 
                @change="updateQty(item)"
                class="qty-input"
              >
              <button @click="increaseQty(item)" class="qty-btn">+</button>
            </div>
            
            <div class="item-total">
               {{ formatPrice(item.price * item.quantity) }}
            </div>
            
            <button @click="cart.removeItem(item.id)" class="remove-btn">
              🗑️
            </button>
          </div>
        </div>
        
        <div class="cart-summary">
           <h2>Tổng cộng</h2>
           <div class="summary-row">
             <span>Tạm tính:</span>
             <span>{{ formatPrice(cart.totalPrice) }}</span>
           </div>
           <div class="summary-row total">
             <span>Thành tiền:</span>
             <span>{{ formatPrice(cart.totalPrice) }}</span>
           </div>
           
           <router-link to="/checkout" class="checkout-btn">
             Tiến hành thanh toán
           </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { cart } from '../utils/cart';

const formatPrice = (price) => {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(price);
};

const increaseQty = (item) => {
  cart.updateQuantity(item.id, item.quantity + 1);
};

const decreaseQty = (item) => {
  if (item.quantity > 1) {
    cart.updateQuantity(item.id, item.quantity - 1);
  } else {
    if(confirm('Bạn có chắc muốn xóa sản phẩm này?')) {
        cart.removeItem(item.id);
    }
  }
};

const updateQty = (item) => {
   if (item.quantity < 1) item.quantity = 1;
   cart.updateQuantity(item.id, item.quantity);
};
</script>

<style scoped>
.cart-page {
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
  color: var(--pink-700);
  margin-bottom: 30px;
}

.empty-cart {
  text-align: center;
  padding: 60px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.05);
}

.empty-icon {
  font-size: 60px;
  margin-bottom: 20px;
  opacity: 0.5;
}

.item-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 12px;
}

.cart-content {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 30px;
}

.cart-items {
  background: white;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.05);
}

.cart-item {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px 0;
  border-bottom: 1px solid #f0f0f0;
}

.cart-item:last-child {
  border-bottom: none;
}

.item-details {
  flex: 1;
}

.item-details h3 {
  margin: 0 0 5px;
  color: #333;
}

.item-quantity {
  display: flex;
  align-items: center;
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
}

.qty-btn {
  padding: 5px 12px;
  background: #f9f9f9;
  border: none;
  cursor: pointer;
  font-weight: bold;
}

.qty-input {
  width: 40px;
  text-align: center;
  border: none;
  font-weight: 600;
  -moz-appearance: textfield;
}

.item-total {
  font-weight: 700;
  color: var(--pink-600);
  min-width: 100px;
  text-align: right;
}

.remove-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1.2rem;
  opacity: 0.5;
  transition: opacity 0.2s;
}

.remove-btn:hover {
  opacity: 1;
}

.cart-summary {
  background: white;
  padding: 24px;
  border-radius: 20px;
  height: fit-content;
  box-shadow: 0 4px 15px rgba(0,0,0,0.05);
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  font-size: 1.05rem;
}

.summary-row.total {
  font-weight: 700;
  color: var(--pink-700);
  font-size: 1.3rem;
  border-top: 2px dashed #eee;
  padding-top: 15px;
}

.checkout-btn {
  display: block;
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, var(--pink-500), var(--pink-400));
  color: white;
  text-align: center;
  text-decoration: none;
  border-radius: 16px;
  font-weight: 600;
  margin-top: 20px;
  transition: transform 0.2s;
}

.checkout-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(243, 109, 161, 0.4);
}

.continue-btn {
    display: inline-block;
    margin-top: 15px;
    padding: 10px 20px;
    background: #f0f0f0;
    color: #555;
    text-decoration: none;
    border-radius: 8px;
}

@media (max-width: 768px) {
  .cart-content {
    grid-template-columns: 1fr;
  }
}
</style>
