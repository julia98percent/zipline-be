package com.zipline.repository.notification;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class NotificationRepository {
  private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

  public SseEmitter save(String id, SseEmitter emitter) {
    emitters.put(id, emitter);
    return emitter;
  }

  public void deleteById(String id) {
    emitters.remove(id);
  }

  public Map<String, SseEmitter> findAll() {
    return emitters;
  }

}