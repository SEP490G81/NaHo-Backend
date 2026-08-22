package org.naho.persona.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.i18n.message.persona.PersonaDetailMessageKey;
import org.naho.persona.exception.PersonaErrorCode;
import org.naho.persona.mapper.ConversationStyleMapper;
import org.naho.persona.mapper.PersonaResultMapper;
import org.naho.persona.model.Persona;
import org.naho.persona.port.out.ConversationStyleRepositoryPort;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.persona.result.PersonaResult;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.exception.CommonErrorCode;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPersonaByIdTest {

    @Mock
    private PersonaRepositoryPort personaRepositoryPort;

    @Mock
    private ConversationStyleRepositoryPort conversationStyleRepositoryPort;

    @Mock
    private PersonaResultMapper personaResultMapper;

    @Mock
    private ConversationStyleMapper conversationStyleMapper;

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
        PersonaResult personaResult = PersonaResult.builder()
                .id(personaId)
                .name("Sensei Tanaka")
                .prompt("You are a friendly Japanese teacher")
                .build();

        when(personaRepositoryPort.findById(personaId)).thenReturn(Optional.of(persona));
        when(personaResultMapper.domainToResult(persona)).thenReturn(personaResult);

        // Act
        PersonaResult result = getPersonaUseCase.getPersonaById(personaId);

        // Assert
        assertNotNull(result);
        assertEquals(personaId, result.id());
        assertEquals("Sensei Tanaka", result.name());
        verify(personaRepositoryPort, times(1)).findById(personaId);
        verify(personaResultMapper, times(1)).domainToResult(persona);
    }

    @Test
    @DisplayName("UTCID02 - Ném ngoại lệ khi truyền ID null")
    void UTCID02_GetPersonaById_NullId_ThrowsException() {
        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> getPersonaUseCase.getPersonaById(null)
        );

        assertEquals(CommonErrorCode.COMMON_INVALID_REQUEST, exception.getErrorCode());
        assertEquals(PersonaDetailMessageKey.PERSONA_ID_NULL, exception.getMessage());
        verifyNoInteractions(personaRepositoryPort);
    }

    @Test
    @DisplayName("UTCID03 - Ném ngoại lệ khi không tìm thấy nhân vật theo ID")
    void UTCID03_GetPersonaById_NotFound_ThrowsException() {
        // Arrange
        Long personaId = 999L;
        when(personaRepositoryPort.findById(personaId)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> getPersonaUseCase.getPersonaById(personaId)
        );

        assertEquals(PersonaErrorCode.PERSONA_NOT_FOUND, exception.getErrorCode());
        assertEquals(PersonaDetailMessageKey.PERSONA_NOT_FOUND, exception.getMessage());
        verify(personaRepositoryPort, times(1)).findById(personaId);
    }
}


