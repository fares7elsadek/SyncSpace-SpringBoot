package com.fares7elsadek.syncspace.channel.application.queries.getroom;

import com.fares7elsadek.syncspace.channel.api.dtos.RoomStateDto;
import com.fares7elsadek.syncspace.channel.application.mapper.ChannelMapper;
import com.fares7elsadek.syncspace.channel.application.services.RoomStateService;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.QueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetRoomQueryHandler implements QueryHandler<GetRoomQuery,ApiResponse<RoomStateDto>> {
    private final RoomStateService roomStateService;
    private final ChannelMapper channelMapper;
    @Override
    public ApiResponse<RoomStateDto> handle(GetRoomQuery query) {
        var room = roomStateService.getRoomState(query.channelId());
        return ApiResponse.success("Room state",room);
    }
}
