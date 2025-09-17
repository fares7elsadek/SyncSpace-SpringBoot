package com.fares7elsadek.syncspace.server.shared;

import com.fares7elsadek.syncspace.server.domain.model.Server;
import com.fares7elsadek.syncspace.server.domain.enums.ServerRoles;
import com.fares7elsadek.syncspace.user.domain.model.User;

import java.util.List;

public interface ServerAccessService {
    boolean isMember(String serverId, String userId);
    boolean hasRole(String serverId, String userId, ServerRoles... roles);
    Server getServer(String serverId);
    List<User> getServerMembers(String serverId);
    List<String> getUserServers(String userId);
}
