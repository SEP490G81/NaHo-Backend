package org.naho.user.config;

import org.naho.user.constant.UserSessionEventProperties;
import org.naho.user.type.SessionRevokedReason;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserSessionConnectionManager {
    private final Map<Long, Map<Long, SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter connect(Long userId, Long userSessonId) {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.computeIfAbsent(userId, id -> new ConcurrentHashMap<>())
                .put(userSessonId, emitter);

        emitter.onCompletion(() -> remove(userId, userSessonId));
        emitter.onTimeout(() -> remove(userId, userSessonId));
        emitter.onError(t -> remove(userId, userSessonId));
        return emitter;
    }

    public void sendForceLogoutToOtherUserSessions(Long userId, Long keepUserSessionId) {
        Map<Long, SseEmitter> userEmitters = this.emitters.get(userId);
        if (userEmitters == null) return;

        userEmitters.forEach((userSessionId, emitter) -> {
            if (userSessionId.equals(keepUserSessionId)) return;

            try {
                emitter.send(SseEmitter.event()
                        .name(UserSessionEventProperties.FORCE_LOGOUT_EVENT)
                        .data(SessionRevokedReason.LOGIN_ON_OTHER_DEVICE));
            } catch (IOException e) {
                remove(userId, userSessionId);
            }
        });
    }

    private void remove(Long userId, Long userSessonId) {
        Map<Long, SseEmitter> userEmitters = this.emitters.get(userId);
        if (userEmitters != null) {
            userEmitters.remove(userSessonId);
            if (userEmitters.isEmpty()) {
                this.emitters.remove(userId);
            }
        }
    }
}
