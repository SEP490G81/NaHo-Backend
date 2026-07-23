package org.naho.speech.llm.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.speech.llm.command.SpeakingAnalysisCommand;
import org.naho.speech.llm.command.SpeakingHistoryFilterCommand;
import org.naho.speech.llm.dto.mapper.SpeakingAnalysisMapper;
import org.naho.speech.llm.dto.request.SpeakingHistoryFilterRequest;
import org.naho.speech.llm.dto.response.SpeakingAnalysisResponse;
import org.naho.speech.llm.dto.response.SpeakingHistoryDetailResponse;
import org.naho.speech.llm.dto.response.SpeakingHistoryListItemResponse;
import org.naho.speech.llm.port.in.SpeakingAnalysisInputPort;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping(value = "/histories", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponseMessage(message = "Lấy danh sách lịch sử làm bài thành công!")
    public ResponseEntity<Page<SpeakingHistoryListItemResponse>> getUserHistoryList(
            @ModelAttribute SpeakingHistoryFilterRequest filter,
            @PageableDefault(page = 0, size = 10, sort = "createdTime", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        SpeakingHistoryFilterCommand command = new SpeakingHistoryFilterCommand(
                payload.userId(),
                filter != null ? filter.speakingQuestionId() : null,
                filter != null ? filter.topicId() : null,
                filter != null ? filter.search() : null,
                pageable.getPageNumber() + 1,
                pageable.getPageSize()
        );
        var resultList = speakingAnalysisInputPort.getUserHistoryList(command);
        var responseItems = resultList.items().stream()
                .map(speakingAnalysisMapper::toListItemResponse)
                .toList();
        return ResponseEntity.ok(new org.springframework.data.domain.PageImpl<>(responseItems, pageable, resultList.totalElements()));
    }

    @GetMapping(value = "/histories/{historyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponseMessage(message = "Lấy chi tiết lịch sử thành công!")
    public ResponseEntity<SpeakingHistoryDetailResponse> getSpeakingHistoryDetail(
            @PathVariable("historyId") Long historyId
    ) {
        var result = speakingAnalysisInputPort.getHistoryDetail(historyId);
        return ResponseEntity.ok(speakingAnalysisMapper.toDetailResponse(result));
    }
}
