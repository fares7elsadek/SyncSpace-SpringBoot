package com.fares7elsadek.syncspace.channel.application.mapper;

import com.fares7elsadek.syncspace.channel.api.dtos.*;
import com.fares7elsadek.syncspace.channel.application.commands.addmember.AddMemberResponse;
import com.fares7elsadek.syncspace.channel.domain.model.Channel;
import com.fares7elsadek.syncspace.channel.domain.model.ChannelUserId;
import com.fares7elsadek.syncspace.channel.domain.model.RoomState;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.ChannelReadStateRepository;
import com.fares7elsadek.syncspace.messaging.domain.model.Message;
import com.fares7elsadek.syncspace.messaging.shared.MessageAccessService;
import com.fares7elsadek.syncspace.user.domain.model.User;
import com.fares7elsadek.syncspace.user.shared.UserAccessService;
import lombok.RequiredArgsConstructor;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChannelMapper {

    private final MessageAccessService messageAccessService;
    private final ChannelReadStateRepository channelReadStateRepository;
    private final UserAccessService userAccessService;

    public ChannelChatUserDto toChannelChatUserDto(User user){
        PrettyTime p = new PrettyTime();
        String lastSeen = p.format(user.getLastSeen());
        String createdAt = p.format(user.getCreatedAt());

        return  new ChannelChatUserDto(
                user.getId(),user.getUsername(),
                user.getFirstName(),user.getLastName(),user.getEmail(),
                lastSeen,createdAt,user.getAvatarUrl(),user.isOnline()
        );
    }

    public ChannelChatDto toChannelChatDto(Channel channel, User user) {
        var optionalReadState = channelReadStateRepository.findById(
                new ChannelUserId(channel.getId(), userAccessService.getCurrentUserInfo().getId())
        );

        int unreadMessages;

        if (optionalReadState.isPresent()) {
            var readState = optionalReadState.get();
            var lastReadMessage = readState.getLastReadMessage();
            unreadMessages = messageAccessService.getUnreadMessages(
                    channel.getId(),
                    lastReadMessage.getId()
            );
        } else {
            unreadMessages = 0;
        }

        Message lastMessage = messageAccessService.getLastMessage(channel.getId());
        return new ChannelChatDto(
                toChannelPreviewDto(channel, lastMessage, unreadMessages),
                toChannelChatUserDto(user)
        );
    }


    public ChannelMessageDto toMessageDto(Message message){
        if(message == null) return null;
        PrettyTime p = new PrettyTime();
        String sentAt = p.format(message.getCreatedAt());
        return new  ChannelMessageDto(message.getChannel().getId(),message.getId(),message.getContent(),
                sentAt);
    }

    public ChannelPreviewDto toChannelPreviewDto(Channel channel,Message lastReadMessage,int unReadMessages){
        return new  ChannelPreviewDto(channel.getId(),channel.getName()
                ,channel.getDescription()
                ,channel.isPrivate()
                ,channel.isGroup(),toMessageDto(lastReadMessage),unReadMessages);
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

    public RoomStateDto toRoomStateDto(RoomState roomState){
        return new RoomStateDto(roomState.getId(),roomState.getVideoUrl(),roomState.getCurrentTimestamp(),
                roomState.getIsPlaying(),roomState.getLastUpdatedAt(),roomState.getPlaybackRate(),toChannelDto(roomState.getChannel()));
    }
}
