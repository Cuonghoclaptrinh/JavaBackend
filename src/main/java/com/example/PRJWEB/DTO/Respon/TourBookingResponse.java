package com.example.PRJWEB.DTO.Respon;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TourBookingResponse {
    Long bookingId;
    String status;
    Integer adultQuantity;
    Integer childQuantity;
    BigDecimal totalPrice;
    LocalDateTime bookingDate;
    Long  tourId;
    Long  customerId;
}
