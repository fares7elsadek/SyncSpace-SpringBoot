package com.fares7elsadek.syncspace.server.api.controller;


import com.fares7elsadek.syncspace.server.application.commands.addmember.AddServerMemberCommand;
import com.fares7elsadek.syncspace.server.application.commands.createserver.CreateServerCommand;
import com.fares7elsadek.syncspace.server.application.commands.deleteserver.DeleteServerCommand;
import com.fares7elsadek.syncspace.server.application.commands.generateinvite.GenerateInviteCodeCommand;
import com.fares7elsadek.syncspace.server.application.commands.invitejoin.InviteJoinCommand;
import com.fares7elsadek.syncspace.server.api.dtos.InviteCodeDto;
import com.fares7elsadek.syncspace.server.api.dtos.ServerDto;
import com.fares7elsadek.syncspace.server.api.dtos.ServerMemberDto;
import com.fares7elsadek.syncspace.server.application.queries.getmember.GetServerMember;
import com.fares7elsadek.syncspace.server.application.queries.getserver.GetServerQuery;
import com.fares7elsadek.syncspace.server.application.queries.getservers.GetServersQuery;
import com.fares7elsadek.syncspace.server.application.queries.listmembers.ListMembersQuery;
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
@RequestMapping("/server")
@RequiredArgsConstructor
@Tag(name = "Servers")
public class ServerController {
    private final CommandBus commandBus;
    private final QueryBus queryBus;


    @GetMapping
    public ResponseEntity<ApiResponse<List<ServerDto>>> getUserServers(){
        return ResponseEntity.ok(queryBus.send(new GetServersQuery()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createServer(
            @RequestBody @Valid CreateServerCommand command){
        return ResponseEntity.ok(commandBus.send(command));
    }

    @DeleteMapping("/{serverId}")
    public ResponseEntity<ApiResponse<String>> deleteServer(
            @PathVariable
            @NotBlank(message = "Server ID cannot be blank")
            @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "Server ID must be a valid UUID")
            String serverId
    ){
        return ResponseEntity.ok(commandBus.send(new DeleteServerCommand(serverId)));
    }

    @PostMapping("/{serverId}/invite")
    public ResponseEntity<ApiResponse<InviteCodeDto>> generateInvite(
            @PathVariable
            @NotBlank(message = "Server ID cannot be blank")
            @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "Server ID must be a valid UUID")
            String serverId
    ){
        return ResponseEntity.ok(commandBus.send(new GenerateInviteCodeCommand(serverId)));
    }

    @PostMapping("/{serverId}/join/{code}")
    public ResponseEntity<ApiResponse<String>> joinViaInvite(
            @PathVariable
            @NotBlank(message = "Server ID cannot be blank")
            @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "Server ID must be a valid UUID")
            String serverId
            ,
            @PathVariable
            @NotBlank(message = "Server invite code cannot be blank")
            String code
    ){
        return ResponseEntity.ok(commandBus.send(new InviteJoinCommand(serverId, code)));
    }

    @PostMapping("/{serverId}/member/{username}")
    public ResponseEntity<ApiResponse<String>> addMember(
            @PathVariable
            @NotBlank(message = "Server ID cannot be blank")
            @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "Server ID must be a valid UUID")
            String serverId
            ,
            @PathVariable
            @NotBlank(message = "Username can't be blank")
            String username
    ){
        return ResponseEntity.ok(commandBus.send(new AddServerMemberCommand(serverId, username)));
    }

    @GetMapping("/{serverId}")
    public ResponseEntity<ApiResponse<ServerDto>> getServer(
            @PathVariable
            @NotBlank(message = "Server ID cannot be blank")
            @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "Server ID must be a valid UUID")
            String serverId
    ){
        return ResponseEntity.ok(queryBus.send(new GetServerQuery(serverId)));
    }

    @GetMapping("/{serverId}/members")
    public ResponseEntity<ApiResponse<List<ServerMemberDto>>> getServerMembers(
            @PathVariable
            @NotBlank(message = "Server ID cannot be blank")
            @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "Server ID must be a valid UUID")
            String serverId
    ){
        return ResponseEntity.ok(queryBus.send(new ListMembersQuery(serverId)));
    }

    @GetMapping("/{serverId}/member")
    public ResponseEntity<ApiResponse<ServerMemberDto>> getServerMember(
            @PathVariable
            @NotBlank(message = "Server ID cannot be blank")
            @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "Server ID must be a valid UUID")
            String serverId
    ){
        return ResponseEntity.ok(queryBus.send(new GetServerMember(serverId)));
    }

}
