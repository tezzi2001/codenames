package com.bondarenko.codenames.domain.entity;

import com.bondarenko.codenames.domain.model.common.TeamType;
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
public class Card {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer id;
    private String word;
    @ManyToOne @JoinColumn private Room room;
    @Enumerated(EnumType.STRING) private TeamType teamType;
    @ManyToOne @JoinColumn private Team pickedTeam;
}
