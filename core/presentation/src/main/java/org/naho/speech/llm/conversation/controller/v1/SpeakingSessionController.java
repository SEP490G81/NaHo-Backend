package org.naho.speech.llm.conversation.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.naho.file.constant.FileAccessStatus;
import org.naho.file.constant.FileFolderConstant;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.port.out.FileValidatorPort;
import org.naho.file.result.StoredFile;
import org.naho.i18n.message.speech.SpeechDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.shared.exception.PresentationException;
import org.naho.speech.azure.exception.AzureSpeechErrorCode;
import org.naho.speech.llm.conversation.command.SendAudioMessageCommand;
import org.naho.speech.llm.conversation.command.SendMessageWithSessionCommand;
import org.naho.speech.llm.conversation.command.StartSpeakingConversationCommand;
import org.naho.speech.llm.conversation.dto.mapper.AudioChatResponseMapper;
import org.naho.speech.llm.conversation.dto.mapper.ChatResponseMapper;
import org.naho.speech.llm.conversation.dto.mapper.SpeakingSessionResponseMapper;
import org.naho.speech.llm.conversation.dto.mapper.StartConversationResponseMapper;
import org.naho.speech.llm.conversation.dto.request.ChatSessionMessageRequest;
import org.naho.speech.llm.conversation.dto.request.EndSessionRequest;
import org.naho.speech.llm.conversation.dto.request.StartConversationRequest;
import org.naho.speech.llm.conversation.dto.response.*;
import org.naho.speech.llm.conversation.port.in.CrudSpeakingSessionInputPort;
import org.naho.speech.llm.conversation.port.in.EndSessionInputPort;
import org.naho.speech.llm.conversation.port.in.SpeakingSessionCleanupInputPort;
import org.naho.speech.llm.conversation.port.in.SpeakingSessionInputPort;
import org.naho.speech.llm.conversation.result.*;
import org.naho.speech.llm.type.SpeakingSessionStatus;
import org.naho.subscription.port.in.GetActiveSubscriptionInputPort;
import org.naho.subscription.result.SubscriptionPlanResult;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/speaking/session")
@RequiredArgsConstructor
public class SpeakingSessionController {

    private final SpeakingSessionInputPort speakingSessionInputPort;
    private final EndSessionInputPort endSessionInputPort;
    private final ChatResponseMapper chatResponseMapper;
    private final AudioChatResponseMapper audioChatResponseMapper;
    private final StartConversationResponseMapper startConversationResponseMapper;
    private final SpeakingSessionResponseMapper speakingSessionResponseMapper;
    private final FileStorageServicePort fileStorageServicePort;
    private final FileValidatorPort fileValidatorPort;
    private final GetActiveSubscriptionInputPort getActiveSubscriptionInputPort;
    private final SpeakingSessionCleanupInputPort speakingSessionCleanupInputPort;
    private final CrudSpeakingSessionInputPort crudSpeakingSessionInputPort;

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
    @PostMapping("/persona/{personaId}")
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
    @PostMapping("/init/{sessionCode}")
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
    @GetMapping("/details/{sessionCode}")
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
    @PostMapping("/message/{sessionCode}")
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
            value = "/audio/{sessionCode}",
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

    @PostMapping("/end/{sessionCode}")
    @ApiResponseMessage(message = SpeechDetailMessageKey.SPEAKING_SESSION_END_SUCCESS)
    public ResponseEntity<SpeakingSessionResponse> endSession(
            @PathVariable("sessionCode") String sessionCode,
            @AuthenticationPrincipal AccessTokenPayload payload,
            @RequestBody(required = false) EndSessionRequest req
    ) {
        Long userId = payload.userId();
        String topic = (req != null && req.topic() != null) ? req.topic() : "";
        String speechMetadata = (req != null && req.speechMetadata() != null) ? req.speechMetadata() : "";
        String asrConfidence = (req != null && req.asrConfidence() != null) ? req.asrConfidence() : "";

        SpeakingSessionResult r = endSessionInputPort.endSession(userId, sessionCode, topic, speechMetadata, asrConfidence);
        return ResponseEntity.ok(speakingSessionResponseMapper.resultToResponse(r));
    }

    /**
     * Delete a session by session code.
     *
     * @param sessionCode session code
     * @param payload     chứa user id
     * @return ResponseEntity<Void>
     */
    @DeleteMapping("/{sessionCode}")
    public ResponseEntity<Void> deleteSession(
            @PathVariable("sessionCode") String sessionCode,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        speakingSessionCleanupInputPort.deleteSession(sessionCode, payload.userId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Lấy toàn bộ phiên nói chuyện đang dở dang của user
     *
     * @param payload chứa user id
     * @return List<SpeakingSessionListItemResponse>
     */
    @ApiResponseMessage
    @GetMapping("/all")
    public ResponseEntity<List<SpeakingSessionListItemResponse>> findAllByUserIdAndSpeakingSessionStatus(
            @AuthenticationPrincipal AccessTokenPayload payload,
            @RequestParam SpeakingSessionStatus status
    ) {
        List<SpeakingSessionListItemResult> results = crudSpeakingSessionInputPort
                .findAllByUserIdAndSpeakingSessionStatus(
                        payload.userId(),
                        status
                );

        List<SpeakingSessionListItemResponse> responses = results
                .stream().map(speakingSessionResponseMapper::resultToResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }
}

