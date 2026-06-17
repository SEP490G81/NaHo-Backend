package org.naho.user.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.user.command.ForceLogoutCommand;
import org.naho.user.constant.UserSessionEventProperties;
import org.naho.user.port.out.UserSessionEventPublisherPort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSessionEventPublisherAdapter implements UserSessionEventPublisherPort {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void publishForceLogoutEvent(ForceLogoutCommand command) {
        redisTemplate.convertAndSend(UserSessionEventProperties.FORCE_LOGOUT_CHANNEL, command);
    }
}
