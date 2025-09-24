package com.fares7elsadek.syncspace.user.application.service;

import com.fares7elsadek.syncspace.channel.domain.model.Channel;
import com.fares7elsadek.syncspace.channel.domain.model.ChannelMembers;
import com.fares7elsadek.syncspace.shared.exceptions.UserNotFoundException;
import com.fares7elsadek.syncspace.user.shared.UserAccessService;
import com.fares7elsadek.syncspace.user.domain.model.Roles;
import com.fares7elsadek.syncspace.user.domain.model.User;
import com.fares7elsadek.syncspace.user.api.dto.UserDto;
import com.fares7elsadek.syncspace.user.infrastructure.repository.RolesRepository;
import com.fares7elsadek.syncspace.user.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserValidationServiceImpl implements UserAccessService {

    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final AvatarService avatarService;


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

    @Override
    public List<Channel> getCurrentUserChatChannels() {
        var user = getCurrentUserInfo();
        if (user == null || user.getUserChannelMembers() == null) {
            return List.of();
        }


        return user.getUserChannelMembers().stream()
                .map(ChannelMembers::getChannel)
                .filter(Objects::nonNull)
                .filter(channel -> !channel.isGroup())
                .sorted(Comparator.comparing(
                        Channel::getUpdatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ).reversed())
                .toList();
    }


    @Override
    public void saveUser(UserDto dto) {
        var user = User.builder()
                .id(dto.id())
                .email(dto.email())
                .firstName(dto.firstname())
                .lastName(dto.lastname())
                .username(dto.username())
                .avatarUrl(avatarService.generateAvatarUrl(dto.id()))
                .build();
        userRepository.save(user);
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }
}
