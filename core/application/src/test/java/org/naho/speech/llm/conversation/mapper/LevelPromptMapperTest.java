package org.naho.speech.llm.conversation.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.naho.i18n.message.llm.LlmDetailMessageKey;
import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.llm.conversation.exception.LlmApplicationError;

import static org.junit.jupiter.api.Assertions.*;

class LevelPromptMapperTest {

    private LevelPromptMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LevelPromptMapper();
    }

    @Test
    void mapMarugotoLevelToPrompt_validLevels_returnsPrompt() {
        assertNotNull(mapper.mapMarugotoLevelToPrompt(MarugotoLevel.STARTER_A1));
        assertNotNull(mapper.mapMarugotoLevelToPrompt(MarugotoLevel.ELEMENTARY_1_A2));
        assertNotNull(mapper.mapMarugotoLevelToPrompt(MarugotoLevel.ELEMENTARY_2_A2));
        assertNotNull(mapper.mapMarugotoLevelToPrompt(MarugotoLevel.PRE_INTERMEDIATE_A2_B1));
        assertNotNull(mapper.mapMarugotoLevelToPrompt(MarugotoLevel.INTERMEDIATE_1_B1));
        assertNotNull(mapper.mapMarugotoLevelToPrompt(MarugotoLevel.INTERMEDIATE_2_B1));
    }

    @Test
    void mapMarugotoLevelToPrompt_nullLevel_throwsApplicationException() {
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> mapper.mapMarugotoLevelToPrompt(null)
        );
        assertEquals(LlmApplicationError.LLM_MARUGOTO_LEVEL_INVALID, exception.getErrorCode());
        assertEquals(LlmDetailMessageKey.LLM_MARUGOTO_LEVEL_NULL, exception.getMessage());
    }

    @Test
    void mapFormalityLevelToPrompt_validLevels_returnsPrompt() {
        assertNotNull(mapper.mapFormalityLevelToPrompt(FormalityLevel.INFORMAL));
        assertNotNull(mapper.mapFormalityLevelToPrompt(FormalityLevel.NEUTRAL));
        assertNotNull(mapper.mapFormalityLevelToPrompt(FormalityLevel.FORMAL));
    }

    @Test
    void mapFormalityLevelToPrompt_nullLevel_throwsApplicationException() {
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> mapper.mapFormalityLevelToPrompt(null)
        );
        assertEquals(LlmApplicationError.LLM_FORMALITY_LEVEL_INVALID, exception.getErrorCode());
        assertEquals(LlmDetailMessageKey.LLM_FORMALITY_LEVEL_NULL, exception.getMessage());
    }
}

