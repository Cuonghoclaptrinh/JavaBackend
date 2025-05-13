package com.example.PRJWEB.Service;


import com.example.PRJWEB.DTO.EmployeePerformanceDTO;
import com.example.PRJWEB.DTO.Respon.UserResponse;
import com.example.PRJWEB.DTO.TourBookingDTO;
import com.example.PRJWEB.DTO.TourDTO;
import com.example.PRJWEB.Entity.Tour_booking;
import com.example.PRJWEB.Entity.Tour;
import com.example.PRJWEB.Enums.TourStatus;
import com.example.PRJWEB.Exception.AppException;
import com.example.PRJWEB.Exception.ErrorCode;
import com.example.PRJWEB.Repository.TourBookingRepository;
import com.example.PRJWEB.Repository.TourRepository;
import com.example.PRJWEB.Repository.PaymentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeService {

    TourBookingRepository tourBookingRepository;
    PaymentRepository paymentRepository;
    TourRepository tourRepository;
    UserService userService;

    public List<TourBookingDTO> getEmployeeOrders() {
        Long employeeId = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getClaim("user_id");
        List<Tour_booking> bookings = tourBookingRepository.findByEmployeeId(employeeId);
        return bookings.stream()
                .map(booking -> new TourBookingDTO(
                        booking.getBookingId(),
                        booking.getCustomer() != null ? booking.getCustomer().getFullname() : "Khách không xác định",
                        booking.getTour() != null ? booking.getTour().getTourName() : "Tour không xác định",
                        booking.getTotalPrice() != null ? booking.getTotalPrice().longValue() : 0L,
                        booking.getStatus() != null ? booking.getStatus() : "UNKNOWN",
                        booking.getBookingDate()
                ))
                .collect(Collectors.toList());
    }

    public void updateOrderStatus(Long orderId, String status) {
        Long employeeId = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getClaim("user_id");
        Tour_booking booking = tourBookingRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));
        if (booking.getEmployee() == null || !booking.getEmployee().getId().equals(employeeId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        booking.setStatus(status);
        tourBookingRepository.save(booking);
    }

    public EmployeePerformanceDTO getEmployeePerformance() {
        Long employeeId = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getClaim("user_id");
        List<Tour_booking> bookings = tourBookingRepository.findByEmployeeId(employeeId);
        long revenue = bookings.stream()
                .flatMap(b -> paymentRepository.findByBooking(b).stream())
                .filter(p -> p.getStatus().toString().equals("PAID"))
                .map(p -> p.getAmount().longValue())
                .reduce(0L, Long::sum);
        return new EmployeePerformanceDTO(bookings.size(), revenue, revenue * 0.05); // Hoa hồng 5%
    }

    public List<TourDTO> getEmployeeTours() {
        Long employeeId = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getClaim("user_id");
        List<Tour_booking> bookings = tourBookingRepository.findByEmployeeId(employeeId);
        List<Integer> tourIds = bookings.stream()
                .filter(b -> b.getTour() != null)
                .map(b -> b.getTour().getTourId())
                .distinct()
                .collect(Collectors.toList());
        List<Tour> tours = tourRepository.findAllById(tourIds);
        LocalDate currentDate = LocalDate.now(); // Ngày hiện tại (04/05/2025)
        return tours.stream()
                .filter(tour -> tour.getTourSchedules() != null && tour.getTourSchedules().stream()
                        .anyMatch(schedule -> schedule.getStatus() == TourStatus.ACTIVE &&
                                schedule.getDepartureDate() != null &&
                                !schedule.getDepartureDate().isBefore(currentDate)))
                .map(tour -> {
                    var activeSchedule = tour.getTourSchedules().stream()
                            .filter(schedule -> schedule.getStatus() == TourStatus.ACTIVE &&
                                    schedule.getDepartureDate() != null &&
                                    !schedule.getDepartureDate().isBefore(currentDate))
                            .findFirst()
                            .orElse(null);
                    return new TourDTO(
                            tour.getTourId().longValue(),
                            tour.getTourName(),
                            activeSchedule != null ? activeSchedule.getDepartureDate() : null,
                            activeSchedule != null ? activeSchedule.getStatus().toString() : "UNKNOWN",
                            bookings.stream()
                                    .filter(b -> b.getTour() != null && b.getTour().getTourId().equals(tour.getTourId()))
                                    .count()
                    );
                })
                .collect(Collectors.toList());
    }
    public UserResponse getEmployeeProfile() {
        return userService.getMyInfo();
    }
}