package com.fares7elsadek.syncspace.channel.domain.model;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ChannelUserId {
    private String channelId;
    private String userId;
}
