package com.fares7elsadek.syncspace.messaging.application.mapper;

import com.fares7elsadek.syncspace.messaging.api.dtos.MessageDto;
import com.fares7elsadek.syncspace.messaging.api.dtos.MessageUserDto;
import com.fares7elsadek.syncspace.messaging.domain.model.Message;
import com.fares7elsadek.syncspace.messaging.domain.model.MessageAttachments;
import com.fares7elsadek.syncspace.user.domain.model.User;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageMapper {

    public MessageUserDto toDto(User user){
        PrettyTime p = new PrettyTime();
        String lastSeen = p.format(user.getLastSeen());
        String createdAt = p.format(user.getCreatedAt());
        return new  MessageUserDto(
                user.getId(),user.getUsername(),
                user.getFirstName(),user.getLastName(),user.getEmail(),
                lastSeen,createdAt,user.isOnline(),user.getAvatarUrl()
        );
    }

    public MessageDto toMessageDto(Message message){
        List<String> urls = new ArrayList<>();
        if(message.getAttachments()!=null)
                urls = message.getAttachments().stream().map(MessageAttachments::getUrl).collect(Collectors.toList());

        PrettyTime p = new PrettyTime();
        String sentAt = p.format(message.getCreatedAt());
        return new  MessageDto(message.getChannel().getId(),message.getId(),message.getContent(),
                toDto(message.getSender()),urls,sentAt);
    }
}
