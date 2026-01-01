import { createRouter, createWebHistory } from "vue-router";
import HomePage from "../components/HomePage.vue";
import ProductDetail from "../components/ProductDetail.vue";
import AdminLogin from "../components/AdminLogin.vue";
import AdminDashboard from "../components/AdminDashboard.vue";
import BlogList from "../components/BlogList.vue";
import BlogDetail from "../components/BlogDetail.vue";
import AdminProductManagement from "../components/AdminProductManagement.vue";

const routes = [
  {
    path: "/",
    name: "Home",
    component: HomePage,
  },
  {
    path: "/products/:id",
    name: "ProductDetail",
    component: ProductDetail,
  },
  {
    path: "/blogs",
    name: "BlogList",
    component: BlogList,
  },
  {
    path: "/blogs/:id",
    name: "BlogDetail",
    component: BlogDetail,
  },
  {
    path: "/admin/login",
    name: "AdminLogin",
    component: AdminLogin,
  },
  {
    path: "/admin/dashboard",
    name: "AdminDashboard",
    component: AdminDashboard,
    meta: { requiresAuth: true },
  },
  {
    path: "/admin/products",
    name: "AdminProductManagement",
    component: AdminProductManagement,
    meta: { requiresAuth: true },
  },
  {
    path: "/login",
    name: "UserLogin",
    component: () => import("../components/UserLogin.vue"),
  },
  {
    path: "/register",
    name: "UserRegister",
    component: () => import("../components/UserRegister.vue"),
  },
  {
    path: "/cart",
    name: "Cart",
    component: () => import("../components/ShoppingCart.vue"),
  },
  {
    path: "/checkout",
    name: "Checkout",
    component: () => import("../components/CheckoutPage.vue"),
  },
  {
    path: "/oauth2/redirect",
    component: () => import("../components/OAuth2RedirectHandler.vue"),
  },
  {
    path: "/payment/result",
    name: "PaymentResult",
    component: () => import("../components/PaymentResult.vue"),
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

// Navigation guard
router.beforeEach((to, from, next) => {
  const admin = JSON.parse(localStorage.getItem("admin") || "null");

  if (to.meta.requiresAuth && !admin) {
    next("/admin/login");
  } else {
    next();
  }
});

export default router;
