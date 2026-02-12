package com.justinaji.chatapp_messages.service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class SseService {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(0L);

        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));

        return emitter;
    }

    public void broadcast(String eventType, String data) {
        emitters.forEach(emitter -> {
            try {
                emitter.send(
                    SseEmitter.event()
                        .name(eventType)
                        .data(data)
                );
            } catch (Exception e) {
                emitters.remove(emitter);
            }
        });
    }
}
