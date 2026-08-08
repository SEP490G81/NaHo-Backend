package org.naho.notification.repository;

import org.naho.notification.entity.NotificationEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, Long> {
    
    List<NotificationEntity> findByUserIdOrderByIdDesc(Long userId, Pageable pageable);
    
    long countByUserIdAndIsReadFalse(Long userId);
    
    @Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
    void markAllAsReadByUserId(@Param("userId") Long userId);
}
