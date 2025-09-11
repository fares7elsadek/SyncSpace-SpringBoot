package com.fares7elsadek.syncspace.channel.commands.deletechannel;

import com.fares7elsadek.syncspace.channel.repository.ChannelRepository;
import com.fares7elsadek.syncspace.channel.shared.DeleteChannelEvent;
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
public class DeleteChannelCommandHandler implements CommandHandler<DeleteChannelCommand,
        ApiResponse<String>> {

    private final UserValidationService userValidationService;
    private final ChannelRepository channelRepository;
    private final ServerAccessService serverAccessService;
    private final SpringEventPublisher springEventPublisher;
    @Override
    @Transactional
    public ApiResponse<String> handle(DeleteChannelCommand command) {

        var user = userValidationService.getCurrentUserInfo();

        if (!serverAccessService.isMember(command.serverId(), user.getId())) {
            throw new ServerExceptions("You are not a member of this server.");
        }

        if (!serverAccessService.hasRole(command.serverId(), user.getId(), ServerRoles.OWNER, ServerRoles.ADMIN)) {
            throw new ServerExceptions("Only Owners or Admins can delete channels.");
        }

        var channel = channelRepository.findById(command.channelId())
                .orElseThrow(() -> new ServerExceptions("Channel not found."));

        if (!channel.getServer().getId().equals(command.serverId())) {
            throw new ServerExceptions("Channel does not belong to this server.");
        }

        channelRepository.delete(channel);

        springEventPublisher.publish(new DeleteChannelEvent(channel.getId(), command.serverId()));

        return ApiResponse.success("Channel deleted successfully", channel.getId());

    }
}
