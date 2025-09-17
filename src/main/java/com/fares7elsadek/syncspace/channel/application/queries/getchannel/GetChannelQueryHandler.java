package com.fares7elsadek.syncspace.channel.application.queries.getchannel;

import com.fares7elsadek.syncspace.channel.domain.model.ChannelUserId;
import com.fares7elsadek.syncspace.channel.api.dtos.ChannelDto;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.ChannelMemberRepository;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.QueryHandler;
import com.fares7elsadek.syncspace.shared.exceptions.ServerExceptions;
import com.fares7elsadek.syncspace.user.shared.UserAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GetChannelQueryHandler
        implements QueryHandler<GetChannelQuery, ApiResponse<ChannelDto>> {

    private final UserAccessService userAccessService;
    private final ChannelMemberRepository channelMemberRepository;

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<ChannelDto> handle(GetChannelQuery query) {
        var user = userAccessService.getCurrentUserInfo();
        var channelMember = channelMemberRepository
                .findById(new ChannelUserId(query.channelId(), user.getId()))
                .orElseThrow(() -> new ServerExceptions(String.format("You don't have access for channel %s", query.channelId())));

        var channel = channelMember.getChannel();
        var dto = new ChannelDto(channel.getId(),channel.getName(),channel.getDescription(),channel.isPrivate(),channel.isGroup());

        return ApiResponse.success("Channel details",dto);
    }
}
