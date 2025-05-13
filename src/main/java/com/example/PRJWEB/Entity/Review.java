package com.example.PRJWEB.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "tour_id", nullable = false)
    String tourId;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "rating", nullable = false)
    Integer rating;

    @Column(name = "comment", nullable = false)
    String comment;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;
}

