package com.healthcare.repository;

import com.healthcare.entity.Notification;
import com.healthcare.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    List<Notification> findByUserAndIsRead(User user, boolean isRead);
    long countByUserAndIsRead(User user, boolean isRead);
}
