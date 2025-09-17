package com.fares7elsadek.syncspace.channel.model;

import com.fares7elsadek.syncspace.messaging.model.Message;
import com.fares7elsadek.syncspace.server.model.Server;
import com.fares7elsadek.syncspace.shared.model.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Channel extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Server server;
    private String name;
    private String description;
    private boolean isPrivate;
    private boolean isGroup = false;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChannelMembers> members;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChannelReadState>  channelReadStates;
}
