package org.naho.subscription.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.payment.model.Money;
import org.naho.subscription.mapper.SubscriptionPlanResultMapper;
import org.naho.subscription.mapper.UserSubscriptionResultMapper;
import org.naho.subscription.model.SubscriptionPlan;
import org.naho.subscription.model.UserSubscription;
import org.naho.subscription.port.out.SubscriptionPlanRepositoryPort;
import org.naho.subscription.port.out.UserSubscriptionRepositoryPort;
import org.naho.subscription.result.SubscriptionPlanResult;
import org.naho.subscription.result.UserSubscriptionResult;
import org.naho.subscription.type.PlanCode;
import org.naho.subscription.type.PlanStatus;
import org.naho.subscription.type.PlanTier;
import org.naho.subscription.type.SubscriptionStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Currency;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserActiveSubscriptionTest {

    @Mock
    private SubscriptionPlanRepositoryPort subscriptionPlanRepositoryPort;

    @Mock
    private SubscriptionPlanResultMapper subscriptionPlanResultMapper;

    @Mock
    private UserSubscriptionResultMapper userSubscriptionResultMapper;

    @Mock
    private UserSubscriptionRepositoryPort userSubscriptionRepositoryPort;

    @InjectMocks
    private GetActiveSubscriptionUseCase getActiveSubscriptionUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy thông tin đăng ký gói cước thành công khi người dùng có bản ghi UserSubscription")
    void UTCID01_GetUserActiveSubscription_HasUserSubscription_Success() {
        // Arrange
        Long userId = 1L;
        Instant now = Instant.now();

        SubscriptionPlan activePlan = SubscriptionPlan.builder()
                .id(2L)
                .code(PlanCode.BASIC)
                .description("Basic Plan")
                .tier(PlanTier.BASIC)
                .price(new Money(BigDecimal.valueOf(199000), Currency.getInstance("VND")))
                .durationDays(30)
                .status(PlanStatus.ACTIVE)
                .build();

        SubscriptionPlanResult planResult = new SubscriptionPlanResult(
                2L,
                PlanCode.BASIC,
                "Basic Plan",
                PlanTier.BASIC,
                BigDecimal.valueOf(199000),
                "VND",
                30,
                50,
                60,
                10,
                5,
                60,
                3,
                true,
                PlanStatus.ACTIVE
        );

        UserSubscription userSub = UserSubscription.builder()
                .id(100L)
                .userId(userId)
                .subscriptionPlanId(2L)
                .paymentOrderId(50L)
                .status(SubscriptionStatus.ACTIVE)
                .startTime(now.minus(5, ChronoUnit.DAYS))
                .endTime(now.plus(25, ChronoUnit.DAYS))
                .build();

        UserSubscriptionResult expectedResult = new UserSubscriptionResult(
                100L,
                userId,
                2L,
                50L,
                SubscriptionStatus.ACTIVE,
                now.minus(5, ChronoUnit.DAYS),
                now.plus(25, ChronoUnit.DAYS),
                planResult
        );

        when(subscriptionPlanRepositoryPort.findCurrentSubscriptionPlanByUserIdAndStatus(
                eq(userId), eq(SubscriptionStatus.ACTIVE), eq(PlanStatus.ACTIVE), any(Instant.class)
        )).thenReturn(Optional.of(activePlan));
        when(subscriptionPlanResultMapper.mapToPlanResult(activePlan)).thenReturn(planResult);

        when(userSubscriptionRepositoryPort.findActiveByUserId(eq(userId), any(Instant.class)))
                .thenReturn(Optional.of(userSub));
        when(userSubscriptionResultMapper.mapToUserSubscriptionResult(userSub, planResult))
                .thenReturn(expectedResult);

        // Act
        UserSubscriptionResult result = getActiveSubscriptionUseCase.getUserActiveSubscription(userId);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.id());
        assertEquals(userId, result.userId());
        assertEquals(2L, result.subscriptionPlanId());
        assertEquals(SubscriptionStatus.ACTIVE, result.status());
        assertNotNull(result.subscriptionPlan());

        verify(userSubscriptionRepositoryPort, times(1)).findActiveByUserId(eq(userId), any(Instant.class));
        verify(userSubscriptionResultMapper, times(1)).mapToUserSubscriptionResult(userSub, planResult);
    }

    @Test
    @DisplayName("UTCID02 - Trả về kết quả UserSubscription mặc định khi người dùng sử dụng gói FREE")
    void UTCID02_GetUserActiveSubscription_DefaultFreeNoSubscription_Success() {
        // Arrange
        Long userId = 1L;

        SubscriptionPlan freePlan = SubscriptionPlan.builder()
                .id(1L)
                .code(PlanCode.FREE)
                .description("Free Plan")
                .tier(PlanTier.FREE)
                .price(new Money(BigDecimal.ZERO, Currency.getInstance("VND")))
                .status(PlanStatus.ACTIVE)
                .build();

        SubscriptionPlanResult freePlanResult = new SubscriptionPlanResult(
                1L,
                PlanCode.FREE,
                "Free Plan",
                PlanTier.FREE,
                BigDecimal.ZERO,
                "VND",
                null,
                10,
                30,
                3,
                1,
                30,
                1,
                false,
                PlanStatus.ACTIVE
        );

        when(subscriptionPlanRepositoryPort.findCurrentSubscriptionPlanByUserIdAndStatus(
                eq(userId), eq(SubscriptionStatus.ACTIVE), eq(PlanStatus.ACTIVE), any(Instant.class)
        )).thenReturn(Optional.empty());
        when(subscriptionPlanRepositoryPort.findByCode(PlanCode.FREE)).thenReturn(Optional.of(freePlan));
        when(subscriptionPlanResultMapper.mapToPlanResult(freePlan)).thenReturn(freePlanResult);

        when(userSubscriptionRepositoryPort.findActiveByUserId(eq(userId), any(Instant.class)))
                .thenReturn(Optional.empty());

        // Act
        UserSubscriptionResult result = getActiveSubscriptionUseCase.getUserActiveSubscription(userId);

        // Assert
        assertNotNull(result);
        assertNull(result.id());
        assertEquals(userId, result.userId());
        assertEquals(1L, result.subscriptionPlanId());
        assertEquals(SubscriptionStatus.ACTIVE, result.status());
        assertNull(result.paymentOrderId());
        assertEquals(freePlanResult, result.subscriptionPlan());

        verify(userSubscriptionRepositoryPort, times(1)).findActiveByUserId(eq(userId), any(Instant.class));
        verifyNoInteractions(userSubscriptionResultMapper);
    }
}
