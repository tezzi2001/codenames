package com.bondarenko.codenames.controller;

import com.bondarenko.codenames.domain.entity.Room;
import com.bondarenko.codenames.domain.entity.Player;
import com.bondarenko.codenames.domain.model.rest.Request;
import com.bondarenko.codenames.domain.model.rest.Response;
import com.bondarenko.codenames.service.PreGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RestController {
    private final PreGameService preGameService;

    @PostMapping("/player")
    public Response createPlayer(@RequestBody Request request) {
        if (request.getPlayerName() == null || request.getPlayerName().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid parameter(s)");
        }
        Player player = preGameService.createPlayer(request.getPlayerName());
        return new Response(player);
    }

    @GetMapping("/player/{id}")
    public Response getPlayer(@PathVariable("id") Integer playerId) {
        if (playerId == null || playerId < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid parameter(s)");
        }
        Player player = preGameService.getPlayer(playerId);
        return new Response(player);
    }

    @PostMapping("/room")
    public Response createRoom(@RequestBody Request request) {
        if (request.getPlayerId() == null || request.getPlayerId() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid parameter(s)");
        }
        Room room = preGameService.createRoom(request.getPlayerId());
        return new Response(room);
    }

    @PutMapping("/room/{id}")
    public void joinRoom(@RequestBody Request request, @PathVariable("id") Integer roomId) {
        if (
            request.getPlayerId() == null || request.getPlayerId() < 1 ||
            roomId == null || roomId < 1
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid parameter(s)");
        }
        preGameService.joinRoom(request.getPlayerId(), roomId);
    }
}
