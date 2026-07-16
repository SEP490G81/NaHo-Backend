package org.naho.season.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.i18n.message.season.SeasonDetailMessageKey;
import org.naho.season.command.CreateSeasonCommand;
import org.naho.season.exception.SeasonErrorCode;
import org.naho.season.mapper.SeasonResultMapper;
import org.naho.season.model.Season;
import org.naho.season.port.out.SeasonRepositoryPort;
import org.naho.season.result.SeasonResult;
import org.naho.shared.exception.ApplicationException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrudSeasonUseCaseTest {

    @Mock
    private SeasonRepositoryPort seasonRepositoryPort;

    @Mock
    private SeasonResultMapper seasonResultMapper;

    @InjectMocks
    private CrudSeasonUseCase crudSeasonUseCase;

    @Test
    void should_CreateNextSeason_Successfully_When_NoOverlapping() {
        // Arrange
        Instant startAt = Instant.now().plus(1, ChronoUnit.DAYS);
        Instant endAt = Instant.now().plus(2, ChronoUnit.DAYS);
        CreateSeasonCommand command = new CreateSeasonCommand(startAt, endAt);

        Season savedSeason = Season.builder()
                .id(1L)
                .seasonNo(1)
                .startAt(startAt)
                .endAt(endAt)
                .build();

        SeasonResult expectedResult = new SeasonResult(1L, 1, startAt, endAt);

        when(seasonRepositoryPort.existsOverlappingSeason(command)).thenReturn(false);
        when(seasonRepositoryPort.findLatestSeasonNo()).thenReturn(null);
        when(seasonRepositoryPort.save(any(Season.class))).thenReturn(savedSeason);
        when(seasonResultMapper.domainToResult(savedSeason)).thenReturn(expectedResult);

        // Act
        SeasonResult result = crudSeasonUseCase.createNextSeason(command);

        // Assert
        assertEquals(expectedResult, result);
        verify(seasonRepositoryPort, times(1)).existsOverlappingSeason(command);
        verify(seasonRepositoryPort, times(1)).findLatestSeasonNo();
        verify(seasonRepositoryPort, times(1)).save(any(Season.class));
        verify(seasonResultMapper, times(1)).domainToResult(savedSeason);
    }

    @Test
    void should_ThrowApplicationException_When_OverlappingSeasonExists() {
        // Arrange
        Instant startAt = Instant.now().plus(1, ChronoUnit.DAYS);
        Instant endAt = Instant.now().plus(2, ChronoUnit.DAYS);
        CreateSeasonCommand command = new CreateSeasonCommand(startAt, endAt);

        when(seasonRepositoryPort.existsOverlappingSeason(command)).thenReturn(true);

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> crudSeasonUseCase.createNextSeason(command)
        );

        assertEquals(SeasonErrorCode.SEASON_OVERLAPPING, exception.getErrorCode());
        assertEquals(SeasonDetailMessageKey.SEASON_CREATION_OVERLAPPING, exception.getMessage());

        verify(seasonRepositoryPort, times(1)).existsOverlappingSeason(command);
        verifyNoMoreInteractions(seasonRepositoryPort);
        verifyNoInteractions(seasonResultMapper);
    }
}
