package com.example.PRJWEB.Mapper;

import com.example.PRJWEB.DTO.Respon.PaymentResponse;
import com.example.PRJWEB.DTO.Respon.TourBookingResponse;
import com.example.PRJWEB.Entity.Payment;
import com.example.PRJWEB.Entity.Tour_booking;
import com.example.PRJWEB.Enums.PaymentStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    @Mapping(source = "booking.bookingId", target = "bookingId")
    @Mapping(source = "paymentDate", target = "paymentDate")
    @Mapping(source = "method", target = "method")
    @Mapping(source = "status", target = "status", qualifiedByName = "statusToString")
    @Mapping(target = "remainingAmount", expression = "java(payment.getRemainingAmount())")
    PaymentResponse toPaymentResponse(Payment payment);

    @Named("statusToString")
    default String statusToString(PaymentStatus status) {
        return status != null ? status.name() : null;
    }
}
