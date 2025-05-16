package com.example.PRJWEB.Service;

import com.example.PRJWEB.DTO.Request.TourBookingRequest;
import com.example.PRJWEB.DTO.Respon.EmployeeStatsResponse;
import com.example.PRJWEB.DTO.Respon.TourBookingResponse;
import com.example.PRJWEB.Entity.Notification;
import com.example.PRJWEB.Entity.Payment;
import com.example.PRJWEB.Entity.Tour_booking;
import com.example.PRJWEB.Entity.User;
import com.example.PRJWEB.Enums.Roles;
import com.example.PRJWEB.Exception.AppException;
import com.example.PRJWEB.Exception.ErrorCode;
import com.example.PRJWEB.Mapper.TourBookingMapper;
import com.example.PRJWEB.Repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TourBookingService {
    TourBookingRepository tourBookingRepository;
    TourRepository tourRepository;
    TourScheduleRepository tourScheduleRepository;
    UserRepository userRepository;
    PaymentRepository paymentRepository;
    TourBookingMapper tourBookingMapper;
    NotificationService notificationService;
    NotificationWebSocketHandler notificationWebSocketHandler;

    public TourBookingResponse bookTour(TourBookingRequest request) {
        var tour = tourRepository.findById(request.getTourId())
                .orElseThrow(() -> new AppException(ErrorCode.TOUR_NOT_EXISTED));

        var tourSchedule = tourScheduleRepository.findById(request.getTourScheduleId())
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_EXISTED));

        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = jwt.getClaim("user_id");
        var customer = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Kiểm tra peopleLimit
        Integer currentPeople = tourBookingRepository.getTotalPeopleByTourScheduleId(request.getTourScheduleId());
        currentPeople = currentPeople != null ? currentPeople : 0;
        int newTotalPeople = currentPeople + request.getAdultQuantity() + request.getChildQuantity();
        if (newTotalPeople > tourSchedule.getPeopleLimit()) {
            throw new AppException(ErrorCode.BOOKING_LIMIT_EXCEEDED);
        }

        BigDecimal totalPrice = calculateTotalPrice(tour.getNewPrice(), request.getAdultQuantity(), request.getChildQuantity());

        Tour_booking booking = new Tour_booking();
        booking.setTour(tour);
        booking.setTourSchedule(tourSchedule);
        booking.setCustomer(customer);
        booking.setAdultQuantity(request.getAdultQuantity());
        booking.setChildQuantity(request.getChildQuantity());
        booking.setTotalPrice(totalPrice);
        booking.setStatus("PENDING");

        Tour_booking savedBooking = tourBookingRepository.save(booking);

        if (!notificationService.existsNotification("BOOKING_SUCCESS", savedBooking.getBookingId(), savedBooking.getCustomer().getId())) {
            Notification notification = new Notification();
            notification.setTitle("Đặt tour thành công!");
            notification.setMessage(String.format("Bạn đã đặt tour %s (#%s) thành công. Vui lòng thanh toán cọc hoặc toàn bộ để xác nhận!",
                    savedBooking.getTour().getTourName(), savedBooking.getBookingId()));
            notification.setType("BOOKING_SUCCESS");
            notification.setIsActive(true);
            notification.setUserId(savedBooking.getCustomer().getId());
            notification.setBookingId(savedBooking.getBookingId());
            notification.setCreatedAt(LocalDateTime.now());
            notification.setExpiresAt(LocalDateTime.now().plusDays(7));
            notificationService.saveNotification(notification);

            try {
                String wsMessage = String.format("{\"title\":\"%s\",\"message\":\"%s\",\"type\":\"%s\"}",
                        notification.getTitle(), notification.getMessage(), notification.getType());
                notificationWebSocketHandler.sendNotificationToUser(
                        String.valueOf(savedBooking.getCustomer().getId()), wsMessage);
            } catch (Exception e) {
                System.err.println("Error sending WebSocket message: " + e.getMessage());
            }
        }

        return mapToTourBookingResponse(savedBooking);
    }

    public List<TourBookingResponse> getBookingsForCurrentUser() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = jwt.getClaim("user_id");

        var customer = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<Tour_booking> bookings = tourBookingRepository.findByCustomer(customer);
        return bookings.stream()
                .map(this::mapToTourBookingResponse)
                .collect(Collectors.toList());
    }

    private TourBookingResponse mapToTourBookingResponse(Tour_booking booking) {
        TourBookingResponse response = tourBookingMapper.toTourBookingResponse(booking);

        List<Payment> payments = paymentRepository.findByBooking(booking);
        BigDecimal paid = payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        String method = payments.isEmpty() ? "-" : payments.get(0).getMethod();
        String paymentTime = payments.isEmpty() ? "-" : payments.get(0).getPaymentDate()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        String departureDate = booking.getTourSchedule() != null ? booking.getTourSchedule().getDepartureDate().toString() : "-";

        response.setPaid(paid);
        response.setMethod(method);
        response.setPaymentTime(paymentTime);
        response.setDepartureDate(departureDate);

        return response;
    }

    public List<TourBookingResponse> getAllBookings() {
        var bookings = tourBookingRepository.findAll();
        return bookings.stream()
                .map(this::mapToTourBookingResponse)
                .collect(Collectors.toList());
    }

    public void updateBookingStatus(Long bookingId, String status) {
        Tour_booking booking = tourBookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));
        booking.setStatus(status);
        tourBookingRepository.save(booking);
    }

    public Tour_booking getBookingById(Long id) {
        return tourBookingRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));
    }

    public TourBookingResponse assignEmployee(Long bookingId, Integer employeeId) {
        Tour_booking booking = tourBookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if (!employee.getRoles().contains(Roles.STAFF.name())) {
            throw new AppException(ErrorCode.INVALID_ROLE);
        }
        booking.setEmployee(employee);
        tourBookingRepository.save(booking);

        if (!notificationService.existsNotification("ASSIGNMENT", booking.getBookingId(), employee.getId())) {
            Notification notification = new Notification();
            notification.setTitle("Được phân công đơn hàng!");
            notification.setMessage(String.format("Bạn được phân công chăm sóc đơn hàng #%s (Tour: %s) của khách hàng %s.",
                    booking.getBookingId(), booking.getTour().getTourName(), booking.getCustomer().getFullname()));
            notification.setType("ASSIGNMENT");
            notification.setIsActive(true);
            notification.setUserId(employee.getId());
            notification.setBookingId(booking.getBookingId());
            notification.setCreatedAt(LocalDateTime.now());
            notification.setExpiresAt(LocalDateTime.now().plusDays(7));
            notificationService.saveNotification(notification);

            try {
                String wsMessage = String.format("{\"title\":\"%s\",\"message\":\"%s\",\"type\":\"%s\"}",
                        notification.getTitle(), notification.getMessage(), notification.getType());
                notificationWebSocketHandler.sendNotificationToUser(
                        String.valueOf(employee.getId()), wsMessage);
            } catch (Exception e) {
                System.err.println("Error sending WebSocket message: " + e.getMessage());
            }
        }

        return mapToTourBookingResponse(booking);
    }

    public List<TourBookingResponse> assignEmployeeToTour(Integer tourId, Integer employeeId) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if (!employee.getRoles().contains(Roles.STAFF.name())) {
            throw new AppException(ErrorCode.INVALID_ROLE);
        }
        List<Tour_booking> bookings = tourBookingRepository.findByTour_TourId(tourId);
        bookings.forEach(booking -> {
            booking.setEmployee(employee);
            if (!notificationService.existsNotification("ASSIGNMENT", booking.getBookingId(), employee.getId())) {
                Notification notification = new Notification();
                notification.setTitle("Được phân công đơn hàng!");
                notification.setMessage(String.format("Bạn được phân công chăm sóc đơn hàng #%s (Tour: %s) của khách hàng %s.",
                        booking.getBookingId(), booking.getTour().getTourName(), booking.getCustomer().getFullname()));
                notification.setType("ASSIGNMENT");
                notification.setIsActive(true);
                notification.setUserId(employee.getId());
                notification.setBookingId(booking.getBookingId());
                notification.setCreatedAt(LocalDateTime.now());
                notification.setExpiresAt(LocalDateTime.now().plusDays(7));
                notificationService.saveNotification(notification);

                try {
                    String wsMessage = String.format("{\"title\":\"%s\",\"message\":\"%s\",\"type\":\"%s\"}",
                            notification.getTitle(), notification.getMessage(), notification.getType());
                    notificationWebSocketHandler.sendNotificationToUser(
                            String.valueOf(employee.getId()), wsMessage);
                } catch (Exception e) {
                    System.err.println("Error sending WebSocket message: " + e.getMessage());
                }
            }
        });
        tourBookingRepository.saveAll(bookings);
        return bookings.stream()
                .map(this::mapToTourBookingResponse)
                .collect(Collectors.toList());
    }

    public BigDecimal calculateTotalPrice(BigDecimal price, Integer adultQuantity, Integer childQuantity) {
        if (adultQuantity < 0 || childQuantity < 0) {
            throw new IllegalArgumentException("Số lượng không hợp lệ");
        }
        BigDecimal adultTotal = price.multiply(BigDecimal.valueOf(adultQuantity));
        BigDecimal childTotal = price.multiply(BigDecimal.valueOf(childQuantity)).multiply(BigDecimal.valueOf(0.5));
        return adultTotal.add(childTotal);
    }

    public List<Tour_booking> findByStatus(String status) {
        return tourBookingRepository.findByStatus(status);
    }

    public List<Tour_booking> findByStatusIn(List<String> statuses) {
        return tourBookingRepository.findByStatusIn(statuses);
    }

    public List<EmployeeStatsResponse> getEmployeeStats() {
        List<User> employees = userRepository.findByRolesContaining(Roles.STAFF.name());
        return employees.stream().map(employee -> {
            List<Tour_booking> bookings = tourBookingRepository.findByEmployee(employee);
            int soLuongTour = bookings.size(); // Đếm số booking
            BigDecimal doanhThu = bookings.stream()
                    .filter(b -> b.getStatus().equals("PAID"))
                    .map(Tour_booking::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal hoaHong = calculateCommission(doanhThu);
            return EmployeeStatsResponse.builder()
                    .id(employee.getId())
                    .tenNhanVien(employee.getFullname())
                    .soLuongTour(soLuongTour)
                    .doanhThu(doanhThu)
                    .hoaHong(hoaHong)
                    .build();
        }).collect(Collectors.toList());
    }

    private BigDecimal calculateCommission(BigDecimal doanhThu) {
        // Hoa hồng 5% doanh thu
        return doanhThu.multiply(BigDecimal.valueOf(0.05));
    }
}