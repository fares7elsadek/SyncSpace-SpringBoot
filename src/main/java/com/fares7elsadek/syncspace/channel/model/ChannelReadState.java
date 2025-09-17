package com.fares7elsadek.syncspace.channel.model;

import com.fares7elsadek.syncspace.messaging.model.Message;
import com.fares7elsadek.syncspace.shared.model.Auditable;
import com.fares7elsadek.syncspace.user.model.User;
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
public class ChannelReadState extends Auditable {
    @EmbeddedId
    private ChannelUserId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @MapsId("channelId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Channel channel;

    @ManyToOne(fetch = FetchType.LAZY)
    private Message lastReadMessage;

    private LocalDateTime lastReadAt;
}
