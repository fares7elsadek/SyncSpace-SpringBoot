package com.fares7elsadek.syncspace.channel.api.controller;

import com.fares7elsadek.syncspace.channel.api.dtos.ChannelChatDto;
import com.fares7elsadek.syncspace.channel.api.dtos.ChannelDto;
import com.fares7elsadek.syncspace.channel.api.dtos.RoomStateDto;
import com.fares7elsadek.syncspace.channel.application.commands.addmember.AddMemberCommand;
import com.fares7elsadek.syncspace.channel.application.commands.addmember.AddMemberResponse;
import com.fares7elsadek.syncspace.channel.application.commands.controlroom.ControlRoomCommand;
import com.fares7elsadek.syncspace.channel.application.commands.createchannel.CreateChannelCommand;
import com.fares7elsadek.syncspace.channel.application.commands.deletechannel.DeleteChannelCommand;
import com.fares7elsadek.syncspace.channel.application.commands.removemember.RemoveMemberCommand;
import com.fares7elsadek.syncspace.channel.application.queries.getchannel.GetChannelQuery;
import com.fares7elsadek.syncspace.channel.application.queries.getroom.GetRoomQuery;
import com.fares7elsadek.syncspace.channel.application.queries.listchannels.ListServerChannelsQuery;
import com.fares7elsadek.syncspace.channel.application.queries.listchat.ListChatsQuery;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandBus;
import com.fares7elsadek.syncspace.shared.cqrs.QueryBus;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/channels")
@RequiredArgsConstructor
@Tag(name = "Channel")
public class ChannelController {
    private final CommandBus commandBus;
    private final QueryBus queryBus;

    @PostMapping("/{channelId}/members/{memberId}")
    public ResponseEntity<ApiResponse<AddMemberResponse>> addMember(
            @PathVariable
            @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "Channel ID must be a valid UUID")
            String channelId,

            @PathVariable
            @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "User ID must be a valid UUID")
            String memberId,

            @NotBlank(message = "Server ID cannot be blank")
            @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "Server ID must be a valid UUID")
            @RequestBody String serverId
    ) {
        return ResponseEntity.ok(commandBus.send(new AddMemberCommand(channelId, serverId,memberId)));
    }

    @PostMapping("/new")
    public ResponseEntity<ApiResponse<ChannelDto>> createChannel(@RequestBody @Valid CreateChannelCommand command) {
        return ResponseEntity.ok(commandBus.send(command));
    }

    @DeleteMapping("/{channelId}/server/{serverId}")
    public ResponseEntity<ApiResponse<String>> deleteChannel(
            @NotBlank(message = "Channel ID cannot be blank")
            @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "Channel ID must be a valid UUID")
            @PathVariable String channelId,
            @NotBlank(message = "Server ID cannot be blank")
            @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "Server ID must be a valid UUID")
            @PathVariable String serverId
    ) {
        return ResponseEntity.ok(commandBus.send(new DeleteChannelCommand(channelId, serverId)));
    }

    @DeleteMapping("/{channelId}/members/{memberId}")
    public ResponseEntity<ApiResponse<String>> removeMember(
            @PathVariable
            @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "Channel ID must be a valid UUID")
            String channelId,

            @PathVariable
            @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "User ID must be a valid UUID")
            String memberId,

            @NotBlank(message = "Server ID cannot be blank")
            @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "Server ID must be a valid UUID")
            @RequestBody String serverId
    ) {
        return ResponseEntity.ok(commandBus.send(new RemoveMemberCommand(channelId, serverId,memberId)));
    }

    @GetMapping("/{channelId}")
    public ResponseEntity<ApiResponse<ChannelDto>> getChannel(
            @PathVariable
            @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "Channel ID must be a valid UUID")
            String channelId
    ){
        return ResponseEntity.ok(queryBus.send(new GetChannelQuery(channelId)));
    }

    @GetMapping("/server/{serverId}")
    public ResponseEntity<ApiResponse<List<ChannelDto>>> listServerChannels(
            @NotBlank(message = "Server ID cannot be blank")
            @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "Server ID must be a valid UUID")
            @PathVariable String serverId
    ){
        return ResponseEntity.ok(queryBus.send(new ListServerChannelsQuery(serverId)));
    }

    @GetMapping("/user/chats")
    public ResponseEntity<ApiResponse<List<ChannelChatDto>>> listUserChats(){
        return ResponseEntity.ok(queryBus.send(new ListChatsQuery()));
    }

    @GetMapping("/room/{channelId}")
    public ResponseEntity<ApiResponse<RoomStateDto>> getRoomState(
            @PathVariable
            @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "Channel ID must be a valid UUID")
            String channelId
    ){
        return ResponseEntity.ok(queryBus.send(new GetRoomQuery(channelId)));
    }

    @PostMapping("/room/control")
    public ResponseEntity<ApiResponse<Void>> updateRoomState(@RequestBody @Valid ControlRoomCommand command){
        return ResponseEntity.ok(commandBus.send(command));
    }

}
