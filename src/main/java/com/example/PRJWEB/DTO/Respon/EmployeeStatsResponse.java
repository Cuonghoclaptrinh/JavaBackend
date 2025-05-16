package com.example.PRJWEB.DTO.Respon;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeStatsResponse {
    Long id;
    String tenNhanVien;
    Integer soLuongTour;
    BigDecimal doanhThu;
    BigDecimal hoaHong;
}