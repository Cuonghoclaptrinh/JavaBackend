package com.example.PRJWEB.Controller;

import com.example.PRJWEB.DTO.Request.ApiResponse;
import com.example.PRJWEB.DTO.Request.NotificationRequest;
import com.example.PRJWEB.DTO.Respon.NotificationResponse;
import com.example.PRJWEB.Entity.Notification;
import com.example.PRJWEB.Service.NotificationService;
import com.example.PRJWEB.Service.NotificationWebSocketHandler;
import jakarta.annotation.security.PermitAll;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    NotificationService notificationService;
    NotificationWebSocketHandler notificationWebSocketHandler;

    @GetMapping
    public ApiResponse<List<NotificationResponse>> getNotifications() {
        List<NotificationResponse> notifications = notificationService.getActiveNotifications();
        return ApiResponse.<List<NotificationResponse>>builder()
                .message("Notifications retrieved successfully")
                .result(notifications)
                .build();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/all")
    public ApiResponse<List<NotificationResponse>> getAllNotifications() {
        List<NotificationResponse> notifications = notificationService.getAllActiveNotifications();
        return ApiResponse.<List<NotificationResponse>>builder()
                .message("All active notifications retrieved successfully")
                .result(notifications)
                .build();
    }

    @PostMapping("/{notificationId}/read")
    public ApiResponse<String> markAsRead(@PathVariable Long notificationId) {
        notificationService.markNotificationAsRead(notificationId);
        return ApiResponse.<String>builder()
                .message("Notification marked as read")
                .result("Success")
                .build();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/create-discount")
    public ApiResponse<String> createDiscountNotification(
            @RequestBody NotificationRequest request) {
        Notification notification = new Notification();
        notification.setTitle(request.title());
        notification.setMessage(request.message());
        notification.setType("PROMOTION");
        notification.setIsActive(true);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setExpiresAt(LocalDateTime.now().plusDays(request.daysValid()));
        notificationService.saveNotification(notification);

        try {
            String wsMessage = String.format("{\"title\":\"%s\",\"message\":\"%s\",\"type\":\"%s\"}",
                    notification.getTitle(), notification.getMessage(), notification.getType());
            notificationWebSocketHandler.sendNotificationToAll(wsMessage);
            logger.info("Sent discount notification to all users: {}", wsMessage);
            return ApiResponse.<String>builder()
                    .message("Discount notification created and sent successfully")
                    .result("Success")
                    .build();
        } catch (Exception e) {
            logger.error("Error sending WebSocket message for discount: {}", e.getMessage());
            return ApiResponse.<String>builder()
                    .message("Notification created but failed to send via WebSocket: " + e.getMessage())
                    .result("Partial Success")
                    .build();
        }
    }

    @GetMapping("/discounts")
    @PermitAll
    public ApiResponse<List<NotificationResponse>> getDiscountNotifications() {
        List<NotificationResponse> notifications = notificationService.getActiveDiscountNotifications();
        return ApiResponse.<List<NotificationResponse>>builder()
                .message("Discount notifications retrieved successfully")
                .result(notifications)
                .build();
    }
}