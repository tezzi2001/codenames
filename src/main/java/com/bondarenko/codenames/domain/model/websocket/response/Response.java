package com.bondarenko.codenames.domain.model.websocket.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class Response<T> {
    private final Action action;
    private final T payload;

    protected enum Action {
        PLAYER_MOVED,
        ERROR
    }
}
