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
import org.naho.speech.llm.command.StartSpeakingConversationCommand;
import org.naho.speech.llm.dto.mapper.*;
import org.naho.speech.llm.dto.request.ChatSessionMessageRequest;
import org.naho.speech.llm.dto.request.EndSessionRequest;
import org.naho.speech.llm.dto.request.SpeakingSessionQueryRequest;
import org.naho.speech.llm.dto.request.StartConversationRequest;
import org.naho.speech.llm.dto.response.*;
import org.naho.speech.llm.port.in.EndSessionInputPort;
import org.naho.speech.llm.port.in.SpeakingSessionCleanupInputPort;
import org.naho.speech.llm.port.in.SpeakingSessionInputPort;
import org.naho.speech.llm.port.in.SuggestedTopicsInputPort;
import org.naho.speech.llm.result.*;
import org.naho.subscription.port.in.GetActiveSubscriptionInputPort;
import org.naho.subscription.result.SubscriptionPlanResult;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
    private final SpeakingSessionResponseMapper speakingSessionResponseMapper;
    private final FileStorageServicePort fileStorageServicePort;
    private final FileValidatorPort fileValidatorPort;
    private final GetActiveSubscriptionInputPort getActiveSubscriptionInputPort;
    private final SpeakingSessionCleanupInputPort speakingSessionCleanupInputPort;

    // ─── Topics ─────────────────────────────────────────────────

    @GetMapping(value = "/topics", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponseMessage(message = SpeechDetailMessageKey.SPEAKING_TOPICS_GET_SUCCESS)
    public ResponseEntity<SuggestedTopicsResponse> getTopics() {
        SuggestedTopicsResult result = suggestedTopicsInputPort.getSuggestedTopics();
        return ResponseEntity.ok(suggestedTopicsResponseMapper.resultToResponse(result));
    }

    // ─── Session Management ─────────────────────────────────────

    /**
     * Khởi tạo 1 session mới và lưu vào ram và database
     *
     * @param personaId persona id
     * @param payload   chứa user id
     * @param request   bao gồm: FormalityLevel và MarugotoLevel
     * @return trả về session code
     */
    @ApiResponseMessage
    @PostMapping("/session/persona/{personaId}")
    public ResponseEntity<String> startConversation(
            @PathVariable("personaId") Long personaId,
            @AuthenticationPrincipal AccessTokenPayload payload,
            @RequestBody(required = false) StartConversationRequest request
    ) {
        var formalityOverride = request != null ? request.formalityLevel() : null;
        var marugotoOverride = request != null ? request.marugotoLevel() : null;
        var command = new StartSpeakingConversationCommand(payload.userId(), personaId, formalityOverride, marugotoOverride);
        String sessionCode = speakingSessionInputPort.startConversation(command);
        return ResponseEntity.ok(sessionCode);
    }

    /**
     * Sau khi có được session code từ API `startConversation` thì bắt đầu vào phiên trò chuyện
     *
     * @param sessionCode session code
     * @param payload     chứa user id
     * @return StartConversationResponse
     */
    @ApiResponseMessage
    @PostMapping("/session/init/{sessionCode}")
    public ResponseEntity<StartConversationResponse> initFirstGreeting(
            @PathVariable("sessionCode") String sessionCode,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        StartConversationResult result = speakingSessionInputPort.initFirstGreeting(sessionCode, payload.userId());
        return ResponseEntity.ok(startConversationResponseMapper.resultToResponse(result));
    }

    /**
     * Lấy ra chi tiết 1 session (gồm các đoạn chat trong đó)
     *
     * @param payload     chứa user id
     * @param sessionCode session code
     * @return SpeakingSessionResponse
     */
    @ApiResponseMessage
    @GetMapping("/session/details/{sessionCode}")
    public ResponseEntity<SpeakingSessionResponse> getInProgressSessionDetails(
            @AuthenticationPrincipal AccessTokenPayload payload,
            @PathVariable("sessionCode") String sessionCode
    ) {
        SpeakingSessionResult result = speakingSessionInputPort.getInProgressSessionDetails(sessionCode, payload.userId());
        return ResponseEntity.ok(speakingSessionResponseMapper.resultToResponse(result));
    }

    /**
     * Method gửi message để chat với AI
     *
     * @param sessionCode session code
     * @param request     chứa transcript (đoạn nội dung người dùng gửi)
     * @return ChatResponse
     */
    @PostMapping("/session/message/{sessionCode}")
    @ApiResponseMessage(message = SpeechDetailMessageKey.SPEAKING_MESSAGE_SEND_SUCCESS)
    public ResponseEntity<ChatResponse> sendMessage(
            @PathVariable("sessionCode") String sessionCode,
            @Valid @RequestBody ChatSessionMessageRequest request
    ) {
        SendMessageWithSessionCommand command = new SendMessageWithSessionCommand(sessionCode, request.transcript());
        ChatResult result = speakingSessionInputPort.sendMessage(command);
        return ResponseEntity.ok(chatResponseMapper.resultToResponse(result));
    }

    // ─── Audio Message (Azure Speech + AI) ──────────────────────
    @PostMapping(
            value = "/session/audio/{sessionCode}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponseMessage(message = SpeechDetailMessageKey.SPEAKING_AUDIO_MESSAGE_PROCESS_SUCCESS)
    public ResponseEntity<AudioChatResponse> sendAudioMessage(
            @PathVariable("sessionCode") String sessionCode,
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "reference-text", required = false) String referenceText,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        try {
            StoredFile storedFile = null;
            Long userId = payload.userId();
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
                    .sessionCode(sessionCode)
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

    @PostMapping("/session/end/{sessionCode}")
    @ApiResponseMessage(message = SpeechDetailMessageKey.SPEAKING_SESSION_END_SUCCESS)
    public ResponseEntity<ScoringResponse> endSession(
            @PathVariable("sessionCode") String sessionCode,
            @AuthenticationPrincipal AccessTokenPayload payload,
            @RequestBody(required = false) EndSessionRequest req
    ) {
        Long userId = payload.userId();
        String topic = (req != null && req.topic() != null) ? req.topic() : "";
        String speechMetadata = (req != null && req.speechMetadata() != null) ? req.speechMetadata() : "";
        String asrConfidence = (req != null && req.asrConfidence() != null) ? req.asrConfidence() : "";

        ScoringResult r = endSessionInputPort.endSession(userId, sessionCode, topic, speechMetadata, asrConfidence);
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
        Long userId = payload.userId();
        SpeakingSessionDetailResult result = speakingSessionInputPort.getSessionHistoryDetail(sessionCode, userId);
        return ResponseEntity.ok(result);
    }

    /**
     * Delete a session by session code.
     *
     * @param sessionCode session code
     * @param payload     chứa user id
     * @return ResponseEntity<Void>
     */
    @DeleteMapping("/session/{sessionCode}")
    public ResponseEntity<Void> deleteSession(
            @PathVariable("sessionCode") String sessionCode,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        speakingSessionCleanupInputPort.deleteSession(sessionCode, payload.userId());
        return ResponseEntity.noContent().build();
    }
}

