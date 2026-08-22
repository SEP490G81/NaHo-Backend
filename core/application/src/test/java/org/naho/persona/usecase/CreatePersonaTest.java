package org.naho.persona.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.persona.command.CreateConversationStyleCommand;
import org.naho.persona.command.CreatePersonaCommand;
import org.naho.persona.mapper.PersonaResultMapper;
import org.naho.persona.model.ConversationStyle;
import org.naho.persona.model.Persona;
import org.naho.persona.port.out.ConversationStyleRepositoryPort;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.persona.result.ConversationStyleResult;
import org.naho.persona.result.PersonaResult;
import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreatePersonaTest {

    @Mock
    private PersonaRepositoryPort personaRepositoryPort;

    @Mock
    private ConversationStyleRepositoryPort conversationStyleRepositoryPort;

    @Mock
    private PersonaResultMapper personaResultMapper;

    @InjectMocks
    private CreatePersonaUseCase createPersonaUseCase;

    @Test
    @DisplayName("UTCID01 - Tạo mới nhân vật thành công với ID phong cách hội thoại có sẵn")
    void UTCID01_CreatePersona_WithExistingStyleId_Success() {
        // Arrange
        CreatePersonaCommand command = new CreatePersonaCommand(
                "Sensei Tanaka",
                "You are a friendly Japanese teacher",
                10L,
                1L,
                null,
                null,
                null,
                null
        );

        Persona savedPersona = Persona.builder()
                .id(1L)
                .name("Sensei Tanaka")
                .prompt("You are a friendly Japanese teacher")
                .avatarFileId(10L)
                .suggestedConversationStyleId(1L)
                .build();

        PersonaResult expectedResult = PersonaResult.builder()
                .id(1L)
                .name("Sensei Tanaka")
                .prompt("You are a friendly Japanese teacher")
                .suggestedConversationStyle(ConversationStyleResult.builder().id(1L).build())
                .build();

        when(personaRepositoryPort.save(any(Persona.class))).thenReturn(savedPersona);
        when(personaResultMapper.domainToResult(savedPersona)).thenReturn(expectedResult);

        // Act
        PersonaResult result = createPersonaUseCase.createPersona(command);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Sensei Tanaka", result.name());
        assertEquals("You are a friendly Japanese teacher", result.prompt());
        assertNotNull(result.suggestedConversationStyle());
        assertEquals(1L, result.suggestedConversationStyle().id());

        verify(personaRepositoryPort, times(1)).save(any(Persona.class));
        verify(personaResultMapper, times(1)).domainToResult(savedPersona);
        verifyNoInteractions(conversationStyleRepositoryPort);
    }

    @Test
    @DisplayName("UTCID02 - Tạo mới nhân vật kèm tạo mới phong cách hội thoại thành công")
    void UTCID02_CreatePersona_WithNewConversationStyleCommand_Success() {
        // Arrange
        CreateConversationStyleCommand styleCommand = new CreateConversationStyleCommand(
                "Anime style",
                "Talk like a shonen hero",
                FormalityLevel.INFORMAL,
                MarugotoLevel.STARTER_A1
        );

        CreatePersonaCommand command = new CreatePersonaCommand(
                "Anime Hero",
                "Energetic anime hero",
                20L,
                null,
                null,
                null,
                null,
                styleCommand
        );

        ConversationStyle savedStyle = ConversationStyle.builder()
                .id(2L)
                .description("Anime style")
                .prompt("Talk like a shonen hero")
                .formalityLevel(FormalityLevel.INFORMAL)
                .marugotoLevel(MarugotoLevel.STARTER_A1)
                .build();

        Persona savedPersona = Persona.builder()
                .id(2L)
                .name("Anime Hero")
                .prompt("Energetic anime hero")
                .avatarFileId(20L)
                .suggestedConversationStyleId(2L)
                .build();

        PersonaResult expectedResult = PersonaResult.builder()
                .id(2L)
                .name("Anime Hero")
                .suggestedConversationStyle(ConversationStyleResult.builder().id(2L).build())
                .build();

        when(conversationStyleRepositoryPort.save(any(ConversationStyle.class))).thenReturn(savedStyle);
        when(personaRepositoryPort.save(any(Persona.class))).thenReturn(savedPersona);
        when(personaResultMapper.domainToResult(savedPersona)).thenReturn(expectedResult);

        // Act
        PersonaResult result = createPersonaUseCase.createPersona(command);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.id());
        assertEquals("Anime Hero", result.name());
        assertNotNull(result.suggestedConversationStyle());
        assertEquals(2L, result.suggestedConversationStyle().id());

        verify(conversationStyleRepositoryPort, times(1)).save(any(ConversationStyle.class));
        verify(personaRepositoryPort, times(1)).save(any(Persona.class));
        verify(personaResultMapper, times(1)).domainToResult(savedPersona);
    }

    @Test
    @DisplayName("UTCID03 - Tạo mới nhân vật không kèm phong cách hội thoại thành công")
    void UTCID03_CreatePersona_WithoutStyle_Success() {
        // Arrange
        CreatePersonaCommand command = new CreatePersonaCommand(
                "Plain Persona",
                "A plain assistant",
                null,
                null,
                null,
                null,
                null,
                null
        );

        Persona savedPersona = Persona.builder()
                .id(3L)
                .name("Plain Persona")
                .prompt("A plain assistant")
                .avatarFileId(null)
                .suggestedConversationStyleId(null)
                .build();

        PersonaResult expectedResult = PersonaResult.builder()
                .id(3L)
                .name("Plain Persona")
                .prompt("A plain assistant")
                .suggestedConversationStyle(null)
                .build();

        when(personaRepositoryPort.save(any(Persona.class))).thenReturn(savedPersona);
        when(personaResultMapper.domainToResult(savedPersona)).thenReturn(expectedResult);

        // Act
        PersonaResult result = createPersonaUseCase.createPersona(command);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.id());
        assertEquals("Plain Persona", result.name());
        assertNull(result.suggestedConversationStyle());

        verify(personaRepositoryPort, times(1)).save(any(Persona.class));
        verify(personaResultMapper, times(1)).domainToResult(savedPersona);
        verifyNoInteractions(conversationStyleRepositoryPort);
    }
}


