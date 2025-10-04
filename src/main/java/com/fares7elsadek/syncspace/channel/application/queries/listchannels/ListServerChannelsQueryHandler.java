package com.fares7elsadek.syncspace.channel.application.queries.listchannels;

import com.fares7elsadek.syncspace.channel.api.dtos.ChannelDto;
import com.fares7elsadek.syncspace.channel.application.mapper.ChannelMapper;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.ChannelRepository;
import com.fares7elsadek.syncspace.server.shared.ServerAccessService;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.QueryHandler;
import com.fares7elsadek.syncspace.shared.exceptions.ServerExceptions;
import com.fares7elsadek.syncspace.user.shared.UserAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListServerChannelsQueryHandler
        implements QueryHandler<ListServerChannelsQuery, ApiResponse<List<ChannelDto>>> {

    private final UserAccessService userAccessService;
    private final ServerAccessService serverAccessService;
    private final ChannelRepository channelRepository;
    private final ChannelMapper channelMapper;
    @Override
    public ApiResponse<List<ChannelDto>> handle(ListServerChannelsQuery query) {

        var user = userAccessService.getCurrentUserInfo();

        if (!serverAccessService.isMember(query.serverId(), user.getId())) {
            throw new ServerExceptions("You are not a member of this server.");
        }

        var server = serverAccessService.getServer(query.serverId());
        var channels = channelRepository.findUserChannelsByServerId(user.getId(), server.getId());

        var channelDtos = channels.stream()
                .map(channelMapper::toChannelDto)
                .toList();

        return ApiResponse.success("channels", channelDtos);
    }
}
