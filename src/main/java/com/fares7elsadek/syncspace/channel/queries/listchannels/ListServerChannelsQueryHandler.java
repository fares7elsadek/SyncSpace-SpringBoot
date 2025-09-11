package com.fares7elsadek.syncspace.channel.queries.listchannels;

import com.fares7elsadek.syncspace.channel.model.dtos.ChannelDto;
import com.fares7elsadek.syncspace.channel.repository.ChannelRepository;
import com.fares7elsadek.syncspace.server.api.ServerAccessService;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.QueryHandler;
import com.fares7elsadek.syncspace.shared.exceptions.ServerExceptions;
import com.fares7elsadek.syncspace.user.api.UserValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListServerChannelsQueryHandler
        implements QueryHandler<ListServerChannelsQuery, ApiResponse<List<ChannelDto>>> {

    private final UserValidationService userValidationService;
    private final ServerAccessService serverAccessService;
    private final ChannelRepository channelRepository;
    @Override
    public ApiResponse<List<ChannelDto>> handle(ListServerChannelsQuery query) {

        var user = userValidationService.getCurrentUserInfo();

        if (!serverAccessService.isMember(query.serverId(), user.getId())) {
            throw new ServerExceptions("You are not a member of this server.");
        }

        var server = serverAccessService.getServer(query.serverId());


        var channels = channelRepository.findByServer(server);

        var channelDtos = channels.stream()
                .filter(channel -> !channel.isPrivate()
                        || channel.getMembers().stream().anyMatch(member -> member.getId().equals(user.getId())))
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
