package com.fares7elsadek.syncspace.channel.domain.model;

import com.fares7elsadek.syncspace.user.domain.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomViewer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    private RoomState roomState;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private LocalDateTime connectedAt;
}
