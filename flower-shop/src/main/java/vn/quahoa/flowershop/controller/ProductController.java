package vn.quahoa.flowershop.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.quahoa.flowershop.dto.product.ImageUrlRequest;
import vn.quahoa.flowershop.dto.product.ProductCreateRequest;
import vn.quahoa.flowershop.dto.product.ProductResponse;
import vn.quahoa.flowershop.dto.product.ProductUpdateRequest;
import vn.quahoa.flowershop.service.ProductService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@Valid @RequestBody ProductCreateRequest request) {
        return ProductResponse.fromEntity(productService.createProduct(request));
    }

    @GetMapping("/products")
    public List<ProductResponse> getAllProducts(@RequestParam(required = false) String search) {
        if (search != null && !search.trim().isEmpty()) {
            return productService.searchProducts(search).stream()
                    .map(ProductResponse::fromEntity)
                    .collect(Collectors.toList());
        }
        return productService.getAllProducts().stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/products/{id}")
    public ProductResponse getProduct(@PathVariable Long id) {
        return ProductResponse.fromEntity(productService.getById(id));
    }

    @PutMapping("/products/{id}")
    public ProductResponse updateProduct(@PathVariable Long id, @Valid @RequestBody ProductUpdateRequest request) {
        return ProductResponse.fromEntity(productService.updateProduct(id, request));
    }

    @DeleteMapping("/products/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    @GetMapping("/categories/{categoryId}/products")
    public List<ProductResponse> getProductsByCategory(@PathVariable Long categoryId) {
        return productService.getByCategory(categoryId).stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping("/products/{id}/images")
    public ResponseEntity<String> uploadProductImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        String imageUrl = productService.uploadProductImage(id, file);
        return ResponseEntity.ok(imageUrl);
    }

    @PostMapping("/products/{id}/images/main")
    public ResponseEntity<String> uploadMainProductImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        String imageUrl = productService.uploadMainProductImage(id, file);
        return ResponseEntity.ok(imageUrl);
    }

    @PostMapping("/products/{id}/images/main-url")
    public ResponseEntity<String> uploadMainProductImageFromUrl(@PathVariable Long id, @RequestBody ImageUrlRequest request) {
        String imageUrl = productService.uploadMainProductImageFromUrl(id, request.getImageUrl());
        return ResponseEntity.ok(imageUrl);
    }

    @PostMapping("/products/{id}/images/url")
    public ResponseEntity<String> uploadProductImageFromUrl(@PathVariable Long id, @RequestBody ImageUrlRequest request) {
        String imageUrl = productService.uploadProductImageFromUrl(id, request.getImageUrl());
        return ResponseEntity.ok(imageUrl);
    }

    // ============================================
    // DELETE IMAGE ENDPOINTS
    // ============================================

    @DeleteMapping("/products/{id}/images/main")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMainImage(@PathVariable Long id) {
        productService.deleteMainImage(id);
    }

    @DeleteMapping("/products/{id}/images/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductImage(@PathVariable Long id, @PathVariable Long imageId) {
        productService.deleteProductImage(id, imageId);
    }

    @DeleteMapping("/products/{id}/images")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllProductImages(@PathVariable Long id) {
        productService.deleteAllProductImages(id);
    }

    // ============================================
    // UPDATE IMAGE ENDPOINTS
    // ============================================

    @PutMapping("/products/{id}/images/main")
    public ResponseEntity<String> updateMainImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        String imageUrl = productService.updateMainImage(id, file);
        return ResponseEntity.ok(imageUrl);
    }

    @PutMapping("/products/{id}/images/main-url")
    public ResponseEntity<String> updateMainImageFromUrl(@PathVariable Long id, @RequestBody ImageUrlRequest request) {
        String imageUrl = productService.updateMainImageFromUrl(id, request.getImageUrl());
        return ResponseEntity.ok(imageUrl);
    }
}




