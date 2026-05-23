package org.naho.speech.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.speech.command.SpeechAssessmentCommand;
import org.naho.speech.result.PronunciationAssessmentResult;
import org.naho.speech.port.in.AssessSpeechInputPort;
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

    @PostMapping(
            value = "/assess",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponseMessage(message = "Speech assessment completed successfully")
    public PronunciationAssessmentResult assessPronunciation(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "referenceText", required = false) String referenceText
    ) throws IOException {
        log.info("Received request to assess speech. File size: {} bytes, Has reference text: {}",
                file.getSize(), (referenceText != null && !referenceText.isBlank()));

        SpeechAssessmentCommand request = new SpeechAssessmentCommand(
                file.getBytes(),
                referenceText
        );

        return assessSpeechInputPort.execute(request);
    }
}