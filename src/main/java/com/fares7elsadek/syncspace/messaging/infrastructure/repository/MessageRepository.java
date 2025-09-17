package com.fares7elsadek.syncspace.messaging.infrastructure.repository;

import com.fares7elsadek.syncspace.messaging.domain.model.Message;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {

    @EntityGraph(attributePaths = {"attachments","channel","sender"})
    @Query("""
        SELECT m FROM Message m
           WHERE m.channel.id = :channelId
             AND m.createdAt < COALESCE(:cursor, CURRENT_TIMESTAMP)
           ORDER BY m.createdAt DESC
    """)
    List<Message> findMessagesByChannelIdWithCursor(
            String channelId,
            LocalDateTime cursor,
            Pageable pageable
    );
}
