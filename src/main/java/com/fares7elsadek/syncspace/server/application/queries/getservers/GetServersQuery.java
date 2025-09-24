package com.fares7elsadek.syncspace.server.application.queries.getservers;

import com.fares7elsadek.syncspace.server.api.dtos.ServerDto;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.Query;

import java.util.List;

public record GetServersQuery() implements Query<ApiResponse<List<ServerDto>>> {
}
