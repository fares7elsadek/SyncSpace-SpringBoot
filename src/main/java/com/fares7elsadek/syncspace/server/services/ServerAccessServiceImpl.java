package com.fares7elsadek.syncspace.server.services;

import com.fares7elsadek.syncspace.server.api.ServerAccessService;
import com.fares7elsadek.syncspace.server.model.Server;
import com.fares7elsadek.syncspace.server.model.ServerMember;
import com.fares7elsadek.syncspace.server.model.ServerMemberId;
import com.fares7elsadek.syncspace.server.repository.ServerMemberRepository;
import com.fares7elsadek.syncspace.server.repository.ServerRepository;
import com.fares7elsadek.syncspace.server.shared.ServerRoles;
import com.fares7elsadek.syncspace.shared.exceptions.ServerExceptions;
import com.fares7elsadek.syncspace.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServerAccessServiceImpl implements ServerAccessService {

    private final ServerMemberRepository serverMemberRepository;
    private final ServerRepository serverRepository;
    @Override
    public boolean isMember(String serverId, String userId) {
        return serverMemberRepository.findById(new ServerMemberId(serverId, userId)).isPresent();
    }

    @Override
    public boolean hasRole(String serverId, String userId, ServerRoles... roles) {
        return serverMemberRepository.findById(new ServerMemberId(serverId, userId))
                .map(member -> {
                    for (ServerRoles role : roles) {
                        if (member.getRole().getName().equals(role.name())) {
                            return true;
                        }
                    }
                    return false;
                })
                .orElse(false);
    }

    @Override
    public Server getServer(String serverId) {
        return serverRepository.findById(serverId).orElseThrow(
                () -> new ServerExceptions(String.format("Server with id %s not found", serverId))
        );
    }

    @Override
    public List<User> getServerMembers(String serverId) {
        return serverMemberRepository.findByIdServerId(serverId)
                .stream().map(ServerMember::getUser)
                .toList();
    }

    @Override
    public List<String> getUserServers(String userId) {
        return serverMemberRepository.findUserServers(userId)
                .stream().map(sm -> sm.getServer().getId())
                .toList();
    }
}
