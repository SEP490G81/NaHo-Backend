package org.naho.speech.llm.question.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.file.constant.FileAccessStatus;
import org.naho.file.constant.FileFolderConstant;
import org.naho.file.port.out.FileAudioConvertPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.port.out.FileValidatorPort;
import org.naho.file.result.StoredFile;
import org.naho.i18n.message.speech.SpeechDetailMessageKey;
import org.naho.question.dto.mapper.AnswerHistoryResponseMapper;
import org.naho.question.dto.response.AnswerHistoryResponse;
import org.naho.question.result.AnswerHistoryResult;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.shared.exception.PresentationException;
import org.naho.speech.azure.exception.AzureSpeechErrorCode;
import org.naho.speech.llm.question.command.SpeakingAnalysisCommand;
import org.naho.speech.llm.question.port.in.SpeakingAnalysisInputPort;
import org.naho.subscription.port.in.GetActiveSubscriptionInputPort;
import org.naho.subscription.result.SubscriptionPlanResult;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/speaking/analysis")
@RequiredArgsConstructor
public class SpeakingAnalysisController {

    private final FileStorageServicePort fileStorageServicePort;
    private final FileValidatorPort fileValidatorPort;
    private final GetActiveSubscriptionInputPort getActiveSubscriptionInputPort;
    private final FileAudioConvertPort fileAudioConvertPort;
    private final SpeakingAnalysisInputPort speakingAnalysisInputPort;
    private final AnswerHistoryResponseMapper answerHistoryResponseMapper;

    @ApiResponseMessage(message = SpeechDetailMessageKey.SPEAKING_ANALYSIS_SUCCESS)
    @PostMapping
    public ResponseEntity<AnswerHistoryResponse> uploadAudioAndAnalyzeSpeaking(
            @RequestPart("file") MultipartFile file,
            @RequestParam("speakingQuestionId") Long speakingQuestionId,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        // lấy gói đăng kí của user
        SubscriptionPlanResult subscriptionPlan = getActiveSubscriptionInputPort
                .getUserActiveSubscriptionPlan(payload.userId());

        try {
            // validate xem có phải file .wav không?
            // và validate xem thời lượng có hợp lệ không?
            // thời lượng tối đa được phép tùy thuộc vào Subscription plan
            // method cũng trả về thời lượng của file record
            double duration = fileValidatorPort.validateAudioFileAndDuration(
                    file.getBytes(),
                    subscriptionPlan.maxSpeakingQuestionRecordingSeconds().doubleValue()
            );

            // chuyển file âm thanh thành dạng wav để Azure chấm
            // định dạng PCM mono 16-bit ở 8 kHz hoặc 16 kHz
            byte[] audioBytes = fileAudioConvertPort.convertToWav(file.getBytes());

            // Step 1: Lưu file vào local
            StoredFile storedFile = fileStorageServicePort.saveFileToLocal(file, FileFolderConstant.RECORDINGS, FileAccessStatus.PRIVATE);

            // Build 1 speaking analysis command với
            // giới hạn đánh giá speaking question hàng ngày
            SpeakingAnalysisCommand command = SpeakingAnalysisCommand.builder()
                    .userId(payload.userId())
                    .speakingQuestionId(speakingQuestionId)
                    .duration(duration)
                    .storedFile(storedFile)
                    .audioBytes(audioBytes)
                    .dailySpeakingQuestionEvaluationLimit(subscriptionPlan.dailySpeakingQuestionEvaluationLimit())
                    .build();

            AnswerHistoryResult result = speakingAnalysisInputPort.analyzeSpeaking(command);
            AnswerHistoryResponse response = answerHistoryResponseMapper.resultToResponse(result);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IOException e) {
            throw new PresentationException(
                    AzureSpeechErrorCode.SPEECH_AUDIO_NOT_VALID,
                    SpeechDetailMessageKey.SPEECH_AUDIO_FILE_EMPTY,
                    e.getMessage());
        }
    }
}