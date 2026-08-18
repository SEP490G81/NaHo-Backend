package org.naho.payment.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.i18n.message.payment.PaymentDetailMessageKey;
import org.naho.i18n.message.subscription.SubscriptionDetailMessageKey;
import org.naho.payment.command.ConfirmPaymentCommand;
import org.naho.payment.event.PaymentConfirmedEvent;
import org.naho.payment.exception.PaymentErrorCode;
import org.naho.payment.model.Money;
import org.naho.payment.model.PaymentOrder;
import org.naho.payment.model.PaymentTransaction;
import org.naho.payment.port.out.PaymentOrderRepositoryPort;
import org.naho.payment.port.out.PaymentTransactionRepositoryPort;
import org.naho.payment.result.ConfirmPaymentResult;
import org.naho.payment.type.ConfirmPaymentStatus;
import org.naho.payment.type.PaymentProvider;
import org.naho.payment.type.PaymentStatus;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.EventPublisherPort;
import org.naho.shared.port.out.TransactionPort;
import org.naho.subscription.model.SubscriptionPlan;
import org.naho.subscription.model.UserSubscription;
import org.naho.subscription.port.out.SubscriptionPlanRepositoryPort;
import org.naho.subscription.port.out.UserSubscriptionRepositoryPort;
import org.naho.subscription.type.PlanCode;
import org.naho.subscription.type.PlanStatus;
import org.naho.subscription.type.PlanTier;
import org.naho.user.event.UserPlanUpgradedEvent;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmPaymentTest {

    @Mock
    private PaymentOrderRepositoryPort orderRepositoryPort;

    @Mock
    private PaymentTransactionRepositoryPort transactionRepositoryPort;

    @Mock
    private SubscriptionPlanRepositoryPort planRepositoryPort;

    @Mock
    private UserSubscriptionRepositoryPort subscriptionRepositoryPort;

    @Mock
    private EventPublisherPort eventPublisherPort;

    @Mock
    private TransactionPort transactionPort;

    @InjectMocks
    private ConfirmPaymentUseCase confirmPaymentUseCase;

    @BeforeEach
    void setUp() {
        lenient().when(transactionPort.execute(any(Supplier.class))).thenAnswer(invocation -> {
            Supplier<?> supplier = invocation.getArgument(0);
            return supplier.get();
        });
    }

    @Test
    @DisplayName("UTCID01 - Xác nhận thanh toán thành công, kích hoạt subscription và gửi event")
    void UTCID01_ConfirmPayment_Success() {
        // Arrange
        String orderCode = "ORDER_123";
        String txnId = "TXN_999";
        Money amount = Money.vnd(99000L);
        Instant now = Instant.now();

        ConfirmPaymentCommand command = new ConfirmPaymentCommand(
                PaymentProvider.SEPAY, orderCode, txnId, amount, true, now, Collections.emptyMap());

        PaymentOrder order = PaymentOrder.builder()
                .id(1L)
                .orderCode(orderCode)
                .userId(100L)
                .subscriptionPlanId(1L)
                .amount(amount)
                .provider(PaymentProvider.SEPAY)
                .status(PaymentStatus.PENDING)
                .createdTime(now)
                .expiresTime(now.plusSeconds(300))
                .build();

        SubscriptionPlan plan = SubscriptionPlan.builder()
                .id(1L)
                .code(PlanCode.BASIC)
                .description("Gói 1 tháng")
                .tier(PlanTier.BASIC)
                .price(amount)
                .durationDays(30)
                .status(PlanStatus.ACTIVE)
                .build();

        UserSubscription previousSub = mock(UserSubscription.class);

        when(transactionRepositoryPort.existsByProviderAndTransactionId(PaymentProvider.SEPAY, txnId)).thenReturn(false);
        when(orderRepositoryPort.findByOrderCodeForUpdate(orderCode)).thenReturn(Optional.of(order));
        when(planRepositoryPort.findById(order.getSubscriptionPlanId())).thenReturn(Optional.of(plan));
        when(subscriptionRepositoryPort.existsByPaymentOrderId(order.getId())).thenReturn(false);
        when(subscriptionRepositoryPort.findActiveByUserId(eq(100L), any(Instant.class))).thenReturn(Optional.of(previousSub));

        // Act
        ConfirmPaymentResult result = confirmPaymentUseCase.confirmPayment(command);

        // Assert
        assertNotNull(result);
        assertEquals(ConfirmPaymentStatus.SUCCESS, result.status());
        assertEquals(orderCode, result.orderCode());
        verify(transactionRepositoryPort, times(1)).save(any(PaymentTransaction.class));
        verify(previousSub, times(1)).cancel(any(Instant.class));
        verify(subscriptionRepositoryPort, times(2)).save(any(UserSubscription.class));
        verify(eventPublisherPort, times(1)).publish(any(UserPlanUpgradedEvent.class));
        verify(eventPublisherPort, times(1)).publish(any(PaymentConfirmedEvent.class));
        verify(orderRepositoryPort, times(1)).save(order);
    }

    @Test
    @DisplayName("UTCID02 - Trả về trạng thái DUPLICATE khi transactionId đã tồn tại")
    void UTCID02_ConfirmPayment_DuplicateTxn() {
        // Arrange
        String orderCode = "ORDER_123";
        String txnId = "TXN_DUPLICATE";
        ConfirmPaymentCommand command = new ConfirmPaymentCommand(
                PaymentProvider.SEPAY, orderCode, txnId, Money.vnd(99000L),
                true, Instant.now(), Collections.emptyMap());

        when(transactionRepositoryPort.existsByProviderAndTransactionId(PaymentProvider.SEPAY, txnId)).thenReturn(true);

        // Act
        ConfirmPaymentResult result = confirmPaymentUseCase.confirmPayment(command);

        // Assert
        assertNotNull(result);
        assertEquals(ConfirmPaymentStatus.DUPLICATE, result.status());
        assertEquals(orderCode, result.orderCode());
        verify(orderRepositoryPort, never()).findByOrderCodeForUpdate(any());
    }

    @Test
    @DisplayName("UTCID03 - Ném ngoại lệ khi không tìm thấy đơn thanh toán cần xác nhận")
    void UTCID03_ConfirmPayment_OrderNotFound() {
        // Arrange
        String orderCode = "ORDER_NOT_FOUND";
        ConfirmPaymentCommand command = new ConfirmPaymentCommand(
                PaymentProvider.SEPAY, orderCode, "TXN_1", Money.vnd(99000L),
                true, Instant.now(), Collections.emptyMap());

        when(transactionRepositoryPort.existsByProviderAndTransactionId(PaymentProvider.SEPAY, "TXN_1")).thenReturn(false);
        when(orderRepositoryPort.findByOrderCodeForUpdate(orderCode)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> confirmPaymentUseCase.confirmPayment(command));

        assertEquals(PaymentErrorCode.PAYMENT_ORDER_NOT_FOUND, exception.getErrorCode());
        assertEquals(PaymentDetailMessageKey.PAYMENT_ORDER_NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("UTCID04 - Trả về trạng thái ALREADY_PAID khi đơn đã ở trạng thái đã thanh toán")
    void UTCID04_ConfirmPayment_AlreadyPaid() {
        // Arrange
        String orderCode = "ORDER_PAID";
        Instant now = Instant.now();
        PaymentOrder order = PaymentOrder.builder()
                .id(1L)
                .orderCode(orderCode)
                .userId(100L)
                .subscriptionPlanId(1L)
                .amount(Money.vnd(99000L))
                .status(PaymentStatus.PAID)
                .createdTime(now.minusSeconds(600))
                .expiresTime(now)
                .build();

        ConfirmPaymentCommand command = new ConfirmPaymentCommand(
                PaymentProvider.SEPAY, orderCode, "TXN_NEW", Money.vnd(99000L),
                true, now, Collections.emptyMap());

        when(transactionRepositoryPort.existsByProviderAndTransactionId(PaymentProvider.SEPAY, "TXN_NEW")).thenReturn(false);
        when(orderRepositoryPort.findByOrderCodeForUpdate(orderCode)).thenReturn(Optional.of(order));

        // Act
        ConfirmPaymentResult result = confirmPaymentUseCase.confirmPayment(command);

        // Assert
        assertNotNull(result);
        assertEquals(ConfirmPaymentStatus.ALREADY_PAID, result.status());
        assertEquals(orderCode, result.orderCode());
        verify(transactionRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("UTCID05 - Giao dịch không thành công và đơn đã hết hạn -> expire order")
    void UTCID05_ConfirmPayment_FailedTxn_Expired() {
        // Arrange
        String orderCode = "ORDER_EXPIRED";
        Instant now = Instant.now();
        PaymentOrder order = PaymentOrder.builder()
                .id(1L)
                .orderCode(orderCode)
                .userId(100L)
                .subscriptionPlanId(1L)
                .amount(Money.vnd(99000L))
                .provider(PaymentProvider.SEPAY)
                .status(PaymentStatus.PENDING)
                .createdTime(now.minusSeconds(600))
                .expiresTime(now.minusSeconds(100))
                .build();

        ConfirmPaymentCommand command = new ConfirmPaymentCommand(
                PaymentProvider.SEPAY, orderCode, "TXN_FAILED", Money.vnd(99000L),
                false, now, Collections.emptyMap());

        when(transactionRepositoryPort.existsByProviderAndTransactionId(PaymentProvider.SEPAY, "TXN_FAILED")).thenReturn(false);
        when(orderRepositoryPort.findByOrderCodeForUpdate(orderCode)).thenReturn(Optional.of(order));

        // Act
        ConfirmPaymentResult result = confirmPaymentUseCase.confirmPayment(command);

        // Assert
        assertNotNull(result);
        assertEquals(ConfirmPaymentStatus.FAILED, result.status());
        assertEquals(orderCode, result.orderCode());
        assertEquals(PaymentStatus.EXPIRED, order.getStatus());
        verify(orderRepositoryPort, times(1)).save(order);
    }

    @Test
    @DisplayName("UTCID06 - Giao dịch không thành công trong hạn -> markFailed")
    void UTCID06_ConfirmPayment_FailedTxn_MarkFailed() {
        // Arrange
        String orderCode = "ORDER_FAILED";
        Instant now = Instant.now();
        PaymentOrder order = PaymentOrder.builder()
                .id(1L)
                .orderCode(orderCode)
                .userId(100L)
                .subscriptionPlanId(1L)
                .amount(Money.vnd(99000L))
                .provider(PaymentProvider.SEPAY)
                .status(PaymentStatus.PENDING)
                .createdTime(now)
                .expiresTime(now.plusSeconds(300))
                .build();

        ConfirmPaymentCommand command = new ConfirmPaymentCommand(
                PaymentProvider.SEPAY, orderCode, "TXN_FAILED", Money.vnd(99000L),
                false, now, Collections.emptyMap());

        when(transactionRepositoryPort.existsByProviderAndTransactionId(PaymentProvider.SEPAY, "TXN_FAILED")).thenReturn(false);
        when(orderRepositoryPort.findByOrderCodeForUpdate(orderCode)).thenReturn(Optional.of(order));

        // Act
        ConfirmPaymentResult result = confirmPaymentUseCase.confirmPayment(command);

        // Assert
        assertNotNull(result);
        assertEquals(ConfirmPaymentStatus.FAILED, result.status());
        assertEquals(orderCode, result.orderCode());
        assertEquals(PaymentStatus.FAILED, order.getStatus());
        verify(orderRepositoryPort, times(1)).save(order);
    }

    @Test
    @DisplayName("UTCID07 - Ném ngoại lệ khi không tìm thấy gói cước của đơn thanh toán")
    void UTCID07_ConfirmPayment_PlanNotFound() {
        // Arrange
        String orderCode = "ORDER_123";
        Instant now = Instant.now();
        PaymentOrder order = PaymentOrder.builder()
                .id(1L)
                .orderCode(orderCode)
                .userId(100L)
                .subscriptionPlanId(999L)
                .amount(Money.vnd(99000L))
                .provider(PaymentProvider.SEPAY)
                .status(PaymentStatus.PENDING)
                .createdTime(now)
                .expiresTime(now.plusSeconds(300))
                .build();

        ConfirmPaymentCommand command = new ConfirmPaymentCommand(
                PaymentProvider.SEPAY, orderCode, "TXN_1", Money.vnd(99000L),
                true, now, Collections.emptyMap());

        when(transactionRepositoryPort.existsByProviderAndTransactionId(PaymentProvider.SEPAY, "TXN_1")).thenReturn(false);
        when(orderRepositoryPort.findByOrderCodeForUpdate(orderCode)).thenReturn(Optional.of(order));
        when(planRepositoryPort.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> confirmPaymentUseCase.confirmPayment(command));

        assertEquals(PaymentErrorCode.PLAN_NOT_FOUND, exception.getErrorCode());
        assertEquals(SubscriptionDetailMessageKey.PLAN_NOT_FOUND, exception.getMessage());
    }
}
