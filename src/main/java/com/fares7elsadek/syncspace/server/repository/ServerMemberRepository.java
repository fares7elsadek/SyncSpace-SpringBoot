package com.fares7elsadek.syncspace.server.repository;

import com.fares7elsadek.syncspace.server.model.ServerMember;
import com.fares7elsadek.syncspace.server.model.ServerMemberId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServerMemberRepository extends JpaRepository<ServerMember, ServerMemberId> {
    @EntityGraph(attributePaths = {"role"})
    Optional<ServerMember> findByIdAndServerId(ServerMemberId serverMemberId);
}
