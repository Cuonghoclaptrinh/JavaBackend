package com.example.PRJWEB.Controller;

import com.example.PRJWEB.DTO.Request.ApiResponse;
import com.example.PRJWEB.DTO.Request.PaymentRequest;
import com.example.PRJWEB.DTO.Respon.PaymentResponse;
import com.example.PRJWEB.Entity.Notification;
import com.example.PRJWEB.Entity.Tour_booking;
import com.example.PRJWEB.Service.NotificationService;
import com.example.PRJWEB.Service.NotificationWebSocketHandler;
import com.example.PRJWEB.Service.PaymentService;
import com.example.PRJWEB.Service.TourBookingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {
    PaymentService paymentService;
    TourBookingService tourBookingService;
    NotificationService notificationService;
    NotificationWebSocketHandler notificationWebSocketHandler;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','STAFF','USER')")
    public ResponseEntity<ApiResponse<PaymentResponse>> makePayment(@RequestBody PaymentRequest request) {
        PaymentResponse paymentResponse = paymentService.makePayment(request);
        return ResponseEntity.ok(ApiResponse.<PaymentResponse>builder()
                .message("Thanh toán thành công!")
                .result(paymentResponse)
                .build());
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getUserPayments() {
        List<PaymentResponse> payments = paymentService.getUserPayments();
        return ResponseEntity.ok(ApiResponse.<List<PaymentResponse>>builder()
                .message("Lấy danh sách thanh toán thành công!")
                .result(payments)
                .build());
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getAllPayments() {
        List<PaymentResponse> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(ApiResponse.<List<PaymentResponse>>builder()
                .message("Lấy toàn bộ danh sách thanh toán thành công!")
                .result(payments)
                .build());
    }

    @GetMapping("/vnpay-url")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<String>> getPaymentUrl(
            @RequestParam("bookingId") Long bookingId,
            @RequestParam("amount") BigDecimal amount
    ) {
        String url = paymentService.createVnpayPaymentUrl(bookingId, amount);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .message("Redirect VNPAY")
                .result(url)
                .build());
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<String> handleVnpayReturn(@RequestParam Map<String, String> params) {
        String bookingId = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");
        if ("00".equals(responseCode)) {
            tourBookingService.updateBookingStatus(Long.parseLong(bookingId), "PAID");
            Tour_booking booking = tourBookingService.getBookingById(Long.parseLong(bookingId));
            if (!notificationService.existsNotification("SUCCESS", Long.parseLong(bookingId), booking.getCustomer().getId())) {
                Notification notification = new Notification();
                notification.setTitle("Thanh toán thành công!");
                notification.setMessage(String.format("Đặt tour %s (#%s) thành công. Cảm ơn bạn đã chọn chúng tôi!",
                        booking.getTour().getTourName(), bookingId));
                notification.setType("SUCCESS");
                notification.setIsActive(true);
                notification.setUserId(booking.getCustomer().getId());
                notification.setBookingId(Long.parseLong(bookingId));
                notification.setCreatedAt(LocalDateTime.now());
                notification.setExpiresAt(LocalDateTime.now().plusHours(24));
                notificationService.saveNotification(notification);

                try {
                    String wsMessage = String.format("{\"title\":\"%s\",\"message\":\"%s\",\"type\":\"%s\"}",
                            notification.getTitle(), notification.getMessage(), notification.getType());
                    notificationWebSocketHandler.sendNotificationToUser(
                            String.valueOf(booking.getCustomer().getId()), wsMessage);
                } catch (Exception e) {
                    System.err.println("Error sending WebSocket message: " + e.getMessage());
                }
            }
            return ResponseEntity.ok("Thanh toán thành công!");
        } else {
            tourBookingService.updateBookingStatus(Long.parseLong(bookingId), "FAILED");
            return ResponseEntity.ok("Thanh toán thất bại!");
        }
    }
}