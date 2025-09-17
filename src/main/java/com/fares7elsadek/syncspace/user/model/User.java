package com.fares7elsadek.syncspace.user.model;


import com.fares7elsadek.syncspace.channel.domain.model.ChannelMembers;
import com.fares7elsadek.syncspace.channel.domain.model.ChannelReadState;
import com.fares7elsadek.syncspace.friendship.domain.model.Friendships;
import com.fares7elsadek.syncspace.messaging.model.Message;
import com.fares7elsadek.syncspace.messaging.model.MessageReactions;
import com.fares7elsadek.syncspace.notification.model.Notifications;
import com.fares7elsadek.syncspace.server.model.ServerMember;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter @Setter @AllArgsConstructor @Builder @NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String username;

    private String firstName;
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedAt;

    @CreatedDate
    @Column(insertable = false)
    private LocalDateTime createdAt;

    private LocalDateTime lastSeen;
    private boolean isOnline = false;

    private LocalDateTime lastMessageAt;
    private Long totalMessages;
    private Long serversJoined;
    private Long friendsCount;
    private String avatarUrl;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServerMember> serverMemberships = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChannelMembers> userChannelMembers = new ArrayList<>();

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MessageReactions> messageReactions = new ArrayList<>();

    @OneToMany(mappedBy = "requester")
    private List<Friendships> sentFriendRequests = new ArrayList<>();

    @OneToMany(mappedBy = "addressee")
    private List<Friendships> receivedFriendRequests = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notifications> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChannelReadState> channelReadStates = new ArrayList<>();

    @Transient
    public String displayName(){
        return firstName + " " + lastName;
    }

}
