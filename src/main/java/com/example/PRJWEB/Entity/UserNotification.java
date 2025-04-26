package com.example.PRJWEB.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "user_notification")
@IdClass(UserNotificationId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserNotification {
    @Id
    @Column(name = "user_id")
    Long userId;

    @Id
    @Column(name = "notification_id")
    Long notificationId;

    @Column(name = "is_read")
    Boolean isRead;
}