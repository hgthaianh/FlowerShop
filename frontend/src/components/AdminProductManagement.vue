<template>
  <div>
    <h1>Quản lý sản phẩm</h1>
    <div v-for="product in products" :key="product.id" class="product-item">
      <h2>{{ product.name }}</h2>
      <p>{{ product.description }}</p>
      <input type="file" @change="onFileChange($event, product.id)" />
      <button @click="uploadImage(product.id)">Tải lên hình ảnh</button>
    </div>
  </div>
</template>

<script>
import { uploadProductImage } from "../config/api";

export default {
  data() {
    return {
      products: [],
      selectedFiles: {},
    };
  },
  methods: {
    onFileChange(event, productId) {
      this.selectedFiles[productId] = event.target.files[0];
    },
    async uploadImage(productId) {
      const file = this.selectedFiles[productId];
      if (!file) {
        alert("Vui lòng chọn một tệp để tải lên.");
        return;
      }
      try {
        const response = await uploadProductImage(productId, file);
        alert("Tải lên thành công: " + response);
      } catch (error) {
        console.error(error);
        alert("Tải lên thất bại.");
      }
    },
  },
};
</script>

<style scoped>
.product-item {
  margin-bottom: 20px;
}
</style>