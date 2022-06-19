package com.bondarenko.codenames.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class CountdownService {
    public static final int STANDARD_DELAY = 60;
    public static final int FIRST_DELAY = 120;
    private static final Map<Integer, ScheduledFuture<?>> TIMERS = new HashMap<>();

    public long setTimer(int roomId, int delay) {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture<?> timer = scheduledExecutorService.scheduleWithFixedDelay(
                this::skipTurn,
                0,
                delay,
                TimeUnit.SECONDS
        );
        TIMERS.put(roomId, timer);
        return delay + timer.getDelay(TimeUnit.SECONDS);
    }

    public Long getDelay(int roomId) {
        return TIMERS.containsKey(roomId) ? STANDARD_DELAY + TIMERS.get(roomId).getDelay(TimeUnit.SECONDS) : null;
    }

    private void skipTurn() {
        // set timer or end game
        // notify users
    }
}
