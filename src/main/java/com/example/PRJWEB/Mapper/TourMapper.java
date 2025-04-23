package com.example.PRJWEB.Mapper;

import com.example.PRJWEB.DTO.Request.TourRequest;
import com.example.PRJWEB.DTO.Respon.TourResponse;
import com.example.PRJWEB.DTO.Respon.TourScheduleResponse;
import com.example.PRJWEB.Entity.Tour;
import com.example.PRJWEB.Entity.TourSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TourMapper {
    @Mapping(source = "price", target = "price")
    Tour toEntity(TourRequest request);
    TourResponse toTourResponse(Tour tour);
    TourScheduleResponse toTourScheduleResponse(TourSchedule tourSchedule);
    void updateTourFromRequest(TourRequest request, @MappingTarget Tour tour);
}
