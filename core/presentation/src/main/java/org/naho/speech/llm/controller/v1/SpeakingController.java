package org.naho.speech.llm.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.naho.file.constant.FileAccessStatus;
import org.naho.file.constant.FileFolderConstant;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.port.out.FileValidatorPort;
import org.naho.file.result.StoredFile;
import org.naho.i18n.message.speech.SpeechDetailMessageKey;
import org.naho.pagination.PageData;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.shared.exception.PresentationException;
import org.naho.speech.azure.exception.AzureSpeechErrorCode;
import org.naho.speech.llm.command.SendAudioMessageCommand;
import org.naho.speech.llm.command.SendMessageWithSessionCommand;
import org.naho.speech.llm.command.SpeakingSessionFilterCommand;
import org.naho.speech.llm.command.StartSpeakingConversationWithAICommand;
import org.naho.speech.llm.dto.mapper.*;
import org.naho.speech.llm.dto.request.ChatSessionMessageRequest;
import org.naho.speech.llm.dto.request.EndSessionRequest;
import org.naho.speech.llm.dto.request.SpeakingSessionQueryRequest;
import org.naho.speech.llm.dto.request.StartConversationRequest;
import org.naho.speech.llm.dto.response.*;
import org.naho.speech.llm.port.in.EndSessionInputPort;
import org.naho.speech.llm.port.in.SpeakingSessionInputPort;
import org.naho.speech.llm.port.in.SuggestedTopicsInputPort;
import org.naho.speech.llm.result.*;
import org.naho.subscription.port.in.GetActiveSubscriptionInputPort;
import org.naho.subscription.result.SubscriptionPlanResult;
import org.naho.subscription.type.PlanCode;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final ChatResponseMapper chatResponseMapper;
    private final AudioChatResponseMapper audioChatResponseMapper;
    private final ScoringResponseMapper scoringResponseMapper;
    private final StartConversationResponseMapper startConversationResponseMapper;
    private final FileStorageServicePort fileStorageServicePort;
    private final FileValidatorPort fileValidatorPort;
    private final GetActiveSubscriptionInputPort getActiveSubscriptionInputPort;

    // ─── Topics ─────────────────────────────────────────────────

    @GetMapping(value = "/topics", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponseMessage(message = SpeechDetailMessageKey.SPEAKING_TOPICS_GET_SUCCESS)
    public ResponseEntity<SuggestedTopicsResponse> getTopics() {
        SuggestedTopicsResult result = suggestedTopicsInputPort.getSuggestedTopics();
        return ResponseEntity.ok(suggestedTopicsResponseMapper.resultToResponse(result));
    }

    // ─── Session Management ─────────────────────────────────────

    @PostMapping(
            value = "/session/{personaId}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.ALL_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponseMessage(message = SpeechDetailMessageKey.SPEAKING_CONVERSATION_START_SUCCESS)
    public ResponseEntity<StartConversationResponse> startConversationWithAISession(
            @PathVariable("personaId") int personaId,
            @AuthenticationPrincipal AccessTokenPayload payload,
            @RequestBody(required = false) StartConversationRequest request
    ) {
        Long userId = payload != null ? payload.userId() : null;
        var formalityOverride = request != null ? request.formalityLevel() : null;
        var marugotoOverride = request != null ? request.marugotoLevel() : null;
        var command = new StartSpeakingConversationWithAICommand(userId, personaId, formalityOverride, marugotoOverride);
        StartConversationResult result = speakingSessionInputPort.startConversationWithAISession(command);
        return ResponseEntity.ok(startConversationResponseMapper.resultToResponse(result));
    }

    @GetMapping(value = "/session/active", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponseMessage(message = "Lấy thông tin phiên nói chuyện dở dang thành công.")
    public ResponseEntity<ActiveSpeakingSessionResponse> getActiveSession(
            @AuthenticationPrincipal AccessTokenPayload payload,
            @RequestParam(value = "personaId", required = false) Integer personaId
    ) {
        Long userId = payload != null ? payload.userId() : null;
        ActiveSpeakingSessionResult result = speakingSessionInputPort.getActiveSession(userId, personaId);
        if (result == null) {
            return ResponseEntity.ok(null);
        }
        ActiveSpeakingSessionResponse response = new ActiveSpeakingSessionResponse(
                result.id(),
                result.sessionCode(),
                result.personaId(),
                result.topic(),
                result.marugotoLevel(),
                result.formalityLevel(),
                result.totalTurns(),
                result.startedAt(),
                result.messages().stream().map(m -> new ActiveSpeakingSessionResponse.SessionMessageItem(
                        m.turnIndex(),
                        m.senderType(),
                        m.content(),
                        m.correctedText(),
                        m.correctionExplanation(),
                        m.grammarNote(),
                        m.hintForLearner(),
                        m.audioUrl()
                )).toList()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/session/{sessionCode}/resume", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponseMessage(message = "Khôi phục phiên nói chuyện thành công.")
    public ResponseEntity<StartConversationResponse> resumeSession(
            @PathVariable("sessionCode") String sessionCode,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        Long userId = payload != null ? payload.userId() : null;
        StartConversationResult result = speakingSessionInputPort.resumeSession(sessionCode, userId);
        return ResponseEntity.ok(startConversationResponseMapper.resultToResponse(result));
    }

    //  Text Message

    @PostMapping(
            value = "/session/{sessionId}/message",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponseMessage(message = SpeechDetailMessageKey.SPEAKING_MESSAGE_SEND_SUCCESS)
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
    @ApiResponseMessage(message = SpeechDetailMessageKey.SPEAKING_AUDIO_MESSAGE_PROCESS_SUCCESS)
    public ResponseEntity<AudioChatResponse> sendAudioMessage(
            @PathVariable("sessionId") String sessionId,
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "reference-text", required = false) String referenceText,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        try {
            StoredFile storedFile = null;
            Long userId = payload != null ? payload.userId() : null;
            byte[] audioBytes = file != null ? file.getBytes() : new byte[0];

            if (userId != null) {
                SubscriptionPlanResult subscriptionPlan = getActiveSubscriptionInputPort.getUserActiveSubscriptionPlan(userId);
                if (subscriptionPlan != null) {
                    double durationLimit = subscriptionPlan.maxSpeakingQuestionRecordingSeconds() != null
                            ? subscriptionPlan.maxSpeakingQuestionRecordingSeconds().doubleValue()
                            : 0.0;
                    fileValidatorPort.validateWavFileAndDuration(audioBytes, durationLimit);

                    storedFile = fileStorageServicePort.saveFileToLocal(file, FileFolderConstant.RECORDINGS, FileAccessStatus.PRIVATE);
                }
            }

            var command = SendAudioMessageCommand.builder()
                    .sessionId(sessionId)
                    .audioBytes(audioBytes)
                    .referenceText(referenceText)
                    .storedFile(storedFile)
                    .userId(userId)
                    .build();

            AudioChatResult result = speakingSessionInputPort.sendAudioMessage(command);
            return ResponseEntity.ok(audioChatResponseMapper.resultToResponse(result));

        } catch (IOException e) {
            throw new PresentationException(
                    AzureSpeechErrorCode.SPEECH_AUDIO_NOT_VALID,
                    SpeechDetailMessageKey.SPEECH_AUDIO_FILE_EMPTY,
                    e.getMessage());
        }
    }

    // ─── End Session + Scoring ───────────────────────────────────

    @PostMapping(
            value = "/session/{sessionId}/end",
            consumes = {MediaType.APPLICATION_JSON_VALUE, "application/json;charset=UTF-8"},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponseMessage(message = SpeechDetailMessageKey.SPEAKING_SESSION_END_SUCCESS)
    public ResponseEntity<ScoringResponse> endSession(
            @PathVariable("sessionId") String sessionId,
            @AuthenticationPrincipal AccessTokenPayload payload,
            @RequestBody(required = false) EndSessionRequest req
    ) {
        Long userId = payload != null ? payload.userId() : null;
        String topic = (req != null && req.topic() != null) ? req.topic() : "";
        String speechMetadata = (req != null && req.speechMetadata() != null) ? req.speechMetadata() : "";
        String asrConfidence = (req != null && req.asrConfidence() != null) ? req.asrConfidence() : "";

        ScoringResult r = endSessionInputPort.endSession(userId, sessionId, topic, speechMetadata, asrConfidence);
        return ResponseEntity.ok(scoringResponseMapper.resultToResponse(r));
    }

    // ─── Session History Queries ────────────────────────────────

    @PostMapping(value = "/session/history", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponseMessage(message = SpeechDetailMessageKey.SPEAKING_HISTORY_GET_ALL_SUCCESS)
    public ResponseEntity<PageData<SpeakingSessionListItemResponse>> getUserSessionHistories(
            @AuthenticationPrincipal AccessTokenPayload payload,
            @RequestBody(required = false) SpeakingSessionQueryRequest request
    ) {
        if (request == null) {
            request = new SpeakingSessionQueryRequest();
        }
        var command = new SpeakingSessionFilterCommand(
                payload.userId(),
                request.getPersonaId(),
                request.getSearch(),
                request.getStatus(),
                request.getPage(),
                request.getSize(),
                request.getSortColumn(),
                request.getSortDirection()
        );
        PageData<SpeakingSessionListItemResult> result = speakingSessionInputPort.getUserSessionHistories(command);

        PageData<SpeakingSessionListItemResponse> response = PageData.<SpeakingSessionListItemResponse>builder()
                .pageMeta(result.getPageMeta())
                .data(result.getData().stream().map(item -> new SpeakingSessionListItemResponse(
                        item.id(),
                        item.sessionCode(),
                        item.topic(),
                        item.personaId(),
                        item.marugotoLevel(),
                        item.formalityLevel(),
                        item.overallScore(),
                        item.jlptEstimate(),
                        item.totalTurns(),
                        item.durationSeconds(),
                        item.startedAt(),
                        item.endedAt(),
                        item.status()   // status: COMPLETED hoặc IN_PROGRESS
                )).toList())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/session/history/{sessionCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponseMessage(message = SpeechDetailMessageKey.SPEAKING_HISTORY_GET_DETAIL_SUCCESS)
    public ResponseEntity<SpeakingSessionDetailResult> getSessionHistoryDetail(
            @PathVariable("sessionCode") String sessionCode,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        Long userId = payload != null ? payload.userId() : null;
        SpeakingSessionDetailResult result = speakingSessionInputPort.getSessionHistoryDetail(sessionCode, userId);
        return ResponseEntity.ok(result);
    }
}

