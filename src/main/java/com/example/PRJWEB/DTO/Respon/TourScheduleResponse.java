package com.example.PRJWEB.DTO.Respon;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TourScheduleResponse {
    private String departureDate;
    private String status;
    private int limitPeople;
}
