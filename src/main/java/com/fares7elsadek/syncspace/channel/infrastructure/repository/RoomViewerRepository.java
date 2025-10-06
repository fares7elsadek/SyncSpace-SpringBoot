package com.fares7elsadek.syncspace.channel.infrastructure.repository;

import com.fares7elsadek.syncspace.channel.domain.model.RoomViewer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomViewerRepository extends JpaRepository<RoomViewer, String> {

    @Query("""
           SELECT rv FROM RoomViewer rv
           WHERE rv.roomState.channel.id = :channelId
           AND rv.user.id = :userId
           """)
    Optional<RoomViewer> findByChannelIdAndUserId(String channelId, String userId);
}
