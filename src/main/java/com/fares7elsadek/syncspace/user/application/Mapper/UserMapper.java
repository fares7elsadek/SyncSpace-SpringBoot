package com.fares7elsadek.syncspace.user.application.Mapper;

import com.fares7elsadek.syncspace.user.shared.UserInfo;
import com.fares7elsadek.syncspace.user.domain.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    public UserInfo toUserInfo(User user) {
        return new UserInfo(user.getId(),user.getUsername(),user.getEmail());
    }

}
