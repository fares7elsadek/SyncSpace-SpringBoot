package com.fares7elsadek.syncspace.server.application.mapper;

import com.fares7elsadek.syncspace.server.api.dtos.ServerDto;
import com.fares7elsadek.syncspace.server.api.dtos.ServerUserDto;
import com.fares7elsadek.syncspace.server.domain.model.Server;
import com.fares7elsadek.syncspace.user.domain.model.User;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.stereotype.Service;

@Service
public class ServerMapper {
    public ServerUserDto toServerMemberDto(User user) {
        PrettyTime p = new PrettyTime();
        String lastSeen = p.format(user.getLastSeen());
        String createdAt = p.format(user.getCreatedAt());
        return  new ServerUserDto(
                user.getId(),user.getUsername(),
                user.getFirstName(),user.getLastName(),user.getEmail(),
                lastSeen,createdAt,user.isOnline(),user.getAvatarUrl()
        );
    }

    public ServerDto toServerDto(Server server) {
        return new ServerDto(server.getId(),server.getName(),server.getIconUrl(),server.getDescription());
    }
}
