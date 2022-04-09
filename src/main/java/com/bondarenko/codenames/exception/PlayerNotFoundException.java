package com.bondarenko.codenames.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PlayerNotFoundException extends ResponseStatusException {
    public PlayerNotFoundException(Integer playerId) {
        super(HttpStatus.BAD_REQUEST, "Player with ID " + playerId + " does not exists");
    }

    public PlayerNotFoundException(String webSocketSessionId) {
        super(HttpStatus.BAD_REQUEST, "Player with Session ID " + webSocketSessionId + " does not exists");
    }
}
