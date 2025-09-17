package com.fares7elsadek.syncspace.server.application.mapper;

import com.fares7elsadek.syncspace.server.api.dtos.ServerUserDto;
import com.fares7elsadek.syncspace.user.domain.model.User;
import org.springframework.stereotype.Service;

@Service
public class ServerMapper {
    public ServerUserDto toServerMemberDto(User user) {
        return  new ServerUserDto(
                user.getId(),user.getUsername(),
                user.getFirstName(),user.getLastName(),user.getEmail(),
                user.getLastSeen(),user.getCreatedAt()
        );
    }
}
