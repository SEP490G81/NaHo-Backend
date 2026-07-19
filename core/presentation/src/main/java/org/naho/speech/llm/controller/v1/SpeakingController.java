package org.naho.speech.llm.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.naho.speech.llm.command.SendAudioMessageCommand;
import org.naho.speech.llm.command.SendMessageWithSessionCommand;
import org.naho.speech.llm.command.StartSpeakingConversationWithAICommand;
import org.naho.speech.llm.command.StartSpeakingTopicCommand;
import org.naho.speech.llm.dto.mapper.AudioChatResponseMapper;
import org.naho.speech.llm.dto.mapper.ChatResponseMapper;
import org.naho.speech.llm.dto.mapper.ScoringResponseMapper;
import org.naho.speech.llm.dto.mapper.StartTopicResponseMapper;
import org.naho.speech.llm.dto.mapper.SuggestedTopicsResponseMapper;
import org.naho.speech.llm.dto.mapper.StartConversationResponseMapper;
import org.naho.speech.llm.port.in.EndSessionInputPort;
import org.naho.speech.llm.port.in.SpeakingSessionInputPort;
import org.naho.speech.llm.port.in.SuggestedTopicsInputPort;
import org.naho.speech.llm.dto.request.ChatSessionMessageRequest;
import org.naho.speech.llm.dto.request.EndSessionRequest;
import org.naho.speech.llm.dto.request.StartTopicRequest;
import org.naho.speech.llm.dto.response.AudioChatResponse;
import org.naho.speech.llm.dto.response.ChatResponse;
import org.naho.speech.llm.dto.response.ScoringResponse;
import org.naho.speech.llm.dto.response.StartSessionResponse;
import org.naho.speech.llm.dto.response.StartTopicResponse;
import org.naho.speech.llm.dto.response.SuggestedTopicsResponse;
import org.naho.speech.llm.dto.response.StartConversationResponse;
import org.naho.speech.llm.result.AudioChatResult;
import org.naho.speech.llm.result.ChatResult;
import org.naho.speech.llm.result.ScoringResult;
import org.naho.speech.llm.result.SpeakingTopicResult;
import org.naho.speech.llm.result.SuggestedTopicsResult;
import org.naho.speech.llm.result.StartConversationResult;
import org.naho.shared.annotation.ApiResponseMessage;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * REST Controller: Speaking Practice — luồng hoàn chỉnh nói chuyện với AI.
 * <p>
 * Endpoints:
 * GET  /topics              → Danh sách chủ đề gợi ý
 * POST /session/start-topic → Bắt đầu session theo chủ đề
 * POST /session/start-free  → Bắt đầu free conversation
 * POST /session/{id}/message        → Gửi text message
 * POST /session/{id}/message/stream → Gửi text message (SSE streaming)
 * POST /session/{id}/audio          → Gửi audio → STT + Assessment + AI reply
 * POST /session/{id}/end            → Kết thúc session + scoring
 */
@RestController
@RequestMapping("/api/v1/speaking")
@RequiredArgsConstructor
public class SpeakingController {

    private final SpeakingSessionInputPort speakingSessionInputPort;
    private final EndSessionInputPort endSessionInputPort;
    private final SuggestedTopicsInputPort suggestedTopicsInputPort;
    private final SuggestedTopicsResponseMapper suggestedTopicsResponseMapper;
    private final StartTopicResponseMapper startTopicResponseMapper;
    private final ChatResponseMapper chatResponseMapper;
    private final AudioChatResponseMapper audioChatResponseMapper;
    private final ScoringResponseMapper scoringResponseMapper;
    private final StartConversationResponseMapper startConversationResponseMapper;

    // ─── Topics ─────────────────────────────────────────────────

