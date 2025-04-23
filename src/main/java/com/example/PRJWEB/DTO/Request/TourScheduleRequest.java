package com.example.PRJWEB.DTO.Request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TourScheduleRequest {
    private LocalDate departureDate;
    private Integer peopleLimit;

}
