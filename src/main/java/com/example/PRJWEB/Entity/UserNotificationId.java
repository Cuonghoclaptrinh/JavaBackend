package com.example.PRJWEB.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationId implements Serializable {
    private Long userId;
    private Long notificationId;
}