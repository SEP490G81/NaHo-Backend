package org.naho.config.application;

import org.naho.notification.port.in.CountUnreadNotificationInputPort;
import org.naho.notification.port.in.GetListNotificationByUserInputPort;
import org.naho.notification.port.in.MarkAllNotificationsAsReadInputPort;
import org.naho.notification.port.in.MarkNotificationAsReadInputPort;
import org.naho.notification.port.out.NotificationRepositoryPort;
import org.naho.notification.usecase.CountUnreadNotificationUseCase;
import org.naho.notification.usecase.GetListNotificationUseCase;
import org.naho.notification.usecase.MarkAllNotificationsAsReadUseCase;
import org.naho.notification.usecase.MarkNotificationAsReadUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationConfig {

    @Bean
    public GetListNotificationByUserInputPort getListNotificationByUserInputPort(
            NotificationRepositoryPort notificationRepositoryPort) {
        return new GetListNotificationUseCase(notificationRepositoryPort);
    }

    @Bean
    public CountUnreadNotificationInputPort countUnreadNotificationInputPort(
            NotificationRepositoryPort notificationRepositoryPort) {
        return new CountUnreadNotificationUseCase(notificationRepositoryPort);
    }

    @Bean
    public MarkNotificationAsReadInputPort markNotificationAsReadInputPort(
            NotificationRepositoryPort notificationRepositoryPort) {
        return new MarkNotificationAsReadUseCase(notificationRepositoryPort);
    }

    @Bean
    public MarkAllNotificationsAsReadInputPort markAllNotificationsAsReadInputPort(
            NotificationRepositoryPort notificationRepositoryPort) {
        return new MarkAllNotificationsAsReadUseCase(notificationRepositoryPort);
    }
}
