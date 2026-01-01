import { reactive, watch } from 'vue';

const CART_KEY = 'flower_shop_cart';

const cartState = reactive({
    items: JSON.parse(localStorage.getItem(CART_KEY) || '[]')
});

watch(() => cartState.items, (newItems) => {
    localStorage.setItem(CART_KEY, JSON.stringify(newItems));
}, { deep: true });

export const cart = {
    get items() {
        return cartState.items;
    },

    addItem(product, quantity = 1) {
        const existingItem = cartState.items.find(item => item.id === product.id);
        if (existingItem) {
            existingItem.quantity += quantity;
        } else {
            cartState.items.push({
                id: product.id,
                name: product.name,
                price: product.price,
                imageUrl: product.imageUrl,
                quantity: quantity
            });
        }
    },

    removeItem(productId) {
        const index = cartState.items.findIndex(item => item.id === productId);
        if (index > -1) {
            cartState.items.splice(index, 1);
        }
    },

    updateQuantity(productId, quantity) {
        const item = cartState.items.find(item => item.id === productId);
        if (item) {
            item.quantity = quantity;
            if (item.quantity <= 0) {
                this.removeItem(productId);
            }
        }
    },

    clear() {
        cartState.items = [];
    },

    get totalItems() {
        return cartState.items.reduce((sum, item) => sum + item.quantity, 0);
    },

    get totalPrice() {
        return cartState.items.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    }
};
