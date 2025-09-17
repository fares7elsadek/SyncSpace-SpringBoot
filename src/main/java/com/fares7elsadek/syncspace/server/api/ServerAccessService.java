package com.fares7elsadek.syncspace.server.api;

import com.fares7elsadek.syncspace.server.model.Server;
import com.fares7elsadek.syncspace.server.shared.ServerRoles;
import com.fares7elsadek.syncspace.user.model.User;

import java.util.List;

public interface ServerAccessService {
    boolean isMember(String serverId, String userId);
    boolean hasRole(String serverId, String userId, ServerRoles... roles);
    Server getServer(String serverId);
    List<User> getServerMembers(String serverId);
    List<String> getUserServers(String userId);
}
