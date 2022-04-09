package com.bondarenko.codenames.service;

import com.bondarenko.codenames.domain.model.common.PlayerType;
import com.bondarenko.codenames.domain.model.common.TeamType;
import com.bondarenko.codenames.domain.entity.Room;
import com.bondarenko.codenames.domain.entity.Player;
import com.bondarenko.codenames.domain.entity.Team;
import com.bondarenko.codenames.domain.model.websocket.response.PlayerMovedResponse;
import com.bondarenko.codenames.exception.PlayerNotFoundException;
import com.bondarenko.codenames.exception.TeamNotFoundException;
import com.bondarenko.codenames.repository.RoomRepository;
import com.bondarenko.codenames.repository.PlayerRepository;
import com.bondarenko.codenames.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PreGameService {
    private final RoomRepository roomRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    public Player createPlayer(String name) {
        Player player = Player.builder()
                .name(name)
                .playerType(PlayerType.NONE)
                .build();
        return this.playerRepository.save(player);
    }

    public Player getPlayer(Integer id) {
        return this.playerRepository.findById(id).orElseThrow(() -> new PlayerNotFoundException(id));
    }

    public Room createRoom(Integer playerId) {
        Room room = this.roomRepository.save(new Room());

        Team teamRed = Team.builder()
                .teamType(TeamType.RED)
                .room(room)
                .build();
        Team teamBlue = Team.builder()
                .teamType(TeamType.BLUE)
                .room(room)
                .build();
        Team teamSpectator = Team.builder()
                .teamType(TeamType.SPECTATOR)
                .room(room)
                .build();
        this.teamRepository.save(teamRed);
        this.teamRepository.save(teamBlue);
        this.teamRepository.save(teamSpectator);

        Player player = this.playerRepository.findById(playerId).orElseThrow(() -> new PlayerNotFoundException(playerId));
        player.setTeam(teamSpectator);
        player.setRoom(room);
        this.playerRepository.save(player);

        return room;
    }

    public void joinRoom(Integer playerId, Integer roomId) {
        Team teamSpectator = this.teamRepository.findByTeamTypeAndRoomId(TeamType.SPECTATOR, roomId).orElseThrow(() -> new TeamNotFoundException(TeamType.SPECTATOR, roomId));

        Player player = this.playerRepository.findById(playerId).orElseThrow(() -> new PlayerNotFoundException(playerId));
        player.setRoom(Room.builder().id(roomId).build());
        player.setTeam(teamSpectator);
        this.playerRepository.save(player);
    }

    public PlayerMovedResponse setWebSocketSessionId(Integer playerId, String webSocketSessionId) {
        Player player = playerRepository.findById(playerId).orElseThrow(() -> new PlayerNotFoundException(playerId));
        player.setPlayerType(PlayerType.DEFAULT);
        player.setWebSocketSessionId(webSocketSessionId);
        playerRepository.save(player);

        return new PlayerMovedResponse(teamRepository.findAllByRoomId(player.getRoom().getId()));
    }
}
