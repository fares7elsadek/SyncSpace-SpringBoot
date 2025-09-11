package com.fares7elsadek.syncspace.server.Queries.getserver;

import com.fares7elsadek.syncspace.server.model.dtos.ServerDto;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.Query;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record GetServerQuery(
        @NotBlank(message = "Server ID cannot be blank")
        @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "Server ID must be a valid UUID")
        String serverId
) implements Query<ApiResponse<ServerDto>> {
}
