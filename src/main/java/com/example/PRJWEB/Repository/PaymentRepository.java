package com.example.PRJWEB.Repository;

import com.example.PRJWEB.Entity.Payment;
import com.example.PRJWEB.Entity.Tour_booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment , Long> {
    List<Payment> findByBooking(Tour_booking booking);


}
