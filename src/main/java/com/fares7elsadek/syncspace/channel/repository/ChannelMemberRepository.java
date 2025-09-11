package com.fares7elsadek.syncspace.channel.repository;

import com.fares7elsadek.syncspace.channel.model.ChannelMembers;
import com.fares7elsadek.syncspace.channel.model.ChannelUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelMemberRepository extends JpaRepository<ChannelMembers, ChannelUserId> {
}
