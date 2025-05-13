package com.example.PRJWEB.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TourBookingDTO {
    private Long orderId;
    private String customerName;
    private String tourName;
    private Long amount;
    private String status;
    private LocalDateTime orderDate;

    public TourBookingDTO(Long orderId, String customerName, String tourName, Long amount, String status, LocalDateTime orderDate) {
        this.orderId = orderId;
        this.customerName = customerName != null ? customerName : "Khách không xác định";
        this.tourName = tourName != null ? tourName : "Tour không xác định";
        this.amount = amount != null ? amount : 0L;
        this.status = status != null ? status : "UNKNOWN";
        this.orderDate = orderDate;
    }
}