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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPersonaByIdTest {

    @Mock
    private PersonaRepositoryPort personaRepositoryPort;

    @Mock
    private ConversationStyleRepositoryPort conversationStyleRepositoryPort;

    @InjectMocks
    private GetPersonaUseCase getPersonaUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy thông tin nhân vật theo ID thành công khi nhân vật tồn tại")
    void UTCID01_GetPersonaById_Found_Success() {
        // Arrange
        Long personaId = 1L;
        Persona persona = Persona.builder()
                .id(personaId)
                .name("Sensei Tanaka")
                .prompt("You are a friendly Japanese teacher")
                .build();
        when(personaRepositoryPort.findById(personaId)).thenReturn(Optional.of(persona));

        // Act
        Optional<Persona> result = getPersonaUseCase.getPersonaById(personaId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(personaId, result.get().getId());
        assertEquals("Sensei Tanaka", result.get().getName());
        verify(personaRepositoryPort, times(1)).findById(personaId);
    }

    @Test
    @DisplayName("UTCID02 - Lấy thông tin nhân vật theo ID trả về rỗng khi không tìm thấy")
    void UTCID02_GetPersonaById_NotFound() {
        // Arrange
        Long personaId = 999L;
        when(personaRepositoryPort.findById(personaId)).thenReturn(Optional.empty());

        // Act
        Optional<Persona> result = getPersonaUseCase.getPersonaById(personaId);

        // Assert
        assertTrue(result.isEmpty());
        verify(personaRepositoryPort, times(1)).findById(personaId);
    }
}


