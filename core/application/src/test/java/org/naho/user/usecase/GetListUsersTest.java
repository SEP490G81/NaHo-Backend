package org.naho.user.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.user.mapper.UserResultMapper;
import org.naho.user.model.User;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.result.UserResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetListUsersTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private UserResultMapper userResultMapper;

    @InjectMocks
    private GetUserUseCase getUserUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy danh sách người dùng thành công khi hệ thống có người dùng")
    void UTCID01_GetListUsersSuccess() {
        // Arrange
        User user1 = User.builder().id(1L).fullName("Nguyen Van A").build();
        User user2 = User.builder().id(2L).fullName("Tran Thi B").build();

        UserResult result1 = mock(UserResult.class);
        UserResult result2 = mock(UserResult.class);

        when(userRepositoryPort.getListUser()).thenReturn(List.of(user1, user2));
        when(userResultMapper.domainToResult(user1)).thenReturn(result1);
        when(userResultMapper.domainToResult(user2)).thenReturn(result2);

        // Act
        List<UserResult> results = getUserUseCase.getListUsers();

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(result1, results.get(0));
        assertEquals(result2, results.get(1));
        verify(userRepositoryPort, times(1)).getListUser();
        verify(userResultMapper, times(1)).domainToResult(user1);
        verify(userResultMapper, times(1)).domainToResult(user2);
    }

    @Test
    @DisplayName("UTCID02 - Lấy danh sách người dùng thành công khi hệ thống không có người dùng nào (trả về danh sách rỗng)")
    void UTCID02_GetListUsersEmpty() {
        // Arrange
        when(userRepositoryPort.getListUser()).thenReturn(List.of());

        // Act
        List<UserResult> results = getUserUseCase.getListUsers();

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(userRepositoryPort, times(1)).getListUser();
        verify(userResultMapper, never()).domainToResult(any());
    }
}
