package org.naho.payment.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.pagination.PageData;
import org.naho.pagination.PageMeta;
import org.naho.payment.command.PaymentOrderQueryCommand;
import org.naho.payment.constant.PaymentOrderSortColumn;
import org.naho.payment.model.Money;
import org.naho.payment.model.PaymentOrder;
import org.naho.payment.port.out.PaymentOrderRepositoryPort;
import org.naho.payment.result.PaymentOrderResult;
import org.naho.payment.type.PaymentStatus;
import org.naho.shared.constant.SortDirection;
import org.naho.shared.port.out.TransactionPort;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllPaymentOrdersTest {

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
    @DisplayName("UTCID01 - Lấy danh sách đơn thanh toán phân trang theo query command thành công")
    void UTCID01_GetAllPaymentOrders_Success() {
        // Arrange
        PaymentOrderQueryCommand command = new PaymentOrderQueryCommand(
                0, 20, PaymentOrderSortColumn.CREATED_TIME, SortDirection.DESC,
                null, null, null, null, null, null);

        Instant now = Instant.now();
        PaymentOrder order = PaymentOrder.create(
                "ORDER_1", 100L, 1L, Money.vnd(99000L),
                now, now.plusSeconds(300));

        PageMeta pageMeta = PageMeta.builder()
                .currentPage(0)
                .pageSize(20)
                .totalElements(1L)
                .totalPages(1)
                .hasNext(false)
                .hasPrevious(false)
                .build();

        PageData<PaymentOrder> pageData = PageData.<PaymentOrder>builder()
                .pageMeta(pageMeta)
                .data(List.of(order))
                .build();

        when(orderRepositoryPort.findAllPaymentOrders(command)).thenReturn(pageData);

        // Act
        PageData<PaymentOrderResult> result = getPaymentUseCase.getAllPaymentOrders(command);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getData().size());
        assertEquals("ORDER_1", result.getData().get(0).orderCode());
        assertEquals(pageMeta, result.getPageMeta());
        verify(orderRepositoryPort, times(1)).findAllPaymentOrders(command);
    }

    @Test
    @DisplayName("UTCID02 - Lấy danh sách đơn thanh toán phân trang trả về trang rỗng khi không có bản ghi")
    void UTCID02_GetAllPaymentOrders_EmptyPage() {
        // Arrange
        PaymentOrderQueryCommand command = new PaymentOrderQueryCommand(
                0, 20, null, null, null, null, null, null, null, null);

        PageMeta pageMeta = PageMeta.builder()
                .currentPage(0)
                .pageSize(20)
                .totalElements(0L)
                .totalPages(0)
                .hasNext(false)
                .hasPrevious(false)
                .build();

        PageData<PaymentOrder> pageData = PageData.<PaymentOrder>builder()
                .pageMeta(pageMeta)
                .data(Collections.emptyList())
                .build();

        when(orderRepositoryPort.findAllPaymentOrders(command)).thenReturn(pageData);

        // Act
        PageData<PaymentOrderResult> result = getPaymentUseCase.getAllPaymentOrders(command);

        // Assert
        assertNotNull(result);
        assertTrue(result.getData().isEmpty());
        verify(orderRepositoryPort, times(1)).findAllPaymentOrders(command);
    }

    @Test
    @DisplayName("UTCID03 - Tự động cập nhật đơn pending đã hết hạn trong kết quả phân trang")
    void UTCID03_GetAllPaymentOrders_WithPendingExpired_AutoExpire() {
        // Arrange
        PaymentOrderQueryCommand command = new PaymentOrderQueryCommand(
                0, 20, null, null, null, null, null, null, null, null);

        Instant now = Instant.now();
        PaymentOrder expiredOrder = PaymentOrder.create(
                "ORDER_EXP", 100L, 1L, Money.vnd(99000L),
                now.minusSeconds(600), now.minusSeconds(100));

        PageMeta pageMeta = PageMeta.builder()
                .currentPage(0)
                .pageSize(20)
                .totalElements(1L)
                .totalPages(1)
                .hasNext(false)
                .hasPrevious(false)
                .build();

        PageData<PaymentOrder> pageData = PageData.<PaymentOrder>builder()
                .pageMeta(pageMeta)
                .data(List.of(expiredOrder))
                .build();

        when(orderRepositoryPort.findAllPaymentOrders(command)).thenReturn(pageData);

        // Act
        PageData<PaymentOrderResult> result = getPaymentUseCase.getAllPaymentOrders(command);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getData().size());
        assertEquals(PaymentStatus.EXPIRED, result.getData().get(0).status());
        verify(orderRepositoryPort, times(1)).save(expiredOrder);
    }
}
