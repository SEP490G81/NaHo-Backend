package org.naho.user.config;

import lombok.RequiredArgsConstructor;
import org.naho.user.constant.UserSessionEventProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
@RequiredArgsConstructor
public class RedisPubSubConfig {
    private final RedisConnectionFactory redisConnectionFactory;
    private final ForceLogoutSubscriber forceLogoutSubscriber;

    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();

        container.setConnectionFactory(redisConnectionFactory);

        container.addMessageListener(
                forceLogoutSubscriber,
                new ChannelTopic(UserSessionEventProperties.FORCE_LOGOUT_CHANNEL)
        );

        return container;
    }
}
