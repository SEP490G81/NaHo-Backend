package org.naho.daily.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.daily.mapper.DailyMissionResultMapper;
import org.naho.daily.model.DailyMission;
import org.naho.daily.port.out.DailyMissionRepositoryPort;
import org.naho.daily.result.DailyMissionResult;
import org.naho.daily.type.MissionType;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindDailyMissionByIdTest {

    @Mock
    private DailyMissionRepositoryPort dailyMissionRepositoryPort;

    @Mock
    private DailyMissionResultMapper dailyMissionResultMapper;

    @InjectMocks
    private CrudDailyMissionUseCase crudDailyMissionUseCase;

    @Test
    @DisplayName("UTCID01 - Tìm nhiệm vụ hằng ngày theo ID thành công")
    void UTCID01_FindDailyMissionByIdSuccess() {
        // Arrange
        Long id = 1L;
        DailyMission dailyMission = DailyMission.builder()
                .id(id)
                .point(10.0)
                .missionType(MissionType.TALK_WITH_AI)
                .build();
        DailyMissionResult expectedResult = mock(DailyMissionResult.class);

        when(dailyMissionRepositoryPort.findById(id)).thenReturn(Optional.of(dailyMission));
        when(dailyMissionResultMapper.domainToResult(dailyMission)).thenReturn(expectedResult);

        // Act
        Optional<DailyMissionResult> result = crudDailyMissionUseCase.findById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedResult, result.get());
        verify(dailyMissionRepositoryPort, times(1)).findById(id);
        verify(dailyMissionResultMapper, times(1)).domainToResult(dailyMission);
    }

    @Test
    @DisplayName("UTCID02 - Tìm nhiệm vụ hằng ngày theo ID không tìm thấy")
    void UTCID02_FindDailyMissionByIdNotFound() {
        // Arrange
        Long id = 999L;
        when(dailyMissionRepositoryPort.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<DailyMissionResult> result = crudDailyMissionUseCase.findById(id);

        // Assert
        assertTrue(result.isEmpty());
        verify(dailyMissionRepositoryPort, times(1)).findById(id);
        verify(dailyMissionResultMapper, never()).domainToResult(any());
    }
}
