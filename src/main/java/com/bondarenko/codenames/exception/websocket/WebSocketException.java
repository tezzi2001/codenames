package com.bondarenko.codenames.exception.websocket;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class WebSocketException extends ResponseStatusException {
    public WebSocketException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
