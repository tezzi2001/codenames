package com.bondarenko.codenames.domain.model.websocket;

import com.bondarenko.codenames.domain.entity.Card;
import com.bondarenko.codenames.domain.entity.Player;
import com.bondarenko.codenames.domain.entity.Team;
import com.bondarenko.codenames.domain.model.common.TeamType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class Response {
    private final Action action;
    private final Payload payload;

    public Response(Action action, List<Team> teams, List<Card> cards) {
        this.action = action;
        this.payload = new Payload(teams, cards);
    }

    public Response(Action action, List<Team> teams) {
        this.action = action;
        this.payload = new Payload(teams);
    }

    public enum Action {
        INIT,
        CHANGE_TEAM,
        CHANGE_PLAYER,
        START_GAME,
        LEAVE_ROOM
    }

    @Getter
    public static class Payload {
        private Payload(List<Team> teams, List<Card> cards) {
            this(teams);
            this.cards.addAll(cards.stream()
                    .map(CardPayload::new)
                    .collect(Collectors.toList())
            );
        }

        private Payload(List<Team> teams) {
            final Set<TeamType> foundTypes = new HashSet<>();
            this.teams = teams.stream()
                    .filter(team -> !foundTypes.contains(team.getTeamType()))
                    .peek(team -> foundTypes.add(team.getTeamType()))
                    .map(Response::buildTeamPayload)
                    .collect(Collectors.toList());
            this.cards = new ArrayList<>();
        }

        private final List<Response.TeamPayload> teams;
        private final List<Response.CardPayload> cards;
    }

    private static Response.TeamPayload buildTeamPayload(Team team) {
        Response.PlayerPayload master = null;
        List<Response.PlayerPayload> defaultPlayers = new ArrayList<>();
        for(Player player : team.getPlayers()) {
            switch (player.getPlayerType()) {
                case MASTER: {
                    master = new Response.PlayerPayload(player.getId(), player.getName());
                    break;
                }
                case NONE:
                case DEFAULT: {
                    defaultPlayers.add(new Response.PlayerPayload(player.getId(), player.getName()));
                    break;
                }
            }
        }
        return new Response.TeamPayload(team.getTeamType(), master, defaultPlayers);
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TeamPayload {
        private final TeamType teamType;
        private final Response.PlayerPayload master;
        private final List<Response.PlayerPayload> players;
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PlayerPayload {
        private final Integer playerId;
        private final String playerName;
    }

    @Getter
    public static class CardPayload {
        private CardPayload(Card card) {
            this.id = card.getId();
            this.value = card.getWord();
            this.type = card.getTeamType();
        }

        private final Integer id;
        private final String value;
        private final TeamType type;
    }
}
