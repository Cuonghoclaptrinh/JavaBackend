package com.example.PRJWEB.Mapper;

import com.example.PRJWEB.DTO.Request.TourBookingRequest;
import com.example.PRJWEB.DTO.Respon.TourBookingResponse;
import com.example.PRJWEB.Entity.Tour_booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TourBookingMapper {
//    @Mapping(source = "tour.tourId", target = "tourId")
//    @Mapping(source = "customer.id", target = "customerId")
@Mapping(source = "bookingId", target = "id")
@Mapping(source = "totalPrice", target = "total")
@Mapping(source = "customer.fullname", target = "customer") // Ánh xạ User.fullname sang String customer
@Mapping(source = "employee.fullname", target = "employee") // Ánh xạ User.fullname sang String employee
@Mapping(target = "maDatTour", expression = "java(String.format(\"T%d-%03d\", java.time.LocalDateTime.now().getYear(), tourBooking.getBookingId()))") // Tạo mã đơn
@Mapping(target = "quantity", expression = "java(tourBooking.getAdultQuantity() + tourBooking.getChildQuantity())") // Tổng số lượng
@Mapping(target = "bookingDate", expression = "java(tourBooking.getBookingDate().format(java.time.format.DateTimeFormatter.ofPattern(\"yyyy-MM-dd\")))") // Định dạng ngày
@Mapping(target = "tourId", source = "tour.tourId")
@Mapping(target = "tourName", source = "tour.tourName")
@Mapping(target = "customerId", source = "customer.id")
@Mapping(target = "employeeId", source = "employee.id")
@Mapping(target = "departureDate", ignore = true) // Tạm bỏ qua, cần logic từ tour_schedule
@Mapping(target = "paid", ignore = true) // Tạm bỏ qua, cần logic từ payment
@Mapping(target = "method", ignore = true) // Tạm bỏ qua
@Mapping(target = "paymentTime", ignore = true) // Tạm bỏ qua
    TourBookingResponse toTourBookingResponse(Tour_booking tourBooking);
}
