package org.naho.speech.llm.conversation.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.naho.file.constant.FileAccessStatus;
import org.naho.file.constant.FileFolderConstant;
import org.naho.file.port.out.FileAudioConvertPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.port.out.FileValidatorPort;
import org.naho.file.result.StoredFile;
import org.naho.i18n.message.speech.SpeechDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.shared.exception.PresentationException;
import org.naho.speech.azure.exception.AzureSpeechErrorCode;
import org.naho.speech.llm.conversation.command.SendAudioMessageCommand;
import org.naho.speech.llm.conversation.command.SendTextMessageCommand;
import org.naho.speech.llm.conversation.command.StartSpeakingConversationCommand;
import org.naho.speech.llm.conversation.dto.mapper.AudioChatResponseMapper;
import org.naho.speech.llm.conversation.dto.mapper.ChatResponseMapper;
import org.naho.speech.llm.conversation.dto.mapper.SpeakingSessionResponseMapper;
import org.naho.speech.llm.conversation.dto.mapper.StartConversationResponseMapper;
import org.naho.speech.llm.conversation.dto.request.ChatSessionMessageRequest;
import org.naho.speech.llm.conversation.dto.request.StartConversationRequest;
import org.naho.speech.llm.conversation.dto.response.ChatResponse;
import org.naho.speech.llm.conversation.dto.response.SpeakingSessionListItemResponse;
import org.naho.speech.llm.conversation.dto.response.SpeakingSessionResponse;
import org.naho.speech.llm.conversation.dto.response.StartConversationResponse;
import org.naho.speech.llm.conversation.port.in.CrudSpeakingSessionInputPort;
import org.naho.speech.llm.conversation.port.in.EndSessionInputPort;
import org.naho.speech.llm.conversation.port.in.SpeakingSessionCleanupInputPort;
import org.naho.speech.llm.conversation.port.in.SpeakingSessionInputPort;
import org.naho.speech.llm.conversation.result.ChatResult;
import org.naho.speech.llm.conversation.result.SpeakingSessionListItemResult;
import org.naho.speech.llm.conversation.result.SpeakingSessionResult;
import org.naho.speech.llm.conversation.result.StartConversationResult;
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
    private final FileAudioConvertPort fileAudioConvertPort;

    /**
     * Khởi tạo 1 session mới và lưu vào ram và database
     *
     * @param payload chứa user id
     * @param request bao gồm: FormalityLevel và MarugotoLevel và persona id
     * @return trả về session code
     */
    @ApiResponseMessage
    @PostMapping("/start")
    public ResponseEntity<String> startConversation(
            @AuthenticationPrincipal AccessTokenPayload payload,
            @RequestBody StartConversationRequest request
    ) {
        StartSpeakingConversationCommand command = new StartSpeakingConversationCommand(
                payload.userId(),
                request.personaId(),
                request.formalityLevel(),
                request.marugotoLevel()
        );
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
     * @param request chứa transcript (đoạn nội dung người dùng gửi), và session code
     * @return ChatResponse
     */
    @PostMapping("/message")
    @ApiResponseMessage(message = SpeechDetailMessageKey.SPEAKING_MESSAGE_SEND_SUCCESS)
    public ResponseEntity<ChatResponse> sendMessage(
            @Valid @RequestBody ChatSessionMessageRequest request
    ) {
        SendTextMessageCommand command = new SendTextMessageCommand(
                request.sessionCode(),
                request.userMessage()
        );

        ChatResult result = speakingSessionInputPort.sendMessage(command);
        return ResponseEntity.ok(chatResponseMapper.resultToResponse(result));
    }

    /**
     * Method gửi audio để chat với AI
     *
     * @param sessionCode session code
     * @param file        file âm thanh người dùng gửi cho AI
     * @param payload     chứa user id
     * @return ChatResponse
     */
    @PostMapping(
            value = "/audio/{sessionCode}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponseMessage(message = SpeechDetailMessageKey.SPEAKING_AUDIO_MESSAGE_PROCESS_SUCCESS)
    public ResponseEntity<ChatResponse> sendAudioMessage(
            @PathVariable("sessionCode") String sessionCode,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        try {
            SubscriptionPlanResult subscriptionPlan = getActiveSubscriptionInputPort.getUserActiveSubscriptionPlan(payload.userId());
            // lấy ra thời gian record tối đa dựa vào gói đăng kí của người dùng
            double durationLimit = subscriptionPlan.maxSpeakingQuestionRecordingSeconds();

            // kiểm tra thời lượng record và định dạng file
            double duration = fileValidatorPort.validateAudioFileAndDuration(file.getBytes(), durationLimit);

            // chuyển file âm thanh thành dạng wav để Azure chấm
            // định dạng PCM mono 16-bit ở 8 kHz hoặc 16 kHz
            byte[] audioBytes = fileAudioConvertPort.convertToWav(file.getBytes());

            // Step 1: Lưu file vào local
            StoredFile storedFile = fileStorageServicePort.saveFileToLocal(file, FileFolderConstant.RECORDINGS, FileAccessStatus.PRIVATE);

            SendAudioMessageCommand command = SendAudioMessageCommand.builder()
                    .sessionCode(sessionCode)
                    .audioBytes(audioBytes)
                    .duration(duration)
                    .storedFile(storedFile)
                    .userId(payload.userId())
                    .build();

            ChatResult result = speakingSessionInputPort.sendAudioMessage(command);
            return ResponseEntity.ok(chatResponseMapper.resultToResponse(result));

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
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        SpeakingSessionResult result = endSessionInputPort.doEndSession(payload.userId(), sessionCode);
        return ResponseEntity.ok(speakingSessionResponseMapper.resultToResponse(result));
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

