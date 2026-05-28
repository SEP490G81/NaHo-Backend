package org.naho.shared.port.out;

import java.util.function.Supplier;

public interface TransactionPort {
    <T> T execute(Supplier<T> action);
}
