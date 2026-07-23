package org.naho.payment.repository;

import jakarta.persistence.LockModeType;
import org.naho.payment.entity.PaymentOrderEntity;
import org.naho.shared.persistence.BaseJpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentOrderJpaRepository extends BaseJpaRepository<PaymentOrderEntity> {

    Optional<PaymentOrderEntity> findByOrderCode(String orderCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT po FROM PaymentOrderEntity po WHERE po.orderCode = :orderCode")
    Optional<PaymentOrderEntity> findByOrderCodeForUpdate(@Param("orderCode") String orderCode);

    boolean existsByOrderCode(String orderCode);
}
