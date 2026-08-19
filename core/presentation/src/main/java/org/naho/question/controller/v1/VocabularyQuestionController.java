package org.naho.question.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.question.VocabularyQuestionDetailMessageKey;
import org.naho.question.command.CompleteVocabularyQuestionCommand;
import org.naho.question.dto.mapper.VocabularyQuestionResponseMapper;
import org.naho.question.dto.request.CompleteVocabularyQuestionRequest;
import org.naho.question.port.in.CompleteVocabularyQuestionInputPort;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vocabulary-questions")
@RequiredArgsConstructor
public class VocabularyQuestionController {

    private final VocabularyQuestionResponseMapper vocabularyQuestionResponseMapper;
    private final CompleteVocabularyQuestionInputPort completeVocabularyQuestionInputPort;

//    @GetMapping
//    @ApiResponseMessage(message = VocabularyQuestionDetailMessageKey.VOCABULARY_QUESTION_GET_SUCCESS)
//    public ResponseEntity<VocabularyQuestionResponse> getVocabulariesOfQuestion(
//            @RequestParam("id") int id,
//            @RequestParam("node_type") String nodeType,
//            @RequestParam("objective_id") int objectiveId,
//            @RequestParam("vocabulary_question_id") int vocabularyQuestionId
//    ) {
//        LearningPathNodeCommand command = new LearningPathNodeCommand(
//                id,
//                nodeType,
//                objectiveId,
//                vocabularyQuestionId
//        );
//
//        VocabulariesOfQuestionResult result = searchVocabulariesOfQuestionInputPort.getVocabularyListOfQuestion(command);
//        VocabulariesOfQuestionResponse response = vocabularyQuestionResponseMapper.toResponse(result);
//        return ResponseEntity.ok(response);
//    }

    @ApiResponseMessage(message = VocabularyQuestionDetailMessageKey.VOCABULARY_QUESTION_COMPLETE_SUCCESS)
    @PostMapping("/completion")
    public ResponseEntity<Void> completeVocabularyQuestion(
            @AuthenticationPrincipal AccessTokenPayload payload,
            @RequestBody CompleteVocabularyQuestionRequest request
    ) {
        CompleteVocabularyQuestionCommand command = new CompleteVocabularyQuestionCommand(
                request.vocabularyQuestionId(),
                payload.userId()
        );

        completeVocabularyQuestionInputPort.completeVocabularyQuestion(command);

        return ResponseEntity.ok().build();
    }
}
