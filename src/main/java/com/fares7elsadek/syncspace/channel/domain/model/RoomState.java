package com.fares7elsadek.syncspace.channel.domain.model;

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
public class RoomState extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne(mappedBy = "roomState")
    private Channel channel;

    private String videoUrl;
    @Column(name = "video_position")
    private Double currentTimestamp;
    private Boolean isPlaying;
    private LocalDateTime lastUpdatedAt;
    private Double playbackRate;
}
