package com.bondarenko.codenames.exception;

import com.bondarenko.codenames.domain.model.common.TeamType;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class TeamNotFoundException extends ResponseStatusException {
    public TeamNotFoundException(Integer teamId) {
        super(HttpStatus.BAD_REQUEST, "Team with ID " + teamId + " does not exists");
    }
    public TeamNotFoundException(TeamType teamType, Integer roomId) {
        super(HttpStatus.BAD_REQUEST, teamType.toString() + " Team with Room ID " + roomId + " does not exists");
    }
}
