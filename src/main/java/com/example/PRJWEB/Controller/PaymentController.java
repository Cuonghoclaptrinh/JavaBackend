package com.example.PRJWEB.Controller;

import com.example.PRJWEB.DTO.Request.ApiResponse;
import com.example.PRJWEB.DTO.Request.PaymentRequest;
import com.example.PRJWEB.DTO.Request.TourBookingRequest;
import com.example.PRJWEB.DTO.Respon.PaymentResponse;
import com.example.PRJWEB.DTO.Respon.TourBookingResponse;
import com.example.PRJWEB.Service.PaymentService;
import com.example.PRJWEB.Service.TourBookingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class PaymentController {
    PaymentService paymentService;

    @PostMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN','STAFF','USER')")
    public ResponseEntity<PaymentResponse> makePayment(@RequestBody PaymentRequest request) {
        // Xử lý thanh toán và trả về response
        PaymentResponse paymentResponse = paymentService.makePayment(request);
        return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
    }

    @GetMapping("/vnpay-url")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<String>> getPaymentUrl(
            @RequestParam("bookingId") Long bookingId,
            @RequestParam("amount") BigDecimal amount
    ) {
        String url = paymentService.createVnpayPaymentUrl(bookingId, amount);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .message("Redirect VNPAY")
                .result(url)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<String> handleVnpayReturn(@RequestParam Map<String, String> params) {
        // Kiểm tra chữ ký, mã hóa...
        String responseCode = params.get("vnp_ResponseCode");
        if ("00".equals(responseCode)) {
            return ResponseEntity.ok("Thanh toán thành công!");
        } else {
            return ResponseEntity.ok("Thanh toán thất bại!");
        }
    }

}
