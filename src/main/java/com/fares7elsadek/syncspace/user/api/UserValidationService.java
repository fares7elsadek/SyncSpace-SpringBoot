package com.fares7elsadek.syncspace.user.api;

import com.fares7elsadek.syncspace.user.model.Roles;
import com.fares7elsadek.syncspace.user.model.User;

public interface UserValidationService {
    User getUserInfo(String userId);
    User getCurrentUserInfo();
    Roles getRoleByName(String name);
}
