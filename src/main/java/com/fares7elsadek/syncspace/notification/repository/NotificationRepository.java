package com.fares7elsadek.syncspace.notification.repository;

import com.fares7elsadek.syncspace.notification.enums.NotificationType;
import com.fares7elsadek.syncspace.notification.model.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notifications, String> {
    @Query("""
           SELECT n FROM Notifications n
           WHERE n.type = :type
           AND n.relatedEntityId = :entityId
           """)
    Optional<Notifications> findByRelatedEntityIdAndType(String entityId, NotificationType type);
}
