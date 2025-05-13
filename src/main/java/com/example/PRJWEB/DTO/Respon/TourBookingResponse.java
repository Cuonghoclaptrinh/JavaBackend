package com.example.PRJWEB.DTO.Respon;

import lombok.*;
import lombok.experimental.FieldDefaults;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TourBookingResponse {
    Long id; // Mã đơn đặt tour
    String maDatTour; // Mã đơn dạng T<year>-<id>
    Integer tourId; // ID tour
    String tourName; // Tên tour
    Long customerId; // ID khách hàng
    String customer; // Tên khách hàng
    String bookingDate; // Ngày đặt tour
    String departureDate; // Ngày khởi hành
    int quantity; // Tổng số lượng (adult + child)
    BigDecimal total; // Tổng tiền
    BigDecimal paid; // Đã thanh toán
    String method; // Phương thức thanh toán
    String paymentTime; // Thời gian thanh toán
    String status; // Trạng thái đơn
    Long employeeId; // ID nhân viên chăm sóc
    String employee; // Tên nhân viên chăm sóc
}
