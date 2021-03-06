package com.bondarenko.codenames.controller;

import com.bondarenko.codenames.domain.model.websocket.Request;
import com.bondarenko.codenames.domain.model.websocket.Response;
import com.bondarenko.codenames.domain.model.websocket.Error;
import com.bondarenko.codenames.exception.websocket.WebSocketException;
import com.bondarenko.codenames.service.CommonService;
import com.bondarenko.codenames.service.PreGameService;
import com.bondarenko.codenames.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class WebSocketController extends TextWebSocketHandler {
    private final Map<String, WebSocketSession> sessions = new HashMap<>();
    private final PreGameService preGameService;
    private final CommonService commonService;
    private final JsonUtil jsonUtil;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        super.afterConnectionClosed(session, status);
        Set<String> webSocketSessionIds = commonService.findRoommateWebSocketSessionIds(session.getId());
        Response response = commonService.leaveRoom(session.getId());
        notifyRoommates(response, webSocketSessionIds);
    }

    @Override
    @Transactional(noRollbackFor = WebSocketException.class)
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);

        Request request = jsonUtil.readValue(message.getPayload(), Request.class);
        request.validate();
        try {
            Response response = doAction(session, request);
            notifyRoommates(response, commonService.findRoommateWebSocketSessionIds(request.getPayload().getPlayerId()));
        } catch (WebSocketException e) {
            notifySelf(new Error(e.getReason()), session);
        }
    }

    private Response doAction(WebSocketSession session, Request request) {
        Request.Payload payload = request.getPayload();
        switch(request.getAction()) {
            case INIT: {
                return preGameService.setWebSocketSessionId(payload.getPlayerId(), session.getId());
            }
            case CHANGE_PLAYER: {
                return commonService.changePlayer(payload.getPlayerId(), payload.getPlayerType(), payload.getTeamType());
            }
            case START_GAME: {
                return commonService.startGame(payload.getRoomId(), payload.getPlayerId());
            }
            default: {
                throw new WebSocketException("This action is not implemented yet: " + request.getAction());
            }
        }
    }

    private void notifySelf(Error error, WebSocketSession session) throws IOException {
        TextMessage message = new TextMessage(jsonUtil.writeValue(error));
        session.sendMessage(message);
    }

    private void notifyRoommates(Response response, Set<String> webSocketSessionIds) throws IOException {
        TextMessage message = new TextMessage(jsonUtil.writeValue(response));
        for (String webSocketSessionId : webSocketSessionIds) {
            WebSocketSession session = sessions.get(webSocketSessionId);
            if (session == null) {
                sessions.remove(webSocketSessionId);
            } else {
                session.sendMessage(message);
            }
        }
    }
}
