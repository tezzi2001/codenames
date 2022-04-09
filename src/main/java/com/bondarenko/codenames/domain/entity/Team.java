package com.bondarenko.codenames.domain.entity;

import com.bondarenko.codenames.domain.model.common.TeamType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Team {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer id;
    @Enumerated(EnumType.STRING) private TeamType teamType;
    @OneToMany(mappedBy = "team", fetch = FetchType.EAGER) private List<Player> players; //TODO: fix FetchType.EAGER
    private Integer cardsLeft;
//    private Boolean isWon;
//    private Boolean itsTurn;
    @ManyToOne @JoinColumn private Room room;
}
