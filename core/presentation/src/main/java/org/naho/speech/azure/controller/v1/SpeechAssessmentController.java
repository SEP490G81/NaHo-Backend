package org.naho.speech.azure.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.speech.SpeechDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.speech.azure.command.SpeechAssessmentCommand;
import org.naho.speech.azure.dto.mapper.SpeechAssessmentResponseMapper;
import org.naho.speech.azure.dto.response.SpeechAssessmentResponse;
import org.naho.speech.azure.port.in.SpeakingAssessmentInputPort;
import org.naho.speech.azure.result.SpeechAssessmentResult;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/speaking/assessment")
@RequiredArgsConstructor
public class SpeechAssessmentController {
    private final SpeakingAssessmentInputPort speakingAssessmentInputPort;
    private final SpeechAssessmentResponseMapper speechAssessmentResponseMapper;

    @PostMapping
    @ApiResponseMessage(message = SpeechDetailMessageKey.SPEECH_PRONUNCIATION_ASSESSMENT_SUCCESSFULLY)
    public ResponseEntity<SpeechAssessmentResponse> assessPronunciation(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "reference-text", required = false) String referenceText,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) throws IOException {
        SpeechAssessmentCommand command = new SpeechAssessmentCommand(
                file.getBytes(),
                referenceText,
                payload.userId()
        );
        SpeechAssessmentResult result = speakingAssessmentInputPort.assessAudio(command);
        return ResponseEntity.ok(speechAssessmentResponseMapper.resultToResponse(result));
    }
}
