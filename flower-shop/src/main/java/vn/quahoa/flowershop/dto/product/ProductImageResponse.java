package vn.quahoa.flowershop.dto.product;

import lombok.Data;
import vn.quahoa.flowershop.model.ProductImage;

@Data
public class ProductImageResponse {
    private Long id;
    private String imageUrl;
    private String fileName;
    private String filePath;
    private Integer displayOrder;

    public static ProductImageResponse fromEntity(ProductImage image) {
        ProductImageResponse response = new ProductImageResponse();
        response.setId(image.getId());
        response.setImageUrl(image.getImageUrl());
        response.setFileName(image.getFileName());
        response.setFilePath(image.getFilePath());
        response.setDisplayOrder(image.getDisplayOrder());
        return response;
    }
}
