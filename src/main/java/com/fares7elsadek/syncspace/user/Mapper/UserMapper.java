package com.fares7elsadek.syncspace.user.Mapper;

import com.fares7elsadek.syncspace.user.api.UserInfo;
import com.fares7elsadek.syncspace.user.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    public UserInfo toUserInfo(User user) {
        return new UserInfo(user.getId(),user.getUsername(),user.getEmail());
    }

}
