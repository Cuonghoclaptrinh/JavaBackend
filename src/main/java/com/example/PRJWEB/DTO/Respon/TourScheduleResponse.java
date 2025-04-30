package com.example.PRJWEB.DTO.Respon;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TourScheduleResponse {
    private Integer id;
    private String departureDate;
    private String status;
    private Integer peopleLimit;
    private Integer currentPeople;
}
