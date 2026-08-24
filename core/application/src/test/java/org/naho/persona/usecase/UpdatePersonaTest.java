package org.naho.persona.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.file.result.FileResult;
import org.naho.i18n.message.persona.PersonaDetailMessageKey;
import org.naho.persona.command.UpdatePersonaCommand;
import org.naho.persona.exception.PersonaErrorCode;
import org.naho.persona.mapper.PersonaResultMapper;
import org.naho.persona.model.Persona;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.persona.result.PersonaResult;
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
    private PersonaResultMapper personaResultMapper;

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
                MarugotoLevel.STARTER_A1,
                FormalityLevel.FORMAL,
                null,
                null,
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
        verifyNoInteractions(personaResultMapper);
    }

    @Test
    @DisplayName("UTCID02 - Cập nhật thông tin cơ bản nhân vật thành công và giữ nguyên cấp độ mặc định")
    void UTCID02_UpdatePersona_BasicInfoOnly_Success() {
        // Arrange
        UpdatePersonaCommand command = new UpdatePersonaCommand(
                1L,
                "Updated Tanaka",
                "Updated prompt",
                15L,
                null,
                null,
                null,
                null,
                null
        );

        Persona existingPersona = Persona.builder()
                .id(1L)
                .name("Old Tanaka")
                .prompt("Old prompt")
                .avatarFileId(10L)
                .defaultMarugotoLevel(MarugotoLevel.ELEMENTARY_1_A2)
                .defaultFormalityLevel(FormalityLevel.NEUTRAL)
                .build();

        Persona updatedPersona = Persona.builder()
                .id(1L)
                .name("Updated Tanaka")
                .prompt("Updated prompt")
                .avatarFileId(15L)
                .defaultMarugotoLevel(MarugotoLevel.ELEMENTARY_1_A2)
                .defaultFormalityLevel(FormalityLevel.NEUTRAL)
                .build();

        PersonaResult expectedResult = PersonaResult.builder()
                .id(1L)
                .name("Updated Tanaka")
                .prompt("Updated prompt")
                .avatarFile(FileResult.builder().id(15L).build())
                .defaultMarugotoLevel(MarugotoLevel.ELEMENTARY_1_A2)
                .defaultFormalityLevel(FormalityLevel.NEUTRAL)
                .build();

        when(personaRepositoryPort.findById(1L)).thenReturn(Optional.of(existingPersona));
        when(personaRepositoryPort.save(any(Persona.class))).thenReturn(updatedPersona);
        when(personaResultMapper.domainToResult(updatedPersona)).thenReturn(expectedResult);

        // Act
        PersonaResult result = updatePersonaUseCase.updatePersona(command);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Tanaka", result.name());
        assertEquals("Updated prompt", result.prompt());
        assertNotNull(result.avatarFile());
        assertEquals(15L, result.avatarFile().id());
        assertEquals(MarugotoLevel.ELEMENTARY_1_A2, result.defaultMarugotoLevel());
        assertEquals(FormalityLevel.NEUTRAL, result.defaultFormalityLevel());

        verify(personaRepositoryPort, times(1)).findById(1L);
        verify(personaRepositoryPort, times(1)).save(any(Persona.class));
        verify(personaResultMapper, times(1)).domainToResult(updatedPersona);
    }

    @Test
    @DisplayName("UTCID03 - Cập nhật thông tin nhân vật kèm cấp độ mặc định mới thành công")
    void UTCID03_UpdatePersona_WithNewLevels_Success() {
        // Arrange
        UpdatePersonaCommand command = new UpdatePersonaCommand(
                1L,
                "Tanaka",
                "Prompt",
                15L,
                MarugotoLevel.INTERMEDIATE_1_B1,
                FormalityLevel.FORMAL,
                null,
                null,
                null
        );

        Persona existingPersona = Persona.builder()
                .id(1L)
                .name("Tanaka")
                .prompt("Prompt")
                .avatarFileId(15L)
                .defaultMarugotoLevel(MarugotoLevel.STARTER_A1)
                .defaultFormalityLevel(FormalityLevel.INFORMAL)
                .build();

        Persona updatedPersona = Persona.builder()
                .id(1L)
                .name("Tanaka")
                .prompt("Prompt")
                .avatarFileId(15L)
                .defaultMarugotoLevel(MarugotoLevel.INTERMEDIATE_1_B1)
                .defaultFormalityLevel(FormalityLevel.FORMAL)
                .build();

        PersonaResult expectedResult = PersonaResult.builder()
                .id(1L)
                .name("Tanaka")
                .prompt("Prompt")
                .defaultMarugotoLevel(MarugotoLevel.INTERMEDIATE_1_B1)
                .defaultFormalityLevel(FormalityLevel.FORMAL)
                .build();

        when(personaRepositoryPort.findById(1L)).thenReturn(Optional.of(existingPersona));
        when(personaRepositoryPort.save(any(Persona.class))).thenReturn(updatedPersona);
        when(personaResultMapper.domainToResult(updatedPersona)).thenReturn(expectedResult);

        // Act
        PersonaResult result = updatePersonaUseCase.updatePersona(command);

        // Assert
        assertNotNull(result);
        assertEquals(MarugotoLevel.INTERMEDIATE_1_B1, result.defaultMarugotoLevel());
        assertEquals(FormalityLevel.FORMAL, result.defaultFormalityLevel());

        verify(personaRepositoryPort, times(1)).findById(1L);
        verify(personaRepositoryPort, times(1)).save(any(Persona.class));
        verify(personaResultMapper, times(1)).domainToResult(updatedPersona);
    }
}


