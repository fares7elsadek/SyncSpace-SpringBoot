package com.fares7elsadek.syncspace.channel.infrastructure.repository;

import com.fares7elsadek.syncspace.channel.domain.model.RoomState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomStateRepository extends JpaRepository<RoomState, String> {
}
