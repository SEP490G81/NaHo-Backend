package org.naho.speech.azure.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.file.port.out.FileAudioConvertPort;
import org.naho.file.port.out.FileValidatorPort;
import org.naho.i18n.message.speech.SpeechDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.speech.azure.command.SpeechAssessmentCommand;
import org.naho.speech.azure.dto.mapper.SpeechAssessmentResponseMapper;
import org.naho.speech.azure.dto.response.SpeechAssessmentResponse;
import org.naho.speech.azure.port.in.SpeechAssessmentInputPort;
import org.naho.speech.azure.result.SpeechAssessmentResult;
import org.naho.subscription.port.in.GetActiveSubscriptionInputPort;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/speaking/assessment")
@RequiredArgsConstructor
public class SpeechAssessmentController {
    private final SpeechAssessmentInputPort speechAssessmentInputPort;
    private final SpeechAssessmentResponseMapper speechAssessmentResponseMapper;
    private final FileAudioConvertPort fileAudioConvertPort;
    private final FileValidatorPort fileValidatorPort;
    private final GetActiveSubscriptionInputPort getActiveSubscriptionInputPort;

    /**
     * Method đánh giá phát âm của người dùng có expected output
     *
     * @param file          file nói của người dùng
     * @param referenceText expected output
     * @param payload       chứa user id
     * @return SpeechAssessmentResponse
     * @throws IOException
     */
    // ROLE: LEARNER
    @PreAuthorize("hasRole('LEARNER')")
    @PostMapping
    @ApiResponseMessage(message = SpeechDetailMessageKey.SPEECH_PRONUNCIATION_ASSESSMENT_SUCCESSFULLY)
    public ResponseEntity<SpeechAssessmentResponse> assessPronunciation(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "reference-text", required = false) String referenceText,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) throws IOException {
        // lấy thời gian được phép nói tối đa của gói đăng kí của người dùng đang đăng nhập
        double maxDuration = getActiveSubscriptionInputPort
                .getUserActiveSubscriptionPlan(payload.userId())
                .maxSpeakingQuestionRecordingSeconds();

        // validate xem có phải file âm thanh không
        // validate xem thời gian của record có hợp lệ không
        // đồng thời trả về duration của file record
        double duration = fileValidatorPort.validateAudioFileAndDuration(
                file.getBytes(),
                maxDuration
        );

        // chuyển file âm thanh thành dạng wav để Azure chấm
        // định dạng PCM mono 16-bit ở 8 kHz hoặc 16 kHz
        byte[] audioBytes = fileAudioConvertPort.convertToWav(file.getBytes());

        SpeechAssessmentCommand command = new SpeechAssessmentCommand(
                audioBytes,
                duration,
                referenceText,
                payload.userId()
        );

        SpeechAssessmentResult result = speechAssessmentInputPort.assessAudio(command);
        return ResponseEntity.ok(speechAssessmentResponseMapper.resultToResponse(result));
    }
}
