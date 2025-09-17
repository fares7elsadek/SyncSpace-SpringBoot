package com.fares7elsadek.syncspace.channel.application.commands.deletechannel;

import com.fares7elsadek.syncspace.channel.domain.events.DeleteChannelEvent;
import com.fares7elsadek.syncspace.channel.domain.model.Channel;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.ChannelRepository;
import com.fares7elsadek.syncspace.server.api.ServerAccessService;
import com.fares7elsadek.syncspace.server.shared.ServerRoles;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandHandler;
import com.fares7elsadek.syncspace.shared.events.SpringEventPublisher;
import com.fares7elsadek.syncspace.shared.exceptions.InsufficientPermissionsException;
import com.fares7elsadek.syncspace.shared.exceptions.ServerExceptions;
import com.fares7elsadek.syncspace.shared.exceptions.UnauthorizedException;
import com.fares7elsadek.syncspace.user.api.UserAccessService;
import com.fares7elsadek.syncspace.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteChannelCommandHandler implements CommandHandler<DeleteChannelCommand,
        ApiResponse<String>> {

    private final UserAccessService userAccessService;
    private final ChannelRepository channelRepository;
    private final ServerAccessService serverAccessService;
    private final SpringEventPublisher springEventPublisher;

    @Override
    @Transactional
    public ApiResponse<String> handle(DeleteChannelCommand command) {

        var user = userAccessService.getCurrentUserInfo();

        validateUserPermissions(command.serverId(),user);

        var channel = validateChannelExistence(command);
        channelRepository.delete(channel);

        springEventPublisher
                .publish(DeleteChannelEvent
                        .toEvent(channel.getId()
                                , command.serverId(),user.getId()));

        return ApiResponse.success("Channel deleted successfully", channel.getId());
    }

    private void validateUserPermissions(String serverId, User user){
        log.debug("Validating user permissions for user '{}' in server '{}'",
                user.getId(), serverId);

        if (!serverAccessService.isMember(serverId, user.getId())) {
            throw new UnauthorizedException("User is not a member of server: " + serverId);
        }

        if (!serverAccessService.hasRole(serverId, user.getId(),
                ServerRoles.OWNER, ServerRoles.ADMIN)) {
            throw new InsufficientPermissionsException(
                    "Only server owners or administrators can delete channels.");
        }
    }

    private Channel validateChannelExistence(DeleteChannelCommand command){
        var channel = channelRepository.findById(command.channelId())
                .orElseThrow(() -> new ServerExceptions("Channel not found."));

        if (!channel.getServer().getId().equals(command.serverId())) {
            throw new ServerExceptions("Channel does not belong to this server.");
        }
        return channel;
    }


}
