package com.fares7elsadek.syncspace.channel.application.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ChannelPresenceService {
    private final StringRedisTemplate stringRedisTemplate;
    private static final String CHANNEL_ACTIVE_KEY = "channel:active:";

    public void setUserActive(String channelId,String userId){
        String key = CHANNEL_ACTIVE_KEY + channelId;
        stringRedisTemplate.opsForSet().add(key, userId);
        stringRedisTemplate.expire(key, 5, TimeUnit.MINUTES);
    }

    public void setUserInAtive(String channelId,String userId){
        String key = CHANNEL_ACTIVE_KEY + channelId;
        stringRedisTemplate.opsForSet().remove(key, userId);
    }

    public Set<String> getActiveUsers(String channelId) {
        String key = CHANNEL_ACTIVE_KEY + channelId;
        return stringRedisTemplate.opsForSet().members(key);
    }
}
