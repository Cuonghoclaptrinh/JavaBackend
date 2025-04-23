package com.example.PRJWEB.Entity;

import com.example.PRJWEB.Enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    Tour_booking booking;

    @Column(name = "amount")
    BigDecimal amount;

    BigDecimal remainingAmount;

    LocalDateTime paymentDate;

    String method;

    @Enumerated(EnumType.STRING)
    PaymentStatus status;
}
