package com.fares7elsadek.syncspace.server.Queries.getserver;

import com.fares7elsadek.syncspace.server.model.ServerMemberId;
import com.fares7elsadek.syncspace.server.model.dtos.ServerDto;
import com.fares7elsadek.syncspace.server.repository.ServerMemberRepository;
import com.fares7elsadek.syncspace.server.repository.ServerRepository;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.QueryHandler;
import com.fares7elsadek.syncspace.shared.exceptions.ServerExceptions;
import com.fares7elsadek.syncspace.user.api.UserValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetServerQueryHandler implements
        QueryHandler<GetServerQuery, ApiResponse<ServerDto>> {

    private final UserValidationService userValidationService;
    private final ServerMemberRepository serverMemberRepository;
    private final ServerRepository serverRepository;

    @Override
    public ApiResponse<ServerDto> handle(GetServerQuery query) {

        var server = serverRepository.findById(query.serverId())
                .orElseThrow(() -> new ServerExceptions(String.format("Server with id %s not found", query.serverId())));

        if(!server.isPublic()){
            var currentUser = userValidationService.getCurrentUserInfo();
            serverMemberRepository
                    .findById(new ServerMemberId(query.serverId(),currentUser.getId()))
                    .orElseThrow(() -> new ServerExceptions(String.format("You don't have access to get details for server id %s ", query.serverId())));
        }

        return ApiResponse
                .success("Server details",
                        new ServerDto(server.getId()
                                ,server.getName(),server.getIconUrl(),server.getDescription()));
    }
}
