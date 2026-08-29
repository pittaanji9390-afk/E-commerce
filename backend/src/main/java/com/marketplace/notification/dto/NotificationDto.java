package com.marketplace.notification.dto;

import com.marketplace.notification.domain.NotificationType;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {
    private UUID id;
    private String title;
    private String message;
    private NotificationType notificationType;
    private String actionUrl;
    private boolean read;
    private Instant createdAt;
}
