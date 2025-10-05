package com.fares7elsadek.syncspace.channel.infrastructure.repository;

import com.fares7elsadek.syncspace.channel.domain.model.RoomState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomStateRepository extends JpaRepository<RoomState, String> {
    @Query("""
       SELECT s FROM RoomState s 
       JOIN s.channel c 
       JOIN c.members m
       WHERE m.user.id = :userId
       AND s.videoUrl <> '' 
       AND s.isPlaying = true ORDER BY s.lastUpdatedAt DESC 
       """)
    List<RoomState> findRoomStatesForUser(String userId);
}
