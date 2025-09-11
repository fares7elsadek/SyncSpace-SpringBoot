package com.fares7elsadek.syncspace.server.api;

import com.fares7elsadek.syncspace.server.model.Server;
import com.fares7elsadek.syncspace.server.shared.ServerRoles;

public interface ServerAccessService {
    boolean isMember(String serverId, String userId);
    boolean hasRole(String serverId, String userId, ServerRoles... roles);
    Server getServer(String serverId);
}
