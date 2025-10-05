package com.fares7elsadek.syncspace.channel.infrastructure.repository;

import com.fares7elsadek.syncspace.channel.domain.model.RoomViewer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomViewerRepository extends JpaRepository<RoomViewer, String> {
}
