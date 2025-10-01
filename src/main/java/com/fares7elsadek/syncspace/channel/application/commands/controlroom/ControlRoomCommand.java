package com.fares7elsadek.syncspace.channel.application.commands.controlroom;

import com.fares7elsadek.syncspace.shared.cqrs.Command;
import jakarta.validation.constraints.*;

public record ControlRoomCommand(

        @NotBlank(message = "channelId is required")
        String channelId,

        @NotBlank(message = "action is required")
        @Pattern(regexp = "PLAY|PAUSE|SEEK|CHANGE_VIDEO", message = "action must be one of: PLAY, PAUSE, SEEK, CHANGE_VIDEO")
        String action,

        @PositiveOrZero(message = "timestamp must be zero or positive")
        Double timestamp,

        @NotBlank(message = "videoUrl is required")
        @Pattern(
                regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$",
                message = "videoUrl must be a valid URL"
        )
        String videoUrl,

        @NotBlank(message = "userId is required")
        String userId

) implements Command { }
