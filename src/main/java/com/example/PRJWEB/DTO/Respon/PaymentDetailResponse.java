package com.example.PRJWEB.DTO.Respon;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PaymentDetailResponse {
    private Long bookingId;
    private BigDecimal amount;
    private BigDecimal remainingAmount;
    private LocalDateTime paymentDate;
    private BigDecimal totalBookingAmount;
    private String bookingStatus;
    private String tourName;
    private UserDTO customer;
    private String departureDate;
    private Integer adultQuantity;
    private Integer childQuantity;
    private UserDTO employee;
    private List<PaymentResponse> relatedPayments;
}
