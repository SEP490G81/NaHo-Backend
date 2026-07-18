package org.naho.payment.repository;

import org.naho.payment.entity.PaymentTransactionEntity;
import org.naho.payment.type.PaymentProvider;
import org.naho.shared.persistence.BaseJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentTransactionJpaRepository extends BaseJpaRepository<PaymentTransactionEntity> {
    boolean existsByProviderAndProviderTransactionId(PaymentProvider provider, String providerTransactionId);
}
