package com.telemetrypulse.infrastructure.streaming;

import com.telemetrypulse.interfaces.rest.TelemetryEventResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class RealtimeEventPublisher {

    private static final long SSE_TIMEOUT_MILLIS = Duration.ofMinutes(30).toMillis();

    private final List<SseEmitter> subscribers = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MILLIS);
        subscribers.add(emitter);
        emitter.onCompletion(() -> subscribers.remove(emitter));
        emitter.onTimeout(() -> subscribers.remove(emitter));
        emitter.onError(error -> subscribers.remove(emitter));
        send(emitter, "connected", "ok");
        return emitter;
    }

    public void publishTelemetry(TelemetryEventResponse response) {
        broadcast("telemetry", response);
    }

    public void publishAlert(TelemetryEventResponse.AlertResponse response) {
        broadcast("alert", response);
    }

    private void broadcast(String eventName, Object payload) {
        subscribers.forEach(emitter -> send(emitter, eventName, payload));
    }

    private void send(SseEmitter emitter, String eventName, Object payload) {
        try {
            emitter.send(SseEmitter.event().name(eventName).data(payload));
        } catch (IOException | IllegalStateException ex) {
            subscribers.remove(emitter);
        }
    }
}
