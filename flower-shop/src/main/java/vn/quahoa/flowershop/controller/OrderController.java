package vn.quahoa.flowershop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import vn.quahoa.flowershop.dto.order.OrderRequest;
import vn.quahoa.flowershop.exception.ResourceNotFoundException;
import vn.quahoa.flowershop.model.*;
import vn.quahoa.flowershop.repository.OrderRepository;
import vn.quahoa.flowershop.repository.ProductRepository;
import vn.quahoa.flowershop.repository.UserRepository;
import vn.quahoa.flowershop.security.UserPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Order order = new Order();
        if (userPrincipal != null) {
            User user = userRepository.findById(userPrincipal.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", userPrincipal.getId()));
            order.setUser(user);
        }

        order.setCustomerName(request.getCustomerName());
        order.setShippingAddress(request.getShippingAddress());
        order.setPhoneNumber(request.getPhoneNumber());
        order.setNotes(request.getNotes());
        order.setStatus(OrderStatus.PENDING);

        double totalPrice = 0;

        for (var itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", itemRequest.getProductId()));

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(itemRequest.getQuantity());
            item.setPrice(product.getPrice());

            order.addItem(item);
            totalPrice += item.getPrice() * item.getQuantity();
        }

        order.setTotalPrice(totalPrice);
        Order savedOrder = orderRepository.save(order);

        return ResponseEntity.ok(savedOrder);
    }

    // Get orders for current user
    @GetMapping("/my-orders")
    public List<Order> getMyOrders(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            throw new RuntimeException("Unauthorized");
        }
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userPrincipal.getId());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        return ResponseEntity.ok(order);
    }
}
