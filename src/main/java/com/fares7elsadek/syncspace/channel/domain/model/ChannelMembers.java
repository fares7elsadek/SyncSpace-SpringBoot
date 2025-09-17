package com.fares7elsadek.syncspace.channel.domain.model;

import com.fares7elsadek.syncspace.shared.model.Auditable;
import com.fares7elsadek.syncspace.user.domain.model.User;
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
public class ChannelMembers extends Auditable {
    @EmbeddedId
    private ChannelUserId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("channelId")
    private Channel channel;

    private LocalDateTime joinedDate;

    private String lastReadMessageId;
}
