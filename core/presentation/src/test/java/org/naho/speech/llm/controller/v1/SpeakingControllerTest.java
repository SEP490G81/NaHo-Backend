package org.naho.speech.llm.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.naho.speech.llm.command.SendAudioMessageCommand;
import org.naho.speech.llm.command.SendMessageWithSessionCommand;
import org.naho.speech.llm.command.StartSpeakingCommand;
import org.naho.speech.llm.dto.mapper.*;
import org.naho.speech.llm.dto.request.ChatSessionMessageRequest;
import org.naho.speech.llm.dto.request.EndSessionRequest;
import org.naho.speech.llm.dto.request.StartTopicRequest;
import org.naho.speech.llm.dto.response.*;
import org.naho.speech.llm.port.in.EndSessionInputPort;
import org.naho.speech.llm.port.in.SpeakingSessionInputPort;
import org.naho.speech.llm.port.in.SuggestedTopicsInputPort;
import org.naho.speech.llm.result.AudioChatResult;
import org.naho.speech.llm.result.ChatResult;
import org.naho.speech.llm.result.ScoringResult;
import org.naho.speech.llm.result.SpeakingTopicResult;
import org.naho.speech.llm.result.SuggestedTopicsResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = SpeakingController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class
        },
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                org.naho.shared.handler.ApiResponseHandler.class,
                                org.naho.shared.handler.GlobalExceptionHandler.class,
                                org.naho.shared.handler.RequestLoggingFilter.class
                        }
                )
        }
)
class SpeakingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SpeakingSessionInputPort speakingSessionInputPort;

    @MockBean
    private EndSessionInputPort endSessionInputPort;

    @MockBean
    private SuggestedTopicsInputPort suggestedTopicsInputPort;

    @MockBean
    private SuggestedTopicsResponseMapper suggestedTopicsResponseMapper;

    @MockBean
    private StartTopicResponseMapper startTopicResponseMapper;

    @MockBean
    private ChatResponseMapper chatResponseMapper;

    @MockBean
    private AudioChatResponseMapper audioChatResponseMapper;

    @MockBean
    private ScoringResponseMapper scoringResponseMapper;

    @Test
    @DisplayName("GET /api/v1/speaking/topics - Nên trả về danh sách chủ đề gợi ý với HTTP 200")
    void getTopics_ShouldReturnSuggestedTopics() throws Exception {
        // Arrange
        SuggestedTopicsResult mockResult = new SuggestedTopicsResult(
                List.of(new SuggestedTopicsResult.TopicItem("Travel_JA", "Travel_VIE", "Hội thoại du lịch", "N4"))
        );
        SuggestedTopicsResponse mockResponse = new SuggestedTopicsResponse(
                List.of(new SuggestedTopicsResponse.TopicItem("Travel_JA", "Travel_VIE", "Hội thoại du lịch", "N4"))
        );

        when(suggestedTopicsInputPort.getSuggestedTopics()).thenReturn(mockResult);
        when(suggestedTopicsResponseMapper.resultToResponse(mockResult)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/speaking/topics")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.topics[0].nameJa").value("Travel_JA"))
                .andExpect(jsonPath("$.topics[0].nameVie").value("Travel_VIE"))
                .andExpect(jsonPath("$.topics[0].description").value("Hội thoại du lịch"))
                .andExpect(jsonPath("$.topics[0].jlptLevel").value("N4"));

        verify(suggestedTopicsInputPort, times(1)).getSuggestedTopics();
    }

    @Test
    @DisplayName("POST /api/v1/speaking/session/start-topic - Nên bắt đầu phiên học theo chủ đề với HTTP 200")
    void startTopicSession_WithValidRequest_ShouldReturnSessionInfo() throws Exception {
        // Arrange
        StartTopicRequest request = new StartTopicRequest("Kobe Beef");
        SpeakingTopicResult mockResult = new SpeakingTopicResult("session-uuid-123", "Kobe Beef", "こんにちは！");
        StartTopicResponse mockResponse = new StartTopicResponse("session-uuid-123", "Kobe Beef", "こんにちは！");

        when(speakingSessionInputPort.startTopicSession(any(StartSpeakingCommand.class))).thenReturn(mockResult);
        when(startTopicResponseMapper.resultToResponse(mockResult)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/speaking/session/start-topic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("session-uuid-123"))
                .andExpect(jsonPath("$.topic").value("Kobe Beef"))
                .andExpect(jsonPath("$.aiGreeting").value("こんにちは！"));

        verify(speakingSessionInputPort, times(1)).startTopicSession(argThat(cmd -> cmd.topic().equals("Kobe Beef")));
    }

    @Test
    @DisplayName("POST /api/v1/speaking/session/start-topic - Trả về 400 Bad Request nếu Topic trống")
    void startTopicSession_WithBlankTopic_ShouldReturn400() throws Exception {
        // Arrange
        StartTopicRequest request = new StartTopicRequest("   ");

        // Act & Assert
        mockMvc.perform(post("/api/v1/speaking/session/start-topic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(speakingSessionInputPort);
    }

    @Test
    @DisplayName("POST /api/v1/speaking/session/start-free - Nên bắt đầu phiên nói chuyện tự do với HTTP 200")
    void startFreeSession_ShouldReturnSessionId() throws Exception {
        // Arrange
        String mockSessionId = "free-session-456";
        when(speakingSessionInputPort.startFreeSession()).thenReturn(mockSessionId);

        // Act & Assert
        mockMvc.perform(post("/api/v1/speaking/session/start-free"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("free-session-456"));

        verify(speakingSessionInputPort, times(1)).startFreeSession();
    }

    @Test
    @DisplayName("POST /api/v1/speaking/session/{id}/message - Nên gửi tin nhắn text thành công")
    void sendMessage_ShouldReturnReply() throws Exception {
        // Arrange
        String sessionId = "session-123";
        ChatSessionMessageRequest request = new ChatSessionMessageRequest("こんにちは");
        ChatResult mockResult = new ChatResult("お元気ですか？");
        ChatResponse mockResponse = new ChatResponse("お元気ですか？");

        when(speakingSessionInputPort.sendMessage(any(SendMessageWithSessionCommand.class))).thenReturn(mockResult);
        when(chatResponseMapper.resultToResponse(mockResult)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/speaking/session/{sessionId}/message", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assistantReply").value("お元気ですか？"));

        verify(speakingSessionInputPort, times(1)).sendMessage(argThat(cmd ->
                cmd.sessionId().equals(sessionId) && cmd.userMessage().equals("こんにちは")
        ));
    }

    @Test
    @DisplayName("POST /api/v1/speaking/session/{id}/audio - Nên xử lý file ghi âm và trả về transcript + phản hồi")
    void sendAudioMessage_ShouldReturnAudioChatResponse() throws Exception {
        // Arrange
        String sessionId = "session-123";
        byte[] audioBytes = new byte[]{9, 8, 7};
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "audio.wav",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                audioBytes
        );

        AudioChatResult mockResult = new AudioChatResult(
                "こんにちは", "お元気ですか？",
                95.0, 90.0, 100.0, 94.0
        );
        AudioChatResponse mockResponse = new AudioChatResponse(
                "こんにちは", "お元気ですか？",
                95.0, 90.0, 100.0, 94.0
        );

        when(speakingSessionInputPort.sendAudioMessage(any(SendAudioMessageCommand.class))).thenReturn(mockResult);
        when(audioChatResponseMapper.resultToResponse(mockResult)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(multipart("/api/v1/speaking/session/{sessionId}/audio", sessionId)
                        .file(mockFile)
                        .param("reference-text", "こんにちは"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transcribedText").value("こんにちは"))
                .andExpect(jsonPath("$.assistantReply").value("お元気ですか？"))
                .andExpect(jsonPath("$.accuracyScore").value(95.0))
                .andExpect(jsonPath("$.fluencyScore").value(90.0));

        verify(speakingSessionInputPort, times(1)).sendAudioMessage(argThat(cmd ->
                cmd.sessionId().equals(sessionId) &&
                cmd.referenceText().equals("こんにちは") &&
                cmd.audioBytes().length == 3
        ));
    }

    @Test
    @DisplayName("POST /api/v1/speaking/session/{id}/end - Nên kết thúc session và chấm điểm thành công")
    void endSession_ShouldReturnScoring() throws Exception {
        // Arrange
        String sessionId = "session-123";
        EndSessionRequest request = new EndSessionRequest("Travel", "metadata", "0.95");
        ScoringResult mockResult = new ScoringResult(
                sessionId, 85, "N3", 80, 85, 90, 80, 85, 85, 90,
                "Good effort", List.of("Grammar"), List.of("Fluency"),
                Map.of("grammar", "Good use of particles"), List.of()
        );
        ScoringResponse.Scores scores = new ScoringResponse.Scores(80, 85, 90, 80, 85, 85, 90);
        ScoringResponse mockResponse = new ScoringResponse(
                sessionId, 85, "N3", scores,
                "Good effort", List.of("Grammar"), List.of("Fluency"),
                Map.of("grammar", "Good use of particles"), List.of()
        );

        when(endSessionInputPort.endSession(eq(sessionId), eq("Travel"), eq("metadata"), eq("0.95"))).thenReturn(mockResult);
        when(scoringResponseMapper.resultToResponse(mockResult)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/speaking/session/{sessionId}/end", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.overallScore").value(85))
                .andExpect(jsonPath("$.jlptEstimate").value("N3"))
                .andExpect(jsonPath("$.summary").value("Good effort"));

        verify(endSessionInputPort, times(1)).endSession(sessionId, "Travel", "metadata", "0.95");
    }
}
