package org.naho.subscription.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.i18n.message.subscription.SubscriptionDetailMessageKey;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.payment.model.Money;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.subscription.command.UpdateSubscriptionPlanCommand;
import org.naho.subscription.exception.SubscriptionErrorCode;
import org.naho.subscription.mapper.SubscriptionPlanResultMapper;
import org.naho.subscription.model.SubscriptionPlan;
import org.naho.subscription.port.out.SubscriptionPlanRepositoryPort;
import org.naho.subscription.result.SubscriptionPlanResult;
import org.naho.subscription.type.PlanCode;
import org.naho.subscription.type.PlanStatus;
import org.naho.subscription.type.PlanTier;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.model.Role;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.type.RoleName;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateSubscriptionPlanTest {

    @Mock
    private RoleRepositoryPort roleRepositoryPort;

    @Mock
    private SubscriptionPlanRepositoryPort planRepositoryPort;

    @Mock
    private SubscriptionPlanResultMapper planResultMapper;

    @Mock
    private TransactionPort transactionPort;

    private UpdateSubscriptionPlanUseCase updateSubscriptionPlanUseCase;

    @BeforeEach
    void setUp() {
        lenient().doAnswer(invocation -> {
            Supplier<?> supplier = invocation.getArgument(0);
            return supplier.get();
        }).when(transactionPort).execute(any(Supplier.class));

        updateSubscriptionPlanUseCase = new UpdateSubscriptionPlanUseCase(
                roleRepositoryPort,
                planRepositoryPort,
                planResultMapper,
                transactionPort
        );
    }

    @Test
    @DisplayName("UTCID01 - Cập nhật gói cước thất bại khi người dùng không có quyền ADMIN")
    void UTCID01_UpdateSubscriptionPlan_AccessDenied() {
        // Arrange
        Long planId = 1L;
        Long userId = 10L;
        UpdateSubscriptionPlanCommand command = new UpdateSubscriptionPlanCommand(
                planId,
                userId,
                "Description",
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

        when(roleRepositoryPort.findRoleNamesByUserId(userId)).thenReturn(List.of("LEARNER"));
        when(roleRepositoryPort.findByUserId(userId)).thenReturn(Optional.of(
                Role.builder().id(1L).userIds(Set.of(userId)).roleName(RoleName.LEARNER).build()
        ));

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> updateSubscriptionPlanUseCase.updateSubscriptionPlan(command)
        );

        assertEquals(UserErrorCode.USER_ACCESS_DENIED, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_ACCESS_DENIED, exception.getMessage());
        verify(planRepositoryPort, never()).findById(any());
        verify(planRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("UTCID02 - Cập nhật gói cước thất bại khi không tìm thấy gói cước theo ID")
    void UTCID02_UpdateSubscriptionPlan_PlanNotFound() {
        // Arrange
        Long planId = 999L;
        Long adminUserId = 1L;
        UpdateSubscriptionPlanCommand command = new UpdateSubscriptionPlanCommand(
                planId,
                adminUserId,
                "Description",
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

        when(roleRepositoryPort.findRoleNamesByUserId(adminUserId)).thenReturn(List.of("ADMIN"));
        when(planRepositoryPort.findById(planId)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> updateSubscriptionPlanUseCase.updateSubscriptionPlan(command)
        );

        assertEquals(SubscriptionErrorCode.PLAN_NOT_FOUND, exception.getErrorCode());
        assertEquals(SubscriptionDetailMessageKey.PLAN_NOT_FOUND, exception.getMessage());
        verify(planRepositoryPort, times(1)).findById(planId);
        verify(planRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("UTCID03 - ADMIN cập nhật thành công thông tin gói cước bao gồm giá mới và các giới hạn")
    void UTCID03_UpdateSubscriptionPlan_Success() {
        // Arrange
        Long planId = 2L;
        Long adminUserId = 1L;
        UpdateSubscriptionPlanCommand command = new UpdateSubscriptionPlanCommand(
                planId,
                adminUserId,
                "Updated Description",
                PlanTier.BASIC,
                BigDecimal.valueOf(250000),
                "VND",
                30,
                100,
                120,
                20,
                10,
                90,
                5,
                true,
                PlanStatus.ACTIVE
        );

        SubscriptionPlan existingPlan = SubscriptionPlan.builder()
                .id(planId)
                .code(PlanCode.BASIC)
                .description("Old Description")
                .tier(PlanTier.BASIC)
                .price(new Money(BigDecimal.valueOf(199000), Currency.getInstance("VND")))
                .durationDays(30)
                .status(PlanStatus.ACTIVE)
                .build();

        SubscriptionPlanResult expectedResult = mock(SubscriptionPlanResult.class);

        when(roleRepositoryPort.findRoleNamesByUserId(adminUserId)).thenReturn(List.of("ADMIN"));
        when(planRepositoryPort.findById(planId)).thenReturn(Optional.of(existingPlan));
        when(planRepositoryPort.save(any(SubscriptionPlan.class))).thenReturn(existingPlan);
        when(planResultMapper.mapToPlanResult(existingPlan)).thenReturn(expectedResult);

        // Act
        SubscriptionPlanResult result = updateSubscriptionPlanUseCase.updateSubscriptionPlan(command);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResult, result);
        assertEquals("Updated Description", existingPlan.getDescription());
        assertEquals(BigDecimal.valueOf(250000), existingPlan.getPrice().amount());

        verify(planRepositoryPort, times(1)).findById(planId);
        verify(planRepositoryPort, times(1)).save(existingPlan);
        verify(planResultMapper, times(1)).mapToPlanResult(existingPlan);
    }

    @Test
    @DisplayName("UTCID04 - Xác thực quyền ADMIN qua đối tượng Role và cập nhật gói cước thành công")
    void UTCID04_UpdateSubscriptionPlan_AdminVerifiedViaRoleObject_Success() {
        // Arrange
        Long planId = 2L;
        Long adminUserId = 1L;
        UpdateSubscriptionPlanCommand command = new UpdateSubscriptionPlanCommand(
                planId,
                adminUserId,
                "Updated Description",
                PlanTier.BASIC,
                null,
                null,
                30,
                100,
                120,
                20,
                10,
                90,
                5,
                true,
                PlanStatus.ACTIVE
        );

        SubscriptionPlan existingPlan = SubscriptionPlan.builder()
                .id(planId)
                .code(PlanCode.BASIC)
                .description("Old Description")
                .tier(PlanTier.BASIC)
                .price(new Money(BigDecimal.valueOf(199000), Currency.getInstance("VND")))
                .durationDays(30)
                .status(PlanStatus.ACTIVE)
                .build();

        SubscriptionPlanResult expectedResult = mock(SubscriptionPlanResult.class);

        when(roleRepositoryPort.findRoleNamesByUserId(adminUserId)).thenReturn(List.of());
        when(roleRepositoryPort.findByUserId(adminUserId)).thenReturn(Optional.of(
                Role.builder().id(1L).userIds(Set.of(adminUserId)).roleName(RoleName.ADMIN).build()
        ));
        when(planRepositoryPort.findById(planId)).thenReturn(Optional.of(existingPlan));
        when(planRepositoryPort.save(any(SubscriptionPlan.class))).thenReturn(existingPlan);
        when(planResultMapper.mapToPlanResult(existingPlan)).thenReturn(expectedResult);

        // Act
        SubscriptionPlanResult result = updateSubscriptionPlanUseCase.updateSubscriptionPlan(command);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(roleRepositoryPort, times(1)).findByUserId(adminUserId);
        verify(planRepositoryPort, times(1)).save(existingPlan);
    }
}
