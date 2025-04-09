package com.example.PRJWEB.Mapper;

import com.example.PRJWEB.DTO.Request.TourBookingRequest;
import com.example.PRJWEB.DTO.Respon.TourBookingResponse;
import com.example.PRJWEB.Entity.Tour_booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TourBookingMapper {
    @Mapping(source = "tour.tourId", target = "tourId")
    @Mapping(source = "customer.id", target = "customerId")
    TourBookingResponse toTourBookingResponse(Tour_booking tourBooking);
}
