package org.naho.subscription.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.subscription.mapper.UserDailyAiUsageResultMapper;
import org.naho.subscription.model.UserDailyAiUsage;
import org.naho.subscription.port.out.UserDailyAiUsageRepositoryPort;
import org.naho.subscription.result.UserDailyAiUsageResult;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindTodayUserDailyAiUsageTest {

    @Mock
    private UserDailyAiUsageRepositoryPort userDailyAiUsageRepositoryPort;

    @Mock
    private UserDailyAiUsageResultMapper userDailyAiUsageResultMapper;

    @InjectMocks
    private CrudUserDailyAiUsageUseCase crudUserDailyAiUsageUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy lượt sử dụng AI trong ngày của người dùng thành công")
    void UTCID01_FindTodayUserDailyAiUsage_Success() {
        // Arrange
        Long userId = 1L;
        LocalDate today = LocalDate.now();

        UserDailyAiUsage usage = UserDailyAiUsage.builder()
                .id(10L)
                .userId(userId)
                .usageDate(today)
                .speakingEvaluationCount(3)
                .aiSessionStartCount(1)
                .build();

        UserDailyAiUsageResult expectedResult = UserDailyAiUsageResult.builder()
                .id(10L)
                .userId(userId)
                .usageDate(today)
                .speakingEvaluationCount(3)
                .aiSessionStartCount(1)
                .build();

        when(userDailyAiUsageRepositoryPort.findByUserIdAndUsageDateCreateIfNotExists(eq(userId), any(LocalDate.class)))
                .thenReturn(usage);
        when(userDailyAiUsageResultMapper.domainToResult(usage)).thenReturn(expectedResult);

        // Act
        UserDailyAiUsageResult result = crudUserDailyAiUsageUseCase.findTodayUserDailyAiUsage(userId);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.id());
        assertEquals(userId, result.userId());
        assertEquals(3, result.speakingEvaluationCount());
        assertEquals(1, result.aiSessionStartCount());

        verify(userDailyAiUsageRepositoryPort, times(1))
                .findByUserIdAndUsageDateCreateIfNotExists(eq(userId), any(LocalDate.class));
        verify(userDailyAiUsageResultMapper, times(1)).domainToResult(usage);
    }
}
