package org.naho.speech.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.speech.command.SpeechAssessmentCommand;
import org.naho.speech.command.TextToSpeechCommand;
import org.naho.speech.port.in.TextToSpeechInputPort;
import org.naho.speech.result.AudioSpeechResult;
import org.naho.speech.result.PronunciationAssessmentResult;
import org.naho.speech.port.in.AssessSpeechInputPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/v1/speech")
@RequiredArgsConstructor
public class SpeechController {

    private final AssessSpeechInputPort assessSpeechInputPort;
    private final TextToSpeechInputPort textToSpeechInputPort; // Inject thông qua constructor

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

    @PostMapping(
            value = "/synthesize",
            produces = "audio/wav"
    )
    @Operation(summary = "Tổng hợp văn bản thành âm thanh (Text-to-Speech)")
    public ResponseEntity<byte[]> synthesizeSpeech(
            @RequestParam("text") String text,
            @RequestParam(value = "voiceName", required = false) String voiceName,
            @RequestParam(value = "language", required = false) String language
    ) {
        log.info("Received request to synthesize text. Text length: {}, Voice: {}",
                text.length(), voiceName);

        TextToSpeechCommand command = new TextToSpeechCommand(text, voiceName, language);
        AudioSpeechResult result = textToSpeechInputPort.execute(command);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, result.contentType());
        // Cho phép trình duyệt đề xuất tải xuống với tên file cụ thể nếu cần
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"synthesized_speech.wav\"");
        headers.set(HttpHeaders.CACHE_CONTROL, "no-cache");

        return new ResponseEntity<>(result.audioData(), headers, HttpStatus.OK);
    }
}