package com.fares7elsadek.syncspace.server.domain.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ServerMemberId implements Serializable {
    private String serverId;
    private String userId;
}
