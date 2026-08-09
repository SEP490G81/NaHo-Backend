package org.naho.league.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.file.port.in.CrudFileInputPort;
import org.naho.league.mapper.LeagueResultMapper;
import org.naho.league.port.out.LeagueRepositoryPort;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.result.LeaderboardUserResult;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindTop10OrderByTotalPointInLeagueTest {

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
    @DisplayName("UTCID01 - Lấy top 10 người dùng theo tổng điểm trong giải đấu thành công khi leagueId hợp lệ và có dữ liệu")
    void UTCID01_FindTop10OrderByTotalPointInLeagueSuccess() {
        // Arrange
        Long leagueId = 1L;
        LeaderboardUserResult user1 = mock(LeaderboardUserResult.class);
        LeaderboardUserResult user2 = mock(LeaderboardUserResult.class);

        when(userRepositoryPort.findTop10OrderByTotalPointInLeague(leagueId))
                .thenReturn(List.of(user1, user2));

        // Act
        List<LeaderboardUserResult> results = crudLeagueUseCase.findTop10OrderByTotalPointInLeague(leagueId);

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(user1, results.get(0));
        assertEquals(user2, results.get(1));
        verify(userRepositoryPort, times(1)).findTop10OrderByTotalPointInLeague(leagueId);
    }

    @Test
    @DisplayName("UTCID02 - Lấy top 10 người dùng theo tổng điểm trong giải đấu thành công (danh sách rỗng) khi giải đấu chưa có người dùng nào")
    void UTCID02_FindTop10OrderByTotalPointInLeagueEmpty() {
        // Arrange
        Long leagueId = 99L;

        when(userRepositoryPort.findTop10OrderByTotalPointInLeague(leagueId))
                .thenReturn(Collections.emptyList());

        // Act
        List<LeaderboardUserResult> results = crudLeagueUseCase.findTop10OrderByTotalPointInLeague(leagueId);

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(userRepositoryPort, times(1)).findTop10OrderByTotalPointInLeague(leagueId);
    }
}
