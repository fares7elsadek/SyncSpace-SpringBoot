package com.fares7elsadek.syncspace.server.infrastructure.repository;

import com.fares7elsadek.syncspace.server.domain.model.Server;
import com.fares7elsadek.syncspace.server.domain.model.ServerInvites;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServerInvitesRepository extends JpaRepository<ServerInvites, String> {
    @EntityGraph(attributePaths = {"server"})
    Optional<ServerInvites> findByCode(String code);

    List<ServerInvites> findByServer(Server server);
}
