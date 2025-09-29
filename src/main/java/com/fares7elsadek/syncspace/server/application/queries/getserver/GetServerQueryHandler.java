package com.fares7elsadek.syncspace.server.application.queries.getserver;

import com.fares7elsadek.syncspace.server.api.dtos.ServerDto;
import com.fares7elsadek.syncspace.server.application.mapper.ServerMapper;
import com.fares7elsadek.syncspace.server.domain.model.ServerMemberId;
import com.fares7elsadek.syncspace.server.infrastructure.repository.ServerMemberRepository;
import com.fares7elsadek.syncspace.server.infrastructure.repository.ServerRepository;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.QueryHandler;
import com.fares7elsadek.syncspace.shared.exceptions.ServerExceptions;
import com.fares7elsadek.syncspace.user.shared.UserAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetServerQueryHandler implements
        QueryHandler<GetServerQuery, ApiResponse<ServerDto>> {

    private final UserAccessService userAccessService;
    private final ServerMemberRepository serverMemberRepository;
    private final ServerRepository serverRepository;
    private final ServerMapper serverMapper;

    @Override
    public ApiResponse<ServerDto> handle(GetServerQuery query) {

        var server = serverRepository.findById(query.serverId())
                .orElseThrow(() -> new ServerExceptions(String.format("Server with id %s not found", query.serverId())));

        if(!server.isPublic()){
            var currentUser = userAccessService.getCurrentUserInfo();
            serverMemberRepository
                    .findById(new ServerMemberId(query.serverId(),currentUser.getId()))
                    .orElseThrow(() -> new ServerExceptions(String.format("You don't have access to get details for server id %s ", query.serverId())));
        }

        return ApiResponse
                .success("Server details",
                        serverMapper.toServerDto(server)
                        );
    }
}
