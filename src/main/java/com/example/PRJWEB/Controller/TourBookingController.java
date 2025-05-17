package com.example.PRJWEB.Controller;

import com.example.PRJWEB.DTO.Request.ApiResponse;
import com.example.PRJWEB.DTO.Request.AssignEmployeeRequest;
import com.example.PRJWEB.DTO.Request.TourBookingRequest;
import com.example.PRJWEB.DTO.Respon.EmployeeStatsResponse;
import com.example.PRJWEB.DTO.Respon.TourBookingResponse;
import com.example.PRJWEB.DTO.Respon.UserResponse;
import com.example.PRJWEB.Entity.Tour_booking;
import com.example.PRJWEB.Service.TourBookingService;
import com.example.PRJWEB.Service.UserService;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TourBookingController {
    TourBookingService tourBookingService;
    UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN','STAFF','USER')")
    public ApiResponse<TourBookingResponse> bookTour(@RequestBody TourBookingRequest request) {
        TourBookingResponse response = tourBookingService.bookTour(request);
        return ApiResponse.<TourBookingResponse>builder()
                .code(201)
                .message("Booking created successfully. Please proceed to payment (deposit or full).")
                .result(response)
                .build();
    }

    @GetMapping("/my")
    public ApiResponse<List<TourBookingResponse>> getMyBookings() {
        List<TourBookingResponse> bookings = tourBookingService.getBookingsForCurrentUser();
        return ApiResponse.<List<TourBookingResponse>>builder()
                .code(200)
                .message("Retrieved user bookings successfully")
                .result(bookings)
                .build();
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ApiResponse<List<TourBookingResponse>> getAllBookings() {
        List<TourBookingResponse> bookings = tourBookingService.getAllBookings();
        return ApiResponse.<List<TourBookingResponse>>builder()
                .code(200)
                .message("Retrieved all bookings successfully")
                .result(bookings)
                .build();
    }

    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<TourBookingResponse> assignEmployee(@PathVariable Long id, @RequestBody AssignEmployeeRequest request) {
        TourBookingResponse response = tourBookingService.assignEmployee(id, request.getEmployeeId());
        return ApiResponse.<TourBookingResponse>builder()
                .message("Employee assigned successfully")
                .result(response)
                .build();
    }

    @PatchMapping("/tour/{tourId}/assign")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<List<TourBookingResponse>> assignEmployeeToTour(@PathVariable Integer tourId, @RequestBody AssignEmployeeRequest request) { // Đổi Long thành Integer
        List<TourBookingResponse> response = tourBookingService.assignEmployeeToTour(tourId, request.getEmployeeId());
        return ApiResponse.<List<TourBookingResponse>>builder()
                .message("Employee assigned to all bookings of tour successfully")
                .result(response)
                .build();
    }

    @GetMapping("/employees")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<List<UserResponse>> getEmployees() {
        List<UserResponse> employees = userService.getEmployees();
        return ApiResponse.<List<UserResponse>>builder()
                .message("Employees retrieved successfully")
                .result(employees)
                .build();
    }

    @GetMapping("/employee-stats")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<List<EmployeeStatsResponse>> getEmployeeStats() {
        List<EmployeeStatsResponse> stats = tourBookingService.getEmployeeStats();
        return ApiResponse.<List<EmployeeStatsResponse>>builder()
                .code(200)
                .message("Retrieved employee stats successfully")
                .result(stats)
                .build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public ApiResponse<TourBookingResponse> getBookingById(@PathVariable Long id) {
        Tour_booking booking = tourBookingService.getBookingByOrderId(id);
        TourBookingResponse response = tourBookingService.mapToTourBookingResponse(booking);
        return ApiResponse.<TourBookingResponse>builder()
                .code(200)
                .message("Retrieved booking successfully")
                .result(response)
                .build();
    }
}