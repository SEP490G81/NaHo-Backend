package org.naho.speech.llm.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.speech.llm.command.SpeakingAnalysisCommand;
import org.naho.speech.llm.dto.mapper.SpeakingAnalysisMapper;
import org.naho.speech.llm.dto.response.SpeakingAnalysisResponse;
import org.naho.speech.llm.dto.response.SpeakingHistoryDetailResponse;
import org.naho.speech.llm.port.in.SpeakingAnalysisInputPort;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SpeakingAnalysisController {

    private final SpeakingAnalysisInputPort speakingAnalysisInputPort;
    private final SpeakingAnalysisMapper speakingAnalysisMapper;

    @PostMapping(
            value = "/analysis",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponseMessage(message = "Phân tích phát âm thành công!")
    public ResponseEntity<SpeakingAnalysisResponse> uploadAudioFileAndAnalyzeSpeakingPronunciation(
            @RequestPart("file") MultipartFile file,
            @RequestParam("topicId") Long topicId,
            @RequestParam("questionId") Long questionId,
            @RequestParam("durationSec") Integer durationSec,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) throws IOException {
        SpeakingAnalysisCommand command = new SpeakingAnalysisCommand(
                payload.userId(),
                topicId,
                questionId,
                file.getBytes(),
                file.getContentType(),
                file.getOriginalFilename(),
                durationSec
        );

        var result = speakingAnalysisInputPort.analyzeSpeaking(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(speakingAnalysisMapper.toResponse(result));
    }

    @GetMapping(value = "/history/{historyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponseMessage(message = "Lấy chi tiết lịch sử thành công!")
    public ResponseEntity<SpeakingHistoryDetailResponse> getSpeakingAnalysisHistoryDetailByHistoryId(
            @PathVariable("historyId") String historyIdStr
    ) {
        Long historyId;
        if (historyIdStr.startsWith("h-")) {
            historyId = Long.parseLong(historyIdStr.substring(2));
        } else {
            historyId = Long.parseLong(historyIdStr);
        }

        var result = speakingAnalysisInputPort.getHistoryDetail(historyId);
        return ResponseEntity.ok(speakingAnalysisMapper.toDetailResponse(result));
    }
}
