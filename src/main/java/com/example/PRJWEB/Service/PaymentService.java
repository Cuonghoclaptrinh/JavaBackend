package com.example.PRJWEB.Service;

import com.example.PRJWEB.Configure.VNPayConfig;
import com.example.PRJWEB.DTO.Request.PaymentRequest;
import com.example.PRJWEB.DTO.Respon.PaymentResponse;
import com.example.PRJWEB.Entity.Payment;
import com.example.PRJWEB.Entity.Tour_booking;
import com.example.PRJWEB.Enums.PaymentStatus;
import com.example.PRJWEB.Exception.AppException;
import com.example.PRJWEB.Exception.ErrorCode;
import com.example.PRJWEB.Mapper.PaymentMapper;
import com.example.PRJWEB.Repository.PaymentRepository;
import com.example.PRJWEB.Repository.TourBookingRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {

    PaymentRepository paymentRepository;
    TourBookingRepository tourBookingRepository;
    PaymentMapper paymentMapper;
    VNPayConfig vnPayConfig;

    public PaymentResponse makePayment(PaymentRequest request) {
        // Tìm booking theo ID
        Tour_booking booking = tourBookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        BigDecimal paidAmount = paymentRepository.findByBooking(booking)
                .stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPrice = booking.getTotalPrice();
        BigDecimal remaining = totalPrice.subtract(paidAmount);

        // Kiểm tra xem số tiền thanh toán có hợp lệ không
        if (request.getPaidAmount().compareTo(remaining) > 0) {
            throw new AppException(ErrorCode.PAYMENT_AMOUNT_EXCEED);
        }

        // Tạo đối tượng Payment
        Payment payment = Payment.builder()
                .booking(booking)
                .amount(request.getPaidAmount())
                .paymentDate(LocalDateTime.now())
                .method(request.getMethod())
                .status(PaymentStatus.PAID)
                .remainingAmount(remaining)
                .build();

        // Lưu Payment vào DB
        paymentRepository.save(payment);

        // Trả về thông tin Payment Response
        return paymentMapper.toPaymentResponse(payment);
    }

    public String createVnpayPaymentUrl(Long bookingId, BigDecimal amount) {
        // Lấy thông tin booking từ ID
        Tour_booking booking = tourBookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        System.out.println("tmnCode: " + vnPayConfig.getTmnCode());
        System.out.println("hashSecret: " + vnPayConfig.getHashSecret());
        System.out.println("payUrl: " + vnPayConfig.getPayUrl());
        System.out.println("returnUrl: " + vnPayConfig.getReturnUrl());

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderInfo = "Thanh toán đơn tour: " + bookingId;
        String orderType = "billpayment";
        String txnRef = String.valueOf(System.currentTimeMillis());

        // Tạo map các tham số cho VNPay
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnPayConfig.getTmnCode());
        vnp_Params.put("vnp_Amount", String.valueOf(amount.multiply(new BigDecimal(100)).intValue())); // nhân 100
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", txnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
        vnp_Params.put("vnp_IpAddr", "127.0.0.1");
        vnp_Params.put("vnp_CreateDate", LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        // B1: Sắp xếp các tham số
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        // Tạo query và dữ liệu hash
        for (String fieldName : fieldNames) {
            String value = vnp_Params.get(fieldName);
            if (value != null && !value.isEmpty()) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(value, StandardCharsets.US_ASCII)).append('&');
                query.append(fieldName).append('=').append(URLEncoder.encode(value, StandardCharsets.US_ASCII)).append('&');
            }
        }

        // Loại bỏ dấu '&' cuối cùng
        hashData.setLength(hashData.length() - 1);
        query.setLength(query.length() - 1);

        // Tạo chữ ký bảo mật
        String secureHash = hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        query.append("&vnp_SecureHash=").append(secureHash);

        return vnPayConfig.getPayUrl() + "?" + query.toString();
    }

    // Hàm tạo chữ ký HMAC-SHA512
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
