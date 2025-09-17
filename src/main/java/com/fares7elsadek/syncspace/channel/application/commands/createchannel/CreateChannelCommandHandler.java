package com.fares7elsadek.syncspace.channel.application.commands.createchannel;

import com.fares7elsadek.syncspace.channel.api.dtos.ChannelDto;
import com.fares7elsadek.syncspace.channel.application.mapper.ChannelMapper;
import com.fares7elsadek.syncspace.channel.domain.events.CreateChannelEvent;
import com.fares7elsadek.syncspace.channel.domain.model.Channel;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.ChannelRepository;
import com.fares7elsadek.syncspace.server.api.ServerAccessService;
import com.fares7elsadek.syncspace.server.shared.ServerRoles;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandHandler;
import com.fares7elsadek.syncspace.shared.events.SpringEventPublisher;
import com.fares7elsadek.syncspace.shared.exceptions.ConflictException;
import com.fares7elsadek.syncspace.shared.exceptions.InsufficientPermissionsException;
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
public class CreateChannelCommandHandler
        implements CommandHandler<CreateChannelCommand, ApiResponse<ChannelDto>>
{
    private final UserAccessService userAccessService;
    private final ChannelRepository channelRepository;
    private final ServerAccessService serverAccessService;
    private final SpringEventPublisher springEventPublisher;
    private final ChannelMapper channelMapper;
    private final String CHANNEL_PREFIX = "#";
    @Override
    @Transactional
    public ApiResponse<ChannelDto> handle(CreateChannelCommand command) {
        var user = userAccessService.getCurrentUserInfo();

        validateUserPermissions(command.serverId(),user);
        validateChannelCreation(command.serverId(),command.name());

        var channel = createChannel(command);
        var savedChannel = channelRepository.save(channel);

        log.info("Channel '{}' created successfully with ID '{}' in server '{}' by user '{}'",
                savedChannel.getName(), savedChannel.getId(),
                command.serverId(), user.getId());

        springEventPublisher
                .publishAsync(CreateChannelEvent
                        .toEvent(savedChannel));

        return ApiResponse.success("Channel create successfully"
                ,channelMapper.toChannelDto(savedChannel));
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
                    "Only server owners or administrators can create channels");
        }
    }

    private void validateChannelCreation(String serverId,String channelName){
        log.debug("Validating channel creation business rules");
        String fullChannelName = CHANNEL_PREFIX + channelName.trim();
        channelRepository.findByNameAndServerId(fullChannelName,serverId)
                .ifPresent(c -> {
                    throw new ConflictException("A channel with name '" + fullChannelName
                            + "' already exists in this server");
                });
    }

    private Channel createChannel(CreateChannelCommand command) {
        log.debug("Creating channel entity");

        return Channel.builder()
                .name(CHANNEL_PREFIX + command.name().trim())
                .description(command.description() != null ? command.description().trim() : null)
                .isPrivate(command.isPrivate())
                .isGroup(true)
                .server(serverAccessService.getServer(command.serverId()))
                .build();
    }


}
