package com.fares7elsadek.syncspace.user.service;

import com.fares7elsadek.syncspace.shared.exceptions.UserNotFoundException;
import com.fares7elsadek.syncspace.user.api.UserValidationService;
import com.fares7elsadek.syncspace.user.model.Roles;
import com.fares7elsadek.syncspace.user.model.User;
import com.fares7elsadek.syncspace.user.repository.RolesRepository;
import com.fares7elsadek.syncspace.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserValidationServiceImpl implements UserValidationService {
    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;


    @Override
    public User getUserInfo(String userId) {
        var user =  userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return user;
    }

    @Override
    public User getCurrentUserInfo() {
        var userId = SecurityContextHolder.getContext().getAuthentication().getName();
        var user  = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return user;
    }

    @Override
    public Roles getRoleByName(String name) {
        return rolesRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Role Not Found"));
    }
}
