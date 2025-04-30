package com.example.PRJWEB.Service;

import com.example.PRJWEB.DTO.Request.TourRequest;
import com.example.PRJWEB.DTO.Request.TourScheduleRequest;
import com.example.PRJWEB.DTO.Respon.TourResponse;
import com.example.PRJWEB.Entity.Tour;
import com.example.PRJWEB.Entity.TourSchedule;
import com.example.PRJWEB.Enums.TourStatus;
import com.example.PRJWEB.Enums.TourType;
import com.example.PRJWEB.Exception.AppException;
import com.example.PRJWEB.Exception.ErrorCode;
import com.example.PRJWEB.Mapper.TourMapper;
import com.example.PRJWEB.Repository.TourRepository;
import com.example.PRJWEB.Repository.TourScheduleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TourService {
    TourRepository tourRepository;
    TourScheduleRepository tourScheduleRepository;
    TourMapper tourMapper;

    @PreAuthorize("hasAnyAuthority('ADMIN','STAFF')")
    public TourResponse addTour(TourRequest request) {
        Tour tour = tourMapper.toEntity(request);
        tour.setTourSchedules(new ArrayList<>());

        TourSchedule tourSchedule = TourSchedule.builder()
                .tour(tour)
                .departureDate(request.getDepartureDate())
                .peopleLimit(request.getPeopleLimit())
                .status(TourStatus.ACTIVE)
                .build();

        tour.getTourSchedules().add(tourSchedule);
        tourRepository.save(tour);
        tourScheduleRepository.save(tourSchedule);

        return tourMapper.toTourResponse(tour);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','STAFF')")
    public TourResponse updateTour(Integer tourId, TourRequest request) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new AppException(ErrorCode.TOUR_NOT_EXISTED));
        tourMapper.updateTourFromRequest(request, tour);
        tourRepository.save(tour);
        return tourMapper.toTourResponse(tour);
    }

    public List<TourResponse> getTour() {
        return tourRepository.findAll().stream()
                .map(tourMapper::toTourResponse)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public TourResponse addScheduleToTour(Integer tourId, TourScheduleRequest scheduleRequest) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new AppException(ErrorCode.TOUR_NOT_EXISTED));

        TourSchedule schedule = TourSchedule.builder()
                .tour(tour)
                .departureDate(scheduleRequest.getDepartureDate())
                .peopleLimit(scheduleRequest.getPeopleLimit())
                .status(TourStatus.ACTIVE)
                .build();

        tour.getTourSchedules().add(schedule);
        tourScheduleRepository.save(schedule);
        tourRepository.save(tour);

        return tourMapper.toTourResponse(tour);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void deleteTour(int id) {
        tourRepository.deleteById(id);
    }

    public List<TourResponse> filterTour(String keyword, String region, String tourTypeStr,
                                         BigDecimal minPrice, BigDecimal maxPrice) {
        log.info("keyword: {}, region: {}, tourTypeStr: {}, minPrice: {}, maxPrice: {}",
                keyword, region, tourTypeStr, minPrice, maxPrice);
        TourType tourType = null;
        if (tourTypeStr != null && !tourTypeStr.isEmpty()) {
            try {
                tourType = TourType.valueOf(tourTypeStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid tourType: {}", tourTypeStr);
            }
        }
        List<Tour> filtered = tourRepository.filterTours(keyword, region, tourType, minPrice, maxPrice);
        return filtered.stream().map(tourMapper::toTourResponse).collect(Collectors.toList());
    }
}