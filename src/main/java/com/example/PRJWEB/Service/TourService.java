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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
@Slf4j
public class TourService {
    TourRepository tourRepository;
    TourScheduleRepository tourScheduleRepository;
    TourMapper tourMapper;

    @PreAuthorize("hasAnyAuthority('ADMIN','STAFF')")
    public TourResponse addTour(TourRequest request) {
        Tour tour = tourMapper.toEntity(request);

        TourSchedule tourSchedule=TourSchedule.builder()
                .tour(tour)
                .departureDate(request.getDepartureDate())
                .peopleLimit(request.getPeopleLimit())
                .status(TourStatus.ACTIVE)
                .build();

        tourRepository.save(tour);

        tourScheduleRepository.save(tourSchedule);


        TourResponse response = tourMapper.toTourResponse(tour);
        response.setTourSchedule(tourMapper.toTourScheduleResponse(tourSchedule));

        return response;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','STAFF')")
    public TourResponse updateTour(Integer tourId, TourRequest request)  {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new AppException(ErrorCode.TOUR_NOT_EXISTED));
        tourMapper.updateTourFromRequest(request, tour);
        tourRepository.save(tour);
        return tourMapper.toTourResponse(tour);
    }


    public List<TourResponse> getTour() {
        return tourRepository.findAll().stream()
                .map(tour -> {
                    TourResponse response = tourMapper.toTourResponse(tour);

                    // Lấy schedule đang hoạt động cho tour này
                    TourSchedule activeSchedule = tour.getTourSchedules().stream()
                            .filter(schedule -> schedule.getStatus() == TourStatus.ACTIVE)
                            .findFirst()
                            .orElse(null);

                    if (activeSchedule != null) {
                        response.setTourSchedule(tourMapper.toTourScheduleResponse(activeSchedule));
                    }

                    return response;
                })
                .collect(Collectors.toList());
    }


    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void addScheduleToTour(Integer tourId, TourScheduleRequest scheduleRequest) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tour"));

        TourSchedule schedule = new TourSchedule();
        schedule.setTour(tour);
        schedule.setDepartureDate(scheduleRequest.getDepartureDate());
        schedule.setPeopleLimit(scheduleRequest.getPeopleLimit());
        schedule.setStatus(TourStatus.ACTIVE); // mặc định chưa khởi hành

        tourScheduleRepository.save(schedule);
    }


    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public  void deleteTour(int id){tourRepository.deleteById(id);}


    public List<TourResponse> filterTour(String keyword, String region, String tourTypeStr,
                                         BigDecimal minPrice, BigDecimal maxPrice) {
        log.info("keyword: {}, region: {}, tourTypeStr: {}, minPrice: {}, maxPrice: {}",
                keyword, region, tourTypeStr, minPrice, maxPrice);
        TourType tourType = null;
        if (tourTypeStr != null && !tourTypeStr.isEmpty()) {
            try {
                tourType = TourType.valueOf(tourTypeStr.toUpperCase()); // Chuyển String -> Enum
            } catch (IllegalArgumentException e) {
                 // Kiểm tra nếu giá trị không hợp lệ
            }
        }
        List<Tour> filtered = tourRepository.filterTours(keyword, region, tourType, minPrice, maxPrice);
        return filtered.stream().map(tourMapper::toTourResponse).collect(Collectors.toList());
    }
}
