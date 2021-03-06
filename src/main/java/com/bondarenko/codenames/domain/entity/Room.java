package com.bondarenko.codenames.domain.entity;

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
public class Room {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer id;
    @OneToMany(mappedBy = "room", fetch = FetchType.EAGER) private List<Player> players; //TODO: fix FetchType.EAGER
    @OneToOne @JoinColumn private Player owner;
    @OneToMany(mappedBy = "room", cascade = CascadeType.REMOVE) private List<Card> cards;
    @OneToMany(mappedBy = "room", cascade = CascadeType.REMOVE) private List<Team> teams;
}
