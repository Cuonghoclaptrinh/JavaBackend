package com.example.PRJWEB.DTO.Respon;

import com.example.PRJWEB.Enums.PaymentStatus;
import com.example.PRJWEB.Enums.TourType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter

public class PaymentResponse {
    Long paymentId;
    Long bookingId;
    String method;
    String status;
    LocalDateTime paymentDate;
    BigDecimal amount;
    BigDecimal remainingAmount;
}
