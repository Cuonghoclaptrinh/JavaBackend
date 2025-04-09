package com.example.PRJWEB.Service;

import com.example.PRJWEB.DTO.Request.TourBookingRequest;
import com.example.PRJWEB.DTO.Respon.TourBookingResponse;
import com.example.PRJWEB.Entity.Tour_booking;
import com.example.PRJWEB.Exception.AppException;
import com.example.PRJWEB.Exception.ErrorCode;
import com.example.PRJWEB.Mapper.TourBookingMapper;
import com.example.PRJWEB.Repository.TourBookingRepository;
import com.example.PRJWEB.Repository.TourRepository;
import com.example.PRJWEB.Repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class TourBookingService {
    TourBookingRepository tourBookingRepository;
    TourRepository tourRepository;
    TourBookingMapper tourBookingMapper;
    UserRepository userRepository;


    public TourBookingResponse bookTour(TourBookingRequest request ) {
        // Lấy thông tin Tour và Customer từ repository
        var tour = tourRepository.findById(request.getTourId())
                .orElseThrow(() -> new AppException(ErrorCode.TOUR_NOT_EXISTED));
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = jwt.getClaim("user_id");
        var customer = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Tính toán tổng giá trị của booking
        BigDecimal totalPrice = calculateTotalPrice(tour.getPrice(), request.getAdultQuantity(), request.getChildQuantity());

        // Tạo đối tượng TourBooking mới
        Tour_booking booking = new Tour_booking();
        booking.setTour(tour);
        booking.setCustomer(customer);
        booking.setAdultQuantity(request.getAdultQuantity());
        booking.setChildQuantity(request.getChildQuantity());
        booking.setTotalPrice(totalPrice);

        // Lưu booking vào cơ sở dữ liệu
        tourBookingRepository.save(booking);

        // Trả về DTO response
        return tourBookingMapper.toTourBookingResponse(booking);
    }

    private BigDecimal calculateTotalPrice(BigDecimal price, Integer adultQuantity, Integer childQuantity) {
        // Giả sử giá trị trẻ em là 50% so với người lớn
        BigDecimal adultTotal = price.multiply(BigDecimal.valueOf(adultQuantity));
        BigDecimal childTotal = price.multiply(BigDecimal.valueOf(childQuantity)).multiply(BigDecimal.valueOf(0.5));

        return adultTotal.add(childTotal);
    }
}
