package com.healthcare.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private boolean isRead = false;
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum NotificationType {
        APPOINTMENT_CONFIRMED, APPOINTMENT_CANCELLED, APPOINTMENT_REMINDER,
        PRESCRIPTION_ISSUED, REFERRAL_CREATED, REFERRAL_UPDATED,
        HEALTH_UPDATE_RECEIVED, GENERAL
    }
}
