package vn.quahoa.flowershop.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    @NotBlank
    private String customerName;

    @NotBlank
    private String shippingAddress;

    @NotBlank
    private String phoneNumber;

    private String notes;

    @NotEmpty
    @Valid
    private List<OrderItemRequest> items;
}
