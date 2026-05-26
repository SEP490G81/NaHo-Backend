package org.naho.speech.azure.controller.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.speech.azure.command.SpeechAssessmentCommand;
import org.naho.speech.azure.constant.AzureSpeechApplicationMessageKey;
import org.naho.speech.azure.dto.mapper.PronunciationAssessmentMapper;
import org.naho.speech.azure.dto.response.PronunciationAssessmentResponse;
import org.naho.speech.azure.port.in.AssessSpeechInputPort;
import org.naho.speech.azure.result.SpeechAssessmentResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/v1/speech")
@RequiredArgsConstructor
public class SpeechController {

    private final AssessSpeechInputPort assessSpeechInputPort;
    private final PronunciationAssessmentMapper pronunciationAssessmentMapper;

    @PostMapping(
            value = "/assess",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponseMessage(message = AzureSpeechApplicationMessageKey.SPEECH_PRONUNCIATION_ASSESSMENT_SUCCESSFULLY)
    public PronunciationAssessmentResponse assessPronunciation(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "reference-text", required = false) String referenceText
    ) throws IOException {
        SpeechAssessmentCommand request = new SpeechAssessmentCommand(
                file.getBytes(),
                referenceText
        );
        SpeechAssessmentResult result = assessSpeechInputPort.execute(request);
        return pronunciationAssessmentMapper.resultToResponse(result);
    }
}