package com.example.PRJWEB.Repository;

import com.example.PRJWEB.Entity.Notification;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification , Long> {
    List<Notification> findByIsActiveTrueAndExpiresAtAfter(LocalDateTime now);
    List<Notification> findByIsActiveTrueAndExpiresAtAfterAndUserIdIsNull(LocalDateTime now);
    List<Notification> findByIsActiveTrueAndExpiresAtAfterAndUserId(LocalDateTime now, Long userId);
    List<Notification> findByIsActiveTrueAndTypeAndBookingIdAndUserId(String type, Long bookingId, Long userId);
    List<Notification> findByIsActiveTrueAndTypeAndUserIdIsNull(String type);
    List<Notification> findByIsActiveTrueAndExpiresAtAfterAndTypeAndUserIdIsNull(LocalDateTime now, String type);

    List<Notification> findByUserIdOrUserIdIsNull(Long userId, Sort sort);
    List<Notification> findAll(Sort sort);
}

