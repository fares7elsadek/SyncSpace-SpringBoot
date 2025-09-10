package com.fares7elsadek.syncspace.friendship.mapper;

import com.fares7elsadek.syncspace.friendship.model.dtos.FriendshipUserDto;
import com.fares7elsadek.syncspace.user.model.User;
import org.springframework.stereotype.Service;

@Service
public class FriendshipMapper {
    public FriendshipUserDto toFriendshipUserDto(User user){
        return  new FriendshipUserDto(
                user.getId(),user.getUsername(),
                user.getFirstName(),user.getLastName(),user.getEmail(),
                user.getLastSeen(),user.getCreatedAt()
        );
    }
}
