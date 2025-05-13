package com.example.PRJWEB.Service;

import com.example.PRJWEB.DTO.Respon.NotificationResponse;
import com.example.PRJWEB.Entity.Notification;
import com.example.PRJWEB.Entity.UserNotification;
import com.example.PRJWEB.Exception.AppException;
import com.example.PRJWEB.Exception.ErrorCode;
import com.example.PRJWEB.Repository.NotificationRepository;
import com.example.PRJWEB.Repository.UserRepository;
import com.example.PRJWEB.Repository.UserNotificationRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {
    NotificationRepository notificationRepository;
    UserNotificationRepository userNotificationRepository;
    UserRepository userRepository;

    public List<NotificationResponse> getAllNotifications() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Lấy tất cả thông báo liên quan đến userId hoặc thông báo toàn cục
        List<Notification> notifications = notificationRepository.findByUserIdOrUserIdIsNull(
                userId, Sort.by(Sort.Direction.DESC, "createdAt"));

        // Lấy trạng thái isRead từ user_notification
        List<UserNotification> userNotificationsStatus = userNotificationRepository.findByUserId(userId);

        return notifications.stream().map(notification -> {
            boolean isRead = userNotificationsStatus.stream()
                    .filter(un -> un.getNotificationId().equals(notification.getId()))
                    .findFirst()
                    .map(UserNotification::getIsRead)
                    .orElse(false);
            return NotificationResponse.builder()
                    .id(notification.getId())
                    .title(notification.getTitle())
                    .message(notification.getMessage())
                    .type(notification.getType())
                    .isActive(notification.getIsActive())
                    .userId(notification.getUserId())
                    .bookingId(notification.getBookingId())
                    .createdAt(notification.getCreatedAt())
                    .expiresAt(notification.getExpiresAt())
                    .isRead(isRead)
                    .build();
        }).toList();
    }

    public List<NotificationResponse> getAllActiveNotifications() {
        Long userId = getCurrentUserId();
        if (userId == null || !isAdminUser()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Lấy tất cả thông báo, không giới hạn isActive hay expiresAt
        List<Notification> notifications = notificationRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        System.out.println("All notifications for admin (userId=" + userId + "): " + notifications);

        // Gắn trạng thái isRead từ user_notification cho admin
        List<UserNotification> userNotificationsStatus = userNotificationRepository.findByUserId(userId);

        return notifications.stream().map(notification -> {
            boolean isRead = userNotificationsStatus.stream()
                    .filter(un -> un.getNotificationId().equals(notification.getId()))
                    .findFirst()
                    .map(UserNotification::getIsRead)
                    .orElse(false);
            return NotificationResponse.builder()
                    .id(notification.getId())
                    .title(notification.getTitle())
                    .message(notification.getMessage())
                    .type(notification.getType())
                    .isActive(notification.getIsActive())
                    .userId(notification.getUserId())
                    .bookingId(notification.getBookingId())
                    .createdAt(notification.getCreatedAt())
                    .expiresAt(notification.getExpiresAt())
                    .isRead(isRead)
                    .build();
        }).toList();
    }

    public List<NotificationResponse> getActiveDiscountNotifications() {
        LocalDateTime now = LocalDateTime.now();
        return notificationRepository.findByIsActiveTrueAndExpiresAtAfterAndTypeAndUserIdIsNull(now, "PROMOTION")
                .stream()
                .map(notification -> NotificationResponse.builder()
                        .id(notification.getId())
                        .title(notification.getTitle())
                        .message(notification.getMessage())
                        .type(notification.getType())
                        .isActive(notification.getIsActive())
                        .userId(notification.getUserId())
                        .bookingId(notification.getBookingId())
                        .createdAt(notification.getCreatedAt())
                        .expiresAt(notification.getExpiresAt())
                        .isRead(false)
                        .build())
                .toList();
    }

    public Notification saveNotification(Notification notification) {
        Notification savedNotification = notificationRepository.save(notification);
        if (notification.getUserId() != null) {
            UserNotification userNotification = UserNotification.builder()
                    .userId(notification.getUserId())
                    .notificationId(savedNotification.getId())
                    .isRead(false)
                    .build();
            userNotificationRepository.save(userNotification);
        } else if ("PROMOTION".equals(notification.getType())) {
            List<Long> allUserIds = userRepository.findAll().stream()
                    .map(user -> user.getId())
                    .toList();
            for (Long userId : allUserIds) {
                UserNotification userNotification = UserNotification.builder()
                        .userId(userId)
                        .notificationId(savedNotification.getId())
                        .isRead(false)
                        .build();
                userNotificationRepository.save(userNotification);
            }
        }
        return savedNotification;
    }

    public boolean existsPromotionNotification() {
        return !notificationRepository.findByIsActiveTrueAndTypeAndUserIdIsNull("PROMOTION").isEmpty();
    }

    public boolean existsNotification(String type, Long bookingId, Long userId) {
        return !notificationRepository.findByIsActiveTrueAndTypeAndBookingIdAndUserId(type, bookingId, userId).isEmpty();
    }

    public void markNotificationAsRead(Long notificationId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        UserNotification userNotification = userNotificationRepository.findByUserIdAndNotificationId(userId, notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
        userNotification.setIsRead(true);
        userNotificationRepository.save(userNotification);
    }

    private Long getCurrentUserId() {
        try {
            Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return jwt.getClaim("user_id");
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isAdminUser() {
        try {
            Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String scope = jwt.getClaim("scope");
            List<String> roles = jwt.getClaimAsStringList("roles");
            return "ADMIN".equals(scope) || (roles != null && roles.contains("ADMIN"));
        } catch (Exception e) {
            return false;
        }
    }
}