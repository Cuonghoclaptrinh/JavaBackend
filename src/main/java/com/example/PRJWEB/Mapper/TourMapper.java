package com.example.PRJWEB.Mapper;

import com.example.PRJWEB.DTO.Request.TourRequest;
import com.example.PRJWEB.DTO.Respon.TourResponse;
import com.example.PRJWEB.DTO.Respon.TourScheduleResponse;
import com.example.PRJWEB.Entity.Tour;
import com.example.PRJWEB.Entity.TourSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {TourMapperHelper.class})
public interface TourMapper {

    @Mapping(target = "tourSchedules", ignore = true)
    @Mapping(source = "images", target = "images")
    Tour toEntity(TourRequest request);

    TourResponse toTourResponse(Tour tour);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "peopleLimit", target = "peopleLimit")
    @Mapping(source = "departureDate", target = "departureDate", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "status", target = "status", qualifiedByName = "enumToString")
    @Mapping(target = "currentPeople", source = "id", qualifiedByName = "mapCurrentPeople")
    TourScheduleResponse toTourScheduleResponse(TourSchedule tourSchedule);

    @Mapping(target = "tourSchedules", ignore = true)
    @Mapping(source = "images", target = "images")
    void updateTourFromRequest(TourRequest request, @MappingTarget Tour tour);

    @Named("enumToString")
    default String enumToString(Enum<?> enumValue) {
        return enumValue != null ? enumValue.name() : null;
    }
}