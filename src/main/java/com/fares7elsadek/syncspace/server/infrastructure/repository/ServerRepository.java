package com.fares7elsadek.syncspace.server.infrastructure.repository;

import com.fares7elsadek.syncspace.server.domain.model.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServerRepository extends JpaRepository<Server,String> {

    @Query("""
           SELECT s FROM Server s 
           JOIN s.members sm WHERE 
           sm.user.id = :id
           """)
    List<Server> GetUserServers(String id);
}
