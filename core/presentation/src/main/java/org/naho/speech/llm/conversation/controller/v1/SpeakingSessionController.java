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
import org.naho.speech.llm.conversation.dto.mapper.ChatResponseMapper;
import org.naho.speech.llm.conversation.dto.mapper.SpeakingSessionAssessmentResponseMapper;
import org.naho.speech.llm.conversation.dto.mapper.SpeakingSessionResponseMapper;
import org.naho.speech.llm.conversation.dto.request.ChatSessionMessageRequest;
import org.naho.speech.llm.conversation.dto.request.StartConversationRequest;
import org.naho.speech.llm.conversation.dto.response.ChatResponse;
import org.naho.speech.llm.conversation.dto.response.SpeakingSessionAssessmentResponse;
import org.naho.speech.llm.conversation.dto.response.SpeakingSessionListItemResponse;
import org.naho.speech.llm.conversation.dto.response.SpeakingSessionResponse;
import org.naho.speech.llm.conversation.port.in.CrudSpeakingSessionInputPort;
import org.naho.speech.llm.conversation.port.in.EndSessionInputPort;
import org.naho.speech.llm.conversation.port.in.SpeakingSessionInputPort;
import org.naho.speech.llm.conversation.result.ChatResult;
import org.naho.speech.llm.conversation.result.SpeakingSessionAssessmentResult;
import org.naho.speech.llm.conversation.result.SpeakingSessionListItemResult;
import org.naho.speech.llm.conversation.result.SpeakingSessionResult;
import org.naho.speech.llm.type.SpeakingSessionStatus;
import org.naho.subscription.port.in.GetActiveSubscriptionInputPort;
import org.naho.subscription.result.SubscriptionPlanResult;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final SpeakingSessionResponseMapper speakingSessionResponseMapper;
    private final SpeakingSessionAssessmentResponseMapper speakingSessionAssessmentResponseMapper;
    private final FileStorageServicePort fileStorageServicePort;
    private final FileValidatorPort fileValidatorPort;
    private final GetActiveSubscriptionInputPort getActiveSubscriptionInputPort;
    private final CrudSpeakingSessionInputPort crudSpeakingSessionInputPort;
    private final FileAudioConvertPort fileAudioConvertPort;

    /**
     * Khởi tạo 1 session mới và lưu vào ram và database
     *
     * @param payload chứa user id
     * @param request bao gồm: FormalityLevel và MarugotoLevel và persona id
     * @return trả về session code
     */
    // ROLE: LEARNER
    @PreAuthorize("hasRole('LEARNER')")
    @ApiResponseMessage(message = SpeechDetailMessageKey.SPEAKING_CONVERSATION_START_SUCCESS)
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
     * Method gửi message để chat với AI
     *
     * @param request chứa transcript (đoạn nội dung người dùng gửi), và session code
     * @return ChatResponse
     */
    // ROLE: LEARNER
    @PreAuthorize("hasRole('LEARNER')")
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
    // ROLE: LEARNER
    @PreAuthorize("hasRole('LEARNER')")
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

    /**
     * Kết thúc phiên trò chuyện và nhận đánh giá
     *
     * @param sessionCode mã của phiên trò chuyện
     * @param payload     chứa user id
     * @return SpeakingSessionAssessmentResponse
     */
    // ROLE: LEARNER
    @PreAuthorize("hasRole('LEARNER')")
    @PostMapping("/end/{sessionCode}")
    @ApiResponseMessage(message = SpeechDetailMessageKey.SPEAKING_SESSION_END_SUCCESS)
    public ResponseEntity<SpeakingSessionAssessmentResponse> endSession(
            @PathVariable String sessionCode,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        SpeakingSessionAssessmentResult result = endSessionInputPort.endSession(
                payload.userId(),
                sessionCode
        );
        return ResponseEntity.ok(speakingSessionAssessmentResponseMapper.resultToResponse(result));
    }

    /**
     * Lấy chấm điểm của 1 session nếu đã completed
     *
     * @param sessionCode session code
     * @param payload     chứa user id
     * @return SpeakingSessionAssessmentResponse
     */
    // ROLE: LEARNER
    @PreAuthorize("hasRole('LEARNER')")
    @ApiResponseMessage
    @GetMapping("/end/{sessionCode}")
    public ResponseEntity<SpeakingSessionResponse> findAssessmentBySessionCodeAndUserId(
            @PathVariable String sessionCode,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        SpeakingSessionResult result = crudSpeakingSessionInputPort
                .findBySessionCodeAndUserId(sessionCode, payload.userId());
        return ResponseEntity.ok(speakingSessionResponseMapper.resultToResponse(result));
    }

    /**
     * Delete a session by session code.
     *
     * @param sessionCode session code
     * @param payload     chứa user id
     * @return ResponseEntity<Void>
     */
    // ROLE: LEARNER
    @PreAuthorize("hasRole('LEARNER')")
    @DeleteMapping("/{sessionCode}")
    public ResponseEntity<Void> deleteSession(
            @PathVariable String sessionCode,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        crudSpeakingSessionInputPort.deleteSessionBySessionCodeAndUserId(sessionCode, payload.userId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Lấy toàn bộ phiên nói chuyện đang dở dang của user
     *
     * @param payload chứa user id
     * @return List<SpeakingSessionListItemResponse>
     */
    // ROLE: LEARNER
    @PreAuthorize("hasRole('LEARNER')")
    @ApiResponseMessage(message = SpeechDetailMessageKey.SPEAKING_HISTORY_GET_ALL_SUCCESS)
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

    /**
     * Lấy ra chi tiết 1 session (gồm các đoạn chat trong đó)
     *
     * @param payload     chứa user id
     * @param sessionCode session code
     * @return SpeakingSessionResponse
     */
    // ROLE: LEARNER
    @PreAuthorize("hasRole('LEARNER')")
    @ApiResponseMessage(message = SpeechDetailMessageKey.SPEAKING_HISTORY_GET_DETAIL_SUCCESS)
    @GetMapping("/details")
    public ResponseEntity<SpeakingSessionResponse> findByUserIdAndSpeakingSessionCodeAndSpeakingSessionStatus(
            @AuthenticationPrincipal AccessTokenPayload payload,
            @RequestParam("sessionCode") String sessionCode,
            @RequestParam("status") SpeakingSessionStatus status
    ) {
        SpeakingSessionResult result = crudSpeakingSessionInputPort
                .findByUserIdAndSpeakingSessionCodeAndSpeakingSessionStatus(
                        payload.userId(),
                        sessionCode,
                        status
                );

        SpeakingSessionResponse response = speakingSessionResponseMapper.resultToResponse(result);

        return ResponseEntity.ok(response);
    }
}

