package com.fares7elsadek.syncspace.channel.application.queries.listchannels;

import com.fares7elsadek.syncspace.channel.api.dtos.ChannelDto;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.ChannelRepository;
import com.fares7elsadek.syncspace.server.api.ServerAccessService;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.QueryHandler;
import com.fares7elsadek.syncspace.shared.exceptions.ServerExceptions;
import com.fares7elsadek.syncspace.user.api.UserAccessService;
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
    @Override
    public ApiResponse<List<ChannelDto>> handle(ListServerChannelsQuery query) {

        var user = userAccessService.getCurrentUserInfo();

        if (!serverAccessService.isMember(query.serverId(), user.getId())) {
            throw new ServerExceptions("You are not a member of this server.");
        }

        var server = serverAccessService.getServer(query.serverId());
        var channels = channelRepository.findUserChannelsByServerId(user.getId(), server.getId());

        var channelDtos = channels.stream()
                .map(channel -> new ChannelDto(
                        channel.getId(),
                        channel.getName(),
                        channel.getDescription(),
                        channel.isPrivate(),
                        channel.isGroup()
                ))
                .toList();

        return ApiResponse.success("channels", channelDtos);
    }
}
