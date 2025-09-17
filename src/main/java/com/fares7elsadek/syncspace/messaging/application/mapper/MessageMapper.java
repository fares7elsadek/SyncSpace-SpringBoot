package com.fares7elsadek.syncspace.messaging.application.mapper;

import com.fares7elsadek.syncspace.messaging.api.dtos.MessageUserDto;
import com.fares7elsadek.syncspace.user.domain.model.User;
import org.springframework.stereotype.Service;

@Service
public class MessageMapper {

    public MessageUserDto toDto(User user){
        return new  MessageUserDto(
                user.getId(),user.getUsername(),
                user.getFirstName(),user.getLastName(),user.getEmail(),
                user.getLastSeen(),user.getCreatedAt(),user.isOnline(),user.getAvatarUrl()
        );
    }
}
