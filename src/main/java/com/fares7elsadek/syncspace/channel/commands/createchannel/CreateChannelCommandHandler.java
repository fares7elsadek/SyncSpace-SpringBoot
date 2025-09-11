package com.fares7elsadek.syncspace.channel.commands.createchannel;

import com.fares7elsadek.syncspace.channel.model.Channel;
import com.fares7elsadek.syncspace.channel.model.dtos.ChannelDto;
import com.fares7elsadek.syncspace.channel.repository.ChannelRepository;
import com.fares7elsadek.syncspace.channel.shared.CreateChannelEvent;
import com.fares7elsadek.syncspace.server.api.ServerAccessService;
import com.fares7elsadek.syncspace.server.shared.ServerRoles;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandHandler;
import com.fares7elsadek.syncspace.shared.events.SpringEventPublisher;
import com.fares7elsadek.syncspace.shared.exceptions.ServerExceptions;
import com.fares7elsadek.syncspace.user.api.UserValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CreateChannelCommandHandler
        implements CommandHandler<CreateChannelCommand, ApiResponse<ChannelDto>>
{
    private final UserValidationService userValidationService;
    private final ChannelRepository channelRepository;
    private final ServerAccessService serverAccessService;
    private final SpringEventPublisher springEventPublisher;
    @Override
    @Transactional
    public ApiResponse<ChannelDto> handle(CreateChannelCommand command) {
        var user = userValidationService.getCurrentUserInfo();

        if (!serverAccessService.isMember(command.serverId(), user.getId())) {
            throw new ServerExceptions("You are not a member of this server.");
        }

        if (!serverAccessService.hasRole(command.serverId(), user.getId(), ServerRoles.OWNER, ServerRoles.ADMIN)) {
            throw new ServerExceptions("Only Owners or Admins can create channels.");
        }

        var channel = Channel.builder()
                .name(command.name())
                .description(command.description())
                .isPrivate(command.isPrivate())
                .isGroup(true)
                .server(serverAccessService.getServer(command.serverId()))
                .build();

        var savedChannel = channelRepository.save(channel);
        springEventPublisher.publish(new
                CreateChannelEvent(savedChannel.getId(),command.serverId()
                ,true,command.isPrivate()));

        return ApiResponse.success("Channel create successfully",new ChannelDto(
                savedChannel.getId(),savedChannel.getName(),savedChannel.getDescription(),savedChannel.isPrivate(),savedChannel.isGroup()
        ));
    }
}
