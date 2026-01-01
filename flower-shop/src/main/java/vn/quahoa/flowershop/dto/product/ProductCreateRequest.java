package vn.quahoa.flowershop.dto.product;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductCreateRequest {

    @NotBlank(message = "Product code is required")
    private String productCode;

    @NotBlank(message = "Name is required")
    private String name;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be greater than or equal to zero")
    private Double price;

    private String imageUrl;
    
    private List<String> imageUrls;

    @NotNull(message = "Category id is required")
    private Long categoryId;
}
