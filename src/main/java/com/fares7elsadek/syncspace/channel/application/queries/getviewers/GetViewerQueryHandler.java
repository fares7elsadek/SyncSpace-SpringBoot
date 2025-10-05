package com.fares7elsadek.syncspace.channel.application.queries.getviewers;

import com.fares7elsadek.syncspace.channel.api.dtos.RoomViewerDto;
import com.fares7elsadek.syncspace.channel.application.mapper.ChannelMapper;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.ChannelRepository;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.QueryHandler;
import com.fares7elsadek.syncspace.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetViewerQueryHandler implements QueryHandler<GetViewerQuery, ApiResponse<List<RoomViewerDto>>> {
    private final ChannelRepository channelRepository;
    private final ChannelMapper channelMapper;
    @Override
    public ApiResponse<List<RoomViewerDto>> handle(GetViewerQuery query) {
        var channel = channelRepository.findById(query.channelId())
                .orElseThrow(() -> new NotFoundException("Channel not found"));

        var roomState = channel.getRoomState();

        var viewers = roomState.getViewers();

        return ApiResponse.success("Room viewers"
                ,viewers.stream().map(channelMapper::toRoomViewerDto).toList());
    }
}
