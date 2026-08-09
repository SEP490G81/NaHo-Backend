package org.naho.league.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.result.FileResult;
import org.naho.league.mapper.LeagueResultMapper;
import org.naho.league.model.League;
import org.naho.league.port.out.LeagueRepositoryPort;
import org.naho.league.result.LeagueResult;
import org.naho.user.port.out.UserRepositoryPort;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindAllTest {

    @Mock
    private LeagueRepositoryPort leagueRepositoryPort;

    @Mock
    private LeagueResultMapper leagueResultMapper;

    @Mock
    private CrudFileInputPort crudFileInputPort;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private CrudLeagueUseCase crudLeagueUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy tất cả danh sách giải đấu thành công khi hệ thống có dữ liệu giải đấu")
    void UTCID01_FindAllSuccess() {
        // Arrange
        League league1 = mock(League.class);
        League league2 = mock(League.class);
        when(league1.getId()).thenReturn(1L);
        when(league2.getId()).thenReturn(2L);
        when(league1.getIconFileId()).thenReturn(10L);
        when(league2.getIconFileId()).thenReturn(20L);

        FileResult fileResult1 = mock(FileResult.class);
        FileResult fileResult2 = mock(FileResult.class);
        when(fileResult1.id()).thenReturn(10L);
        when(fileResult2.id()).thenReturn(20L);

        LeagueResult expectedResult1 = mock(LeagueResult.class);
        LeagueResult expectedResult2 = mock(LeagueResult.class);

        when(leagueRepositoryPort.findAll()).thenReturn(List.of(league1, league2));
        when(crudFileInputPort.findAllByLeagueIds(List.of(1L, 2L))).thenReturn(List.of(fileResult1, fileResult2));
        when(leagueResultMapper.domainToResult(league1, fileResult1)).thenReturn(expectedResult1);
        when(leagueResultMapper.domainToResult(league2, fileResult2)).thenReturn(expectedResult2);

        // Act
        List<LeagueResult> results = crudLeagueUseCase.findAll();

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(expectedResult1, results.get(0));
        assertEquals(expectedResult2, results.get(1));
        verify(leagueRepositoryPort, times(1)).findAll();
        verify(crudFileInputPort, times(1)).findAllByLeagueIds(List.of(1L, 2L));
        verify(leagueResultMapper, times(1)).domainToResult(league1, fileResult1);
        verify(leagueResultMapper, times(1)).domainToResult(league2, fileResult2);
    }

    @Test
    @DisplayName("UTCID02 - Lấy tất cả danh sách giải đấu thành công khi hệ thống chưa có giải đấu nào (danh sách rỗng)")
    void UTCID02_FindAllEmpty() {
        // Arrange
        when(leagueRepositoryPort.findAll()).thenReturn(Collections.emptyList());
        when(crudFileInputPort.findAllByLeagueIds(Collections.emptyList())).thenReturn(Collections.emptyList());

        // Act
        List<LeagueResult> results = crudLeagueUseCase.findAll();

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(leagueRepositoryPort, times(1)).findAll();
        verify(crudFileInputPort, times(1)).findAllByLeagueIds(Collections.emptyList());
        verify(leagueResultMapper, never()).domainToResult(any(), any());
    }
}
