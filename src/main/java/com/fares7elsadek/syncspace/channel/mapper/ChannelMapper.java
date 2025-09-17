package com.fares7elsadek.syncspace.channel.mapper;

import com.fares7elsadek.syncspace.channel.model.Channel;
import com.fares7elsadek.syncspace.channel.model.dtos.ChannelChatDto;
import com.fares7elsadek.syncspace.channel.model.dtos.ChannelChatUserDto;
import com.fares7elsadek.syncspace.user.model.User;
import org.springframework.stereotype.Service;

@Service
public class ChannelMapper {
    public ChannelChatUserDto toFriendshipUserDto(User user){
        return  new ChannelChatUserDto(
                user.getId(),user.getUsername(),
                user.getFirstName(),user.getLastName(),user.getEmail(),
                user.getLastSeen(),user.getCreatedAt(),user.isOnline()
        );
    }

    public ChannelChatDto  toChannelChatDto(Channel channel,User user){
        return new ChannelChatDto(channel.getId(), channel.getName()
                , channel.getDescription(),channel.isPrivate()
                ,channel.isGroup(),toFriendshipUserDto(user));
    }
}
