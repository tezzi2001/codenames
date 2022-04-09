package com.bondarenko.codenames.domain.model.rest;

import com.bondarenko.codenames.domain.entity.Room;
import com.bondarenko.codenames.domain.entity.Player;
import lombok.Data;

@Data
public class Response {
    private Integer playerId;
    private String playerName;
    private Integer roomId;

    public Response(Player player) {
        this.playerId = player.getId();
        this.playerName = player.getName();
    }

    public Response(Room room) {
        this.roomId = room.getId();
    }
}
