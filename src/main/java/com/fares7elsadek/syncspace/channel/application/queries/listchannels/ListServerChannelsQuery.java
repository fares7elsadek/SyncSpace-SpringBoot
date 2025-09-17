package com.fares7elsadek.syncspace.channel.application.queries.listchannels;

import com.fares7elsadek.syncspace.channel.api.dtos.ChannelDto;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.Query;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record ListServerChannelsQuery(
        @NotBlank(message = "Server ID cannot be blank")
        @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "Server ID must be a valid UUID")
        String serverId

) implements Query<ApiResponse<List<ChannelDto>>> {
}
