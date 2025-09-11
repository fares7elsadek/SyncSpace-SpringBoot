package com.fares7elsadek.syncspace.server.model;

import com.fares7elsadek.syncspace.shared.model.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(
        indexes = {
                @Index(name = "idx_server_invites_code",columnList = "code",unique = true)
        }
)
public class ServerInvites extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Server server;

    @Column(nullable = false,unique = true)
    private String code;

    private LocalDateTime expiresAt;
    private int maxUses;
    private int uses = 0;

    @Transient
    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }
}