    @GetMapping(value = "/topics", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponseMessage(message = "Get suggested topics successfully!")
    public ResponseEntity<SuggestedTopicsResponse> getTopics() {
        SuggestedTopicsResult result = suggestedTopicsInputPort.getSuggestedTopics();
        return ResponseEntity.ok(suggestedTopicsResponseMapper.resultToResponse(result));
    }

    // ─── Session Management ─────────────────────────────────────

    @PostMapping(
            value = "/session/start-topic",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponseMessage(message = "Topic session started successfully!")
    public ResponseEntity<StartTopicResponse> startTopicSession(
            @Valid @RequestBody StartTopicRequest request
    ) {
        var command = new StartSpeakingTopicCommand(request.topic());
        SpeakingTopicResult result = speakingSessionInputPort.startTopicSession(command);
        return ResponseEntity.ok(startTopicResponseMapper.resultToResponse(result));
    }

    @PostMapping(
            value = "/session/{personaId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponseMessage(message = "Conversation started successfully!")
    public ResponseEntity<StartConversationResponse> startConversationWithAISession(
            @PathVariable("personaId") int personaId
    ) {
        var command = new StartSpeakingConversationWithAICommand(personaId);
        StartConversationResult result = speakingSessionInputPort.startConversationWithAISession(command);
        return ResponseEntity.ok(startConversationResponseMapper.resultToResponse(result));
    }

    //  Text Message

    @PostMapping(
            value = "/session/{sessionId}/message",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponseMessage(message = "Message sent successfully!")
    public ResponseEntity<ChatResponse> sendMessage(
            @PathVariable("sessionId") String sessionId,
            @Valid @RequestBody ChatSessionMessageRequest request
    ) {
        var command = new SendMessageWithSessionCommand(sessionId, request.transcript());
        ChatResult result = speakingSessionInputPort.sendMessage(command);
        return ResponseEntity.ok(chatResponseMapper.resultToResponse(result));
    }

    @PostMapping(
            value = "/session/{sessionId}/message/stream",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public SseEmitter sendMessageStream(
            @PathVariable("sessionId") String sessionId,
            @Valid @RequestBody ChatSessionMessageRequest request
    ) {
        SseEmitter emitter = new SseEmitter(120_000L);

        var command = new SendMessageWithSessionCommand(sessionId, request.transcript());

        Thread.ofVirtual().start(() -> {
            try {
                speakingSessionInputPort.sendMessageStream(command, token -> {
                    try {
                        emitter.send(SseEmitter.event().data(token));
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                    }
                });

                emitter.send(SseEmitter.event().data("[DONE]"));
                emitter.complete();

            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    // ─── Audio Message (Azure Speech + AI) ──────────────────────

    @PostMapping(
            value = "/session/{sessionId}/audio",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponseMessage(message = "Audio message processed successfully!")
    public ResponseEntity<AudioChatResponse> sendAudioMessage(
            @PathVariable("sessionId") String sessionId,
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "reference-text", required = false) String referenceText
    ) throws IOException {
        var command = new SendAudioMessageCommand(sessionId, file.getBytes(), referenceText);
        AudioChatResult result = speakingSessionInputPort.sendAudioMessage(command);
        return ResponseEntity.ok(audioChatResponseMapper.resultToResponse(result));
    }

    // ─── End Session + Scoring ───────────────────────────────────

    @PostMapping(
            value = "/session/{sessionId}/end",
            consumes = {MediaType.APPLICATION_JSON_VALUE, "application/json;charset=UTF-8"},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponseMessage(message = "Session ended and scored successfully!")
    public ResponseEntity<ScoringResponse> endSession(
            @PathVariable("sessionId") String sessionId,
            @RequestBody(required = false) EndSessionRequest req
    ) {
        String topic = (req != null && req.topic() != null) ? req.topic() : "";
        String speechMetadata = (req != null && req.speechMetadata() != null) ? req.speechMetadata() : "";
        String asrConfidence = (req != null && req.asrConfidence() != null) ? req.asrConfidence() : "";

        ScoringResult r = endSessionInputPort.endSession(sessionId, topic, speechMetadata, asrConfidence);
        return ResponseEntity.ok(scoringResponseMapper.resultToResponse(r));
    }
}
