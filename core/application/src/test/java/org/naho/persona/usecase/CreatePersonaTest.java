package org.naho.persona.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.persona.command.CreatePersonaCommand;
import org.naho.persona.mapper.PersonaResultMapper;
import org.naho.persona.model.Persona;
import org.naho.persona.port.out.PersonaRepositoryPort;
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
    private PersonaResultMapper personaResultMapper;

    @InjectMocks
    private CreatePersonaUseCase createPersonaUseCase;

    @Test
    @DisplayName("UTCID01 - Tạo mới nhân vật thành công với thông tin cấp độ mặc định")
    void UTCID01_CreatePersona_WithDefaultLevels_Success() {
        // Arrange
        CreatePersonaCommand command = new CreatePersonaCommand(
                "Sensei Tanaka",
                "You are a friendly Japanese teacher",
                10L,
                MarugotoLevel.STARTER_A1,
                FormalityLevel.FORMAL,
                null,
                null,
                null
        );

        Persona savedPersona = Persona.builder()
                .id(1L)
                .name("Sensei Tanaka")
                .prompt("You are a friendly Japanese teacher")
                .avatarFileId(10L)
                .defaultMarugotoLevel(MarugotoLevel.STARTER_A1)
                .defaultFormalityLevel(FormalityLevel.FORMAL)
                .build();

        PersonaResult expectedResult = PersonaResult.builder()
                .id(1L)
                .name("Sensei Tanaka")
                .prompt("You are a friendly Japanese teacher")
                .defaultMarugotoLevel(MarugotoLevel.STARTER_A1)
                .defaultFormalityLevel(FormalityLevel.FORMAL)
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
        assertEquals(MarugotoLevel.STARTER_A1, result.defaultMarugotoLevel());
        assertEquals(FormalityLevel.FORMAL, result.defaultFormalityLevel());

        verify(personaRepositoryPort, times(1)).save(any(Persona.class));
        verify(personaResultMapper, times(1)).domainToResult(savedPersona);
    }

    @Test
    @DisplayName("UTCID02 - Tạo mới nhân vật không kèm cấp độ mặc định thành công")
    void UTCID02_CreatePersona_WithoutLevels_Success() {
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
                .id(2L)
                .name("Plain Persona")
                .prompt("A plain assistant")
                .avatarFileId(null)
                .defaultMarugotoLevel(null)
                .defaultFormalityLevel(null)
                .build();

        PersonaResult expectedResult = PersonaResult.builder()
                .id(2L)
                .name("Plain Persona")
                .prompt("A plain assistant")
                .defaultMarugotoLevel(null)
                .defaultFormalityLevel(null)
                .build();

        when(personaRepositoryPort.save(any(Persona.class))).thenReturn(savedPersona);
        when(personaResultMapper.domainToResult(savedPersona)).thenReturn(expectedResult);

        // Act
        PersonaResult result = createPersonaUseCase.createPersona(command);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.id());
        assertEquals("Plain Persona", result.name());
        assertNull(result.defaultMarugotoLevel());
        assertNull(result.defaultFormalityLevel());

        verify(personaRepositoryPort, times(1)).save(any(Persona.class));
        verify(personaResultMapper, times(1)).domainToResult(savedPersona);
    }
}


