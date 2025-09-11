package com.fares7elsadek.syncspace.channel.repository;

import com.fares7elsadek.syncspace.channel.model.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRepository extends JpaRepository<Channel,String> {
}
