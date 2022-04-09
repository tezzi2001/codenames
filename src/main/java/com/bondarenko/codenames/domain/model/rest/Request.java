package com.bondarenko.codenames.domain.model.rest;

import lombok.Data;

@Data
public class Request {
    private Integer playerId;
    private String playerName;
}
