package com.example.PRJWEB.Repository;

import com.example.PRJWEB.Entity.Tour_booking;
import com.example.PRJWEB.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TourBookingRepository extends JpaRepository<Tour_booking , Long> {
    List<Tour_booking> findByCustomer(User customer);
    List<Tour_booking> findByCustomerId(Long customerId);

    List<Tour_booking> findByStatus(String status);
    List<Tour_booking> findByStatusIn(List<String> statuses);
    List<Tour_booking> findByTour_TourId(Integer tourId);
}
