package com.example.PRJWEB.Controller;

import com.example.PRJWEB.DTO.Request.ApiResponse;
import com.example.PRJWEB.Entity.Notification;
import com.example.PRJWEB.Service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {
    NotificationService notificationService;

    @GetMapping
    public ApiResponse<List<Notification>> getNotifications() {
        List<Notification> notifications = notificationService.getActiveNotifications();
        return ApiResponse.<List<Notification>>builder()
                .message("Notifications retrieved successfully")
                .result(notifications)
                .build();
    }
}