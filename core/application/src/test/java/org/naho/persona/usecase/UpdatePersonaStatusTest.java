package org.naho.persona.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.i18n.message.persona.PersonaDetailMessageKey;
import org.naho.persona.exception.PersonaErrorCode;
import org.naho.persona.model.Persona;
import org.naho.persona.port.out.ConversationStyleRepositoryPort;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.persona.type.PersonaStatus;
import org.naho.shared.exception.ApplicationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdatePersonaStatusTest {

    @Mock
    private PersonaRepositoryPort personaRepositoryPort;

    @Mock
    private ConversationStyleRepositoryPort conversationStyleRepositoryPort;

    @InjectMocks
    private UpdatePersonaUseCase updatePersonaUseCase;

    @Test
    @DisplayName("UTCID01 - Đổi trạng thái từ ACTIVE sang UNACTIVE thành công")
    void UTCID01_UpdateStatus_ActiveToUnactive_Success() {
        // Arrange
        Long personaId = 1L;
        Persona existingPersona = Persona.builder()
                .id(personaId)
                .name("Sensei Tanaka")
                .prompt("You are a friendly Japanese teacher")
                .status(PersonaStatus.ACTIVE)
                .build();

        when(personaRepositoryPort.findById(personaId)).thenReturn(Optional.of(existingPersona));
        when(personaRepositoryPort.updatePersonaStatus(personaId, PersonaStatus.UNACTIVE)).thenReturn(PersonaStatus.UNACTIVE);

        // Act
        PersonaStatus result = updatePersonaUseCase.updateStatus(personaId);

        // Assert
        assertEquals(PersonaStatus.UNACTIVE, result);
        verify(personaRepositoryPort, times(1)).findById(personaId);
        verify(personaRepositoryPort, times(1)).updatePersonaStatus(personaId, PersonaStatus.UNACTIVE);
    }

    @Test
    @DisplayName("UTCID02 - Đổi trạng thái từ UNACTIVE sang ACTIVE thành công")
    void UTCID02_UpdateStatus_UnactiveToActive_Success() {
        // Arrange
        Long personaId = 2L;
        Persona existingPersona = Persona.builder()
                .id(personaId)
                .name("Anime Hero")
                .prompt("Energetic anime hero")
                .status(PersonaStatus.UNACTIVE)
                .build();

        when(personaRepositoryPort.findById(personaId)).thenReturn(Optional.of(existingPersona));
        when(personaRepositoryPort.updatePersonaStatus(personaId, PersonaStatus.ACTIVE)).thenReturn(PersonaStatus.ACTIVE);

        // Act
        PersonaStatus result = updatePersonaUseCase.updateStatus(personaId);

        // Assert
        assertEquals(PersonaStatus.ACTIVE, result);
        verify(personaRepositoryPort, times(1)).findById(personaId);
        verify(personaRepositoryPort, times(1)).updatePersonaStatus(personaId, PersonaStatus.ACTIVE);
    }

    @Test
    @DisplayName("UTCID03 - Đổi trạng thái thất bại khi không tìm thấy nhân vật theo ID")
    void UTCID03_UpdateStatus_PersonaNotFound() {
        // Arrange
        Long personaId = 999L;
        when(personaRepositoryPort.findById(personaId)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> updatePersonaUseCase.updateStatus(personaId)
        );

        assertEquals(PersonaErrorCode.PERSONA_NOT_FOUND, exception.getErrorCode());
        assertEquals(PersonaDetailMessageKey.PERSONA_NOT_FOUND, exception.getMessage());
        verify(personaRepositoryPort, times(1)).findById(personaId);
        verify(personaRepositoryPort, never()).updatePersonaStatus(any(), any());
    }
}
