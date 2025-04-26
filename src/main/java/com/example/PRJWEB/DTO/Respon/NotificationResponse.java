package com.example.PRJWEB.DTO.Respon;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class NotificationResponse {
    Long id;
    String title;
    String message;
    String type;
    Boolean isActive;
    Long userId;
    Long bookingId;
    LocalDateTime createdAt;
    LocalDateTime expiresAt;
    Boolean isRead;
}