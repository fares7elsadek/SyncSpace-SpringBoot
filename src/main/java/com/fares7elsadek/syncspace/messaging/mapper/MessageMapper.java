package com.fares7elsadek.syncspace.messaging.mapper;

import com.fares7elsadek.syncspace.messaging.model.dtos.MessageUserDto;
import com.fares7elsadek.syncspace.user.model.User;
import org.springframework.stereotype.Service;

@Service
public class MessageMapper {

    public MessageUserDto toDto(User user){
        return new  MessageUserDto(
                user.getId(),user.getUsername(),
                user.getFirstName(),user.getLastName(),user.getEmail(),
                user.getLastSeen(),user.getCreatedAt()
        );
    }
}
