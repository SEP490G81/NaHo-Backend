package org.naho.question.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.naho.question.command.SuggestCustomQuestionCommand;
import org.naho.question.dto.mapper.CustomQuestionRequestMapper;
import org.naho.question.dto.mapper.CustomQuestionResponseMapper;
import org.naho.question.dto.request.SuggestCustomQuestionRequest;
import org.naho.question.dto.response.SuggestCustomQuestionResponse;
import org.naho.question.port.in.SuggestCustomQuestionInputPort;
import org.naho.question.result.SuggestCustomQuestionResult;
import org.naho.shared.annotation.ApiResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/custom-question")
@RequiredArgsConstructor
public class CustomQuestionController {

    private final SuggestCustomQuestionInputPort suggestCustomQuestionInputPort;
    private final CustomQuestionRequestMapper requestMapper;
    private final CustomQuestionResponseMapper responseMapper;

    @PostMapping("/suggest")
    @ApiResponseMessage(message = "Suggest custom question successfully!")
    public ResponseEntity<SuggestCustomQuestionResponse> suggestCustomQuestion(
            @Valid @RequestBody SuggestCustomQuestionRequest request
    ) {
        SuggestCustomQuestionCommand command = requestMapper.requestToCommand(request);
        SuggestCustomQuestionResult result = suggestCustomQuestionInputPort.suggestCustomQuestion(command);
        SuggestCustomQuestionResponse response = responseMapper.resultToResponse(result);
        return ResponseEntity.ok(response);
    }
}
