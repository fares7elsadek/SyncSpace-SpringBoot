package com.fares7elsadek.syncspace.channel.application.queries.useractivity;

import com.fares7elsadek.syncspace.channel.api.dtos.RoomStateDto;
import com.fares7elsadek.syncspace.channel.application.mapper.ChannelMapper;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.RoomStateRepository;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.QueryHandler;
import com.fares7elsadek.syncspace.user.shared.UserAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetUserActivityQueryHandler implements QueryHandler<GetUserActivityQuery, ApiResponse<List<RoomStateDto>>> {

    private final RoomStateRepository roomStateRepository;
    private final ChannelMapper channelMapper;
    private final UserAccessService userAccessService;
    @Override
    public ApiResponse<List<RoomStateDto>> handle(GetUserActivityQuery query) {
        var user = userAccessService.getCurrentUserInfo();
        var states = roomStateRepository.findRoomStatesForUser(user.getId());


        return ApiResponse.success("User activities",states.stream()
                .map(channelMapper::toRoomStateDto).toList());
    }
}
