package com.bondarenko.codenames.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RoomNotFoundException extends ResponseStatusException {
    public RoomNotFoundException(Integer roomId, Integer ownerId) {
        super(HttpStatus.BAD_REQUEST, "Room with ID " + roomId + " and Owner " + ownerId + " does not exists");
    }
}
