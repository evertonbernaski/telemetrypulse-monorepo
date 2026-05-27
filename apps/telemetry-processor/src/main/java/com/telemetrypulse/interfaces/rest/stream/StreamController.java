package com.telemetrypulse.interfaces.rest.stream;

import com.telemetrypulse.infrastructure.streaming.RealtimeEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/stream")
public class StreamController {

    private final RealtimeEventPublisher realtimeEventPublisher;

    public StreamController(RealtimeEventPublisher realtimeEventPublisher) {
        this.realtimeEventPublisher = realtimeEventPublisher;
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe() {
        return realtimeEventPublisher.subscribe();
    }
}
