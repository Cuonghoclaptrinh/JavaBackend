package com.example.PRJWEB.Entity;

import com.example.PRJWEB.Enums.TourStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TourSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "tour_id", nullable = false)
    Tour tour;

    @Column(name = "departure_date", nullable = false)
    LocalDate departureDate;

    @Column(name = "people_limit", nullable = false)
        Integer peopleLimit;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    TourStatus status;

}
