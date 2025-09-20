package com.fares7elsadek.syncspace.channel.application.mapper;

import com.fares7elsadek.syncspace.channel.api.dtos.ChannelChatDto;
import com.fares7elsadek.syncspace.channel.api.dtos.ChannelChatUserDto;
import com.fares7elsadek.syncspace.channel.api.dtos.ChannelDto;
import com.fares7elsadek.syncspace.channel.application.commands.addmember.AddMemberResponse;
import com.fares7elsadek.syncspace.channel.domain.model.Channel;
import com.fares7elsadek.syncspace.user.domain.model.User;
import org.springframework.stereotype.Service;

@Service
public class ChannelMapper {
    public ChannelChatUserDto toChannelChatUserDto(User user){
        return  new ChannelChatUserDto(
                user.getId(),user.getUsername(),
                user.getFirstName(),user.getLastName(),user.getEmail(),
                user.getLastSeen(),user.getCreatedAt(),user.getAvatarUrl(),user.isOnline()
        );
    }

    public ChannelChatDto  toChannelChatDto(Channel channel,User user){
        return new ChannelChatDto(channel.getId(), channel.getName()
                , channel.getDescription(),channel.isPrivate()
                ,channel.isGroup(), toChannelChatUserDto(user));
    }

    public ChannelDto toChannelDto(Channel channel){
        return new  ChannelDto(channel.getId(),channel.getName()
                ,channel.getDescription()
                ,channel.isPrivate()
                ,channel.isGroup());
    }

    public AddMemberResponse toAddMemberResponse(Channel channel,User user){
        return new AddMemberResponse(toChannelDto(channel), toChannelChatUserDto(user));
    }
}
