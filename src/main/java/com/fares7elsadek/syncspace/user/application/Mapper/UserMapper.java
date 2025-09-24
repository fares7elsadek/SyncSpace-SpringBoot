package com.fares7elsadek.syncspace.user.application.Mapper;

import com.fares7elsadek.syncspace.user.api.dto.UserDto;
import com.fares7elsadek.syncspace.user.domain.model.User;
import com.fares7elsadek.syncspace.user.shared.UserInfo;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    public UserInfo toUserInfo(User user) {
        return new UserInfo(user.getId(),user.getUsername(),user.getEmail());
    }
    public UserDto toUserDto(User user){
        return  new UserDto(
                user.getId(),user.getUsername(),
                user.getFirstName(),user.getLastName(),user.getEmail(),
                user.getLastSeen(),user.getCreatedAt(),user.getAvatarUrl(),user.isOnline()
        );
    }
}
