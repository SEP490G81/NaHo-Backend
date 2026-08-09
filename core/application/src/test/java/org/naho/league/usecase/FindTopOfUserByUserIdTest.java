package org.naho.league.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.file.port.in.CrudFileInputPort;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.league.mapper.LeagueResultMapper;
import org.naho.league.port.out.LeagueRepositoryPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.result.LeaderboardUserResult;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindTopOfUserByUserIdTest {

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
    @DisplayName("UTCID01 - Lấy thứ hạng của người dùng thành công khi userId hợp lệ và tồn tại trong giải đấu")
    void UTCID01_FindTopOfUserByUserIdSuccess() {
        // Arrange
        Long userId = 100L;
        LeaderboardUserResult expectedResult = mock(LeaderboardUserResult.class);

        when(userRepositoryPort.findTopOfUserByUserId(userId))
                .thenReturn(Optional.of(expectedResult));

        // Act
        LeaderboardUserResult result = crudLeagueUseCase.findTopOfUserByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(userRepositoryPort, times(1)).findTopOfUserByUserId(userId);
    }

    @Test
    @DisplayName("UTCID02 - Lấy thứ hạng của người dùng thất bại khi không tìm thấy người dùng với userId tương ứng")
    void UTCID02_UserNotFound() {
        // Arrange
        Long userId = 999L;

        when(userRepositoryPort.findTopOfUserByUserId(userId))
                .thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> crudLeagueUseCase.findTopOfUserByUserId(userId)
        );

        assertEquals(UserErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_ID_NOT_FOUND, exception.getMessage());
        verify(userRepositoryPort, times(1)).findTopOfUserByUserId(userId);
    }
}
