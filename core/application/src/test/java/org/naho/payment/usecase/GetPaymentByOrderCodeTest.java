package org.naho.payment.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.i18n.message.payment.PaymentDetailMessageKey;
import org.naho.payment.exception.PaymentErrorCode;
import org.naho.payment.model.Money;
import org.naho.payment.model.PaymentOrder;
import org.naho.payment.port.out.PaymentOrderRepositoryPort;
import org.naho.payment.result.PaymentOrderResult;
import org.naho.payment.type.PaymentStatus;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;

import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPaymentByOrderCodeTest {

    @Mock
    private PaymentOrderRepositoryPort orderRepositoryPort;

    @Mock
    private TransactionPort transactionPort;

    @InjectMocks
    private GetPaymentUseCase getPaymentUseCase;

    @BeforeEach
    void setUp() {
        lenient().when(transactionPort.execute(any(Supplier.class))).thenAnswer(invocation -> {
            Supplier<?> supplier = invocation.getArgument(0);
            return supplier.get();
        });
    }

    @Test
    @DisplayName("UTCID01 - Lấy thông tin đơn thanh toán theo orderCode thành công")
    void UTCID01_GetPaymentByOrderCode_Success() {
        // Arrange
        String orderCode = "ORDER_123";
        Instant now = Instant.now();
        PaymentOrder order = PaymentOrder.create(
                orderCode, 100L, 1L, Money.vnd(99000L),
                now, now.plusSeconds(300));

        when(orderRepositoryPort.findByOrderCode(orderCode)).thenReturn(Optional.of(order));

        // Act
        PaymentOrderResult result = getPaymentUseCase.getPaymentByOrderCode(orderCode);

        // Assert
        assertNotNull(result);
        assertEquals(orderCode, result.orderCode());
        assertEquals(100L, result.userId());
        assertEquals(PaymentStatus.PENDING, result.status());
        verify(orderRepositoryPort, times(1)).findByOrderCode(orderCode);
    }

    @Test
    @DisplayName("UTCID02 - Ném ngoại lệ khi không tìm thấy đơn thanh toán theo orderCode")
    void UTCID02_GetPaymentByOrderCode_OrderNotFound() {
        // Arrange
        String orderCode = "ORDER_NOT_FOUND";
        when(orderRepositoryPort.findByOrderCode(orderCode)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> getPaymentUseCase.getPaymentByOrderCode(orderCode));

        assertEquals(PaymentErrorCode.PAYMENT_ORDER_NOT_FOUND, exception.getErrorCode());
        assertEquals(PaymentDetailMessageKey.PAYMENT_ORDER_NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("UTCID03 - Tự động chuyển trạng thái sang EXPIRED khi đơn pending đã quá hạn")
    void UTCID03_GetPaymentByOrderCode_PendingExpired_AutoExpire() {
        // Arrange
        String orderCode = "ORDER_EXPIRED";
        Instant now = Instant.now();
        PaymentOrder order = PaymentOrder.create(
                orderCode, 100L, 1L, Money.vnd(99000L),
                now.minusSeconds(600), now.minusSeconds(100));

        when(orderRepositoryPort.findByOrderCode(orderCode)).thenReturn(Optional.of(order));

        // Act
        PaymentOrderResult result = getPaymentUseCase.getPaymentByOrderCode(orderCode);

        // Assert
        assertNotNull(result);
        assertEquals(PaymentStatus.EXPIRED, result.status());
        verify(orderRepositoryPort, times(1)).save(order);
    }
}
