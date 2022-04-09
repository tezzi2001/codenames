package com.bondarenko.codenames.service;

import com.bondarenko.codenames.domain.entity.Player;
import com.bondarenko.codenames.domain.entity.Team;
import com.bondarenko.codenames.domain.model.common.PlayerType;
import com.bondarenko.codenames.domain.model.common.TeamType;
import com.bondarenko.codenames.domain.model.websocket.response.PlayerMovedResponse;
import com.bondarenko.codenames.exception.PlayerNotFoundException;
import com.bondarenko.codenames.exception.TeamNotFoundException;
import com.bondarenko.codenames.exception.websocket.WebSocketException;
import com.bondarenko.codenames.repository.PlayerRepository;
import com.bondarenko.codenames.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommonService {
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    public PlayerMovedResponse changeTeam(Integer playerId, TeamType teamType) {
        Player player = playerRepository.findById(playerId).orElseThrow(() -> new PlayerNotFoundException(playerId));
        Integer roomId = player.getRoom().getId();
        Team newTeam = teamRepository.findByTeamTypeAndRoomId(teamType, roomId).orElseThrow(() -> new TeamNotFoundException(teamType, roomId));
        player.setTeam(newTeam);
        playerRepository.save(player);

        return new PlayerMovedResponse(teamRepository.findAllByRoomId(roomId));
    }

    public PlayerMovedResponse changePlayer(Integer playerId, PlayerType playerType) {
        Player player = playerRepository.findById(playerId).orElseThrow(() -> new PlayerNotFoundException(playerId));
        Integer roomId = player.getRoom().getId();
        if (PlayerType.MASTER.equals(playerType)) {
            Optional<Player> master = playerRepository.findByRoomIdAndPlayerType(roomId, playerType);
            if (master.isPresent()) {
                throw new WebSocketException("In room with ID " + roomId + " master has already set");
            }
        }
        player.setPlayerType(playerType);
        playerRepository.save(player);

        return new PlayerMovedResponse(teamRepository.findAllByRoomId(player.getRoom().getId()));
    }

    public PlayerMovedResponse leaveRoom(String webSocketSessionId) {
        Player player = playerRepository.findByWebSocketSessionId(webSocketSessionId).orElseThrow(() -> new PlayerNotFoundException(webSocketSessionId));
        Integer roomId = player.getRoom().getId();
        player.setPlayerType(PlayerType.NONE);
        player.setRoom(null);
        player.setTeam(null);
        player.setWebSocketSessionId(null);
        playerRepository.save(player);

        return new PlayerMovedResponse(teamRepository.findAllByRoomId(roomId));
    }

    public Set<String> findRoommateWebSocketSessionIds(String webSocketSessionId) {
        return this.playerRepository.findByWebSocketSessionId(webSocketSessionId).orElseThrow(() -> new PlayerNotFoundException(webSocketSessionId))
                .getRoom()
                .getPlayers().stream()
                .map(Player::getWebSocketSessionId)
                .collect(Collectors.toSet());
    }

    public Set<String> findRoommateWebSocketSessionIds(Integer playerId) {
        return this.playerRepository.findById(playerId).orElseThrow(() -> new PlayerNotFoundException(playerId))
                .getRoom()
                .getPlayers().stream()
                .map(Player::getWebSocketSessionId)
                .collect(Collectors.toSet());
    }
}
