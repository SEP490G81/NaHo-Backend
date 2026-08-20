package org.naho.persona.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.i18n.message.persona.PersonaDetailMessageKey;
import org.naho.persona.command.UpdateConversationStyleCommand;
import org.naho.persona.command.UpdatePersonaCommand;
import org.naho.persona.exception.PersonaErrorCode;
import org.naho.persona.model.ConversationStyle;
import org.naho.persona.model.Persona;
import org.naho.persona.port.out.ConversationStyleRepositoryPort;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;
import org.naho.shared.exception.ApplicationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdatePersonaTest {

    @Mock
    private PersonaRepositoryPort personaRepositoryPort;

    @Mock
    private ConversationStyleRepositoryPort conversationStyleRepositoryPort;

    @InjectMocks
    private UpdatePersonaUseCase updatePersonaUseCase;

    @Test
    @DisplayName("UTCID01 - Cập nhật nhân vật thất bại khi không tìm thấy nhân vật theo ID")
    void UTCID01_UpdatePersona_PersonaNotFound() {
        // Arrange
        UpdatePersonaCommand command = new UpdatePersonaCommand(
                999L,
                "Updated Name",
                "Updated Prompt",
                10L,
                1L,
                null
        );

        when(personaRepositoryPort.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> updatePersonaUseCase.updatePersona(command)
        );

        assertEquals(PersonaErrorCode.PERSONA_NOT_FOUND, exception.getErrorCode());
        assertEquals(PersonaDetailMessageKey.PERSONA_NOT_FOUND, exception.getMessage());
        verify(personaRepositoryPort, times(1)).findById(999L);
        verify(personaRepositoryPort, never()).save(any());
        verifyNoInteractions(conversationStyleRepositoryPort);
    }

    @Test
    @DisplayName("UTCID02 - Cập nhật thông tin cơ bản nhân vật thành công và giữ nguyên phong cách cũ")
    void UTCID02_UpdatePersona_BasicInfoOnly_Success() {
        // Arrange
        UpdatePersonaCommand command = new UpdatePersonaCommand(
                1L,
                "Updated Tanaka",
                "Updated prompt",
                15L,
                null,
                null
        );

        Persona existingPersona = Persona.builder()
                .id(1L)
                .name("Old Tanaka")
                .prompt("Old prompt")
                .avatarFileId(10L)
                .suggestedConversationStyleId(10L)
                .build();

        Persona updatedPersona = Persona.builder()
                .id(1L)
                .name("Updated Tanaka")
                .prompt("Updated prompt")
                .avatarFileId(15L)
                .suggestedConversationStyleId(10L)
                .build();

        when(personaRepositoryPort.findById(1L)).thenReturn(Optional.of(existingPersona));
        when(personaRepositoryPort.save(any(Persona.class))).thenReturn(updatedPersona);

        // Act
        Persona result = updatePersonaUseCase.updatePersona(command);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Tanaka", result.getName());
        assertEquals("Updated prompt", result.getPrompt());
        assertEquals(15L, result.getAvatarFileId());
        assertEquals(10L, result.getSuggestedConversationStyleId());

        verify(personaRepositoryPort, times(1)).findById(1L);
        verify(personaRepositoryPort, times(1)).save(any(Persona.class));
        verifyNoInteractions(conversationStyleRepositoryPort);
    }

    @Test
    @DisplayName("UTCID03 - Cập nhật thông tin nhân vật và mã phong cách hội thoại mới thành công")
    void UTCID03_UpdatePersona_WithNewStyleId_Success() {
        // Arrange
        UpdatePersonaCommand command = new UpdatePersonaCommand(
                1L,
                "Tanaka",
                "Prompt",
                15L,
                20L,
                null
        );

        Persona existingPersona = Persona.builder()
                .id(1L)
                .name("Tanaka")
                .prompt("Prompt")
                .avatarFileId(15L)
                .suggestedConversationStyleId(10L)
                .build();

        Persona updatedPersona = Persona.builder()
                .id(1L)
                .name("Tanaka")
                .prompt("Prompt")
                .avatarFileId(15L)
                .suggestedConversationStyleId(20L)
                .build();

        when(personaRepositoryPort.findById(1L)).thenReturn(Optional.of(existingPersona));
        when(personaRepositoryPort.save(any(Persona.class))).thenReturn(updatedPersona);

        // Act
        Persona result = updatePersonaUseCase.updatePersona(command);

        // Assert
        assertNotNull(result);
        assertEquals(20L, result.getSuggestedConversationStyleId());

        verify(personaRepositoryPort, times(1)).findById(1L);
        verify(personaRepositoryPort, times(1)).save(any(Persona.class));
        verifyNoInteractions(conversationStyleRepositoryPort);
    }

    @Test
    @DisplayName("UTCID04 - Cập nhật nhân vật kèm lưu thông tin phong cách hội thoại thành công")
    void UTCID04_UpdatePersona_WithConversationStyleCommand_Success() {
        // Arrange
        UpdateConversationStyleCommand styleCommand = new UpdateConversationStyleCommand(
                5L,
                "Polite",
                "Use keigo",
                FormalityLevel.FORMAL,
                MarugotoLevel.ELEMENTARY_1_A2
        );

        UpdatePersonaCommand command = new UpdatePersonaCommand(
                1L,
                "Tanaka Sensei",
                "Sensei prompt",
                10L,
                null,
                styleCommand
        );

        Persona existingPersona = Persona.builder()
                .id(1L)
                .name("Tanaka")
                .prompt("Old prompt")
                .suggestedConversationStyleId(5L)
                .build();

        ConversationStyle savedStyle = ConversationStyle.builder()
                .id(5L)
                .description("Polite")
                .prompt("Use keigo")
                .formalityLevel(FormalityLevel.FORMAL)
                .marugotoLevel(MarugotoLevel.ELEMENTARY_1_A2)
                .build();

        Persona updatedPersona = Persona.builder()
                .id(1L)
                .name("Tanaka Sensei")
                .prompt("Sensei prompt")
                .avatarFileId(10L)
                .suggestedConversationStyleId(5L)
                .build();

        when(personaRepositoryPort.findById(1L)).thenReturn(Optional.of(existingPersona));
        when(conversationStyleRepositoryPort.save(any(ConversationStyle.class))).thenReturn(savedStyle);
        when(personaRepositoryPort.save(any(Persona.class))).thenReturn(updatedPersona);

        // Act
        Persona result = updatePersonaUseCase.updatePersona(command);

        // Assert
        assertNotNull(result);
        assertEquals("Tanaka Sensei", result.getName());
        assertEquals(5L, result.getSuggestedConversationStyleId());

        verify(personaRepositoryPort, times(1)).findById(1L);
        verify(conversationStyleRepositoryPort, times(1)).save(any(ConversationStyle.class));
        verify(personaRepositoryPort, times(1)).save(any(Persona.class));
    }
}


