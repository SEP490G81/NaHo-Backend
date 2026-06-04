package org.naho.speech.llm.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.speech.llm.command.SendAudioMessageCommand;
import org.naho.speech.llm.command.SendMessageWithSessionCommand;
import org.naho.speech.llm.command.StartSpeakingCommand;
import org.naho.speech.llm.port.out.AiChatPort;
import org.naho.speech.llm.port.out.SessionStorePort;
import org.naho.speech.llm.port.out.SpeechToTextPort;
import org.naho.speech.llm.result.AudioChatResult;
import org.naho.speech.llm.result.ChatResult;
import org.naho.speech.llm.result.SpeakingTopicResult;
import org.naho.speech.llm.result.SpeechToTextResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpeakingSessionUseCaseTest {

    @Mock
    private AiChatPort aiChatPort;

    @Mock
    private SessionStorePort sessionStorePort;

    @Mock
    private SpeechToTextPort speechToTextPort;

    @InjectMocks
    private SpeakingSessionUseCase speakingSessionUseCase;

    @Test
    void testStartTopicSession() {
        // Arrange
        String topic = "自己紹介";
        StartSpeakingCommand command = new StartSpeakingCommand(topic);
        List<Map<String, String>> mockHistory = new ArrayList<>();
        Map<String, String> msg = new HashMap<>();
        msg.put("role", "system");
        msg.put("content", "prompt");
        mockHistory.add(msg);

        when(aiChatPort.chatWithContext(anyList())).thenReturn("はじめまして！");
        when(sessionStorePort.getConversationHistory(anyString())).thenReturn(mockHistory);

        // Act
        SpeakingTopicResult result = speakingSessionUseCase.startTopicSession(command);

        // Assert
        assertNotNull(result);
        assertNotNull(result.sessionId());
        assertEquals(topic, result.topic());
        assertEquals("はじめまして！", result.aiGreeting());

        verify(sessionStorePort, times(1)).initSession(result.sessionId());
        verify(sessionStorePort, times(1)).setTopic(result.sessionId(), topic);
        verify(sessionStorePort, times(3)).addMessage(eq(result.sessionId()), anyString(), anyString());
        verify(sessionStorePort, times(1)).appendTranscript(eq(result.sessionId()), anyString());
    }

    @Test
    void testStartFreeSession() {
        // Act
        String sessionId = speakingSessionUseCase.startFreeSession();

        // Assert
        assertNotNull(sessionId);
        verify(sessionStorePort, times(1)).initSession(sessionId);
        verify(sessionStorePort, times(1)).setTopic(sessionId, "Free conversation");
        verify(sessionStorePort, times(1)).addMessage(eq(sessionId), eq("system"), anyString());
    }

    @Test
    void testSendMessage() {
        // Arrange
        String sessionId = "session-123";
        String userMessage = "こんにちは";
        SendMessageWithSessionCommand command = new SendMessageWithSessionCommand(sessionId, userMessage);
        List<Map<String, String>> mockHistory = new ArrayList<>();

        when(aiChatPort.chatWithContext(anyList())).thenReturn("こんにちは！元気ですか？");
        when(sessionStorePort.getConversationHistory(sessionId)).thenReturn(mockHistory);

        // Act
        ChatResult result = speakingSessionUseCase.sendMessage(command);

        // Assert
        assertNotNull(result);
        assertEquals("こんにちは！元気ですか？", result.assistantReply());
        verify(sessionStorePort, times(1)).addMessage(sessionId, "user", userMessage);
        verify(sessionStorePort, times(1)).addMessage(sessionId, "assistant", "こんにちは！元気ですか？");
        verify(sessionStorePort, times(1)).appendTranscript(eq(sessionId), anyString());
    }

    @Test
    void testSendMessageStream() {
        // Arrange
        String sessionId = "session-123";
        String userMessage = "こんにちは";
        SendMessageWithSessionCommand command = new SendMessageWithSessionCommand(sessionId, userMessage);
        List<Map<String, String>> mockHistory = new ArrayList<>();

        doAnswer(invocation -> {
            Consumer<String> consumer = invocation.getArgument(1);
            consumer.accept("こん");
            consumer.accept("にちは");
            return null;
        }).when(aiChatPort).chatStreamWithContext(anyList(), any());

        when(sessionStorePort.getConversationHistory(sessionId)).thenReturn(mockHistory);

        StringBuilder output = new StringBuilder();

        // Act
        speakingSessionUseCase.sendMessageStream(command, output::append);

        // Assert
        assertEquals("こんにちは", output.toString());
        verify(sessionStorePort, times(1)).addMessage(sessionId, "user", userMessage);
        verify(sessionStorePort, times(1)).addMessage(sessionId, "assistant", "こんにちは");
        verify(sessionStorePort, times(1)).appendTranscript(eq(sessionId), anyString());
    }

    @Test
    void testSendAudioMessage() {
        // Arrange
        String sessionId = "session-123";
        byte[] audioBytes = new byte[]{1, 2, 3};
        String referenceText = "こんにちは";
        SendAudioMessageCommand command = new SendAudioMessageCommand(sessionId, audioBytes, referenceText);

        SpeechToTextResult mockStt = new SpeechToTextResult("こんにちは", 95.0, 90.0, 100.0, 94.0);
        when(speechToTextPort.transcribeAndAssess(audioBytes, referenceText)).thenReturn(mockStt);
        when(aiChatPort.chatWithContext(anyList())).thenReturn("こんにちは！");
        when(sessionStorePort.getConversationHistory(sessionId)).thenReturn(new ArrayList<>());

        // Act
        AudioChatResult result = speakingSessionUseCase.sendAudioMessage(command);

        // Assert
        assertNotNull(result);
        assertEquals("こんにちは", result.transcribedText());
        assertEquals("こんにちは！", result.assistantReply());
        assertEquals(95.0, result.accuracyScore());
        assertEquals(90.0, result.fluencyScore());
        assertEquals(100.0, result.completenessScore());
        assertEquals(94.0, result.pronunciationScore());

        verify(sessionStorePort, times(1)).addMessage(sessionId, "user", "こんにちは");
        verify(sessionStorePort, times(1)).addMessage(sessionId, "assistant", "こんにちは！");
        verify(sessionStorePort, times(1)).appendTranscript(eq(sessionId), anyString());
    }
}
