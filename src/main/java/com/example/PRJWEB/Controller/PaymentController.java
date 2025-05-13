package com.example.PRJWEB.Controller;

import com.example.PRJWEB.DTO.Request.ApiResponse;
import com.example.PRJWEB.DTO.Request.PaymentRequest;
import com.example.PRJWEB.DTO.Respon.PaymentDetailResponse;
import com.example.PRJWEB.DTO.Respon.PaymentResponse;
import com.example.PRJWEB.Entity.Notification;
import com.example.PRJWEB.Entity.Tour_booking;
import com.example.PRJWEB.Exception.AppException;
import com.example.PRJWEB.Exception.ErrorCode;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @GetMapping("/{bookingId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','STAFF','USER')")
    public ResponseEntity<ApiResponse<PaymentDetailResponse>> getPaymentDetail(@PathVariable Long bookingId) {
        PaymentDetailResponse response = paymentService.getPaymentDetail(bookingId);
        return ResponseEntity.ok(ApiResponse.<PaymentDetailResponse>builder()
                .message("Lấy chi tiết giao dịch thành công!")
                .result(response)
                .build());
    }

    @GetMapping("/vnpay-callback")
    public String handleVnpayCallback(@RequestParam Map<String, String> params) {
        try {
            paymentService.handleVnpayCallback(params);
            return "redirect:/payment-result?status=success";
        } catch (Exception e) {
            return "redirect:/payment-result?status=failed&message=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
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

        String orderInfo = params.get("vnp_OrderInfo");
        Long bookingId;
        try {
            System.out.println("Parsing vnp_OrderInfo: " + orderInfo);
            String[] parts = orderInfo.split(":");
            String idStr = parts[parts.length - 1].trim();
            bookingId = Long.valueOf(idStr);
        } catch (Exception e) {
            System.err.println("Error parsing vnp_OrderInfo: " + e.getMessage());
            return ResponseEntity.status(302).header("Location", "http://localhost:3000/payment-result?status=failed").body(null);
        }

        String responseCode = params.get("vnp_ResponseCode");
        String amountStr = params.get("vnp_Amount");
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr).divide(new BigDecimal(100));
        } catch (Exception e) {
            return ResponseEntity.status(302).header("Location", "http://localhost:3000/payment-result?status=failed").body(null);
        }

        if ("00".equals(responseCode)) {
            Tour_booking booking = tourBookingService.getBookingById(bookingId);
            if (!booking.getStatus().equals("PENDING") && !booking.getStatus().equals("DEPOSITED")) {
                System.out.println("Booking already processed: status=" + booking.getStatus());
                return ResponseEntity.status(302).header("Location", "http://localhost:3000/payment-result?status=already_processed").body(null);
            }

            PaymentRequest paymentRequest = new PaymentRequest();
            paymentRequest.setBookingId(bookingId);
            paymentRequest.setAmount(amount);
            paymentRequest.setMethod("VNPAY");
            boolean isPayFull = amount.compareTo(booking.getTotalPrice()) >= 0;
            paymentRequest.setPayFull(isPayFull);
            paymentService.makePayment(paymentRequest);

            if (!notificationService.existsNotification("PAYMENT_SUCCESS", bookingId, booking.getCustomer().getId())) {
                Notification notification = new Notification();
                notification.setTitle("Thanh toán thành công!");
                notification.setMessage(String.format("Bạn đã thanh toán %s VNĐ cho tour %s (#%s). Cảm ơn bạn đã chọn chúng tôi!",
                        amount, booking.getTour().getTourName(), bookingId));
                notification.setType("PAYMENT_SUCCESS");
                notification.setIsActive(true);
                notification.setUserId(booking.getCustomer().getId());
                notification.setBookingId(bookingId);
                notification.setCreatedAt(LocalDateTime.now());
                notification.setExpiresAt(LocalDateTime.now().plusDays(7));
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
            return ResponseEntity.status(302).header("Location", "http://localhost:3000/payment-result?status=success").body(null);
        } else {
            tourBookingService.updateBookingStatus(bookingId, "PENDING");
            return ResponseEntity.status(302).header("Location", "http://localhost:3000/payment-result?status=failed").body(null);
        }
    }
}