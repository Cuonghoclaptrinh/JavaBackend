package com.example.PRJWEB.DTO;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TourDTO {
    private Long tourId;
    private String tourName;
    private LocalDate departureDate;
    private String status;
    private long customerCount;

    public TourDTO(Long tourId, String tourName, LocalDate departureDate, String status, long customerCount) {
        this.tourId = tourId;
        this.tourName = tourName != null ? tourName : "Tour không xác định";
        this.departureDate = departureDate;
        this.status = status != null ? status : "UNKNOWN";
        this.customerCount = customerCount;
    }
}