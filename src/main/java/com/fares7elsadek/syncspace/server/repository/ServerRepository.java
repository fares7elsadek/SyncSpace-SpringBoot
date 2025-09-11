package com.fares7elsadek.syncspace.server.repository;

import com.fares7elsadek.syncspace.server.model.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServerRepository extends JpaRepository<Server,String> {
}
