package com.fares7elsadek.syncspace.user.application.Mapper;

import com.fares7elsadek.syncspace.user.api.dto.UserDto;
import com.fares7elsadek.syncspace.user.domain.model.User;
import com.fares7elsadek.syncspace.user.shared.UserInfo;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    public UserInfo toUserInfo(User user) {
        return new UserInfo(user.getId(),user.getUsername(),user.getEmail());
    }
    public UserDto toUserDto(User user){

        PrettyTime p = new PrettyTime();
        String lastSeen = p.format(user.getLastSeen());
        String createdAt = p.format(user.getCreatedAt());
        return  new UserDto(
                user.getId(),user.getUsername(),
                user.getFirstName(),user.getLastName(),user.getEmail(),
                lastSeen,createdAt,user.getAvatarUrl(),user.isOnline()
        );
    }
}
