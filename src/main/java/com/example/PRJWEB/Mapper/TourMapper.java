package com.example.PRJWEB.Mapper;

import com.example.PRJWEB.DTO.Request.TourRequest;
import com.example.PRJWEB.DTO.Respon.TourResponse;
import com.example.PRJWEB.Entity.Tour;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TourMapper {
    Tour toEntity(TourRequest request);
    TourResponse toTourResponse(Tour tour);

}
