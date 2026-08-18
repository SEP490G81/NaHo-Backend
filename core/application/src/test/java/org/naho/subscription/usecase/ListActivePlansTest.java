package org.naho.subscription.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.payment.model.Money;
import org.naho.subscription.mapper.SubscriptionPlanResultMapper;
import org.naho.subscription.model.SubscriptionPlan;
import org.naho.subscription.port.out.SubscriptionPlanRepositoryPort;
import org.naho.subscription.result.SubscriptionPlanResult;
import org.naho.subscription.type.PlanCode;
import org.naho.subscription.type.PlanStatus;
import org.naho.subscription.type.PlanTier;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListActivePlansTest {

    @Mock
    private SubscriptionPlanRepositoryPort planRepositoryPort;

    @Mock
    private SubscriptionPlanResultMapper planResultMapper;

    @InjectMocks
    private ListActivePlansUseCase listActivePlansUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy danh sách các gói dịch vụ đang hoạt động thành công khi có dữ liệu")
    void UTCID01_ListActivePlans_HasData_Success() {
        // Arrange
        SubscriptionPlan plan1 = SubscriptionPlan.builder()
                .id(1L)
                .code(PlanCode.FREE)
                .price(new Money(BigDecimal.ZERO, Currency.getInstance("VND")))
                .tier(PlanTier.FREE)
                .status(PlanStatus.ACTIVE)
                .build();

        SubscriptionPlan plan2 = SubscriptionPlan.builder()
                .id(2L)
                .code(PlanCode.BASIC)
                .price(new Money(BigDecimal.valueOf(199000), Currency.getInstance("VND")))
                .tier(PlanTier.BASIC)
                .status(PlanStatus.ACTIVE)
                .build();

        SubscriptionPlanResult result1 = mock(SubscriptionPlanResult.class);
        SubscriptionPlanResult result2 = mock(SubscriptionPlanResult.class);

        when(planRepositoryPort.findAllActive()).thenReturn(List.of(plan1, plan2));
        when(planResultMapper.mapToPlanResult(plan1)).thenReturn(result1);
        when(planResultMapper.mapToPlanResult(plan2)).thenReturn(result2);

        // Act
        List<SubscriptionPlanResult> results = listActivePlansUseCase.listActivePlans();

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(result1, results.get(0));
        assertEquals(result2, results.get(1));

        verify(planRepositoryPort, times(1)).findAllActive();
        verify(planResultMapper, times(1)).mapToPlanResult(plan1);
        verify(planResultMapper, times(1)).mapToPlanResult(plan2);
    }

    @Test
    @DisplayName("UTCID02 - Lấy danh sách gói dịch vụ trả về danh sách rỗng khi không có gói active")
    void UTCID02_ListActivePlans_EmptyList_Success() {
        // Arrange
        when(planRepositoryPort.findAllActive()).thenReturn(Collections.emptyList());

        // Act
        List<SubscriptionPlanResult> results = listActivePlansUseCase.listActivePlans();

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());

        verify(planRepositoryPort, times(1)).findAllActive();
        verifyNoInteractions(planResultMapper);
    }
}
