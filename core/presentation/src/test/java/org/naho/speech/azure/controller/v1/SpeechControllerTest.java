package org.naho.speech.azure.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.naho.shared.filter.RequestLoggingFilter;
import org.naho.speech.azure.command.SpeechAssessmentCommand;
import org.naho.speech.azure.command.TextToSpeechCommand;
import org.naho.speech.azure.dto.mapper.PronunciationAssessmentMapper;
import org.naho.speech.azure.dto.mapper.TextToSpeechRequestMapper;
import org.naho.speech.azure.dto.request.TextToSpeechRequest;
import org.naho.speech.azure.dto.response.PronunciationAssessmentResponse;
import org.naho.speech.azure.port.in.AssessSpeechInputPort;
import org.naho.speech.azure.port.in.TextToSpeechInputPort;
import org.naho.speech.azure.result.AudioSpeechResult;
import org.naho.speech.azure.result.SpeechAssessmentResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = SpeechController.class,
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
                                RequestLoggingFilter.class
                        }
                )
        }
)
class SpeechControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AssessSpeechInputPort assessSpeechInputPort;

    @MockBean
    private PronunciationAssessmentMapper pronunciationAssessmentMapper;

    @MockBean
    private TextToSpeechInputPort textToSpeechInputPort;

    @MockBean
    private TextToSpeechRequestMapper textToSpeechRequestMapper;

    @Test
    @DisplayName("POST /api/v1/speech/assess - Nên đánh giá phát âm trực tiếp từ file audio thành công")
    void assessPronunciation_ShouldReturnAssessment() throws Exception {
        // Arrange
        byte[] audioBytes = new byte[]{1, 2, 3, 4};
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.wav",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                audioBytes
        );

        SpeechAssessmentResult mockResult = new SpeechAssessmentResult(
                1L, "こんにちは", 90.0, 95.0, 100.0, 93.0, List.of()
        );
        PronunciationAssessmentResponse mockResponse = new PronunciationAssessmentResponse(
                1L, "こんにちは", 90.0, 95.0, 100.0, 93.0, List.of()
        );

        when(assessSpeechInputPort.execute(any(SpeechAssessmentCommand.class))).thenReturn(mockResult);
        when(pronunciationAssessmentMapper.resultToResponse(mockResult)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(multipart("/api/v1/speech/assess")
                        .file(mockFile)
                        .param("reference-text", "こんにちは"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.transcriptText").value("こんにちは"))
                .andExpect(jsonPath("$.accuracyScore").value(90.0))
                .andExpect(jsonPath("$.fluencyScore").value(95.0));

        verify(assessSpeechInputPort, times(1)).execute(argThat(cmd ->
                cmd.referenceText().equals("こんにちは") &&
                        cmd.audioBytes().length == 4
        ));
    }

    @Test
    @DisplayName("POST /api/v1/speech/synthesis - Nên tổng hợp văn bản thành tiếng nói (TTS) thành công")
    void synthesizeSpeech_ShouldReturnAudioWavBytes() throws Exception {
        // Arrange
        TextToSpeechRequest request = new TextToSpeechRequest("こんにちは", "ja-JP-NanamiNeural", "ja-JP");
        TextToSpeechCommand command = new TextToSpeechCommand("こんにちは", "ja-JP-NanamiNeural", "ja-JP");
        byte[] expectedAudioBytes = new byte[]{12, 11, 10, 9};
        AudioSpeechResult result = new AudioSpeechResult(expectedAudioBytes, "audio/wav");

        when(textToSpeechRequestMapper.requestToCommand(any(TextToSpeechRequest.class))).thenReturn(command);
        when(textToSpeechInputPort.execute(any(TextToSpeechCommand.class))).thenReturn(result);

        // Act & Assert
        mockMvc.perform(post("/api/v1/speech/synthesis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "audio/wav"))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"synthesized_speech.wav\""))
                .andExpect(content().bytes(expectedAudioBytes));

        verify(textToSpeechRequestMapper, times(1)).requestToCommand(argThat(req ->
                req.text().equals("こんにちは") &&
                        req.voiceName().equals("ja-JP-NanamiNeural") &&
                        req.language().equals("ja-JP")
        ));
        verify(textToSpeechInputPort, times(1)).execute(command);
    }
}
