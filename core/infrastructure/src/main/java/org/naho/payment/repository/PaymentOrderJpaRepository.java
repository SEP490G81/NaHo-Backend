package org.naho.payment.repository;

import jakarta.persistence.LockModeType;
import org.naho.payment.entity.PaymentOrderEntity;
import org.naho.shared.persistence.BaseJpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentOrderJpaRepository extends BaseJpaRepository<PaymentOrderEntity> {

    Optional<PaymentOrderEntity> findByOrderCode(String orderCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT po FROM PaymentOrderEntity po WHERE po.orderCode = :orderCode")
    Optional<PaymentOrderEntity> findByOrderCodeForUpdate(@Param("orderCode") String orderCode);

    boolean existsByOrderCode(String orderCode);

    @Query("""
                SELECT po
                FROM PaymentOrderEntity po
                WHERE po.userId = :userId
                  AND po.status = org.naho.payment.type.PaymentStatus.PENDING
                ORDER BY po.createdTime DESC
            """)
    List<PaymentOrderEntity> findAllPendingByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("""
                UPDATE PaymentOrderEntity po
                SET po.status = org.naho.payment.type.PaymentStatus.EXPIRED,
                    po.modifiedTime = :now
                WHERE po.status = org.naho.payment.type.PaymentStatus.PENDING
                  AND po.expiresTime <= :now
            """)
    int expirePendingBefore(@Param("now") Instant now);
}
