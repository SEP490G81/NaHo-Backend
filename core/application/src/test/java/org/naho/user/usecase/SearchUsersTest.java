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
class SearchUsersTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private UserResultMapper userResultMapper;

    @InjectMocks
    private GetUserUseCase getUserUseCase;

    @Test
    @DisplayName("UTCID01 - Tìm kiếm người dùng thành công khi có các bộ lọc phù hợp")
    void UTCID01_SearchUsersSuccess() {
        // Arrange
        String userNameOrEmail = "truc";
        String role = "LEARNER";
        String status = "ACTIVE";
        String jlptLevel = "N3";

        User user = User.builder().id(1L).fullName("Nguyen Vuong Truc").build();
        UserResult expectedResult = mock(UserResult.class);

        when(userRepositoryPort.findByFilters(userNameOrEmail, role, status, jlptLevel)).thenReturn(List.of(user));
        when(userResultMapper.domainToResult(user)).thenReturn(expectedResult);

        // Act
        List<UserResult> results = getUserUseCase.searchUsers(userNameOrEmail, role, status, jlptLevel);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(expectedResult, results.get(0));
        verify(userRepositoryPort, times(1)).findByFilters(userNameOrEmail, role, status, jlptLevel);
        verify(userResultMapper, times(1)).domainToResult(user);
    }

    @Test
    @DisplayName("UTCID02 - Tìm kiếm người dùng thành công khi không có kết quả phù hợp (trả về danh sách rỗng)")
    void UTCID02_SearchUsersEmpty() {
        // Arrange
        String userNameOrEmail = "nonexistent";
        String role = "ADMIN";
        String status = "DELETED";
        String jlptLevel = "N1";

        when(userRepositoryPort.findByFilters(userNameOrEmail, role, status, jlptLevel)).thenReturn(List.of());

        // Act
        List<UserResult> results = getUserUseCase.searchUsers(userNameOrEmail, role, status, jlptLevel);

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(userRepositoryPort, times(1)).findByFilters(userNameOrEmail, role, status, jlptLevel);
        verify(userResultMapper, never()).domainToResult(any());
    }

    @Test
    @DisplayName("UTCID03 - Tìm kiếm người dùng thành công khi tất cả bộ lọc là null (Boundary)")
    void UTCID03_SearchUsersAllNullFilters() {
        // Arrange
        User user1 = User.builder().id(1L).fullName("Nguyen Van A").build();
        User user2 = User.builder().id(2L).fullName("Tran Thi B").build();

        UserResult result1 = mock(UserResult.class);
        UserResult result2 = mock(UserResult.class);

        when(userRepositoryPort.findByFilters(null, null, null, null)).thenReturn(List.of(user1, user2));
        when(userResultMapper.domainToResult(user1)).thenReturn(result1);
        when(userResultMapper.domainToResult(user2)).thenReturn(result2);

        // Act
        List<UserResult> results = getUserUseCase.searchUsers(null, null, null, null);

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        verify(userRepositoryPort, times(1)).findByFilters(null, null, null, null);
    }
}
