package com.fares7elsadek.syncspace.user.service;

import com.fares7elsadek.syncspace.shared.exceptions.UserNotFoundException;
import com.fares7elsadek.syncspace.user.Mapper.UserMapper;
import com.fares7elsadek.syncspace.user.api.UserValidationService;
import com.fares7elsadek.syncspace.user.model.User;
import com.fares7elsadek.syncspace.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserValidationServiceImpl implements UserValidationService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
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
}
