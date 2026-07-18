package org.naho.payment.port.out;

import org.naho.payment.model.PaymentTransaction;
import org.naho.payment.type.PaymentProvider;

public interface PaymentTransactionRepositoryPort {
    PaymentTransaction save(PaymentTransaction transaction);

    boolean existsByProviderAndTransactionId(PaymentProvider provider, String providerTransactionId);
}
