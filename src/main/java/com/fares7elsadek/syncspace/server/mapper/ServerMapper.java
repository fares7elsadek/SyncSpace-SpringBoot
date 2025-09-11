package com.fares7elsadek.syncspace.server.mapper;

import com.fares7elsadek.syncspace.server.model.dtos.ServerUserDto;
import com.fares7elsadek.syncspace.user.model.User;
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
