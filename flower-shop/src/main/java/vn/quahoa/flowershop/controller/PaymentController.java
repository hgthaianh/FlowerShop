package vn.quahoa.flowershop.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.quahoa.flowershop.service.VNPayService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final VNPayService vnPayService;

    @PostMapping("/create_payment_url")
    public ResponseEntity<?> createPaymentUrl(@RequestParam("amount") long amount,
            @RequestParam("orderInfo") String orderInfo,
            HttpServletRequest request) throws UnsupportedEncodingException {
        // Force use of 8080 or better yet, read from config
        String returnUrl = "http://localhost:8080/api/payment/vnpay_return";

        String paymentUrl = vnPayService.createPaymentUrl(amount, orderInfo, returnUrl, request);
        Map<String, String> response = new HashMap<>();
        response.put("url", paymentUrl);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vnpay_return")
    public void vnpayReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int paymentStatus = vnPayService.orderReturn(request);

        String orderInfo = request.getParameter("vnp_OrderInfo");
        String totalPrice = request.getParameter("vnp_Amount");

        String frontendUrl = "http://localhost:84/payment/result";

        frontendUrl += "?status=" + (paymentStatus == 1 ? "success" : "failed");
        frontendUrl += "&orderInfo=" + (orderInfo != null ? orderInfo : "");
        frontendUrl += "&totalPrice=" + (totalPrice != null ? totalPrice : "");

        response.sendRedirect(frontendUrl);
    }
}
