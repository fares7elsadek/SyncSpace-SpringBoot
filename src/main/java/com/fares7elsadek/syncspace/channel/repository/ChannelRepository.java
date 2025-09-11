package com.fares7elsadek.syncspace.channel.repository;

import com.fares7elsadek.syncspace.channel.model.Channel;
import com.fares7elsadek.syncspace.server.model.Server;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelRepository extends JpaRepository<Channel,String> {
    @EntityGraph(attributePaths = {"members"})
    List<Channel> findByServer(Server server);
}
