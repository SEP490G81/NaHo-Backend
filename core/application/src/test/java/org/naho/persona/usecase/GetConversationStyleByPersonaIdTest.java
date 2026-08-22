package org.naho.persona.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.i18n.message.persona.ConversationStyleDetailMessageKey;
import org.naho.i18n.message.persona.PersonaDetailMessageKey;
import org.naho.persona.exception.PersonaErrorCode;
import org.naho.persona.mapper.ConversationStyleMapper;
import org.naho.persona.mapper.PersonaResultMapper;
import org.naho.persona.model.ConversationStyle;
import org.naho.persona.model.Persona;
import org.naho.persona.port.out.ConversationStyleRepositoryPort;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.persona.result.ConversationStyleResult;
import org.naho.persona.result.PersonaResult;
import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.exception.CommonErrorCode;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetConversationStyleByPersonaIdTest {

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
    @DisplayName("UTCID01 - Ném ngoại lệ khi truyền personaId null")
    void UTCID01_GetConversationStyleByPersonaId_NullId_ThrowsException() {
        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> getPersonaUseCase.getConversationStyleByPersonaId(null)
        );

        assertEquals(CommonErrorCode.COMMON_INVALID_REQUEST, exception.getErrorCode());
        assertEquals(PersonaDetailMessageKey.PERSONA_ID_NULL, exception.getMessage());
        verifyNoInteractions(personaRepositoryPort);
        verifyNoInteractions(conversationStyleRepositoryPort);
    }

    @Test
    @DisplayName("UTCID02 - Lấy phong cách hội thoại thành công từ suggestedConversationStyleId qua repository")
    void UTCID02_GetConversationStyleByPersonaId_Success() {
        // Arrange
        Long personaId = 1L;
        Long styleId = 10L;

        Persona persona = Persona.builder()
                .id(personaId)
                .name("Tanaka")
                .prompt("Prompt Tanaka")
                .suggestedConversationStyleId(styleId)
                .build();

        ConversationStyleResult styleResult = ConversationStyleResult.builder()
                .id(styleId)
                .description("Polite style")
                .prompt("Speak politely")
                .formalityLevel(FormalityLevel.FORMAL)
                .marugotoLevel(MarugotoLevel.ELEMENTARY_1_A2)
                .build();

        PersonaResult personaResult = PersonaResult.builder()
                .id(personaId)
                .name("Tanaka")
                .prompt("Prompt Tanaka")
                .suggestedConversationStyle(styleResult)
                .build();

        ConversationStyle style = ConversationStyle.builder()
                .id(styleId)
                .description("Polite style")
                .prompt("Speak politely")
                .formalityLevel(FormalityLevel.FORMAL)
                .marugotoLevel(MarugotoLevel.ELEMENTARY_1_A2)
                .build();

        when(personaRepositoryPort.findById(personaId)).thenReturn(Optional.of(persona));
        when(personaResultMapper.domainToResult(persona)).thenReturn(personaResult);
        when(conversationStyleRepositoryPort.findById(styleId)).thenReturn(Optional.of(style));
        when(conversationStyleMapper.domainToResult(style)).thenReturn(styleResult);

        // Act
        ConversationStyleResult result = getPersonaUseCase.getConversationStyleByPersonaId(personaId);

        // Assert
        assertNotNull(result);
        assertEquals(styleId, result.id());
        assertEquals("Polite style", result.description());
        verify(personaRepositoryPort, times(1)).findById(personaId);
        verify(conversationStyleRepositoryPort, times(1)).findById(styleId);
        verify(conversationStyleMapper, times(1)).domainToResult(style);
    }

    @Test
    @DisplayName("UTCID03 - Ném ngoại lệ khi nhân vật không có cấu hình style")
    void UTCID03_GetConversationStyleByPersonaId_NoStyleConfigured_ThrowsException() {
        // Arrange
        Long personaId = 1L;
        Persona persona = Persona.builder()
                .id(personaId)
                .name("Tanaka")
                .prompt("Prompt Tanaka")
                .suggestedConversationStyleId(null)
                .build();

        PersonaResult personaResult = PersonaResult.builder()
                .id(personaId)
                .name("Tanaka")
                .prompt("Prompt Tanaka")
                .suggestedConversationStyle(null)
                .build();

        when(personaRepositoryPort.findById(personaId)).thenReturn(Optional.of(persona));
        when(personaResultMapper.domainToResult(persona)).thenReturn(personaResult);

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> getPersonaUseCase.getConversationStyleByPersonaId(personaId)
        );

        assertEquals(CommonErrorCode.COMMON_INVALID_REQUEST, exception.getErrorCode());
        assertEquals(ConversationStyleDetailMessageKey.CONVERSATION_STYLE_ID_NULL, exception.getMessage());
        verify(personaRepositoryPort, times(1)).findById(personaId);
        verifyNoInteractions(conversationStyleRepositoryPort);
    }

    @Test
    @DisplayName("UTCID04 - Ném ngoại lệ khi không tìm thấy style trong repository")
    void UTCID04_GetConversationStyleByPersonaId_StyleNotFoundInRepo_ThrowsException() {
        // Arrange
        Long personaId = 1L;
        Long styleId = 5L;

        Persona persona = Persona.builder()
                .id(personaId)
                .name("Tanaka")
                .prompt("Prompt Tanaka")
                .suggestedConversationStyleId(styleId)
                .build();

        ConversationStyleResult styleResult = ConversationStyleResult.builder()
                .id(styleId)
                .build();

        PersonaResult personaResult = PersonaResult.builder()
                .id(personaId)
                .name("Tanaka")
                .prompt("Prompt Tanaka")
                .suggestedConversationStyle(styleResult)
                .build();

        when(personaRepositoryPort.findById(personaId)).thenReturn(Optional.of(persona));
        when(personaResultMapper.domainToResult(persona)).thenReturn(personaResult);
        when(conversationStyleRepositoryPort.findById(styleId)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> getPersonaUseCase.getConversationStyleByPersonaId(personaId)
        );

        assertEquals(PersonaErrorCode.PERSONA_NOT_FOUND, exception.getErrorCode());
        assertEquals(ConversationStyleDetailMessageKey.CONVERSATION_STYLE_NOT_FOUND, exception.getMessage());
        verify(personaRepositoryPort, times(1)).findById(personaId);
        verify(conversationStyleRepositoryPort, times(1)).findById(styleId);
    }
}
