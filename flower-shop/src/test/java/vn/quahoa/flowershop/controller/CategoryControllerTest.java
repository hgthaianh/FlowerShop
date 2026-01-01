package vn.quahoa.flowershop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import vn.quahoa.flowershop.dto.category.CategoryRequest;
import vn.quahoa.flowershop.exception.ResourceNotFoundException;
import vn.quahoa.flowershop.model.Category;
import vn.quahoa.flowershop.security.CustomUserDetailsService;
import vn.quahoa.flowershop.security.TokenProvider;
import vn.quahoa.flowershop.service.CategoryService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@DisplayName("CategoryController Integration Tests")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private Category category;
    private CategoryRequest categoryRequest;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Hoa Tươi");

        categoryRequest = new CategoryRequest();
        categoryRequest.setName("Hoa Tươi");
    }

    // ========================================
    // CREATE CATEGORY TESTS
    // ========================================

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("TC_CAT_CREATE_01: POST /api/categories - Tạo category thành công")
    void createCategory_ValidRequest_Returns201() throws Exception {
        when(categoryService.createCategory(any(CategoryRequest.class))).thenReturn(category);

        mockMvc.perform(post("/api/categories")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Hoa Tươi"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("TC_CAT_CREATE_02: POST /api/categories với name trống - Returns 400")
    void createCategory_EmptyName_Returns400() throws Exception {
        CategoryRequest invalidRequest = new CategoryRequest();
        invalidRequest.setName("");

        mockMvc.perform(post("/api/categories")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    // ========================================
    // GET ALL CATEGORIES TESTS
    // ========================================

    @Test
    @WithMockUser
    @DisplayName("TC_CAT_LIST_01: GET /api/categories - Lấy tất cả categories")
    void getAllCategories_Returns200() throws Exception {
        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Hoa Khô");

        List<Category> categories = Arrays.asList(category, category2);
        when(categoryService.findAll()).thenReturn(categories);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Hoa Tươi"))
                .andExpect(jsonPath("$[1].name").value("Hoa Khô"));
    }

    // ========================================
    // GET CATEGORY BY ID TESTS
    // ========================================

    @Test
    @WithMockUser
    @DisplayName("TC_CAT_LIST_04: GET /api/categories/{id} - Category tồn tại")
    void getCategory_Exists_Returns200() throws Exception {
        when(categoryService.getById(1L)).thenReturn(category);

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Hoa Tươi"));
    }

    @Test
    @WithMockUser
    @DisplayName("TC_CAT_LIST_05: GET /api/categories/{id} - Category không tồn tại")
    void getCategory_NotFound_Returns404() throws Exception {
        when(categoryService.getById(anyLong())).thenThrow(new ResourceNotFoundException("Category", 999L));

        mockMvc.perform(get("/api/categories/999"))
                .andExpect(status().isNotFound());
    }

    // ========================================
    // UPDATE CATEGORY TESTS
    // ========================================

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("TC_CAT_UPDATE_01: PUT /api/categories/{id} - Cập nhật thành công")
    void updateCategory_ValidRequest_Returns200() throws Exception {
        Category updatedCategory = new Category();
        updatedCategory.setId(1L);
        updatedCategory.setName("Hoa Tươi Mới");

        when(categoryService.updateCategory(anyLong(), any(CategoryRequest.class))).thenReturn(updatedCategory);

        CategoryRequest updateRequest = new CategoryRequest();
        updateRequest.setName("Hoa Tươi Mới");

        mockMvc.perform(put("/api/categories/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Hoa Tươi Mới"));
    }

    // ========================================
    // DELETE CATEGORY TESTS
    // ========================================

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("TC_CAT_DELETE_01: DELETE /api/categories/{id} - Xóa thành công")
    void deleteCategory_Exists_Returns204() throws Exception {
        doNothing().when(categoryService).deleteCategory(1L);

        mockMvc.perform(delete("/api/categories/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).deleteCategory(1L);
    }
}
