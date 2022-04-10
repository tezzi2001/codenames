package com.bondarenko.codenames.repository;

import com.bondarenko.codenames.domain.entity.Player;
import com.bondarenko.codenames.domain.model.common.PlayerType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends CrudRepository<Player, Integer> {
    Optional<Player> findByWebSocketSessionId(String webSocketSessionId);
    Optional<Player> findByRoomIdAndPlayerType(Integer roomId, PlayerType playerType);
    int countByRoomId(Integer roomId);
}
