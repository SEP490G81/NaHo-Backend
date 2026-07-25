package org.naho.payment.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.payment.entity.PaymentOrderEntity;
import org.naho.payment.entity.PaymentTransactionEntity;
import org.naho.payment.mapper.PaymentTransactionEntityMapper;
import org.naho.payment.model.PaymentTransaction;
import org.naho.payment.port.out.PaymentTransactionRepositoryPort;
import org.naho.payment.repository.PaymentOrderJpaRepository;
import org.naho.payment.repository.PaymentTransactionJpaRepository;
import org.naho.payment.type.PaymentProvider;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentTransactionRepositoryAdapter implements PaymentTransactionRepositoryPort {

    private final PaymentTransactionJpaRepository transactionJpaRepository;
    private final PaymentOrderJpaRepository orderJpaRepository;
    private final PaymentTransactionEntityMapper transactionEntityMapper;

    @Override
    public PaymentTransaction save(PaymentTransaction transaction) {
        PaymentTransactionEntity entity = transactionEntityMapper.domainToEntity(transaction);

        PaymentOrderEntity orderEntity = orderJpaRepository.getReferenceById(transaction.getPaymentOrderId());
        entity.setPaymentOrder(orderEntity);

        PaymentTransactionEntity savedEntity = transactionJpaRepository.save(entity);
        return transactionEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public boolean existsByProviderAndTransactionId(PaymentProvider provider, String providerTransactionId) {
        return transactionJpaRepository.existsByProviderAndProviderTransactionId(provider, providerTransactionId);
    }
}
