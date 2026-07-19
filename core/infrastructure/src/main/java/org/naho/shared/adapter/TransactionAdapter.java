package org.naho.shared.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.shared.port.out.TransactionPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class TransactionAdapter implements TransactionPort {
    private final TransactionTemplate transactionTemplate;

    @Override
    public <T> T execute(Supplier<T> action) {
        return transactionTemplate.execute(status -> action.get());
    }

    @Override
    public void execute(Runnable action) {
        transactionTemplate.executeWithoutResult(status -> action.run());
    }
}