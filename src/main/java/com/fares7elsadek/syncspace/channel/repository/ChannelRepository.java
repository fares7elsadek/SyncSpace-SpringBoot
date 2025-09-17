package com.fares7elsadek.syncspace.channel.repository;

import com.fares7elsadek.syncspace.channel.model.Channel;
import com.fares7elsadek.syncspace.server.model.Server;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelRepository extends JpaRepository<Channel,String> {
    @EntityGraph(attributePaths = {"members"})
    List<Channel> findByServer(Server server);

    @Query("""
            SELECT c FROM Channel c
              JOIN c.members m1
              JOIN c.members m2
              WHERE c.isGroup = false
                AND m1.id.userId = :user1
                AND m2.id.userId = :user2 
           """)
    Optional<Channel> findPrivateChannelByUsers(@Param("user1") String user1, @Param("user2") String user2);

    @Query("""
            SELECT DISTINCT c FROM Channel c
              JOIN c.members m1
              WHERE c.isGroup = false
                AND m1.id.userId = :userId
        """)
    List<Channel> findUserPrivateChats(String userId);
    @Query("""
            SELECT c FROM Channel c 
            WHERE c.server.id = :serverId AND c.isGroup = true
            AND c.isPrivate = false
            """)
    List<Channel> findAllPublicServerChannels(String serverId);

}
