package com.example.PRJWEB.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tour_booking")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Tour_booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    Long bookingId;

    @ManyToOne
    @JoinColumn(name = "tour_id", referencedColumnName = "tourId")
    Tour tour;  // Liên kết đến bảng Tour

    @ManyToOne
    @JoinColumn(name = "tour_schedule_id", referencedColumnName = "id")
    TourSchedule tourSchedule;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    User customer;  // Liên kết đến bảng User (customer)

    @ManyToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    User employee;

    @Column(name = "booking_date")
    LocalDateTime bookingDate = LocalDateTime.now();

    @Column(name = "status")
    String status = "Pending";

    @Column(name = "adult_quantity")
    int adultQuantity;

    @Column(name = "child_quantity")
    int childQuantity;

    @Column(name = "total_price")
    BigDecimal totalPrice;
}
