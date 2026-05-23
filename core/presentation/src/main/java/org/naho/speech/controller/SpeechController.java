package org.naho.speech.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.speech.dto.SpeechAssessmentRequest;
import org.naho.speech.dto.SpeechAssessmentResponse;
import org.naho.speech.port.in.AssessSpeechInputPort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/v1/speech")
@RequiredArgsConstructor
@Tag(name = "Speech AI", description = "Các API tương tác và xử lý giọng nói, đánh giá phát âm bằng AI")
public class SpeechController {

    private final AssessSpeechInputPort assessSpeechInputPort;

    @PostMapping(
            value = "/assess",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Đánh giá phát âm giọng nói (Tiếng Nhật)", description = "Chấp nhận file âm thanh (.wav) và trả về nội dung text kèm điểm số chi tiết")
    @ApiResponseMessage(message = "Speech assessment completed successfully")
    public SpeechAssessmentResponse assessPronunciation(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "referenceText", required = false) String referenceText
    ) throws IOException {
        log.info("Received request to assess speech. File size: {} bytes, Has reference text: {}",
                file.getSize(), (referenceText != null && !referenceText.isBlank()));

        SpeechAssessmentRequest request = new SpeechAssessmentRequest(
                file.getBytes(),
                referenceText
        );

        return assessSpeechInputPort.execute(request);
    }
}