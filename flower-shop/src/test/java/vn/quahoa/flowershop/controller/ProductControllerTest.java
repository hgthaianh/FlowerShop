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
import vn.quahoa.flowershop.dto.product.ProductCreateRequest;
import vn.quahoa.flowershop.dto.product.ProductUpdateRequest;
import vn.quahoa.flowershop.exception.ResourceNotFoundException;
import vn.quahoa.flowershop.model.Category;
import vn.quahoa.flowershop.model.Product;
import vn.quahoa.flowershop.security.CustomUserDetailsService;
import vn.quahoa.flowershop.security.TokenProvider;
import vn.quahoa.flowershop.service.ProductService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@DisplayName("ProductController Integration Tests")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private Product product;
    private Category category;
    private ProductCreateRequest createRequest;
    private ProductUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Hoa Tươi");

        product = new Product();
        product.setId(1L);
        product.setProductCode("SP001");
        product.setName("Hoa Hồng Đỏ");
        product.setDescription("Hoa hồng đỏ tươi");
        product.setPrice(150000.0);
        product.setCategory(category);

        createRequest = new ProductCreateRequest();
        createRequest.setProductCode("SP001");
        createRequest.setName("Hoa Hồng Đỏ");
        createRequest.setDescription("Hoa hồng đỏ tươi");
        createRequest.setPrice(150000.0);
        createRequest.setCategoryId(1L);

        updateRequest = new ProductUpdateRequest();
        updateRequest.setProductCode("SP001");
        updateRequest.setName("Hoa Hồng Đỏ Updated");
        updateRequest.setDescription("Hoa hồng đỏ tươi - updated");
        updateRequest.setPrice(180000.0);
        updateRequest.setCategoryId(1L);
    }

    // ========================================
    // CREATE PRODUCT TESTS
    // ========================================

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("TC_PROD_CREATE_01: POST /api/products - Tạo sản phẩm thành công")
    void createProduct_ValidRequest_Returns201() throws Exception {
        when(productService.createProduct(any(ProductCreateRequest.class))).thenReturn(product);

        mockMvc.perform(post("/api/products")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productCode").value("SP001"))
                .andExpect(jsonPath("$.name").value("Hoa Hồng Đỏ"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("TC_PROD_CREATE_02: POST /api/products với productCode trống - Returns 400")
    void createProduct_InvalidRequest_Returns400() throws Exception {
        ProductCreateRequest invalidRequest = new ProductCreateRequest();
        invalidRequest.setProductCode("");
        invalidRequest.setName("");
        invalidRequest.setPrice(150000.0);
        invalidRequest.setCategoryId(1L);

        mockMvc.perform(post("/api/products")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    // ========================================
    // GET ALL PRODUCTS TESTS
    // ========================================

    @Test
    @WithMockUser
    @DisplayName("TC_PROD_LIST_01: GET /api/products - Lấy tất cả sản phẩm")
    void getAllProducts_Returns200() throws Exception {
        Product product2 = new Product();
        product2.setId(2L);
        product2.setProductCode("SP002");
        product2.setName("Hoa Ly");
        product2.setPrice(200000.0);
        product2.setCategory(category);

        List<Product> products = Arrays.asList(product, product2);
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Hoa Hồng Đỏ"))
                .andExpect(jsonPath("$[1].name").value("Hoa Ly"));
    }

    @Test
    @WithMockUser
    @DisplayName("TC_PROD_LIST_02: GET /api/products?search=keyword - Tìm kiếm sản phẩm")
    void getAllProducts_WithSearch_Returns200() throws Exception {
        when(productService.searchProducts("Hồng")).thenReturn(List.of(product));

        mockMvc.perform(get("/api/products")
                .param("search", "Hồng"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Hoa Hồng Đỏ"));
    }

    // ========================================
    // GET PRODUCT BY ID TESTS
    // ========================================

    @Test
    @WithMockUser
    @DisplayName("TC_PROD_GET_01: GET /api/products/{id} - Product tồn tại")
    void getProduct_Exists_Returns200() throws Exception {
        when(productService.getById(1L)).thenReturn(product);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productCode").value("SP001"))
                .andExpect(jsonPath("$.name").value("Hoa Hồng Đỏ"));
    }

    @Test
    @WithMockUser
    @DisplayName("TC_PROD_GET_02: GET /api/products/{id} - Product không tồn tại")
    void getProduct_NotFound_Returns404() throws Exception {
        when(productService.getById(anyLong())).thenThrow(new ResourceNotFoundException("Product", 999L));

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }

    // ========================================
    // UPDATE PRODUCT TESTS
    // ========================================

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("TC_PROD_UPDATE_01: PUT /api/products/{id} - Cập nhật thành công")
    void updateProduct_ValidRequest_Returns200() throws Exception {
        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setProductCode("SP001");
        updatedProduct.setName("Hoa Hồng Đỏ Updated");
        updatedProduct.setPrice(180000.0);
        updatedProduct.setCategory(category);

        when(productService.updateProduct(anyLong(), any(ProductUpdateRequest.class))).thenReturn(updatedProduct);

        mockMvc.perform(put("/api/products/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Hoa Hồng Đỏ Updated"));
    }

    // ========================================
    // DELETE PRODUCT TESTS
    // ========================================

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("TC_PROD_DELETE_01: DELETE /api/products/{id} - Xóa thành công")
    void deleteProduct_Exists_Returns204() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }

    // ========================================
    // GET PRODUCTS BY CATEGORY TESTS
    // ========================================

    @Test
    @WithMockUser
    @DisplayName("TC_PROD_CAT_01: GET /api/categories/{categoryId}/products - Lấy sản phẩm theo category")
    void getProductsByCategory_Returns200() throws Exception {
        when(productService.getByCategory(1L)).thenReturn(List.of(product));

        mockMvc.perform(get("/api/categories/1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Hoa Hồng Đỏ"));
    }
}
