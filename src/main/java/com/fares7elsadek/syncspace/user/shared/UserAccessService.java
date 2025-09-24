package com.fares7elsadek.syncspace.user.shared;

import com.fares7elsadek.syncspace.channel.domain.model.Channel;
import com.fares7elsadek.syncspace.user.domain.model.Roles;
import com.fares7elsadek.syncspace.user.domain.model.User;
import com.fares7elsadek.syncspace.user.api.dto.UserDto;

import java.util.List;

public interface UserAccessService {
    User getUserInfo(String userId);
    User getCurrentUserInfo();
    Roles getRoleByName(String name);
    List<Channel> getCurrentUserChatChannels();
    void saveUser(UserDto userDto);
    User getByUsername(String username);
}
