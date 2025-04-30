package com.example.PRJWEB.Mapper;

import com.example.PRJWEB.Repository.TourBookingRepository;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TourMapperHelper {

    @Autowired
    private TourBookingRepository tourBookingRepository;

    @Named("mapCurrentPeople")
    public Integer mapCurrentPeople(Integer tourScheduleId) {
        Integer totalPeople = tourBookingRepository.getTotalPeopleByTourScheduleId(tourScheduleId);
        return totalPeople != null ? totalPeople : 0;
    }
}