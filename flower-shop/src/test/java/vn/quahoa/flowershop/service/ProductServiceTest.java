package vn.quahoa.flowershop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.quahoa.flowershop.dto.product.ProductCreateRequest;
import vn.quahoa.flowershop.dto.product.ProductUpdateRequest;
import vn.quahoa.flowershop.exception.ResourceNotFoundException;
import vn.quahoa.flowershop.exception.ValidationException;
import vn.quahoa.flowershop.model.Category;
import vn.quahoa.flowershop.model.Product;
import vn.quahoa.flowershop.repository.CategoryRepository;
import vn.quahoa.flowershop.repository.ProductImageRepository;
import vn.quahoa.flowershop.repository.ProductRepository;

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
@DisplayName("ProductService Unit Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ImageStorageService imageStorageService;

    @Mock
    private ProductImageRepository productImageRepository;

    @InjectMocks
    private ProductService productService;

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
    @DisplayName("TC_PROD_CREATE_01: Tạo sản phẩm thành công với thông tin hợp lệ")
    void createProduct_Success() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.findByProductCodeIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(productRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        Product result = productService.createProduct(createRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getProductCode()).isEqualTo("SP001");
        assertThat(result.getName()).isEqualTo("Hoa Hồng Đỏ");
        verify(productRepository, atLeastOnce()).save(any(Product.class));
    }

    @Test
    @DisplayName("TC_PROD_CREATE_05: Tạo sản phẩm với categoryId không tồn tại")
    void createProduct_CategoryNotFound_ThrowsException() {
        // Arrange
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.createProduct(createRequest))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("TC_PROD_CREATE_02: Tạo sản phẩm với productCode trùng")
    void createProduct_DuplicateCode_ThrowsException() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.findByProductCodeIgnoreCase(anyString())).thenReturn(Optional.of(product));

        // Act & Assert
        assertThatThrownBy(() -> productService.createProduct(createRequest))
                .isInstanceOf(ValidationException.class);

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("TC_PROD_CREATE_03: Tạo sản phẩm với tên trùng")
    void createProduct_DuplicateName_ThrowsException() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.findByProductCodeIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(productRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(product));

        // Act & Assert
        assertThatThrownBy(() -> productService.createProduct(createRequest))
                .isInstanceOf(ValidationException.class);

        verify(productRepository, never()).save(any(Product.class));
    }

    // ========================================
    // GET ALL PRODUCTS TESTS
    // ========================================

    @Test
    @DisplayName("TC_PROD_LIST_01: Lấy tất cả sản phẩm")
    void getAllProducts_ReturnsAllProducts() {
        // Arrange
        Product product2 = new Product();
        product2.setId(2L);
        product2.setProductCode("SP002");
        product2.setName("Hoa Ly");
        product2.setPrice(200000.0);
        product2.setCategory(category);

        List<Product> products = Arrays.asList(product, product2);
        when(productRepository.findAll()).thenReturn(products);

        // Act
        List<Product> result = productService.getAllProducts();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Product::getName).containsExactly("Hoa Hồng Đỏ", "Hoa Ly");
    }

    // ========================================
    // GET BY ID TESTS
    // ========================================

    @Test
    @DisplayName("TC_PROD_GET_01: Lấy sản phẩm theo ID thành công")
    void getById_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        Product result = productService.getById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Hoa Hồng Đỏ");
    }

    @Test
    @DisplayName("TC_PROD_GET_02: Lấy sản phẩm với ID không tồn tại")
    void getById_NotFound_ThrowsException() {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ========================================
    // GET BY CATEGORY TESTS
    // ========================================

    @Test
    @DisplayName("TC_PROD_CAT_01: Lấy sản phẩm theo category thành công")
    void getByCategory_Success() {
        // Arrange
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(productRepository.findByCategory_Id(1L)).thenReturn(List.of(product));

        // Act
        List<Product> result = productService.getByCategory(1L);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Hoa Hồng Đỏ");
    }

    @Test
    @DisplayName("TC_PROD_CAT_02: Lấy sản phẩm với category không tồn tại")
    void getByCategory_CategoryNotFound_ThrowsException() {
        // Arrange
        when(categoryRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> productService.getByCategory(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ========================================
    // SEARCH PRODUCTS TESTS
    // ========================================

    @Test
    @DisplayName("TC_PROD_LIST_02: Tìm kiếm sản phẩm theo keyword")
    void searchProducts_ReturnsMatchingProducts() {
        // Arrange
        when(productRepository.searchProducts("Hồng")).thenReturn(List.of(product));

        // Act
        List<Product> result = productService.searchProducts("Hồng");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).contains("Hồng");
    }

    // ========================================
    // UPDATE PRODUCT TESTS
    // ========================================

    @Test
    @DisplayName("TC_PROD_UPDATE_01: Cập nhật sản phẩm thành công")
    void updateProduct_Success() {
        // Arrange
        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setProductCode("SP001");
        updatedProduct.setName("Hoa Hồng Đỏ Updated");
        updatedProduct.setPrice(180000.0);
        updatedProduct.setCategory(category);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.findByProductCodeIgnoreCase(anyString())).thenReturn(Optional.of(product)); // Same
                                                                                                           // product
        when(productRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // Act
        Product result = productService.updateProduct(1L, updateRequest);

        // Assert
        assertThat(result.getName()).isEqualTo("Hoa Hồng Đỏ Updated");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("TC_PROD_UPDATE_02: Cập nhật sản phẩm không tồn tại")
    void updateProduct_NotFound_ThrowsException() {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.updateProduct(999L, updateRequest))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(productRepository, never()).save(any(Product.class));
    }

    // ========================================
    // DELETE PRODUCT TESTS
    // ========================================

    @Test
    @DisplayName("TC_PROD_DELETE_01: Xóa sản phẩm thành công")
    void deleteProduct_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(any(Product.class));

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    @DisplayName("TC_PROD_DELETE_02: Xóa sản phẩm không tồn tại")
    void deleteProduct_NotFound_ThrowsException() {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.deleteProduct(999L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(productRepository, never()).delete(any(Product.class));
    }
}
