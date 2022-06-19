package com.bondarenko.codenames.domain.model.websocket;

import com.bondarenko.codenames.domain.model.common.PlayerType;
import com.bondarenko.codenames.domain.model.common.TeamType;
import com.bondarenko.codenames.exception.websocket.WebSocketException;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Request {
    private Action action;
    private Payload payload;

    public enum Action {
        INIT,
        CHANGE_TEAM,
        CHANGE_PLAYER,
        START_GAME,
        MAX_PIDOR
    }

    @Data
    @NoArgsConstructor
    public static class Payload {
        private Integer playerId;
        private Integer roomId;
        private TeamType teamType;
        private PlayerType playerType;
    }

    public void validate() {
        if (action == null || payload == null) {
            throw new WebSocketException("Wrong data received");
        }

        switch (action) {
            case INIT: {
                if (payload.getPlayerId() == null || payload.getPlayerId() < 1) {
                    throw new WebSocketException("Wrong data received");
                }
                break;
            }
            case CHANGE_PLAYER: {
                if (
                    payload.getPlayerId() == null || payload.getPlayerId() < 1 ||
                    payload.getPlayerType() == null ||
                    payload.getTeamType() == null || payload.getTeamType().equals(TeamType.NEUTRAL) || payload.getTeamType().equals(TeamType.LOST)
                ) {
                    throw new WebSocketException("Wrong data received");
                }
                break;
            }
            case START_GAME: {
                if (payload.getRoomId() == null || payload.getRoomId() < 1) {
                    throw new WebSocketException("Wrong data received");
                }
                break;
            }
        }

        if (payload.getPlayerType() == PlayerType.NONE) {
            throw new WebSocketException("Wrong data received");
        }

        if (payload.getTeamType() == TeamType.LOST || payload.getTeamType() == TeamType.NEUTRAL) {
            throw new WebSocketException("Wrong data received");
        }
    }
}
