package org.naho.payment.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.payment.model.Money;
import org.naho.payment.model.PaymentOrder;
import org.naho.payment.port.out.PaymentOrderRepositoryPort;
import org.naho.payment.result.PaymentOrderResult;
import org.naho.payment.type.PaymentStatus;
import org.naho.shared.port.out.TransactionPort;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPaymentsByUserIdTest {

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
    @DisplayName("UTCID01 - Lấy danh sách đơn thanh toán của người dùng thành công khi có dữ liệu")
    void UTCID01_GetPaymentsByUserId_Success() {
        // Arrange
        Long userId = 100L;
        Instant now = Instant.now();
        PaymentOrder o1 = PaymentOrder.create(
                "ORDER_1", userId, 1L, Money.vnd(99000L),
                now, now.plusSeconds(300));
        PaymentOrder o2 = PaymentOrder.create(
                "ORDER_2", userId, 2L, Money.vnd(199000L),
                now, now.plusSeconds(300));

        when(orderRepositoryPort.findAllByUserId(userId)).thenReturn(List.of(o1, o2));

        // Act
        List<PaymentOrderResult> results = getPaymentUseCase.getPaymentsByUserId(userId);

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("ORDER_1", results.get(0).orderCode());
        assertEquals("ORDER_2", results.get(1).orderCode());
        verify(orderRepositoryPort, times(1)).findAllByUserId(userId);
    }

    @Test
    @DisplayName("UTCID02 - Lấy danh sách đơn thanh toán trả về rỗng khi người dùng chưa có đơn nào")
    void UTCID02_GetPaymentsByUserId_EmptyList() {
        // Arrange
        Long userId = 100L;
        when(orderRepositoryPort.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        // Act
        List<PaymentOrderResult> results = getPaymentUseCase.getPaymentsByUserId(userId);

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(orderRepositoryPort, times(1)).findAllByUserId(userId);
    }

    @Test
    @DisplayName("UTCID03 - Tự động cập nhật đơn pending đã hết hạn sang EXPIRED trong danh sách")
    void UTCID03_GetPaymentsByUserId_ContainsPendingExpired_AutoExpire() {
        // Arrange
        Long userId = 100L;
        Instant now = Instant.now();
        PaymentOrder expiredOrder = PaymentOrder.create(
                "ORDER_EXP", userId, 1L, Money.vnd(99000L),
                now.minusSeconds(600), now.minusSeconds(100));

        when(orderRepositoryPort.findAllByUserId(userId)).thenReturn(List.of(expiredOrder));

        // Act
        List<PaymentOrderResult> results = getPaymentUseCase.getPaymentsByUserId(userId);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(PaymentStatus.EXPIRED, results.get(0).status());
        verify(orderRepositoryPort, times(1)).save(expiredOrder);
    }
}
