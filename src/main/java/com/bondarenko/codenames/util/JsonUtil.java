package com.bondarenko.codenames.util;

import com.bondarenko.codenames.exception.websocket.WebSocketException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JsonUtil {
    private final ObjectMapper objectMapper;

    public <T> T readValue(String content, Class<T> clazz) {
        try {
            return objectMapper.readValue(content, clazz);
        } catch (JsonProcessingException e) {
            throw new WebSocketException(e.getMessage());
        }
    }

    public String writeValue(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new WebSocketException(e.getMessage());
        }
    }
}
