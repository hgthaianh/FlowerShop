package vn.quahoa.flowershop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import vn.quahoa.flowershop.dto.order.OrderItemRequest;
import vn.quahoa.flowershop.dto.order.OrderRequest;
import vn.quahoa.flowershop.model.*;
import vn.quahoa.flowershop.repository.OrderRepository;
import vn.quahoa.flowershop.repository.ProductRepository;
import vn.quahoa.flowershop.repository.UserRepository;
import vn.quahoa.flowershop.security.CustomUserDetailsService;
import vn.quahoa.flowershop.security.TokenProvider;
import vn.quahoa.flowershop.security.UserPrincipal;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@DisplayName("OrderController Integration Tests")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private Order order;
    private OrderRequest orderRequest;
    private Product product;
    private Category category;
    private User testUser;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Hoa Tươi");

        product = new Product();
        product.setId(1L);
        product.setProductCode("SP001");
        product.setName("Hoa Hồng Đỏ");
        product.setPrice(150000.0);
        product.setCategory(category);

        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Nguyen Van A");
        testUser.setEmail("user@example.com");
        testUser.setProvider(AuthProvider.LOCAL);

        order = new Order();
        order.setId(1L);
        order.setCustomerName("Nguyen Van A");
        order.setShippingAddress("123 Street");
        order.setPhoneNumber("0901234567");
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(300000.0);
        order.setUser(testUser);

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);

        orderRequest = new OrderRequest();
        orderRequest.setCustomerName("Nguyen Van A");
        orderRequest.setShippingAddress("123 Street");
        orderRequest.setPhoneNumber("0901234567");
        orderRequest.setNotes("Giao buổi sáng");
        orderRequest.setItems(List.of(itemRequest));
    }

    // ========================================
    // CREATE ORDER TESTS
    // ========================================

    @Test
    @WithMockUser
    @DisplayName("TC_ORDER_CREATE_01: POST /api/orders - Tạo đơn hàng thành công")
    void createOrder_ValidRequest_Returns200() throws Exception {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        mockMvc.perform(post("/api/orders")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Nguyen Van A"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @WithMockUser
    @DisplayName("TC_ORDER_CREATE_05: POST /api/orders với items rỗng - Returns 400")
    void createOrder_EmptyItems_Returns400() throws Exception {
        orderRequest.setItems(List.of());

        mockMvc.perform(post("/api/orders")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("TC_ORDER_CREATE_06: POST /api/orders với product không tồn tại - Returns 404")
    void createOrder_ProductNotFound_Returns404() throws Exception {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/orders")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isNotFound());
    }

    // ========================================
    // GET MY ORDERS TESTS
    // ========================================

    @Test
    @DisplayName("TC_ORDER_MYORDERS_01: GET /api/orders/my-orders - Lấy đơn hàng của user")
    void getMyOrders_Authenticated_Returns200() throws Exception {
        UserPrincipal userPrincipal = UserPrincipal.create(testUser);

        when(orderRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(order));

        mockMvc.perform(get("/api/orders/my-orders")
                .with(user(userPrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].customerName").value("Nguyen Van A"));
    }

    @Test
    @WithMockUser
    @DisplayName("TC_ORDER_MYORDERS_03: GET /api/orders/my-orders - User không có đơn hàng nào")
    void getMyOrders_NoOrders_ReturnsEmptyList() throws Exception {
        UserPrincipal userPrincipal = UserPrincipal.create(testUser);

        when(orderRepository.findByUserIdOrderByCreatedAtDesc(anyLong())).thenReturn(List.of());

        mockMvc.perform(get("/api/orders/my-orders")
                .with(user(userPrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ========================================
    // GET ORDER BY ID TESTS
    // ========================================

    @Test
    @WithMockUser
    @DisplayName("TC_ORDER_DETAIL_01: GET /api/orders/{id} - Order tồn tại")
    void getOrderById_Exists_Returns200() throws Exception {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerName").value("Nguyen Van A"));
    }

    @Test
    @WithMockUser
    @DisplayName("TC_ORDER_DETAIL_02: GET /api/orders/{id} - Order không tồn tại")
    void getOrderById_NotFound_Returns404() throws Exception {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/orders/999"))
                .andExpect(status().isNotFound());
    }
}
