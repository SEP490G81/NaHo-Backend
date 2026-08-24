package org.naho.user.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.user.config.UserSessionConnectionManager;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sse")
public class SseController {
    private final UserSessionConnectionManager userSessionConnectionManager;

    // PUBLIC RESOURCE
    @GetMapping("/connect")
    public SseEmitter connect(@AuthenticationPrincipal AccessTokenPayload payload) {
        return userSessionConnectionManager.connect(
                payload.userId(),
                payload.userSessionId()
        );
    }
}
