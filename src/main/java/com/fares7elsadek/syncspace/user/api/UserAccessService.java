package com.fares7elsadek.syncspace.user.api;

import com.fares7elsadek.syncspace.channel.domain.model.Channel;
import com.fares7elsadek.syncspace.user.model.Roles;
import com.fares7elsadek.syncspace.user.model.User;
import com.fares7elsadek.syncspace.user.model.dto.UserDto;

import java.util.List;

public interface UserAccessService {
    User getUserInfo(String userId);
    User getCurrentUserInfo();
    Roles getRoleByName(String name);
    List<Channel> getCurrentUserChatChannels();
    void saveUser(UserDto userDto);
}
