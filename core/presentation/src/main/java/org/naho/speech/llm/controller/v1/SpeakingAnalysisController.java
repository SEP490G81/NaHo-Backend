package org.naho.speech.llm.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.speech.SpeechDetailMessageKey;
import org.naho.pagination.PageData;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.speech.llm.command.SpeakingAnalysisCommand;
import org.naho.speech.llm.command.SpeakingHistoryFilterCommand;
import org.naho.speech.llm.dto.mapper.SpeakingAnalysisMapper;
import org.naho.speech.llm.dto.request.SpeakingHistoryQueryRequest;
import org.naho.speech.llm.dto.response.SpeakingAnalysisResponse;
import org.naho.speech.llm.dto.response.SpeakingHistoryDetailResponse;
import org.naho.speech.llm.dto.response.SpeakingHistoryListItemResponse;
import org.naho.speech.llm.port.in.SpeakingAnalysisInputPort;
import org.naho.speech.llm.result.SpeakingHistoryListItemResult;
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
    @ApiResponseMessage(message = SpeechDetailMessageKey.SPEAKING_ANALYSIS_SUCCESS)
    public ResponseEntity<SpeakingAnalysisResponse> uploadAudioAndAnalyzeSpeaking(
            @RequestPart("file") MultipartFile file,
            @RequestParam("speakingQuestionId") Long speakingQuestionId,
            @RequestParam("durationSec") Integer durationSec,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) throws IOException {
        SpeakingAnalysisCommand command = new SpeakingAnalysisCommand(
                payload.userId(),
                speakingQuestionId,
                file.getBytes(),
                file.getContentType(),
                file.getOriginalFilename(),
                durationSec
        );

        var result = speakingAnalysisInputPort.analyzeSpeaking(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(speakingAnalysisMapper.toResponse(result));
    }

    @PostMapping(value = "/speaking-histories", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponseMessage(message = SpeechDetailMessageKey.SPEAKING_HISTORY_GET_ALL_SUCCESS)
    public ResponseEntity<PageData<SpeakingHistoryListItemResponse>> getUserHistoryList(
            @AuthenticationPrincipal AccessTokenPayload payload,
            @RequestBody(required = false) SpeakingHistoryQueryRequest request
    ) {
        if (request == null) {
            request = new SpeakingHistoryQueryRequest();
        }
        SpeakingHistoryFilterCommand command = speakingAnalysisMapper.requestToCommand(request, payload.userId());

        PageData<SpeakingHistoryListItemResult> result = speakingAnalysisInputPort.getUserHistoryList(command);

        PageData<SpeakingHistoryListItemResponse> response = PageData.<SpeakingHistoryListItemResponse>builder()
                .pageMeta(result.getPageMeta())
                .data(result.getData()
                        .stream()
                        .map(speakingAnalysisMapper::toListItemResponse)
                        .toList()
                )
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/speaking-histories/{historyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponseMessage(message = SpeechDetailMessageKey.SPEAKING_HISTORY_GET_DETAIL_SUCCESS)
    public ResponseEntity<SpeakingHistoryDetailResponse> getSpeakingHistoryDetail(
            @PathVariable("historyId") Long historyId
    ) {
        var result = speakingAnalysisInputPort.getHistoryDetail(historyId);
        return ResponseEntity.ok(speakingAnalysisMapper.toDetailResponse(result));
    }
}
