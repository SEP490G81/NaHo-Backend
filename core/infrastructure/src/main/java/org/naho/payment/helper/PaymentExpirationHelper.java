package org.naho.payment.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.payment.port.out.PaymentOrderRepositoryPort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentExpirationHelper {

    private final PaymentOrderRepositoryPort paymentOrderRepositoryPort;

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void expirePendingOrders() {
        try {
            paymentOrderRepositoryPort.expirePendingBefore(Instant.now());
        } catch (Exception e) {
            log.error("Error executing expirePendingOrders job", e);
        }
    }
}
