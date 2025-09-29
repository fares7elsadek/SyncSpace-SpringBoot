package com.fares7elsadek.syncspace.server.application.queries.getmember;

import com.fares7elsadek.syncspace.server.api.dtos.ServerMemberDto;
import com.fares7elsadek.syncspace.server.application.mapper.ServerMapper;
import com.fares7elsadek.syncspace.server.domain.model.ServerMemberId;
import com.fares7elsadek.syncspace.server.infrastructure.repository.ServerMemberRepository;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.QueryHandler;
import com.fares7elsadek.syncspace.shared.exceptions.NotFoundException;
import com.fares7elsadek.syncspace.user.shared.UserAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("getServerMemberHandler")
@RequiredArgsConstructor
public class GetServerMemberQueryHandler implements
        QueryHandler<GetServerMember, ApiResponse<ServerMemberDto>> {

    private final ServerMemberRepository serverMemberRepository;
    private final UserAccessService userAccessService;
    private final ServerMapper serverMapper;

    @Override
    public ApiResponse<ServerMemberDto> handle(GetServerMember query) {
        var user = userAccessService.getCurrentUserInfo();
        var serverMember = serverMemberRepository.findById(new ServerMemberId(query.serverId(),user.getId()))
                .orElseThrow(()-> new NotFoundException("Member not found"));
        return ApiResponse.success("Server member", serverMapper.toServerMemberDto(serverMember));
    }
}
