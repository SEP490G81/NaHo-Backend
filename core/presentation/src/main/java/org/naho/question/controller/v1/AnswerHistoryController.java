package org.naho.question.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.i18n.message.question.SpeakingQuestionDetailMessageKey;
import org.naho.question.dto.mapper.AnswerHistoryResponseMapper;
import org.naho.question.dto.response.AnswerHistoryListItemResponse;
import org.naho.question.dto.response.AnswerHistoryResponse;
import org.naho.question.port.in.CrudAnswerHistoryInputPort;
import org.naho.question.result.AnswerHistoryListItemResult;
import org.naho.question.result.AnswerHistoryResult;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/answer-histories")
@RequiredArgsConstructor
public class AnswerHistoryController {
    private final CrudAnswerHistoryInputPort crudAnswerHistoryInputPort;
    private final AnswerHistoryResponseMapper answerHistoryResponseMapper;

    // ROLE: LEARNER
    @PreAuthorize("hasRole('LEARNER')")
    @ApiResponseMessage(message = FileDetailMessageKey.FILE_GENERATE_PRESIGNED_URL_SUCCESSFULLY)
    @GetMapping("/{id}/presigned-url")
    public ResponseEntity<Void> generateAudioFilePresignedUrl(
            @PathVariable Long id,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        String presignedUrl = crudAnswerHistoryInputPort
                .generateAudioFilePresignedUrl(id, payload.userId());

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(presignedUrl))
                .build();
    }

    // ROLE: LEARNER
    @PreAuthorize("hasRole('LEARNER')")
    @ApiResponseMessage(message = SpeakingQuestionDetailMessageKey.ANSWER_HISTORY_GET_LIST_SUCCESS)
    @GetMapping("/speaking-question/{speakingQuestionId}")
    public ResponseEntity<List<AnswerHistoryListItemResponse>> findAllBySpeakingQuestionIdAndUserId(
            @PathVariable Long speakingQuestionId,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        List<AnswerHistoryListItemResult> results = crudAnswerHistoryInputPort
                .findAllBySpeakingQuestionIdAndUserId(speakingQuestionId, payload.userId());

        List<AnswerHistoryListItemResponse> responses = results.stream()
                .map(answerHistoryResponseMapper::resultToResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    // ROLE: LEARNER
    @PreAuthorize("hasRole('LEARNER')")
    @ApiResponseMessage(message = SpeakingQuestionDetailMessageKey.ANSWER_HISTORY_GET_DETAIL_SUCCESS)
    @GetMapping("/{answerHistoryId}")
    public ResponseEntity<AnswerHistoryResponse> findByAnswerHistoryIdAndUserId(
            @PathVariable Long answerHistoryId,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        AnswerHistoryResult result = crudAnswerHistoryInputPort.findByAnswerHistoryIdAndUserId(answerHistoryId, payload.userId());
        AnswerHistoryResponse response = answerHistoryResponseMapper.resultToResponse(result);
        return ResponseEntity.ok(response);
    }
}
