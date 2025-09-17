package com.fares7elsadek.syncspace.channel.repository;

import com.fares7elsadek.syncspace.channel.model.ChannelMembers;
import com.fares7elsadek.syncspace.channel.model.ChannelUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelMemberRepository extends JpaRepository<ChannelMembers, ChannelUserId> {
    @Query("""
            SELECT c FROM ChannelMembers c
            WHERE c.id.channelId = :channelId
            """)
    List<ChannelMembers> findByChannelId(String channelId);
}
