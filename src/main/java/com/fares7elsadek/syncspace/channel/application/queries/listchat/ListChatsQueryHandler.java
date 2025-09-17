package com.fares7elsadek.syncspace.channel.application.queries.listchat;

import com.fares7elsadek.syncspace.channel.application.mapper.ChannelMapper;
import com.fares7elsadek.syncspace.channel.domain.model.ChannelMembers;
import com.fares7elsadek.syncspace.channel.api.dtos.ChannelChatDto;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.ChannelRepository;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.QueryHandler;
import com.fares7elsadek.syncspace.user.api.UserAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ListChatsQueryHandler implements QueryHandler<ListChatsQuery, ApiResponse<List<ChannelChatDto>>> {

    private final UserAccessService userAccessService;
    private final ChannelMapper channelMapper;
    private final ChannelRepository channelRepository;

    @Override
    public ApiResponse<List<ChannelChatDto>> handle(ListChatsQuery query) {
        var userId = userAccessService.getCurrentUserInfo().getId();
        var channels = channelRepository.findUserPrivateChats(userId);

        List<ChannelChatDto>  channelChatDtos = new ArrayList<>();
        channels.forEach(ch -> {
            var otherUser = ch.getMembers().stream()
                    .map(ChannelMembers::getUser)
                    .filter(cUser -> !cUser.getId().equals(userId))
                    .findFirst()
                    .orElse(null);

            if (otherUser != null) {
                channelChatDtos.add(channelMapper.toChannelChatDto(ch, otherUser));
            }
        });

        return ApiResponse.success("User chat",channelChatDtos);
    }
}
