package org.naho.speech.azure.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.azure.command.SpeechAssessmentCommand;
import org.naho.speech.azure.exception.AzureSpeechErrorCode;
import org.naho.speech.azure.mapper.SpeechAssessmentResultMapper;
import org.naho.speech.azure.model.SpeechAssessment;
import org.naho.speech.azure.port.out.AzureSpeechServicePort;
import org.naho.speech.azure.result.SpeechAssessmentResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpeechAssessmentTest {

    @Mock
    private AzureSpeechServicePort azureSpeechServicePort;
    @Mock
    private SpeechAssessmentResultMapper speechAssessmentResultMapper;

    @InjectMocks
    private SpeechAssessmentUseCase speechAssessmentUseCase;

    @Test
    @DisplayName("UTCID01 - Đánh giá phát âm audio thành công")
    void UTCID01_AssessAudio_Success() {
        SpeechAssessmentCommand command = SpeechAssessmentCommand.builder()
                .audioBytes(new byte[]{1, 2, 3})
                .duration(10.0)
                .userId(1L)
                .build();
        SpeechAssessment assessment = mock(SpeechAssessment.class);
        SpeechAssessmentResult assessmentResult = mock(SpeechAssessmentResult.class);

        when(azureSpeechServicePort.assessAudio(command)).thenReturn(assessment);
        when(speechAssessmentResultMapper.modelToResult(assessment)).thenReturn(assessmentResult);

        SpeechAssessmentResult result = speechAssessmentUseCase.assessAudio(command);

        assertNotNull(result);
        verify(azureSpeechServicePort, times(1)).assessAudio(command);
    }

    @Test
    @DisplayName("UTCID02 - Thất bại do audioBytes rỗng")
    void UTCID02_AssessAudio_AudioEmpty() {
        SpeechAssessmentCommand command = SpeechAssessmentCommand.builder()
                .audioBytes(new byte[0])
                .build();

        ApplicationException ex = assertThrows(ApplicationException.class, () -> speechAssessmentUseCase.assessAudio(command));
        assertEquals(AzureSpeechErrorCode.SPEECH_AUDIO_NOT_VALID, ex.getErrorCode());
    }
}
