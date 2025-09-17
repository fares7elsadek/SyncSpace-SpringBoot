package com.fares7elsadek.syncspace.channel.application.queries.getchannel;

import com.fares7elsadek.syncspace.channel.api.dtos.ChannelDto;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.Query;

public record GetChannelQuery(
        String channelId
) implements Query<ApiResponse<ChannelDto>> {
}
