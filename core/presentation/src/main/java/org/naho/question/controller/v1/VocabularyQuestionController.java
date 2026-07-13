package org.naho.question.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.question.VocabularyQuestionDetailMessageKey;
import org.naho.question.command.LearningPathNodeCommand;
import org.naho.question.dto.mapper.VocabularyQuestionResponseMapper;
import org.naho.question.dto.response.VocabulariesOfQuestionResponse;
import org.naho.question.port.in.SearchVocabulariesOfQuestionInputPort;
import org.naho.question.result.VocabulariesOfQuestionResult;
import org.naho.shared.annotation.ApiResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vocabulary-questions")
@RequiredArgsConstructor
public class VocabularyQuestionController {

    private final SearchVocabulariesOfQuestionInputPort searchVocabulariesOfQuestionInputPort;
    private final VocabularyQuestionResponseMapper vocabularyQuestionResponseMapper;

    @GetMapping
    @ApiResponseMessage(message = VocabularyQuestionDetailMessageKey.VOCABULARY_QUESTION_GET_SUCCESS)
    public ResponseEntity<VocabulariesOfQuestionResponse> getVocabulariesOfQuestion(
            @RequestParam("id") int id,
            @RequestParam("node_type") String nodeType,
            @RequestParam("objective_id") int objectiveId,
            @RequestParam("vocabulary_question_id") int vocabularyQuestionId
    ) {
        LearningPathNodeCommand command = new LearningPathNodeCommand(
                id,
                nodeType,
                objectiveId,
                vocabularyQuestionId
        );

        VocabulariesOfQuestionResult result = searchVocabulariesOfQuestionInputPort.getVocabularyListOfQuestion(command);
        VocabulariesOfQuestionResponse response = vocabularyQuestionResponseMapper.toResponse(result);
        return ResponseEntity.ok(response);
    }
}
