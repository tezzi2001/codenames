package com.bondarenko.codenames.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
public class JsonConfig {
    @Bean
    public ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        return objectMapper;
    }

    @Bean
    @Primary
    public List<String> getWords(ObjectMapper objectMapper) {
        return Arrays.asList(getJson(objectMapper, "static/words.json", String[].class));
    }

    private <T> T getJson(ObjectMapper objectMapper, String path, Class<T> tClass) {
        try {
            return objectMapper.readValue(Thread.currentThread().getContextClassLoader().getResource(path), tClass);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
            return null;
        }
    }
}
