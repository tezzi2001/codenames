package com.bondarenko.codenames.domain.model.websocket;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class Error {
    private final List<String> errors;

    public Error(String error) {
        this.errors = new ArrayList<>();
        this.errors.add(error);
    }
}
