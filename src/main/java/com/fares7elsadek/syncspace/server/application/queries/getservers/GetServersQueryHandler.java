package com.fares7elsadek.syncspace.server.application.queries.getservers;

import com.fares7elsadek.syncspace.server.api.dtos.ServerDto;
import com.fares7elsadek.syncspace.server.application.mapper.ServerMapper;
import com.fares7elsadek.syncspace.server.infrastructure.repository.ServerRepository;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.QueryHandler;
import com.fares7elsadek.syncspace.user.shared.UserAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetServersQueryHandler
        implements QueryHandler<GetServersQuery, ApiResponse<List<ServerDto>>> {

    private final UserAccessService userAccessService;
    private final ServerMapper serverMapper;
    private final ServerRepository serverRepository;
    @Override
    public ApiResponse<List<ServerDto>> handle(GetServersQuery query) {
        var user = userAccessService.getCurrentUserInfo();
        var servers = serverRepository.GetUserServers(user.getId());

        return ApiResponse.success("User servers",servers.stream().map(
                serverMapper::toServerDto
        ).toList());
    }
}
