package com.fares7elsadek.syncspace.channel.queries.getchannel;

import com.fares7elsadek.syncspace.channel.model.dtos.ChannelDto;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.Query;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record GetChannelQuery(
        @NotBlank(message = "Channel ID cannot be blank")
        @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "Channel ID must be a valid UUID")
        String channelId
) implements Query<ApiResponse<ChannelDto>> {
}
