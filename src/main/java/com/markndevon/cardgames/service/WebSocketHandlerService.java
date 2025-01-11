package com.markndevon.cardgames.service;

import com.markndevon.cardgames.controller.HeartsController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

@Service
public class WebSocketHandlerService {
    public static final int RECONNECT_WINDOW_SECONDS = 10;

    @Autowired
    public HeartsController heartsController;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ConcurrentMap<String, ScheduledFuture<?>> disconnectTasks = new ConcurrentHashMap<>();

    public void scheduleRemoval(String username) {
        // Schedule the task and store the ScheduledFuture
        if(!disconnectTasks.containsKey(username)){
            ScheduledFuture<?> scheduledTask = scheduler.schedule(() -> {
                disconnectTasks.remove(username); // Remove the task reference after execution
                heartsController.kickUser(username);
            }, RECONNECT_WINDOW_SECONDS, TimeUnit.SECONDS);

            disconnectTasks.put(username, scheduledTask);
        }
    }

    public void cancelRemoval(String username) {
        // Cancel the task if it exists
        ScheduledFuture<?> scheduledTask = disconnectTasks.remove(username);
        if (scheduledTask != null) {
            scheduledTask.cancel(false); // Cancel without interrupting if already running
            System.out.println("Kick task is cancelled: " + scheduledTask.isCancelled());
        }
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}
