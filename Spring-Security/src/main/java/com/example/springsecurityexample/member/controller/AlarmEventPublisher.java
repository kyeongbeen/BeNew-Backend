package com.example.springsecurityexample.member.controller;

import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class AlarmEventPublisher {
    private final ConcurrentHashMap<Long, List<SseEmitter>> userEmitters = new ConcurrentHashMap<>();
    private final List<SseEmitter> broadcastEmitters = new CopyOnWriteArrayList<>();

    @EventListener
    public void handleAlarmCreatedEvent(AlarmCreatedEvent event) {
        Long receiverUserId = event.getAlarmResponse().getReceiverUserId();

        // 특정 사용자에게 이벤트 전송
        if (userEmitters.containsKey(receiverUserId)) {
            List<SseEmitter> emitters = userEmitters.get(receiverUserId);
            sendEventToEmitters(emitters, event);
        }

        // 모든 사용자에게 이벤트 전송
        sendEventToEmitters(broadcastEmitters, event);
    }

    private void sendEventToEmitters(List<SseEmitter> emitters, AlarmCreatedEvent event) {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event().name("alarm").data(event.getAlarmResponse(), MediaType.APPLICATION_JSON));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        });
        emitters.removeAll(deadEmitters);
    }

    public SseEmitter createEmitter(Long userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        setupEmitter(emitter, userId);
        userEmitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);
        return emitter;
    }

    public SseEmitter createBroadcastEmitter() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        setupEmitter(emitter, null); // Null indicates a broadcast emitter
        broadcastEmitters.add(emitter);
        return emitter;
    }

    private void setupEmitter(SseEmitter emitter, Long userId) {
        emitter.onCompletion(() -> removeEmitter(emitter, userId));
        emitter.onTimeout(() -> removeEmitter(emitter, userId));
        emitter.onError((e) -> removeEmitter(emitter, userId));
    }

    private void removeEmitter(SseEmitter emitter, Long userId) {
        if (userId != null) {
            userEmitters.getOrDefault(userId, new CopyOnWriteArrayList<>()).remove(emitter);
        } else {
            broadcastEmitters.remove(emitter);
        }
    }
}
