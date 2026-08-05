package org.naho.question.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.question.SpeakingQuestionDetailMessageKey;
import org.naho.question.command.SuggestCustomSpeakingQuestionCommand;
import org.naho.question.dto.mapper.CustomSpeakingQuestionRequestMapper;
import org.naho.question.dto.mapper.CustomSpeakingQuestionResponseMapper;
import org.naho.question.dto.request.SuggestCustomSpeakingQuestionRequest;
import org.naho.question.dto.response.SuggestCustomSpeakingQuestionResponse;
import org.naho.question.port.in.SuggestCustomSpeakingQuestionInputPort;
import org.naho.question.result.SuggestCustomSpeakingQuestionResult;
import org.naho.shared.annotation.ApiResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/custom-speaking-question")
@RequiredArgsConstructor
public class CustomSpeakingQuestionController {

    private final SuggestCustomSpeakingQuestionInputPort suggestCustomSpeakingQuestionInputPort;
    private final CustomSpeakingQuestionRequestMapper requestMapper;
    private final CustomSpeakingQuestionResponseMapper responseMapper;

    @PostMapping("/suggest")
    @ApiResponseMessage(message = SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_CUSTOM_SUGGEST_SUCCESS)
    public ResponseEntity<SuggestCustomSpeakingQuestionResponse> suggestCustomSpeakingQuestion(
            @Valid @RequestBody SuggestCustomSpeakingQuestionRequest request
    ) {
        SuggestCustomSpeakingQuestionCommand command = requestMapper.requestToCommand(request);
        SuggestCustomSpeakingQuestionResult result = suggestCustomSpeakingQuestionInputPort.suggestCustomSpeakingQuestion(command);
        SuggestCustomSpeakingQuestionResponse response = responseMapper.resultToResponse(result);
        return ResponseEntity.ok(response);
    }
}
