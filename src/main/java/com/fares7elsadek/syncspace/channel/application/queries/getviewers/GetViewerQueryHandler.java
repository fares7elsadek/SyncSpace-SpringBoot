package com.fares7elsadek.syncspace.channel.application.queries.getviewers;

import com.fares7elsadek.syncspace.channel.api.dtos.RoomViewerDto;
import com.fares7elsadek.syncspace.channel.application.mapper.ChannelMapper;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.RoomViewerRepository;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.QueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetViewerQueryHandler implements QueryHandler<GetViewerQuery, ApiResponse<List<RoomViewerDto>>> {
    private final RoomViewerRepository roomViewerRepository;
    private final ChannelMapper channelMapper;
    @Override
    public ApiResponse<List<RoomViewerDto>> handle(GetViewerQuery query) {
        var viewers = roomViewerRepository.findByChannelId(query.channelId());
        return ApiResponse.success("Room viewers"
                ,viewers.stream().map(channelMapper::toRoomViewerDto).toList());
    }
}
