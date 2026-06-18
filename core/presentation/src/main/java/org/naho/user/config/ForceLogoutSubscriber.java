package org.naho.user.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.naho.i18n.message.common.CommonDetailMessageKey;
import org.naho.shared.exception.CommonErrorCode;
import org.naho.shared.exception.PresentationException;
import org.naho.user.command.ForceLogoutCommand;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class ForceLogoutSubscriber implements MessageListener {
    private final ObjectMapper objectMapper;
    private final UserSessionConnectionManager userSessionConnectionManager;

    @Override
    public void onMessage(@NonNull Message message, byte @Nullable [] pattern) {
        try {
            String json = new String(message.getBody(), StandardCharsets.UTF_8);
            ForceLogoutCommand command = objectMapper.readValue(json, ForceLogoutCommand.class);

            Long userId = command.userId();
            Long newUserSessionId = command.newUserSessionId();

            userSessionConnectionManager.sendForceLogoutToOtherUserSessions(userId, newUserSessionId);

        } catch (Exception e) {
            throw new PresentationException(
                    CommonErrorCode.COMMON_CANNOT_READ_JSON,
                    CommonDetailMessageKey.COMMON_CANNOT_READ_JSON,
                    e.getMessage()
            );
        }
    }
}
