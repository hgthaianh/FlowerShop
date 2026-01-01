package vn.quahoa.flowershop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.quahoa.flowershop.dto.category.CategoryRequest;
import vn.quahoa.flowershop.exception.ResourceNotFoundException;
import vn.quahoa.flowershop.exception.ValidationException;
import vn.quahoa.flowershop.model.Category;
import vn.quahoa.flowershop.repository.CategoryRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService Unit Tests")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

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
    @DisplayName("TC_CAT_CREATE_01: Tạo category thành công với tên hợp lệ")
    void createCategory_Success() {
        // Arrange
        when(categoryRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // Act
        Category result = categoryService.createCategory(categoryRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Hoa Tươi");
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("TC_CAT_CREATE_04: Tạo category với tên trùng lặp - throw ValidationException")
    void createCategory_DuplicateName_ThrowsException() {
        // Arrange
        when(categoryRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(category));

        // Act & Assert
        assertThatThrownBy(() -> categoryService.createCategory(categoryRequest))
                .isInstanceOf(ValidationException.class);

        verify(categoryRepository, never()).save(any(Category.class));
    }

    // ========================================
    // FIND ALL CATEGORIES TESTS
    // ========================================

    @Test
    @DisplayName("TC_CAT_LIST_01: Lấy tất cả categories thành công")
    void findAll_ReturnsAllCategories() {
        // Arrange
        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Hoa Khô");

        List<Category> categories = Arrays.asList(category, category2);
        when(categoryRepository.findAll()).thenReturn(categories);

        // Act
        List<Category> result = categoryService.findAll();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Category::getName).containsExactly("Hoa Tươi", "Hoa Khô");
    }

    @Test
    @DisplayName("TC_CAT_LIST_02: Lấy categories khi database rỗng")
    void findAll_EmptyDatabase_ReturnsEmptyList() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(List.of());

        // Act
        List<Category> result = categoryService.findAll();

        // Assert
        assertThat(result).isEmpty();
    }

    // ========================================
    // GET BY ID TESTS
    // ========================================

    @Test
    @DisplayName("TC_CAT_LIST_04: Lấy category theo ID thành công")
    void getById_Success() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // Act
        Category result = categoryService.getById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Hoa Tươi");
    }

    @Test
    @DisplayName("TC_CAT_LIST_05: Lấy category với ID không tồn tại - throw ResourceNotFoundException")
    void getById_NotFound_ThrowsException() {
        // Arrange
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> categoryService.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ========================================
    // UPDATE CATEGORY TESTS
    // ========================================

    @Test
    @DisplayName("TC_CAT_UPDATE_01: Cập nhật category thành công")
    void updateCategory_Success() {
        // Arrange
        CategoryRequest updateRequest = new CategoryRequest();
        updateRequest.setName("Hoa Tươi Mới");

        Category updatedCategory = new Category();
        updatedCategory.setId(1L);
        updatedCategory.setName("Hoa Tươi Mới");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.findByNameIgnoreCase("Hoa Tươi Mới")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        // Act
        Category result = categoryService.updateCategory(1L, updateRequest);

        // Assert
        assertThat(result.getName()).isEqualTo("Hoa Tươi Mới");
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("TC_CAT_UPDATE_02: Cập nhật category không tồn tại - throw ResourceNotFoundException")
    void updateCategory_NotFound_ThrowsException() {
        // Arrange
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> categoryService.updateCategory(999L, categoryRequest))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(categoryRepository, never()).save(any(Category.class));
    }

    // ========================================
    // DELETE CATEGORY TESTS
    // ========================================

    @Test
    @DisplayName("TC_CAT_DELETE_01: Xóa category thành công")
    void deleteCategory_Success() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).delete(any(Category.class));

        // Act
        categoryService.deleteCategory(1L);

        // Assert
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    @DisplayName("TC_CAT_DELETE_02: Xóa category không tồn tại - throw ResourceNotFoundException")
    void deleteCategory_NotFound_ThrowsException() {
        // Arrange
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> categoryService.deleteCategory(999L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(categoryRepository, never()).delete(any(Category.class));
    }
}
