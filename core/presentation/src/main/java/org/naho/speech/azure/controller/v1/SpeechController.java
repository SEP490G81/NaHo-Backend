package org.naho.speech.azure.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.speech.azure.command.SpeechAssessmentCommand;
import org.naho.speech.azure.command.TextToSpeechCommand;
import org.naho.i18n.message.speech.SpeechDetailMessageKey;
import org.naho.speech.azure.constant.AzureSpeechContentType;
import org.naho.speech.azure.dto.mapper.PronunciationAssessmentMapper;
import org.naho.speech.azure.dto.mapper.TextToSpeechRequestMapper;
import org.naho.speech.azure.dto.request.TextToSpeechRequest;
import org.naho.speech.azure.dto.response.PronunciationAssessmentResponse;
import org.naho.speech.azure.port.in.AssessSpeechInputPort;
import org.naho.speech.azure.port.in.TextToSpeechInputPort;
import org.naho.speech.azure.result.AudioSpeechResult;
import org.naho.speech.azure.result.SpeechAssessmentResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/speech")
@RequiredArgsConstructor
public class SpeechController {

    private final AssessSpeechInputPort assessSpeechInputPort;
    private final PronunciationAssessmentMapper pronunciationAssessmentMapper;
    private final TextToSpeechInputPort textToSpeechInputPort;
    private final TextToSpeechRequestMapper textToSpeechRequestMapper;

    @PostMapping(
            value = "/assess",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponseMessage(message = SpeechDetailMessageKey.SPEECH_PRONUNCIATION_ASSESSMENT_SUCCESSFULLY)
    public ResponseEntity<PronunciationAssessmentResponse> assessPronunciation(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "reference-text", required = false) String referenceText
    ) throws IOException {
        SpeechAssessmentCommand request = new SpeechAssessmentCommand(
                file.getBytes(),
                referenceText
        );
        SpeechAssessmentResult result = assessSpeechInputPort.execute(request);
        return ResponseEntity.ok(pronunciationAssessmentMapper.resultToResponse(result));
    }

    @PostMapping(
            value = "/synthesis",
            produces = AzureSpeechContentType.AUDIO_WAV
    )
    public ResponseEntity<byte[]> synthesizeSpeech(
            @RequestBody TextToSpeechRequest request
    ) {
        TextToSpeechCommand command = textToSpeechRequestMapper.requestToCommand(request);
        AudioSpeechResult result = textToSpeechInputPort.execute(command);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, result.contentType());
        // Cho phép trình duyệt đề xuất tải xuống với tên file cụ thể nếu cần
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"synthesized_speech.wav\"");
        headers.set(HttpHeaders.CACHE_CONTROL, "no-cache");

        return new ResponseEntity<>(result.audioData(), headers, HttpStatus.OK);
    }
}