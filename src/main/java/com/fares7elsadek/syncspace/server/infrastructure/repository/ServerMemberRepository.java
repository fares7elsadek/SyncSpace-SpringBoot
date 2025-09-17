package com.fares7elsadek.syncspace.server.infrastructure.repository;

import com.fares7elsadek.syncspace.server.domain.model.ServerMember;
import com.fares7elsadek.syncspace.server.domain.model.ServerMemberId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServerMemberRepository extends JpaRepository<ServerMember, ServerMemberId> {
    @EntityGraph(attributePaths = {"role"})
    Optional<ServerMember> findById(ServerMemberId serverMemberId);

    @EntityGraph(attributePaths = {"user","role"})
    List<ServerMember> findByIdServerId(String serverId);
    @EntityGraph(attributePaths = {"server"})
    @Query("""
           SELECT sm
           FROM ServerMember sm
           WHERE sm.id.userId = :userId
           """)
    List<ServerMember> findUserServers(String userId);
}
