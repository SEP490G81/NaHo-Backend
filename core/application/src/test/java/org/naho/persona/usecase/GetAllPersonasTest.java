package org.naho.persona.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.persona.mapper.PersonaResultMapper;
import org.naho.persona.model.Persona;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.persona.result.PersonaResult;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllPersonasTest {

    @Mock
    private PersonaRepositoryPort personaRepositoryPort;

    @Mock
    private PersonaResultMapper personaResultMapper;

    @InjectMocks
    private GetPersonaUseCase getPersonaUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy danh sách tất cả nhân vật thành công khi có dữ liệu")
    void UTCID01_GetAllPersonas_HasData_Success() {
        // Arrange
        Persona persona1 = Persona.builder().id(1L).name("Persona 1").prompt("Prompt 1").build();
        Persona persona2 = Persona.builder().id(2L).name("Persona 2").prompt("Prompt 2").build();
        List<Persona> personas = List.of(persona1, persona2);

        PersonaResult result1 = PersonaResult.builder().id(1L).name("Persona 1").prompt("Prompt 1").build();
        PersonaResult result2 = PersonaResult.builder().id(2L).name("Persona 2").prompt("Prompt 2").build();

        when(personaRepositoryPort.findAll()).thenReturn(personas);
        when(personaResultMapper.domainToResult(persona1)).thenReturn(result1);
        when(personaResultMapper.domainToResult(persona2)).thenReturn(result2);

        // Act
        List<PersonaResult> result = getPersonaUseCase.getAllPersonas();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Persona 1", result.get(0).name());
        assertEquals("Persona 2", result.get(1).name());
        verify(personaRepositoryPort, times(1)).findAll();
        verify(personaResultMapper, times(1)).domainToResult(persona1);
        verify(personaResultMapper, times(1)).domainToResult(persona2);
    }

    @Test
    @DisplayName("UTCID02 - Lấy danh sách tất cả nhân vật trả về danh sách rỗng khi chưa có dữ liệu")
    void UTCID02_GetAllPersonas_Empty_Success() {
        // Arrange
        when(personaRepositoryPort.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<PersonaResult> result = getPersonaUseCase.getAllPersonas();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(personaRepositoryPort, times(1)).findAll();
        verifyNoInteractions(personaResultMapper);
    }
}


