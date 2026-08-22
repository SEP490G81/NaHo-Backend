package org.naho.speech.llm.question.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.question.exception.SpeakingQuestionErrorCode;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.llm.model.question.AiFeedback;
import org.naho.speech.llm.question.mapper.AiFeedbackResultMapper;
import org.naho.speech.llm.question.port.out.AiFeedbackRepositoryPort;
import org.naho.speech.llm.question.result.AiFeedbackResult;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrudAiFeedbackTest {

    @Mock
    private AiFeedbackRepositoryPort aiFeedbackRepositoryPort;
    @Mock
    private AiFeedbackResultMapper aiFeedbackResultMapper;

    @InjectMocks
    private CrudAiFeedbackUseCase crudAiFeedbackUseCase;

    @Test
    @DisplayName("UTCID01 - Tìm AiFeedback theo ID thành công")
    void UTCID01_FindById_Success() {
        AiFeedback feedback = mock(AiFeedback.class);
        AiFeedbackResult resultObj = mock(AiFeedbackResult.class);

        when(aiFeedbackRepositoryPort.findById(1L)).thenReturn(Optional.of(feedback));
        when(aiFeedbackResultMapper.domainToResult(feedback)).thenReturn(resultObj);

        AiFeedbackResult result = crudAiFeedbackUseCase.findById(1L);

        assertNotNull(result);
    }

    @Test
    @DisplayName("UTCID02 - Thất bại do id null")
    void UTCID02_FindById_IdNull() {
        ApplicationException ex = assertThrows(ApplicationException.class, () -> crudAiFeedbackUseCase.findById(null));
        assertEquals(SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("UTCID03 - Thất bại do không tìm thấy AiFeedback")
    void UTCID03_FindById_NotFound() {
        when(aiFeedbackRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> crudAiFeedbackUseCase.findById(99L));
        assertEquals(SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND, ex.getErrorCode());
    }
}
