package com.fares7elsadek.syncspace.server.api.dtos;

import java.time.LocalDateTime;

public record InviteCodeDto(String id, String serverId, String serverName,String code ,int maxUses, int uses,
                            LocalDateTime expiresAt) {
}
