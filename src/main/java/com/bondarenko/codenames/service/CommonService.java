package com.bondarenko.codenames.service;

import com.bondarenko.codenames.domain.entity.Card;
import com.bondarenko.codenames.domain.entity.Player;
import com.bondarenko.codenames.domain.entity.Room;
import com.bondarenko.codenames.domain.entity.Team;
import com.bondarenko.codenames.domain.model.common.PlayerType;
import com.bondarenko.codenames.domain.model.common.TeamType;
import com.bondarenko.codenames.domain.model.websocket.Response;
import com.bondarenko.codenames.exception.PlayerNotFoundException;
import com.bondarenko.codenames.exception.RoomNotFoundException;
import com.bondarenko.codenames.exception.TeamNotFoundException;
import com.bondarenko.codenames.exception.websocket.WebSocketException;
import com.bondarenko.codenames.repository.CardRepository;
import com.bondarenko.codenames.repository.PlayerRepository;
import com.bondarenko.codenames.repository.RoomRepository;
import com.bondarenko.codenames.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommonService {
    private final CountdownService countdownService;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final CardRepository cardRepository;
    private final RoomRepository roomRepository;
    private final List<String> words;
    private final static Random RANDOMIZER = new Random();

    public Response changePlayer(Integer playerId, PlayerType playerType, TeamType teamType) {
        Player player = playerRepository.findById(playerId).orElseThrow(() -> new PlayerNotFoundException(playerId));
        Room room = this.roomRepository.findById(player.getRoom().getId()).orElseThrow(() -> new RoomNotFoundException(player.getRoom().getId()));
        Team newTeam = teamRepository.findByTeamTypeAndRoomId(teamType, room.getId()).orElseThrow(() -> new TeamNotFoundException(teamType, room.getId()));
        player.setTeam(newTeam);
        if (PlayerType.MASTER.equals(playerType)) {
            Optional<Player> master = playerRepository.findByRoomIdAndPlayerType(room.getId(), playerType);
            if (master.isPresent()) {
                throw new WebSocketException("In room with ID " + room.getId() + " master has already set");
            }
            if (newTeam.isSpectator()) {
                throw new WebSocketException("Cannot set spectator as master");
            }
        }
        player.setPlayerType(playerType);
        playerRepository.save(player);

        Response response = new Response(Response.Action.CHANGE_PLAYER, room, teamRepository.findAllByRoomId(player.getRoom().getId()));
        response.setSecondsLeft(this.countdownService.getDelay(room.getId()));
        return response;
    }

    @Transactional
    public Response leaveRoom(String webSocketSessionId) {
        Player player = playerRepository.findByWebSocketSessionId(webSocketSessionId).orElseThrow(() -> new PlayerNotFoundException(webSocketSessionId));
        Room room = this.roomRepository.findById(player.getRoom().getId()).orElseThrow(() -> new RoomNotFoundException(player.getRoom().getId()));
        player.setPlayerType(PlayerType.NONE);
        player.setRoom(null);
        player.setTeam(null);
        player.setWebSocketSessionId(null);
        playerRepository.save(player);

//        if (playerRepository.countByRoomId(roomId) == 0) {
//            roomRepository.deleteById(roomId);
//        }

        Response response = new Response(Response.Action.LEAVE_ROOM, room, teamRepository.findAllByRoomId(room.getId()));
        response.setSecondsLeft(this.countdownService.getDelay(room.getId()));
        return response;
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

    @Transactional
    public Response startGame(Integer roomId, Integer playerId) {
        Room room = roomRepository.findByIdAndOwnerId(roomId, playerId).orElseThrow(() -> new RoomNotFoundException(roomId, playerId));
        for (Team team : teamRepository.findAllByRoomId(roomId)) {
            switch (team.getTeamType()) {
                case FIRST: {
                    team.setCardsLeft(9);
                    team.setHasTurn(true);
                    team.setIsWon(false);
                    team.setIsLost(false);
                    teamRepository.save(team);
                    break;
                }
                case SECOND: {
                    team.setCardsLeft(8);
                    team.setHasTurn(false);
                    team.setIsWon(false);
                    team.setIsLost(false);
                    teamRepository.save(team);
                    break;
                }
            }
        }
        CardBuilder cardBuilder = new CardBuilder();
        List<Card> cards = generateWords(25).stream()
                .map(word -> cardRepository.save(cardBuilder.build(room, word)))
                .collect(Collectors.toList());

        long secondsLeft = this.countdownService.setTimer(roomId, CountdownService.FIRST_DELAY);
        Response response = new Response(Response.Action.START_GAME, room, teamRepository.findAllByRoomId(roomId), cards);
        response.setSecondsLeft(secondsLeft);
        return response;
    }

    private Set<String> generateWords(int wordsQuantity) {
        Set<String> selectedWords = new HashSet<>(wordsQuantity);
        while (selectedWords.size() < wordsQuantity) {
            selectedWords.add(words.get(RANDOMIZER.nextInt(words.size())));
        }
        return selectedWords;
    }

    private static class CardBuilder {
        private final Map<TeamType, Integer> typesMaps = new HashMap<>();

        private CardBuilder() {
            typesMaps.put(TeamType.FIRST, 9);
            typesMaps.put(TeamType.SECOND, 8);
            typesMaps.put(TeamType.NEUTRAL, 7);
            typesMaps.put(TeamType.LOST, 1);
            typesMaps.put(TeamType.SPECTATOR, 0);
        }

        //TODO: optimize(card picking)
        private Card build(Room room, String value) {
            TeamType type;
            Integer quantity;
            do {
                int index = RANDOMIZER.nextInt(TeamType.values().length);
                type = TeamType.values()[index];
                quantity = typesMaps.get(type);
            } while (quantity == 0);

            typesMaps.put(type, --quantity);
            return Card.builder()
                    .room(room)
                    .word(value)
                    .teamType(type)
                    .build();
        }
    }
}
