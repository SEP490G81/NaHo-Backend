package org.naho.speech.azure.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.azure.command.SpeechAssessmentCommand;
import org.naho.speech.azure.mapper.PronunciationAssessmentMapper;
import org.naho.speech.azure.port.out.AzureSpeechServicePort;
import org.naho.speech.azure.result.SpeechAssessmentResult;
import org.naho.speech.model.SpeechAssessment;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssessSpeechUseCaseTest {

    @Mock
    private AzureSpeechServicePort azureSpeechServicePort;

    @Mock
    private PronunciationAssessmentMapper pronunciationAssessmentMapper;

    @InjectMocks
    private AssessSpeechUseCase assessSpeechUseCase;

    @Test
    void testExecute_WhenAudioBytesIsNull_ShouldThrowException() {
        SpeechAssessmentCommand command = new SpeechAssessmentCommand(null, "hello");

        ApplicationException exception = assertThrows(ApplicationException.class, () -> {
            assessSpeechUseCase.execute(command);
        });

        assertEquals("SPEECH_A001", exception.getErrorCode().getCode());
        verifyNoInteractions(azureSpeechServicePort);
    }

    @Test
    void testExecute_WhenAudioBytesIsEmpty_ShouldThrowException() {
        // Arrange
        SpeechAssessmentCommand command = new SpeechAssessmentCommand(new byte[0], "hello");

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class, () -> {
            assessSpeechUseCase.execute(command);
        });

        assertEquals("SPEECH_A001", exception.getErrorCode().getCode());
        verifyNoInteractions(azureSpeechServicePort);
    }

    @Test
    void testExecute_WhenValidCommand_ShouldReturnResult() {
        // Arrange
        byte[] audioBytes = new byte[]{1, 2, 3};
        String referenceText = "こんにちは";
        SpeechAssessmentCommand command = new SpeechAssessmentCommand(audioBytes, referenceText);

        SpeechAssessment mockModel = mock(SpeechAssessment.class);
        when(azureSpeechServicePort.assess(command)).thenReturn(mockModel);

        SpeechAssessmentResult mockResult = new SpeechAssessmentResult(
                1L, "こんにちは", 90.0, 95.0, 100.0, 93.0, new ArrayList<>()
        );
        when(pronunciationAssessmentMapper.modelToResult(mockModel)).thenReturn(mockResult);

        // Act
        SpeechAssessmentResult result = assessSpeechUseCase.execute(command);

        // Assert
        assertNotNull(result);
        assertEquals("こんにちは", result.transcriptText());
        assertEquals(90.0, result.accuracyScore());
        assertEquals(95.0, result.fluencyScore());

        verify(azureSpeechServicePort, times(1)).assess(command);
        verify(pronunciationAssessmentMapper, times(1)).modelToResult(mockModel);
    }
}
