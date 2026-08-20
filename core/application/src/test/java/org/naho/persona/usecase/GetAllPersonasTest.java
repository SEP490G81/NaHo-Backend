package org.naho.persona.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.persona.model.Persona;
import org.naho.persona.port.out.ConversationStyleRepositoryPort;
import org.naho.persona.port.out.PersonaRepositoryPort;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllPersonasTest {

    @Mock
    private PersonaRepositoryPort personaRepositoryPort;

    @Mock
    private ConversationStyleRepositoryPort conversationStyleRepositoryPort;

    @InjectMocks
    private GetPersonaUseCase getPersonaUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy danh sách tất cả nhân vật thành công khi có dữ liệu")
    void UTCID01_GetAllPersonas_HasData_Success() {
        // Arrange
        List<Persona> personas = List.of(
                Persona.builder().id(1L).name("Persona 1").prompt("Prompt 1").build(),
                Persona.builder().id(2L).name("Persona 2").prompt("Prompt 2").build()
        );
        when(personaRepositoryPort.findAll()).thenReturn(personas);

        // Act
        List<Persona> result = getPersonaUseCase.getAllPersonas();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Persona 1", result.get(0).getName());
        assertEquals("Persona 2", result.get(1).getName());
        verify(personaRepositoryPort, times(1)).findAll();
    }

    @Test
    @DisplayName("UTCID02 - Lấy danh sách tất cả nhân vật trả về danh sách rỗng khi chưa có dữ liệu")
    void UTCID02_GetAllPersonas_Empty_Success() {
        // Arrange
        when(personaRepositoryPort.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Persona> result = getPersonaUseCase.getAllPersonas();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(personaRepositoryPort, times(1)).findAll();
    }
}


