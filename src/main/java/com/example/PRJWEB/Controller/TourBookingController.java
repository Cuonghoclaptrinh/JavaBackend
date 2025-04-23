package com.example.PRJWEB.Controller;

import com.example.PRJWEB.DTO.Request.ApiResponse;
import com.example.PRJWEB.DTO.Request.TourBookingRequest;
import com.example.PRJWEB.DTO.Respon.TourBookingResponse;
import com.example.PRJWEB.Service.TourBookingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tour_booking")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class TourBookingController {
    TourBookingService tourBookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN','STAFF','USER')")
    public ApiResponse<TourBookingResponse> bookTour(@RequestBody TourBookingRequest request) {
        TourBookingResponse response = tourBookingService.bookTour(request);
        return new ApiResponse<>(200, "Booking successful", response);
    }

    @GetMapping("/my")
    public List<TourBookingResponse> getMyBookings() {
        return tourBookingService.getBookingsForCurrentUser();
    }


    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public List<TourBookingResponse> getAllBookings() {
        return tourBookingService.getAllBookings();
    }
}
