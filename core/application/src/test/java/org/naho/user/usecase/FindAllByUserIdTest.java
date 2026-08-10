package org.naho.user.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.mapper.RoleResultMapper;
import org.naho.user.model.Role;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.result.RoleResult;
import org.naho.user.type.RoleName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindAllByUserIdTest {

    @Mock
    private RoleRepositoryPort roleRepositoryPort;
    @Mock
    private RoleResultMapper roleResultMapper;

    @InjectMocks
    private CrudRoleUseCase crudRoleUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy danh sách vai trò theo userId thành công khi userId hợp lệ và có vai trò")
    void UTCID01_FindAllByUserIdSuccess() {
        // Arrange
        Long userId = 1L;
        Role role = Role.builder().id(1L).roleName(RoleName.LEARNER).build();
        RoleResult roleResult = mock(RoleResult.class);

        when(roleRepositoryPort.findAllByUserId(userId)).thenReturn(List.of(role));
        when(roleResultMapper.domainToResult(role)).thenReturn(roleResult);

        // Act
        List<RoleResult> results = crudRoleUseCase.findAllByUserId(userId);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(roleResult, results.get(0));
        verify(roleRepositoryPort, times(1)).findAllByUserId(userId);
        verify(roleResultMapper, times(1)).domainToResult(role);
    }

    @Test
    @DisplayName("UTCID02 - Lấy danh sách vai trò theo userId thất bại khi userId là null")
    void UTCID02_UserIdNull() {
        // Arrange
        Long userId = null;

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> crudRoleUseCase.findAllByUserId(userId)
        );

        assertEquals(UserErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_ID_NULL, exception.getMessage());
        verify(roleRepositoryPort, never()).findAllByUserId(any());
    }

    @Test
    @DisplayName("UTCID03 - Lấy danh sách vai trò theo userId thành công khi người dùng chưa có vai trò nào (trả về rỗng)")
    void UTCID03_FindAllByUserIdEmpty() {
        // Arrange
        Long userId = 2L;

        when(roleRepositoryPort.findAllByUserId(userId)).thenReturn(List.of());

        // Act
        List<RoleResult> results = crudRoleUseCase.findAllByUserId(userId);

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(roleRepositoryPort, times(1)).findAllByUserId(userId);
        verify(roleResultMapper, never()).domainToResult(any());
    }

    @Test
    @DisplayName("UTCID04 - Lấy vai trò theo roleId thành công")
    void UTCID04_FindRoleByIdSuccess() {
        Long roleId = 1L;
        Role role = Role.builder().id(roleId).roleName(RoleName.LEARNER).build();
        RoleResult roleResult = mock(RoleResult.class);

        when(roleRepositoryPort.findById(roleId)).thenReturn(java.util.Optional.of(role));
        when(roleResultMapper.domainToResult(role)).thenReturn(roleResult);

        RoleResult result = crudRoleUseCase.findRoleById(roleId);

        assertNotNull(result);
        assertEquals(roleResult, result);
    }

    @Test
    @DisplayName("UTCID05 - Lấy vai trò theo userId thành công")
    void UTCID05_FindRoleByUserIdSuccess() {
        Long userId = 1L;
        Role role = Role.builder().id(1L).roleName(RoleName.LEARNER).build();
        RoleResult roleResult = mock(RoleResult.class);

        when(roleRepositoryPort.findByUserId(userId)).thenReturn(java.util.Optional.of(role));
        when(roleResultMapper.domainToResult(role)).thenReturn(roleResult);

        RoleResult result = crudRoleUseCase.findRoleByUserId(userId);

        assertNotNull(result);
        assertEquals(roleResult, result);
    }
}
