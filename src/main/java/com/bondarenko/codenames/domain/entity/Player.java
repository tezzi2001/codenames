package com.bondarenko.codenames.domain.entity;

import com.bondarenko.codenames.domain.model.common.PlayerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer id;
    @Column(unique=true) private String webSocketSessionId;
    private String name;
    @ManyToOne @JoinColumn private Card pickedCard;
    @Enumerated(EnumType.STRING) private PlayerType playerType = PlayerType.NONE;
    @ManyToOne @JoinColumn private Team team;
    @ManyToOne @JoinColumn private Room room;
}
