package org.naho.speech.llm.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.speech.llm.port.out.AiScoringPort;
import org.naho.speech.llm.port.out.SessionStorePort;
import org.naho.speech.llm.result.ScoringResult;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EndSessionUseCaseTest {

    @Mock
    private SessionStorePort sessionStorePort;

    @Mock
    private AiScoringPort aiScoringPort;

    @InjectMocks
    private EndSessionUseCase endSessionUseCase;

    @Test
    void testEndSession() {
        // Arrange
        String sessionId = "session-123";
        String topic = "自己紹介";
        String speechMetadata = "meta-data";
        String asrConfidence = "0.95";
        String fullTranscript = "User: こんにちは\nAssistant: はじめまして！";

        when(sessionStorePort.getFullTranscript(sessionId)).thenReturn(fullTranscript);

        ScoringResult mockResult = new ScoringResult(
                sessionId,
                85,
                "N4",
                80, 85, 75, 80, 90, 85, 80,
                "Good summary",
                new ArrayList<>(),
                new ArrayList<>(),
                new HashMap<>(),
                new ArrayList<>()
        );

        when(aiScoringPort.score(sessionId, topic, fullTranscript, speechMetadata, asrConfidence))
                .thenReturn(mockResult);

        // Act
        ScoringResult result = endSessionUseCase.endSession(sessionId, topic, speechMetadata, asrConfidence);

        // Assert
        assertNotNull(result);
        assertEquals(85, result.overallScore());
        assertEquals("N4", result.jlptEstimate());
        assertEquals("Good summary", result.summary());

        verify(sessionStorePort, times(1)).getFullTranscript(sessionId);
        verify(aiScoringPort, times(1)).score(sessionId, topic, fullTranscript, speechMetadata, asrConfidence);
        verify(sessionStorePort, times(1)).clearSession(sessionId);
    }
}
