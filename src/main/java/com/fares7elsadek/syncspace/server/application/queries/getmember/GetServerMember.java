package com.fares7elsadek.syncspace.server.application.queries.getmember;

import com.fares7elsadek.syncspace.server.api.dtos.ServerMemberDto;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.Query;

public record GetServerMember(
        String serverId
) implements Query<ApiResponse<ServerMemberDto>> {
}
