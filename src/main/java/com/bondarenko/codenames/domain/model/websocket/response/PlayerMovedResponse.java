package com.bondarenko.codenames.domain.model.websocket.response;

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

public class PlayerMovedResponse extends Response<PlayerMovedResponse.Payload> {
    public PlayerMovedResponse(List<Team> teams) {
        super(Action.PLAYER_MOVED, new Payload(teams));
    }

    @Getter
    public static class Payload {
        private Payload(List<Team> teams) {
            final Set<TeamType> foundTypes = new HashSet<>();
            this.teams = teams.stream()
                    .filter(team -> !foundTypes.contains(team.getTeamType()))
                    .peek(team -> foundTypes.add(team.getTeamType()))
                    .map(PlayerMovedResponse::buildTeamPayload)
                    .collect(Collectors.toList());
        }

        private final List<TeamPayload> teams;
    }

    private static TeamPayload buildTeamPayload(Team team) {
        PlayerPayload master = null;
        List<PlayerPayload> defaultPlayers = new ArrayList<>();
        for(Player player : team.getPlayers()) {
            switch (player.getPlayerType()) {
                case MASTER: {
                    master = new PlayerPayload(player.getId(), player.getName());
                    break;
                }
                case NONE:
                case DEFAULT: {
                    defaultPlayers.add(new PlayerPayload(player.getId(), player.getName()));
                    break;
                }
            }
        }
        return new TeamPayload(team.getTeamType(), master, defaultPlayers);
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TeamPayload {
        private final TeamType teamType;
        private final PlayerPayload master;
        private final List<PlayerPayload> players;
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PlayerPayload {
        private final Integer playerId;
        private final String playerName;
    }
}
