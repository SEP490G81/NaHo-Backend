package org.naho.user.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.user.mapper.RoleResultMapper;
import org.naho.user.model.Role;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.result.RoleResult;
import org.naho.user.type.RoleName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllRolesTest {

    @Mock
    private RoleRepositoryPort roleRepositoryPort;
    @Mock
    private RoleResultMapper roleResultMapper;

    @InjectMocks
    private CrudRoleUseCase crudRoleUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy tất cả vai trò thành công khi hệ thống có vai trò")
    void UTCID01_GetAllRolesSuccess() {
        // Arrange
        Role role1 = Role.builder().id(1L).roleName(RoleName.LEARNER).build();
        Role role2 = Role.builder().id(2L).roleName(RoleName.ADMIN).build();

        RoleResult result1 = mock(RoleResult.class);
        RoleResult result2 = mock(RoleResult.class);

        when(roleRepositoryPort.findAll()).thenReturn(List.of(role1, role2));
        when(roleResultMapper.domainToResult(role1)).thenReturn(result1);
        when(roleResultMapper.domainToResult(role2)).thenReturn(result2);

        // Act
        List<RoleResult> results = crudRoleUseCase.getAllRoles();

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(result1, results.get(0));
        assertEquals(result2, results.get(1));
        verify(roleRepositoryPort, times(1)).findAll();
        verify(roleResultMapper, times(1)).domainToResult(role1);
        verify(roleResultMapper, times(1)).domainToResult(role2);
    }

    @Test
    @DisplayName("UTCID02 - Lấy tất cả vai trò thành công khi hệ thống không có vai trò nào (trả về danh sách rỗng)")
    void UTCID02_GetAllRolesEmpty() {
        // Arrange
        when(roleRepositoryPort.findAll()).thenReturn(List.of());

        // Act
        List<RoleResult> results = crudRoleUseCase.getAllRoles();

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(roleRepositoryPort, times(1)).findAll();
        verify(roleResultMapper, never()).domainToResult(any());
    }
}
