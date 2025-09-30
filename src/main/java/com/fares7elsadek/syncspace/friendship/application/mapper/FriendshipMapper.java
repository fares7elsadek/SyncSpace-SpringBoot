package com.fares7elsadek.syncspace.friendship.application.mapper;

import com.fares7elsadek.syncspace.friendship.api.dtos.FriendShipDto;
import com.fares7elsadek.syncspace.friendship.api.dtos.FriendshipUserDto;
import com.fares7elsadek.syncspace.friendship.domain.model.Friendships;
import com.fares7elsadek.syncspace.user.domain.model.User;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.stereotype.Service;

@Service
public class FriendshipMapper {
    public FriendshipUserDto toFriendshipUserDto(User user){
        PrettyTime p = new PrettyTime();
        String lastSeen = p.format(user.getLastSeen());
        String createdAt = p.format(user.getCreatedAt());
        return  new FriendshipUserDto(
                user.getId(),user.getUsername(),
                user.getFirstName(),user.getLastName(),user.getEmail(),
                lastSeen,createdAt,user.isOnline(),user.getAvatarUrl()
        );
    }

    public FriendShipDto toFriendshipDto(Friendships friendship,User user){
        PrettyTime p = new PrettyTime();
        String createdAt = p.format(friendship.getCreatedAt());
        return new  FriendShipDto(friendship.getId(),toFriendshipUserDto(user),createdAt,friendship.getFriendShipStatus().name());
    }
}
