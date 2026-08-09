package org.naho.notification.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.notification.entity.NotificationEntity;
import org.naho.notification.mapper.NotificationEntityMapper;
import org.naho.notification.model.Notification;
import org.naho.notification.port.out.NotificationRepositoryPort;
import org.naho.notification.repository.NotificationJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationRepositoryAdapter implements NotificationRepositoryPort {

    private final NotificationJpaRepository notificationJpaRepository;
    private final NotificationEntityMapper notificationEntityMapper;

    @Override
    public Notification save(Notification notification) {
        NotificationEntity entity = notificationEntityMapper.toEntity(notification);
        NotificationEntity savedEntity = notificationJpaRepository.save(entity);
        return notificationEntityMapper.toDomain(savedEntity);
    }

    @Override
    public List<Notification> findByUserId(Long userId, int limit, int offset) {
        int page = offset / limit;
        List<NotificationEntity> entities = notificationJpaRepository.findByUserIdOrderByIdDesc(userId, PageRequest.of(page, limit));
        return entities.stream().map(notificationEntityMapper::toDomain).toList();
    }

    @Override
    public long countUnreadByUserId(Long userId) {
        return notificationJpaRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Override
    public Notification findById(Long id) {
        return notificationJpaRepository.findById(id)
                .map(notificationEntityMapper::toDomain)
                .orElse(null);
    }

    @Override
    public void markAllAsReadByUserId(Long userId) {
        notificationJpaRepository.markAllAsReadByUserId(userId);
    }
}
