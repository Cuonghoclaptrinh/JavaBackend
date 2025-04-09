package com.example.PRJWEB.Service;

import com.example.PRJWEB.DTO.Request.TourRequest;
import com.example.PRJWEB.DTO.Respon.TourResponse;
import com.example.PRJWEB.Entity.Tour;
import com.example.PRJWEB.Exception.AppException;
import com.example.PRJWEB.Exception.ErrorCode;
import com.example.PRJWEB.Mapper.TourMapper;
import com.example.PRJWEB.Repository.TourRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class TourService {
    TourRepository tourRepository;
    TourMapper tourMapper;

    @PreAuthorize("hasAnyAuthority('ADMIN','STAFF')")
    public TourResponse addTour(TourRequest request){
        Tour tour = tourMapper.toEntity(request);
        tourRepository.save(tour);
        return tourMapper.toTourResponse(tour);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','STAFF')")
    public TourResponse updateTour(Integer tourId, TourRequest request){
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new AppException(ErrorCode.TOUR_NOT_EXISTED));
        tour.setTourName(request.getTourName());
        tour.setPrice(request.getPrice());
        tour.setDuration(request.getDuration());
        tour.setDescription(request.getDescription());
        tour.setItinerary(request.getItinerary());
        tour.setTransportation(request.getTransportation());
        tour.setPeopleLimit(request.getPeopleLimit());
        tour.setAccommodation(request.getAccommodation());
        tour.setTourType(request.getTourType());
        tour.setRegion(request.getRegion());
        tour.setDiscount(request.getDiscount());
        tour.setNewPrice(request.getNewPrice());
        tour.setImage(request.getImage());

        tourRepository.save(tour);
        return tourMapper.toTourResponse(tour);
    }


    public List<TourResponse> getTour(){
        return tourRepository.findAll()
                .stream().map(tourMapper::toTourResponse)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public  void deleteTour(int id){tourRepository.deleteById(id);}
}
