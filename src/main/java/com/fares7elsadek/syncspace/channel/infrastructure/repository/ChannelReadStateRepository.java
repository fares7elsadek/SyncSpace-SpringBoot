package com.fares7elsadek.syncspace.channel.infrastructure.repository;

import com.fares7elsadek.syncspace.channel.domain.model.ChannelReadState;
import com.fares7elsadek.syncspace.channel.domain.model.ChannelUserId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelReadStateRepository extends JpaRepository<ChannelReadState, ChannelUserId> {
}
