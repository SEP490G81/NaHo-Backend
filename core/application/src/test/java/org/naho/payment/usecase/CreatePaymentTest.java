package org.naho.payment.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.i18n.message.payment.PaymentDetailMessageKey;
import org.naho.i18n.message.subscription.SubscriptionDetailMessageKey;
import org.naho.payment.command.CreatePaymentCommand;
import org.naho.payment.exception.PaymentErrorCode;
import org.naho.payment.model.Money;
import org.naho.payment.model.PaymentIdempotency;
import org.naho.payment.model.PaymentOrder;
import org.naho.payment.port.out.*;
import org.naho.payment.result.CreatePaymentResult;
import org.naho.payment.type.PaymentProvider;
import org.naho.payment.type.PaymentReuseReason;
import org.naho.payment.type.PaymentStatus;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.subscription.exception.SubscriptionErrorCode;
import org.naho.subscription.model.SubscriptionPlan;
import org.naho.subscription.model.UserSubscription;
import org.naho.subscription.port.out.SubscriptionPlanRepositoryPort;
import org.naho.subscription.port.out.UserSubscriptionRepositoryPort;
import org.naho.subscription.type.PlanCode;
import org.naho.subscription.type.PlanStatus;
import org.naho.subscription.type.PlanTier;
import org.naho.user.port.out.UserRepositoryPort;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreatePaymentTest {

    @Mock
    private SubscriptionPlanRepositoryPort planRepositoryPort;

    @Mock
    private PaymentOrderRepositoryPort paymentOrderRepositoryPort;

    @Mock
    private UserSubscriptionRepositoryPort subscriptionRepositoryPort;

    @Mock
    private PaymentGatewayResolver gatewayResolver;

    @Mock
    private PaymentOrderCodeGenerator orderCodeGenerator;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private PaymentIdempotencyRepositoryPort idempotencyRepositoryPort;

    @Mock
    private TransactionPort transactionPort;

    @Mock
    private PaymentGatewayPort paymentGatewayPort;

    private CreatePaymentUseCase createPaymentUseCase;

    @BeforeEach
    void setUp() {
        lenient().when(transactionPort.execute(any(Supplier.class))).thenAnswer(invocation -> {
            Supplier<?> supplier = invocation.getArgument(0);
            return supplier.get();
        });

        createPaymentUseCase = new CreatePaymentUseCase(
                planRepositoryPort,
                paymentOrderRepositoryPort,
                subscriptionRepositoryPort,
                gatewayResolver,
                orderCodeGenerator,
                userRepositoryPort,
                idempotencyRepositoryPort,
                5,
                transactionPort
        );
    }

    @Test
    @DisplayName("UTCID01 - Tạo đơn thanh toán mới thành công qua cổng gateway")
    void UTCID01_CreatePayment_Success() {
        // Arrange
        Long userId = 100L;
        String idempotencyKey = "IDEM_123";
        CreatePaymentCommand command = new CreatePaymentCommand(
                userId, PlanCode.BASIC, PaymentProvider.SEPAY, "127.0.0.1", "vi", idempotencyKey);

        Instant now = Instant.now();
        SubscriptionPlan plan = SubscriptionPlan.builder()
                .id(1L)
                .code(PlanCode.BASIC)
                .description("Gói tháng")
                .tier(PlanTier.BASIC)
                .price(Money.vnd(99000L))
                .durationDays(30)
                .status(PlanStatus.ACTIVE)
                .build();

        PaymentOrder savedOrder = PaymentOrder.builder()
                .id(1L)
                .orderCode("ORDER123")
                .userId(userId)
                .subscriptionPlanId(1L)
                .amount(plan.getPrice())
                .provider(PaymentProvider.SEPAY)
                .status(PaymentStatus.PENDING)
                .createdTime(now)
                .expiresTime(now.plus(Duration.ofMinutes(5)))
                .build();

        when(planRepositoryPort.findActiveByCode(PlanCode.BASIC)).thenReturn(Optional.of(plan));
        when(subscriptionRepositoryPort.findActiveByUserId(eq(userId), any(Instant.class))).thenReturn(Optional.empty());
        when(idempotencyRepositoryPort.findByUserIdAndKey(userId, idempotencyKey)).thenReturn(Optional.empty());
        when(paymentOrderRepositoryPort.findPendingByUserId(userId)).thenReturn(Optional.empty());
        when(orderCodeGenerator.generate()).thenReturn("ORDER123");
        when(paymentOrderRepositoryPort.save(any(PaymentOrder.class))).thenReturn(savedOrder);
        when(gatewayResolver.resolve(PaymentProvider.SEPAY)).thenReturn(paymentGatewayPort);
        when(paymentGatewayPort.initialize(any(), any())).thenReturn(
                new PaymentGatewayPort.PaymentInitializationResult(
                        URI.create("https://sepay.vn/pay/ORDER123"), now.plus(Duration.ofMinutes(5)), Collections.emptyMap()));

        // Act
        CreatePaymentResult result = createPaymentUseCase.createPayment(command);

        // Assert
        assertNotNull(result);
        assertEquals("ORDER123", result.orderCode());
        assertFalse(result.reused());
        assertEquals(PaymentReuseReason.CREATED, result.reuseReason());
        verify(userRepositoryPort, times(1)).lockById(userId);
        verify(idempotencyRepositoryPort, times(1)).save(any(PaymentIdempotency.class));
    }

    @Test
    @DisplayName("UTCID02 - Ném ngoại lệ khi Idempotency Key không hợp lệ (null/blank)")
    void UTCID02_CreatePayment_InvalidIdempotencyKey() {
        // Arrange
        CreatePaymentCommand command = new CreatePaymentCommand(
                100L, PlanCode.BASIC, PaymentProvider.SEPAY, "127.0.0.1", "vi", "");

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> createPaymentUseCase.createPayment(command));

        assertEquals(PaymentErrorCode.INVALID_IDEMPOTENCY_KEY, exception.getErrorCode());
        assertEquals(PaymentDetailMessageKey.PAYMENT_IDEMPOTENCY_KEY_INVALID, exception.getMessage());
    }

    @Test
    @DisplayName("UTCID03 - Ném ngoại lệ khi gói cước không tồn tại")
    void UTCID03_CreatePayment_PlanNotFound() {
        // Arrange
        CreatePaymentCommand command = new CreatePaymentCommand(
                100L, PlanCode.BASIC, PaymentProvider.SEPAY, "127.0.0.1", "vi", "IDEM_1");

        when(planRepositoryPort.findActiveByCode(PlanCode.BASIC)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> createPaymentUseCase.createPayment(command));

        assertEquals(SubscriptionErrorCode.PLAN_NOT_FOUND, exception.getErrorCode());
        assertEquals(SubscriptionDetailMessageKey.PLAN_NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("UTCID04 - Ném ngoại lệ khi gói cước không mở bán (isAvailableForPurchase = false)")
    void UTCID04_CreatePayment_PlanUnavailable() {
        // Arrange
        CreatePaymentCommand command = new CreatePaymentCommand(
                100L, PlanCode.BASIC, PaymentProvider.SEPAY, "127.0.0.1", "vi", "IDEM_1");

        SubscriptionPlan inactivePlan = SubscriptionPlan.builder()
                .id(1L)
                .code(PlanCode.BASIC)
                .price(Money.vnd(99000L))
                .tier(PlanTier.BASIC)
                .status(PlanStatus.INACTIVE)
                .build();

        when(planRepositoryPort.findActiveByCode(PlanCode.BASIC)).thenReturn(Optional.of(inactivePlan));

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> createPaymentUseCase.createPayment(command));

        assertEquals(SubscriptionErrorCode.PLAN_UNAVAILABLE, exception.getErrorCode());
        assertEquals(SubscriptionDetailMessageKey.PLAN_UNAVAILABLE, exception.getMessage());
    }

    @Test
    @DisplayName("UTCID05 - Ném ngoại lệ khi chọn mua gói FREE")
    void UTCID05_CreatePayment_PlanFreeNotPurchasable() {
        // Arrange
        CreatePaymentCommand command = new CreatePaymentCommand(
                100L, PlanCode.FREE, PaymentProvider.SEPAY, "127.0.0.1", "vi", "IDEM_1");

        SubscriptionPlan freePlan = SubscriptionPlan.builder()
                .id(0L)
                .code(PlanCode.FREE)
                .description("Gói miễn phí")
                .tier(PlanTier.FREE)
                .price(Money.vnd(0L))
                .status(PlanStatus.ACTIVE)
                .build();

        when(planRepositoryPort.findActiveByCode(PlanCode.FREE)).thenReturn(Optional.of(freePlan));

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> createPaymentUseCase.createPayment(command));

        assertEquals(SubscriptionErrorCode.PLAN_UNAVAILABLE, exception.getErrorCode());
        assertEquals(SubscriptionDetailMessageKey.PLAN_FREE_NOT_PURCHASABLE, exception.getMessage());
    }

    @Test
    @DisplayName("UTCID06 - Ném ngoại lệ khi người dùng đang có gói active bậc cao hơn hoặc bằng")
    void UTCID06_CreatePayment_AlreadyActiveHigherOrEqualPlan() {
        // Arrange
        Long userId = 100L;
        CreatePaymentCommand command = new CreatePaymentCommand(
                userId, PlanCode.BASIC, PaymentProvider.SEPAY, "127.0.0.1", "vi", "IDEM_1");

        SubscriptionPlan targetPlan = SubscriptionPlan.builder()
                .id(1L)
                .code(PlanCode.BASIC)
                .description("Gói tháng BASIC")
                .tier(PlanTier.BASIC)
                .price(Money.vnd(99000L))
                .durationDays(30)
                .status(PlanStatus.ACTIVE)
                .build();

        SubscriptionPlan activeHigherPlan = SubscriptionPlan.builder()
                .id(10L)
                .code(PlanCode.PREMIUM)
                .description("Gói năm PREMIUM")
                .tier(PlanTier.PREMIUM)
                .price(Money.vnd(999000L))
                .durationDays(365)
                .status(PlanStatus.ACTIVE)
                .build();

        UserSubscription activeSub = mock(UserSubscription.class);
        when(activeSub.getSubscriptionPlanId()).thenReturn(10L);

        when(planRepositoryPort.findActiveByCode(PlanCode.BASIC)).thenReturn(Optional.of(targetPlan));
        when(subscriptionRepositoryPort.findActiveByUserId(eq(userId), any(Instant.class))).thenReturn(Optional.of(activeSub));
        when(planRepositoryPort.findById(10L)).thenReturn(Optional.of(activeHigherPlan));

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> createPaymentUseCase.createPayment(command));

        assertEquals(SubscriptionErrorCode.ALREADY_ACTIVE_HIGHER_OR_EQUAL_PLAN, exception.getErrorCode());
        assertEquals(SubscriptionDetailMessageKey.PLAN_ALREADY_ACTIVE_OR_HIGHER, exception.getMessage());
    }

    @Test
    @DisplayName("UTCID07 - Tái sử dụng đơn thanh toán cũ qua Idempotency Replay khi trùng key & hash")
    void UTCID07_CreatePayment_IdempotencyReplay_Success() {
        // Arrange
        Long userId = 100L;
        String idempotencyKey = "IDEM_KEY_REPLAY";
        CreatePaymentCommand command = new CreatePaymentCommand(
                userId, PlanCode.BASIC, PaymentProvider.SEPAY, "127.0.0.1", "vi", idempotencyKey);

        Instant now = Instant.now();
        SubscriptionPlan plan = SubscriptionPlan.builder()
                .id(1L)
                .code(PlanCode.BASIC)
                .description("Gói tháng")
                .tier(PlanTier.BASIC)
                .price(Money.vnd(99000L))
                .durationDays(30)
                .status(PlanStatus.ACTIVE)
                .build();

        PaymentOrder existingOrder = PaymentOrder.builder()
                .id(1L)
                .orderCode("ORDER_EXISTING")
                .userId(userId)
                .subscriptionPlanId(1L)
                .amount(plan.getPrice())
                .provider(PaymentProvider.SEPAY)
                .status(PaymentStatus.PENDING)
                .createdTime(now)
                .expiresTime(now.plus(Duration.ofMinutes(5)))
                .build();

        PaymentIdempotency mockedIdem = mock(PaymentIdempotency.class);
        when(mockedIdem.matchesRequestHash(anyString())).thenReturn(true);
        when(mockedIdem.getPaymentOrderId()).thenReturn(1L);

        when(planRepositoryPort.findActiveByCode(PlanCode.BASIC)).thenReturn(Optional.of(plan));
        when(subscriptionRepositoryPort.findActiveByUserId(eq(userId), any(Instant.class))).thenReturn(Optional.empty());
        when(idempotencyRepositoryPort.findByUserIdAndKey(userId, idempotencyKey)).thenReturn(Optional.of(mockedIdem));
        when(paymentOrderRepositoryPort.findById(1L)).thenReturn(Optional.of(existingOrder));

        // Act
        CreatePaymentResult result = createPaymentUseCase.createPayment(command);

        // Assert
        assertNotNull(result);
        assertTrue(result.reused());
        assertEquals(PaymentReuseReason.IDEMPOTENCY_REPLAY, result.reuseReason());
        verify(paymentOrderRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("UTCID08 - Ném ngoại lệ khi Idempotency Key bị dùng lại với payload yêu cầu khác")
    void UTCID08_CreatePayment_IdempotencyKeyReused_DifferentRequest() {
        // Arrange
        Long userId = 100L;
        String idempotencyKey = "IDEM_KEY_CONFLICT";
        CreatePaymentCommand command = new CreatePaymentCommand(
                userId, PlanCode.BASIC, PaymentProvider.SEPAY, "127.0.0.1", "vi", idempotencyKey);

        SubscriptionPlan plan = SubscriptionPlan.builder()
                .id(1L)
                .code(PlanCode.BASIC)
                .description("Gói tháng")
                .tier(PlanTier.BASIC)
                .price(Money.vnd(99000L))
                .durationDays(30)
                .status(PlanStatus.ACTIVE)
                .build();

        PaymentIdempotency mockedIdem = mock(PaymentIdempotency.class);
        when(mockedIdem.matchesRequestHash(anyString())).thenReturn(false);

        when(planRepositoryPort.findActiveByCode(PlanCode.BASIC)).thenReturn(Optional.of(plan));
        when(subscriptionRepositoryPort.findActiveByUserId(eq(userId), any(Instant.class))).thenReturn(Optional.empty());
        when(idempotencyRepositoryPort.findByUserIdAndKey(userId, idempotencyKey)).thenReturn(Optional.of(mockedIdem));

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> createPaymentUseCase.createPayment(command));

        assertEquals(PaymentErrorCode.IDEMPOTENCY_KEY_REUSED, exception.getErrorCode());
        assertEquals(PaymentDetailMessageKey.PAYMENT_IDEMPOTENCY_KEY_REUSED, exception.getMessage());
    }

    @Test
    @DisplayName("UTCID09 - Đơn pending cũ đã hết hạn -> expire và tạo đơn mới")
    void UTCID09_CreatePayment_PendingOrderExpired_CreateNew() {
        // Arrange
        Long userId = 100L;
        String idempotencyKey = "IDEM_NEW";
        CreatePaymentCommand command = new CreatePaymentCommand(
                userId, PlanCode.BASIC, PaymentProvider.SEPAY, "127.0.0.1", "vi", idempotencyKey);

        Instant now = Instant.now();
        SubscriptionPlan plan = SubscriptionPlan.builder()
                .id(1L)
                .code(PlanCode.BASIC)
                .description("Gói tháng")
                .tier(PlanTier.BASIC)
                .price(Money.vnd(99000L))
                .durationDays(30)
                .status(PlanStatus.ACTIVE)
                .build();

        PaymentOrder expiredPendingOrder = PaymentOrder.builder()
                .id(1L)
                .orderCode("ORDER_OLD")
                .userId(userId)
                .subscriptionPlanId(1L)
                .amount(plan.getPrice())
                .provider(PaymentProvider.SEPAY)
                .status(PaymentStatus.PENDING)
                .createdTime(now.minusSeconds(600))
                .expiresTime(now.minusSeconds(100))
                .build();

        PaymentOrder newOrder = PaymentOrder.builder()
                .id(2L)
                .orderCode("ORDER_NEW")
                .userId(userId)
                .subscriptionPlanId(1L)
                .amount(plan.getPrice())
                .provider(PaymentProvider.SEPAY)
                .status(PaymentStatus.PENDING)
                .createdTime(now)
                .expiresTime(now.plus(Duration.ofMinutes(5)))
                .build();

        when(planRepositoryPort.findActiveByCode(PlanCode.BASIC)).thenReturn(Optional.of(plan));
        when(subscriptionRepositoryPort.findActiveByUserId(eq(userId), any(Instant.class))).thenReturn(Optional.empty());
        when(idempotencyRepositoryPort.findByUserIdAndKey(userId, idempotencyKey)).thenReturn(Optional.empty());
        when(paymentOrderRepositoryPort.findPendingByUserId(userId)).thenReturn(Optional.of(expiredPendingOrder));
        when(orderCodeGenerator.generate()).thenReturn("ORDER_NEW");
        when(paymentOrderRepositoryPort.save(any(PaymentOrder.class))).thenReturn(newOrder);
        when(gatewayResolver.resolve(PaymentProvider.SEPAY)).thenReturn(paymentGatewayPort);
        when(paymentGatewayPort.initialize(any(), any())).thenReturn(
                new PaymentGatewayPort.PaymentInitializationResult(
                        URI.create("https://sepay.vn/pay/ORDER_NEW"), now.plus(Duration.ofMinutes(5)), Collections.emptyMap()));

        // Act
        CreatePaymentResult result = createPaymentUseCase.createPayment(command);

        // Assert
        assertNotNull(result);
        assertEquals(PaymentReuseReason.CREATED, result.reuseReason());
        assertEquals(PaymentStatus.EXPIRED, expiredPendingOrder.getStatus());
        verify(paymentOrderRepositoryPort, atLeast(2)).save(any(PaymentOrder.class));
    }

    @Test
    @DisplayName("UTCID10 - Tái sử dụng đơn pending còn hạn khi cùng gói và cổng thanh toán")
    void UTCID10_CreatePayment_PendingOrderReused_SamePlanAndProvider() {
        // Arrange
        Long userId = 100L;
        String idempotencyKey = "IDEM_SAME_PENDING";
        CreatePaymentCommand command = new CreatePaymentCommand(
                userId, PlanCode.BASIC, PaymentProvider.SEPAY, "127.0.0.1", "vi", idempotencyKey);

        Instant now = Instant.now();
        SubscriptionPlan plan = SubscriptionPlan.builder()
                .id(1L)
                .code(PlanCode.BASIC)
                .description("Gói tháng")
                .tier(PlanTier.BASIC)
                .price(Money.vnd(99000L))
                .durationDays(30)
                .status(PlanStatus.ACTIVE)
                .build();

        PaymentOrder activePendingOrder = PaymentOrder.builder()
                .id(1L)
                .orderCode("ORDER_ACTIVE_PENDING")
                .userId(userId)
                .subscriptionPlanId(1L)
                .amount(plan.getPrice())
                .provider(PaymentProvider.SEPAY)
                .status(PaymentStatus.PENDING)
                .createdTime(now)
                .expiresTime(now.plus(Duration.ofMinutes(5)))
                .build();

        when(planRepositoryPort.findActiveByCode(PlanCode.BASIC)).thenReturn(Optional.of(plan));
        when(subscriptionRepositoryPort.findActiveByUserId(eq(userId), any(Instant.class))).thenReturn(Optional.empty());
        when(idempotencyRepositoryPort.findByUserIdAndKey(userId, idempotencyKey)).thenReturn(Optional.empty());
        when(paymentOrderRepositoryPort.findPendingByUserId(userId)).thenReturn(Optional.of(activePendingOrder));

        // Act
        CreatePaymentResult result = createPaymentUseCase.createPayment(command);

        // Assert
        assertNotNull(result);
        assertTrue(result.reused());
        assertEquals(PaymentReuseReason.ACTIVE_PENDING_REUSED, result.reuseReason());
        verify(idempotencyRepositoryPort, times(1)).save(any(PaymentIdempotency.class));
    }

    @Test
    @DisplayName("UTCID11 - Ném ngoại lệ khi có đơn pending khác gói hoặc khác cổng thanh toán")
    void UTCID11_CreatePayment_ActiveOrderExists_DifferentPlanOrProvider() {
        // Arrange
        Long userId = 100L;
        String idempotencyKey = "IDEM_DIFF";
        CreatePaymentCommand command = new CreatePaymentCommand(
                userId, PlanCode.BASIC, PaymentProvider.SEPAY, "127.0.0.1", "vi", idempotencyKey);

        Instant now = Instant.now();
        SubscriptionPlan plan = SubscriptionPlan.builder()
                .id(1L)
                .code(PlanCode.BASIC)
                .description("Gói tháng")
                .tier(PlanTier.BASIC)
                .price(Money.vnd(99000L))
                .durationDays(30)
                .status(PlanStatus.ACTIVE)
                .build();

        PaymentOrder differentPendingOrder = PaymentOrder.builder()
                .id(2L)
                .orderCode("ORDER_DIFF")
                .userId(userId)
                .subscriptionPlanId(2L)
                .amount(plan.getPrice())
                .provider(PaymentProvider.SEPAY)
                .status(PaymentStatus.PENDING)
                .createdTime(now)
                .expiresTime(now.plus(Duration.ofMinutes(5)))
                .build();

        when(planRepositoryPort.findActiveByCode(PlanCode.BASIC)).thenReturn(Optional.of(plan));
        when(subscriptionRepositoryPort.findActiveByUserId(eq(userId), any(Instant.class))).thenReturn(Optional.empty());
        when(idempotencyRepositoryPort.findByUserIdAndKey(userId, idempotencyKey)).thenReturn(Optional.empty());
        when(paymentOrderRepositoryPort.findPendingByUserId(userId)).thenReturn(Optional.of(differentPendingOrder));

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> createPaymentUseCase.createPayment(command));

        assertEquals(PaymentErrorCode.PAYMENT_ACTIVE_ORDER_EXISTS, exception.getErrorCode());
        assertEquals(PaymentDetailMessageKey.PAYMENT_ACTIVE_ORDER_EXISTS, exception.getMessage());
    }
}
