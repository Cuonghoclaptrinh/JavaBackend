package com.example.PRJWEB.DTO.Respon;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewResponse {
    Long id;
    String tourId;
    String name;
    Integer rating;
    String comment;
    LocalDateTime createdAt;
}