package com.fares7elsadek.syncspace.server.queries.listmembers;

import com.fares7elsadek.syncspace.server.mapper.ServerMapper;
import com.fares7elsadek.syncspace.server.model.ServerMember;
import com.fares7elsadek.syncspace.server.model.ServerMemberId;
import com.fares7elsadek.syncspace.server.model.dtos.ServerMemberDto;
import com.fares7elsadek.syncspace.server.repository.ServerMemberRepository;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.QueryHandler;
import com.fares7elsadek.syncspace.shared.exceptions.ServerExceptions;
import com.fares7elsadek.syncspace.user.api.UserAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListMembersQueryHandler
        implements QueryHandler<ListMembersQuery, ApiResponse<List<ServerMemberDto>>> {

    private final UserAccessService userAccessService;
    private final ServerMapper serverMapper;
    private final ServerMemberRepository serverMemberRepository;

    @Override
    public ApiResponse<List<ServerMemberDto>> handle(ListMembersQuery query) {

        var currentUser = userAccessService.getCurrentUserInfo();

        var serverMember = serverMemberRepository
                .findById(new ServerMemberId(query.serverId(),currentUser.getId()))
                .orElseThrow(() -> new ServerExceptions(String.format("You don't have access to list members for server id %s ", query.serverId())));

        List<ServerMember> serverMemberList = serverMemberRepository.findByIdServerId(query.serverId());

        List<ServerMemberDto> serverMemberDtoList = serverMemberList
                .stream().map(member ->
                        new ServerMemberDto(member.getNickname()
                                ,member.getRole().getName()
                                ,serverMapper.toServerMemberDto(member.getUser()))).toList();


        return ApiResponse.success("List of server members", serverMemberDtoList);
    }
}
