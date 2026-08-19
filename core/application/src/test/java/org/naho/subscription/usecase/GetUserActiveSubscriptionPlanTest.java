package org.naho.subscription.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.i18n.message.subscription.SubscriptionDetailMessageKey;
import org.naho.payment.model.Money;
import org.naho.shared.exception.ApplicationException;
import org.naho.subscription.exception.SubscriptionErrorCode;
import org.naho.subscription.mapper.SubscriptionPlanResultMapper;
import org.naho.subscription.mapper.UserSubscriptionResultMapper;
import org.naho.subscription.model.SubscriptionPlan;
import org.naho.subscription.port.out.SubscriptionPlanRepositoryPort;
import org.naho.subscription.port.out.UserSubscriptionRepositoryPort;
import org.naho.subscription.result.SubscriptionPlanResult;
import org.naho.subscription.type.PlanCode;
import org.naho.subscription.type.PlanStatus;
import org.naho.subscription.type.PlanTier;
import org.naho.subscription.type.SubscriptionStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserActiveSubscriptionPlanTest {

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
    @DisplayName("UTCID01 - Lấy thông tin gói dịch vụ đang hoạt động thành công khi người dùng có gói active")
    void UTCID01_GetUserActiveSubscriptionPlan_HasActivePlan_Success() {
        // Arrange
        Long userId = 1L;
        SubscriptionPlan activePlan = SubscriptionPlan.builder()
                .id(2L)
                .code(PlanCode.BASIC)
                .description("Basic Plan")
                .tier(PlanTier.BASIC)
                .price(new Money(BigDecimal.valueOf(199000), Currency.getInstance("VND")))
                .durationDays(30)
                .status(PlanStatus.ACTIVE)
                .build();

        SubscriptionPlanResult expectedResult = new SubscriptionPlanResult(
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

        when(subscriptionPlanRepositoryPort.findCurrentSubscriptionPlanByUserIdAndStatus(
                eq(userId), eq(SubscriptionStatus.ACTIVE), eq(PlanStatus.ACTIVE), any(Instant.class)
        )).thenReturn(Optional.of(activePlan));

        when(subscriptionPlanResultMapper.mapToPlanResult(activePlan)).thenReturn(expectedResult);

        // Act
        SubscriptionPlanResult result = getActiveSubscriptionUseCase.getUserActiveSubscriptionPlan(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.id());
        assertEquals(PlanCode.BASIC, result.code());
        assertEquals(PlanTier.BASIC, result.tier());

        verify(subscriptionPlanRepositoryPort, times(1))
                .findCurrentSubscriptionPlanByUserIdAndStatus(eq(userId), eq(SubscriptionStatus.ACTIVE), eq(PlanStatus.ACTIVE), any(Instant.class));
        verify(subscriptionPlanResultMapper, times(1)).mapToPlanResult(activePlan);
        verify(subscriptionPlanRepositoryPort, never()).findByCode(any());
    }

    @Test
    @DisplayName("UTCID02 - Fallback trả về gói cước FREE mặc định khi người dùng không có gói active")
    void UTCID02_GetUserActiveSubscriptionPlan_NoActivePlan_FallbackFreePlan_Success() {
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

        SubscriptionPlanResult expectedResult = new SubscriptionPlanResult(
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
        when(subscriptionPlanResultMapper.mapToPlanResult(freePlan)).thenReturn(expectedResult);

        // Act
        SubscriptionPlanResult result = getActiveSubscriptionUseCase.getUserActiveSubscriptionPlan(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(PlanCode.FREE, result.code());

        verify(subscriptionPlanRepositoryPort, times(1))
                .findCurrentSubscriptionPlanByUserIdAndStatus(eq(userId), eq(SubscriptionStatus.ACTIVE), eq(PlanStatus.ACTIVE), any(Instant.class));
        verify(subscriptionPlanRepositoryPort, times(1)).findByCode(PlanCode.FREE);
        verify(subscriptionPlanResultMapper, times(1)).mapToPlanResult(freePlan);
    }

    @Test
    @DisplayName("UTCID03 - Lấy gói dịch vụ thất bại khi không tìm thấy gói FREE mặc định trong hệ thống")
    void UTCID03_GetUserActiveSubscriptionPlan_FreePlanNotFound() {
        // Arrange
        Long userId = 1L;
        when(subscriptionPlanRepositoryPort.findCurrentSubscriptionPlanByUserIdAndStatus(
                eq(userId), eq(SubscriptionStatus.ACTIVE), eq(PlanStatus.ACTIVE), any(Instant.class)
        )).thenReturn(Optional.empty());

        when(subscriptionPlanRepositoryPort.findByCode(PlanCode.FREE)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> getActiveSubscriptionUseCase.getUserActiveSubscriptionPlan(userId)
        );

        assertEquals(SubscriptionErrorCode.PLAN_NOT_FOUND, exception.getErrorCode());
        assertEquals(SubscriptionDetailMessageKey.PLAN_NOT_FOUND, exception.getMessage());
        verify(subscriptionPlanRepositoryPort, times(1)).findByCode(PlanCode.FREE);
    }
}
