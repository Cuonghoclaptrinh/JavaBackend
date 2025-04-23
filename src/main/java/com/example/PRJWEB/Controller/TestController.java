package com.example.PRJWEB.Controller;

import com.example.PRJWEB.DTO.Request.ApiResponse;
import com.example.PRJWEB.Service.NotificationScheduler;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test/scheduler")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TestController {
    NotificationScheduler notificationScheduler;

    @PostMapping("/{method}")
    public ApiResponse<String> triggerScheduler(@PathVariable String method) {
        switch (method) {
            case "createWeeklyPromotion":
                notificationScheduler.createWeeklyPromotion();
                return ApiResponse.<String>builder()
                        .message("Triggered createWeeklyPromotion")
                        .result("Success")
                        .build();
            case "checkRemainingPayments":
                notificationScheduler.checkRemainingPayments();
                return ApiResponse.<String>builder()
                        .message("Triggered checkRemainingPayments")
                        .result("Success")
                        .build();
            case "checkUpcomingTours":
                notificationScheduler.checkUpcomingTours();
                return ApiResponse.<String>builder()
                        .message("Triggered checkUpcomingTours")
                        .result("Success")
                        .build();
            default:
                return ApiResponse.<String>builder()
                        .message("Unknown method")
                        .result("Failed")
                        .build();
        }
    }
}