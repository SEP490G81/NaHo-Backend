package org.naho.payment.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.i18n.message.payment.PaymentDetailMessageKey;
import org.naho.payment.command.CancelPaymentCommand;
import org.naho.payment.exception.PaymentErrorCode;
import org.naho.payment.model.Money;
import org.naho.payment.model.PaymentOrder;
import org.naho.payment.port.out.PaymentOrderRepositoryPort;
import org.naho.payment.result.CancelPaymentResult;
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
class CancelPaymentTest {

    @Mock
    private PaymentOrderRepositoryPort orderRepositoryPort;

    @Mock
    private TransactionPort transactionPort;

    @InjectMocks
    private CancelPaymentUseCase cancelPaymentUseCase;

    @BeforeEach
    void setUp() {
        lenient().when(transactionPort.execute(any(Supplier.class))).thenAnswer(invocation -> {
            Supplier<?> supplier = invocation.getArgument(0);
            return supplier.get();
        });
    }

    @Test
    @DisplayName("UTCID01 - Huỷ đơn thanh toán thành công khi đơn hợp lệ và thuộc về người dùng")
    void UTCID01_CancelPayment_Success() {
        // Arrange
        String orderCode = "ORDER123";
        Long userId = 100L;
        CancelPaymentCommand command = new CancelPaymentCommand(orderCode, userId);

        Instant now = Instant.now();
        PaymentOrder order = PaymentOrder.create(
                orderCode, userId, 1L, Money.vnd(99000L),
                now, now.plusSeconds(300));

        when(orderRepositoryPort.findByOrderCode(orderCode)).thenReturn(Optional.of(order));
        when(orderRepositoryPort.save(any(PaymentOrder.class))).thenReturn(order);

        // Act
        CancelPaymentResult result = cancelPaymentUseCase.cancelPayment(command);

        // Assert
        assertNotNull(result);
        assertEquals(orderCode, result.orderCode());
        assertEquals(PaymentStatus.CANCELLED, result.status());
        verify(orderRepositoryPort, times(1)).findByOrderCode(orderCode);
        verify(orderRepositoryPort, times(1)).save(order);
    }

    @Test
    @DisplayName("UTCID02 - Ném ngoại lệ khi không tìm thấy đơn thanh toán cần huỷ")
    void UTCID02_CancelPayment_OrderNotFound() {
        // Arrange
        String orderCode = "ORDER_NOT_FOUND";
        Long userId = 100L;
        CancelPaymentCommand command = new CancelPaymentCommand(orderCode, userId);

        when(orderRepositoryPort.findByOrderCode(orderCode)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> cancelPaymentUseCase.cancelPayment(command));

        assertEquals(PaymentErrorCode.PAYMENT_ORDER_NOT_FOUND, exception.getErrorCode());
        assertEquals(PaymentDetailMessageKey.PAYMENT_ORDER_NOT_FOUND, exception.getMessage());
        verify(orderRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("UTCID03 - Ném ngoại lệ khi đơn thanh toán không thuộc về người dùng yêu cầu")
    void UTCID03_CancelPayment_OrderNotBelongToUser() {
        // Arrange
        String orderCode = "ORDER123";
        Long requestingUserId = 100L;
        Long actualOwnerId = 200L;
        CancelPaymentCommand command = new CancelPaymentCommand(orderCode, requestingUserId);

        Instant now = Instant.now();
        PaymentOrder order = PaymentOrder.create(
                orderCode, actualOwnerId, 1L, Money.vnd(99000L),
                now, now.plusSeconds(300));

        when(orderRepositoryPort.findByOrderCode(orderCode)).thenReturn(Optional.of(order));

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> cancelPaymentUseCase.cancelPayment(command));

        assertEquals(PaymentErrorCode.PAYMENT_INVALID_STATE, exception.getErrorCode());
        assertEquals(PaymentDetailMessageKey.PAYMENT_ORDER_NOT_BELONG_TO_USER, exception.getMessage());
        verify(orderRepositoryPort, never()).save(any());
    }
}
