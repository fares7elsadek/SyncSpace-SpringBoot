package com.fares7elsadek.syncspace.notification.infrastructure.repository;

import com.fares7elsadek.syncspace.notification.domain.enums.NotificationType;
import com.fares7elsadek.syncspace.notification.domain.model.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notifications, String> {
    @Query("""
           SELECT n FROM Notifications n
           WHERE n.type = :type
           AND n.relatedEntityId = :entityId
           """)
    Optional<Notifications> findByRelatedEntityIdAndType(String entityId, NotificationType type);

    @Query("""
            SELECT n FROM Notifications n 
            WHERE n.read = :read AND n.user.id = :userId
           """)
    List<Notifications> findByUserAndRead(String userId,boolean read);

    @Query("""
            SELECT n FROM Notifications n 
            WHERE n.user.id = :userId
           """)
    List<Notifications> findByUser(String userId);
}
