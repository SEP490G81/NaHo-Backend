package org.naho.persona.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.persona.model.ConversationStyle;
import org.naho.persona.model.Persona;
import org.naho.persona.port.out.ConversationStyleRepositoryPort;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetConversationStyleByPersonaIdTest {

    @Mock
    private PersonaRepositoryPort personaRepositoryPort;

    @Mock
    private ConversationStyleRepositoryPort conversationStyleRepositoryPort;

    @InjectMocks
    private GetPersonaUseCase getPersonaUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy phong cách hội thoại trả về rỗng khi không tìm thấy nhân vật")
    void UTCID01_GetConversationStyleByPersonaId_PersonaNotFound() {
        // Arrange
        Long personaId = 999L;
        when(personaRepositoryPort.findById(personaId)).thenReturn(Optional.empty());

        // Act
        Optional<ConversationStyle> result = getPersonaUseCase.getConversationStyleByPersonaId(personaId);

        // Assert
        assertTrue(result.isEmpty());
        verify(personaRepositoryPort, times(1)).findById(personaId);
        verifyNoInteractions(conversationStyleRepositoryPort);
    }

    @Test
    @DisplayName("UTCID02 - Lấy phong cách hội thoại thành công từ đối tượng style liên kết trực tiếp trong persona")
    void UTCID02_GetConversationStyleByPersonaId_EmbeddedStylePresent_Success() {
        // Arrange
        Long personaId = 1L;
        ConversationStyle style = ConversationStyle.builder()
                .id(10L)
                .description("Polite style")
                .prompt("Speak politely")
                .formalityLevel(FormalityLevel.FORMAL)
                .marugotoLevel(MarugotoLevel.ELEMENTARY_1_A2)
                .build();

        Persona persona = Persona.builder()
                .id(personaId)
                .name("Tanaka")
                .prompt("Prompt Tanaka")
                .conversationStyle(style)
                .build();

        when(personaRepositoryPort.findById(personaId)).thenReturn(Optional.of(persona));

        // Act
        Optional<ConversationStyle> result = getPersonaUseCase.getConversationStyleByPersonaId(personaId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(10L, result.get().getId());
        assertEquals("Polite style", result.get().getDescription());
        verify(personaRepositoryPort, times(1)).findById(personaId);
        verifyNoInteractions(conversationStyleRepositoryPort);
    }

    @Test
    @DisplayName("UTCID03 - Lấy phong cách hội thoại thành công từ suggestedConversationStyleId qua repository")
    void UTCID03_GetConversationStyleByPersonaId_FromSuggestedStyleId_Success() {
        // Arrange
        Long personaId = 1L;
        Long styleId = 5L;

        Persona persona = Persona.builder()
                .id(personaId)
                .name("Tanaka")
                .prompt("Prompt Tanaka")
                .suggestedConversationStyleId(styleId)
                .conversationStyle(null)
                .build();

        ConversationStyle style = ConversationStyle.builder()
                .id(styleId)
                .description("Casual style")
                .prompt("Speak casually")
                .formalityLevel(FormalityLevel.INFORMAL)
                .marugotoLevel(MarugotoLevel.STARTER_A1)
                .build();

        when(personaRepositoryPort.findById(personaId)).thenReturn(Optional.of(persona));
        when(conversationStyleRepositoryPort.findById(styleId)).thenReturn(Optional.of(style));

        // Act
        Optional<ConversationStyle> result = getPersonaUseCase.getConversationStyleByPersonaId(personaId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(styleId, result.get().getId());
        assertEquals("Casual style", result.get().getDescription());
        verify(personaRepositoryPort, times(1)).findById(personaId);
        verify(conversationStyleRepositoryPort, times(1)).findById(styleId);
    }

    @Test
    @DisplayName("UTCID04 - Lấy phong cách hội thoại trả về rỗng khi nhân vật không có cấu hình style")
    void UTCID04_GetConversationStyleByPersonaId_NoStyleConfigured() {
        // Arrange
        Long personaId = 1L;
        Persona persona = Persona.builder()
                .id(personaId)
                .name("Tanaka")
                .prompt("Prompt Tanaka")
                .suggestedConversationStyleId(null)
                .conversationStyle(null)
                .build();

        when(personaRepositoryPort.findById(personaId)).thenReturn(Optional.of(persona));

        // Act
        Optional<ConversationStyle> result = getPersonaUseCase.getConversationStyleByPersonaId(personaId);

        // Assert
        assertTrue(result.isEmpty());
        verify(personaRepositoryPort, times(1)).findById(personaId);
        verifyNoInteractions(conversationStyleRepositoryPort);
    }
}
