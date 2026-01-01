package vn.quahoa.flowershop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.quahoa.flowershop.model.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProductIdOrderByDisplayOrderAsc(Long productId);
}
