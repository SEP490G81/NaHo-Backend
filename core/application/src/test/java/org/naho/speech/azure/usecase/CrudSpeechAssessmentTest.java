package org.naho.speech.azure.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.question.exception.SpeakingQuestionErrorCode;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.azure.mapper.SpeechAssessmentResultMapper;
import org.naho.speech.azure.model.SpeechAssessment;
import org.naho.speech.azure.port.out.SpeechAssessmentRepositoryPort;
import org.naho.speech.azure.result.SpeechAssessmentResult;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrudSpeechAssessmentTest {

    @Mock
    private SpeechAssessmentResultMapper speechAssessmentResultMapper;
    @Mock
    private SpeechAssessmentRepositoryPort speechAssessmentRepositoryPort;

    @InjectMocks
    private CrudSpeechAssessmentUseCase crudSpeechAssessmentUseCase;

    @Test
    @DisplayName("UTCID01 - Tìm SpeechAssessment theo ID thành công")
    void UTCID01_FindById_Success() {
        SpeechAssessment assessment = mock(SpeechAssessment.class);
        SpeechAssessmentResult assessmentResult = mock(SpeechAssessmentResult.class);

        when(speechAssessmentRepositoryPort.findById(1L)).thenReturn(Optional.of(assessment));
        when(speechAssessmentResultMapper.modelToResult(assessment)).thenReturn(assessmentResult);

        SpeechAssessmentResult result = crudSpeechAssessmentUseCase.findById(1L);

        assertNotNull(result);
    }

    @Test
    @DisplayName("UTCID02 - Thất bại khi id null")
    void UTCID02_FindById_IdNull() {
        ApplicationException ex = assertThrows(ApplicationException.class, () -> crudSpeechAssessmentUseCase.findById(null));
        assertEquals(SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("UTCID03 - Thất bại do không tìm thấy SpeechAssessment")
    void UTCID03_FindById_NotFound() {
        when(speechAssessmentRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> crudSpeechAssessmentUseCase.findById(99L));
        assertEquals(SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND, ex.getErrorCode());
    }
}
