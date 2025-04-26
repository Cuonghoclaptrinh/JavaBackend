package com.example.PRJWEB.Service;

import com.example.PRJWEB.Entity.Notification;
import com.example.PRJWEB.Entity.Payment;
import com.example.PRJWEB.Entity.Tour_booking;
import com.example.PRJWEB.Repository.PaymentRepository;
import com.example.PRJWEB.Repository.TourBookingRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationScheduler {
    private static final Logger logger = LoggerFactory.getLogger(NotificationScheduler.class);
    NotificationService notificationService;
    NotificationWebSocketHandler notificationWebSocketHandler;
    TourBookingRepository tourBookingRepository;
    PaymentRepository paymentRepository;

    // Thông báo ưu đãi (tất cả người dùng)
    @Scheduled(cron = "0 0 8 * * MON")
    public void createWeeklyPromotion() {
        // Kiểm tra xem đã có thông báo khuyến mãi đang hoạt động chưa
        if (notificationService.existsPromotionNotification()) {
            logger.info("Weekly promotion notification already exists, skipping creation.");
            return;
        }

        Notification notification = new Notification();
        notification.setTitle("Ưu đãi tuần mới!");
        notification.setMessage("Giảm 15% tất cả tour biển khi đặt trước Chủ Nhật!");
        notification.setType("PROMOTION");
        notification.setIsActive(true);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setExpiresAt(LocalDateTime.now().plusDays(7));
        notificationService.saveNotification(notification);

        try {
            String wsMessage = String.format("{\"title\":\"%s\",\"message\":\"%s\"}",
                    notification.getTitle(), notification.getMessage());
            notificationWebSocketHandler.sendNotificationToAll(wsMessage);
            logger.info("Sent weekly promotion notification to all users.");
        } catch (Exception e) {
            logger.error("Error sending WebSocket message for promotion: {}", e.getMessage());
        }
    }

    // Thông báo thanh toán phần còn lại (người dùng cụ thể)
    @Scheduled(cron = "0 0 9 * * *")
    public void checkRemainingPayments() {
        List<Tour_booking> bookings = tourBookingRepository.findByStatus("Booked");
        for (Tour_booking booking : bookings) {
            // Kiểm tra xem đã có thông báo nhắc nhở cho booking này chưa
            if (notificationService.existsNotification("REMINDER", booking.getBookingId(), booking.getCustomer().getId())) {
                logger.info("Reminder notification for booking {} already exists, skipping.", booking.getBookingId());
                continue;
            }

            List<Payment> payments = paymentRepository.findByBooking(booking);
            BigDecimal paidAmount = payments.stream()
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal remaining = booking.getTotalPrice().subtract(paidAmount);

            if (remaining.compareTo(BigDecimal.ZERO) > 0) {
                Notification notification = new Notification();
                notification.setTitle("Nhắc nhở thanh toán!");
                notification.setMessage(String.format("Vui lòng thanh toán %s VNĐ còn lại cho tour %s (#%s) trước ngày khởi hành!",
                        remaining, booking.getTour().getTourName(), booking.getBookingId()));
                notification.setType("REMINDER");
                notification.setIsActive(true);
                notification.setUserId(booking.getCustomer().getId());
                notification.setBookingId(booking.getBookingId());
                notification.setCreatedAt(LocalDateTime.now());
                notification.setExpiresAt(LocalDateTime.now().plusDays(3));
                notificationService.saveNotification(notification);

                try {
                    String wsMessage = String.format("{\"title\":\"%s\",\"message\":\"%s\"}",
                            notification.getTitle(), notification.getMessage());
                    notificationWebSocketHandler.sendNotificationToUser(
                            String.valueOf(booking.getCustomer().getId()), wsMessage);
                    logger.info("Sent payment reminder to user {} for booking {}.", booking.getCustomer().getId(), booking.getBookingId());
                } catch (Exception e) {
                    logger.error("Error sending WebSocket message for payment reminder: {}", e.getMessage());
                }
            }
        }
    }

    // Thông báo sắp đến ngày khởi hành tour (người đặt tour)
    @Scheduled(cron = "0 0 8 * * *")
    public void checkUpcomingTours() {
        LocalDate today = LocalDate.now();
        LocalDate threeDaysFromNow = today.plusDays(3);
        List<Tour_booking> bookings = tourBookingRepository.findByStatusIn(List.of("Booked", "Paid"));
        for (Tour_booking booking : bookings) {
            // Kiểm tra xem đã có thông báo cho tour này chưa
            if (notificationService.existsNotification("UPCOMING_TOUR", booking.getBookingId(), booking.getCustomer().getId())) {
                logger.info("Upcoming tour notification for booking {} already exists, skipping.", booking.getBookingId());
                continue;
            }

            LocalDate startDate = booking.getTour().getTourSchedules().getFirst().getDepartureDate();
            if (startDate != null && startDate.isEqual(threeDaysFromNow)) {
                Notification notification = new Notification();
                notification.setTitle("Sắp đến ngày khởi hành!");
                notification.setMessage(String.format("Tour %s (#%s) sẽ khởi hành vào %s. Hãy chuẩn bị nhé!",
                        booking.getTour().getTourName(), booking.getBookingId(), startDate));
                notification.setType("UPCOMING_TOUR");
                notification.setIsActive(true);
                notification.setUserId(booking.getCustomer().getId());
                notification.setBookingId(booking.getBookingId());
                notification.setCreatedAt(LocalDateTime.now());
                notification.setExpiresAt(LocalDateTime.now().plusDays(3));
                notificationService.saveNotification(notification);

                try {
                    String wsMessage = String.format("{\"title\":\"%s\",\"message\":\"%s\"}",
                            notification.getTitle(), notification.getMessage());
                    notificationWebSocketHandler.sendNotificationToUser(
                            String.valueOf(booking.getCustomer().getId()), wsMessage);
                    logger.info("Sent upcoming tour notification to user {} for booking {}.", booking.getCustomer().getId(), booking.getBookingId());
                } catch (Exception e) {
                    logger.error("Error sending WebSocket message for upcoming tour: {}", e.getMessage());
                }
            }
        }
    }
}