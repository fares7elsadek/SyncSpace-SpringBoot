package com.fares7elsadek.syncspace.server.model;

import com.fares7elsadek.syncspace.user.model.Roles;
import com.fares7elsadek.syncspace.user.model.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "server_members")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServerMember {
    @EmbeddedId
    private ServerMemberId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("serverId")
    private Server server;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;

    private String nickname;
    private boolean isBanned = false;
    private String banReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Roles role;
}
