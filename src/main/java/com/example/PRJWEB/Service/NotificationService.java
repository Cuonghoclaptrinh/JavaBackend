package com.example.PRJWEB.Service;

import com.example.PRJWEB.Entity.Notification;
import com.example.PRJWEB.Repository.NotificationRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {
    NotificationRepository notificationRepository;

    public List<Notification> getActiveNotifications() {
        Long userId = getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();
        List<Notification> globalNotifications = notificationRepository.findByIsActiveTrueAndExpiresAtAfterAndUserIdIsNull(now);
        List<Notification> userNotifications = userId != null
                ? notificationRepository.findByIsActiveTrueAndExpiresAtAfterAndUserId(now, userId)
                : List.of();

        return Stream.concat(globalNotifications.stream(), userNotifications.stream()).toList();
    }

    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public boolean existsPromotionNotification() {
        return !notificationRepository.findByIsActiveTrueAndTypeAndUserIdIsNull("PROMOTION").isEmpty();
    }

    public boolean existsNotification(String type, Long bookingId, Long userId) {
        return !notificationRepository.findByIsActiveTrueAndTypeAndBookingIdAndUserId(type, bookingId, userId).isEmpty();
    }

    private Long getCurrentUserId() {
        try {
            Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return jwt.getClaim("user_id");
        } catch (Exception e) {
            return null;
        }
    }
}