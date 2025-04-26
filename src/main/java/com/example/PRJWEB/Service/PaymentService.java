package com.example.PRJWEB.Service;

import com.example.PRJWEB.Configure.VNPayConfig;
import com.example.PRJWEB.DTO.Request.PaymentRequest;
import com.example.PRJWEB.DTO.Respon.PaymentResponse;
import com.example.PRJWEB.Entity.Payment;
import com.example.PRJWEB.Entity.Tour_booking;
import com.example.PRJWEB.Entity.Notification;
import com.example.PRJWEB.Enums.PaymentStatus;
import com.example.PRJWEB.Exception.AppException;
import com.example.PRJWEB.Exception.ErrorCode;
import com.example.PRJWEB.Mapper.PaymentMapper;
import com.example.PRJWEB.Repository.PaymentRepository;
import com.example.PRJWEB.Repository.TourBookingRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {

    PaymentRepository paymentRepository;
    TourBookingRepository tourBookingRepository;
    TourBookingService tourBookingService;
    PaymentMapper paymentMapper;
    VNPayConfig vnPayConfig;
    NotificationService notificationService;
    NotificationWebSocketHandler notificationWebSocketHandler;

    private static final BigDecimal DEPOSIT_RATIO = new BigDecimal("0.3"); // Tỷ lệ cọc tối thiểu: 30%

    public PaymentResponse makePayment(PaymentRequest request) {
        // Tìm booking
        Tour_booking booking = tourBookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        // Kiểm tra trạng thái booking
        if (!booking.getStatus().equals("PENDING") && !booking.getStatus().equals("DEPOSITED")) {
            throw new AppException(ErrorCode.INVALID_BOOKING_STATUS);
        }

        // Tính tổng số tiền đã thanh toán
        List<Payment> existingPayments = paymentRepository.findByBooking(booking);
        BigDecimal paidAmount = existingPayments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPrice = booking.getTotalPrice();
        BigDecimal remainingAmount = totalPrice.subtract(paidAmount);
        BigDecimal amountToPay = request.getAmount();

        // Kiểm tra số tiền thanh toán
        if (amountToPay.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ErrorCode.INVALID_PAYMENT_AMOUNT);
        }

        // Xử lý thanh toán
        String notificationType;
        String bookingStatus;
        Payment payment = Payment.builder()
                .booking(booking)
                .amount(amountToPay)
                .paymentDate(LocalDateTime.now())
                .method(request.getMethod())
                .build();

        if (booking.getStatus().equals("PENDING")) {
            // Lần thanh toán đầu tiên
            BigDecimal minDeposit = totalPrice.multiply(DEPOSIT_RATIO);
            if (request.isPayFull() || amountToPay.compareTo(totalPrice) >= 0) {
                // Thanh toán toàn bộ
                if (amountToPay.compareTo(totalPrice) < 0) {
                    throw new AppException(ErrorCode.INSUFFICIENT_PAYMENT_AMOUNT);
                }
                notificationType = "PAYMENT_SUCCESS";
                bookingStatus = "PAID";
                payment.setStatus(PaymentStatus.PAID);
                payment.setRemainingAmount(BigDecimal.ZERO);
            } else {
                // Đặt cọc
                if (amountToPay.compareTo(minDeposit) < 0) {
                    throw new AppException(ErrorCode.INSUFFICIENT_DEPOSIT_AMOUNT);
                }
                notificationType = "DEPOSIT_SUCCESS";
                bookingStatus = "DEPOSITED";
                payment.setStatus(PaymentStatus.PENDING);
                payment.setRemainingAmount(totalPrice.subtract(amountToPay));
            }
        } else {
            // Thanh toán phần còn lại
            if (amountToPay.compareTo(remainingAmount) < 0) {
                throw new AppException(ErrorCode.INSUFFICIENT_PAYMENT_AMOUNT);
            }
            notificationType = "PAYMENT_SUCCESS";
            bookingStatus = "PAID";
            payment.setStatus(PaymentStatus.PAID);
            payment.setRemainingAmount(BigDecimal.ZERO);
        }

        // Lưu thanh toán
        Payment savedPayment = paymentRepository.save(payment);

        // Cập nhật trạng thái booking
        tourBookingService.updateBookingStatus(booking.getBookingId(), bookingStatus);

        // Tạo thông báo
        if (!notificationService.existsNotification(notificationType, booking.getBookingId(), booking.getCustomer().getId())) {
            Notification notification = new Notification();
            notification.setTitle(notificationType.equals("DEPOSIT_SUCCESS") ? "Đặt cọc thành công!" : "Thanh toán thành công!");
            notification.setMessage(String.format(
                    notificationType.equals("DEPOSIT_SUCCESS") ?
                            "Bạn đã đặt cọc %s VNĐ cho tour %s (#%s). Vui lòng thanh toán phần còn lại!" :
                            "Bạn đã thanh toán %s VNĐ cho tour %s (#%s). Cảm ơn bạn đã chọn chúng tôi!",
                    amountToPay, booking.getTour().getTourName(), booking.getBookingId()
            ));
            notification.setType(notificationType);
            notification.setIsActive(true);
            notification.setUserId(booking.getCustomer().getId());
            notification.setBookingId(booking.getBookingId());
            notification.setCreatedAt(LocalDateTime.now());
            notification.setExpiresAt(LocalDateTime.now().plusDays(7));
            notificationService.saveNotification(notification);

            try {
                String wsMessage = String.format("{\"title\":\"%s\",\"message\":\"%s\",\"type\":\"%s\"}",
                        notification.getTitle(), notification.getMessage(), notification.getType());
                notificationWebSocketHandler.sendNotificationToUser(
                        String.valueOf(booking.getCustomer().getId()), wsMessage);
            } catch (Exception e) {
                System.out.println("Failed to send WebSocket message: " + e.getMessage());
            }
        }

        // Trả về response
        return paymentMapper.toPaymentResponse(savedPayment);
    }

    public List<PaymentResponse> getUserPayments() {
        Long userId = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getClaim("user_id");
        if (userId == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        List<Tour_booking> bookings = tourBookingRepository.findByCustomerId(userId);
        List<Payment> payments = bookings.stream()
                .flatMap(booking -> paymentRepository.findByBooking(booking).stream())
                .collect(Collectors.toList());

        return payments.stream()
                .map(paymentMapper::toPaymentResponse)
                .collect(Collectors.toList());
    }

    public List<PaymentResponse> getAllPayments() {
        String scope = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getClaimAsString("scope");
        if (!"ADMIN".equals(scope)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        List<Payment> payments = paymentRepository.findAll();
        return payments.stream()
                .map(paymentMapper::toPaymentResponse)
                .collect(Collectors.toList());
    }

    public String createVnpayPaymentUrl(Long bookingId, BigDecimal amount) {
        Tour_booking booking = tourBookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        // Kiểm tra số tiền hợp lệ
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ErrorCode.INVALID_PAYMENT_AMOUNT);
        }

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderInfo = "Thanh toán đơn tour: " + bookingId;
        String orderType = "billpayment";
        String txnRef = String.valueOf(System.currentTimeMillis());

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnPayConfig.getTmnCode());
        vnp_Params.put("vnp_Amount", String.valueOf(amount.multiply(new BigDecimal(100)).intValue()));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", txnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
        vnp_Params.put("vnp_IpAddr", "127.0.0.1");
        vnp_Params.put("vnp_CreateDate", LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (String fieldName : fieldNames) {
            String value = vnp_Params.get(fieldName);
            if (value != null && !value.isEmpty()) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(value, StandardCharsets.US_ASCII)).append('&');
                query.append(fieldName).append('=').append(URLEncoder.encode(value, StandardCharsets.US_ASCII)).append('&');
            }
        }

        hashData.setLength(hashData.length() - 1);
        query.setLength(query.length() - 1);

        String secureHash = hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        query.append("&vnp_SecureHash=").append(secureHash);

        return vnPayConfig.getPayUrl() + "?" + query.toString();
    }

    public boolean verifyChecksum(Map<String, String> params) {
        String vnp_SecureHash = params.remove("vnp_SecureHash");
        if (vnp_SecureHash == null) {
            return false;
        }

        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        for (String fieldName : fieldNames) {
            String value = params.get(fieldName);
            if (value != null && !value.isEmpty()) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(value, StandardCharsets.US_ASCII)).append('&');
            }
        }
        hashData.setLength(hashData.length() - 1);

        String calculatedHash = hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        return calculatedHash.equals(vnp_SecureHash);
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] bytes = hmac512.doFinal(data.getBytes());
            StringBuilder hash = new StringBuilder();
            for (byte b : bytes) {
                hash.append(String.format("%02x", b));
            }
            return hash.toString();
        } catch (Exception e) {
            System.err.println("Error in hmacSHA512: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Lỗi tạo chữ ký thanh toán", e);
        }
    }
}