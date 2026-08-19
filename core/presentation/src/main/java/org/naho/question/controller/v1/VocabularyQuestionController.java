package org.naho.question.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.question.VocabularyQuestionDetailMessageKey;
import org.naho.question.command.CompleteVocabularyQuestionCommand;
import org.naho.question.command.LearningPathNodeCommand;
import org.naho.question.dto.mapper.VocabularyQuestionResponseMapper;
import org.naho.question.dto.request.CompleteVocabularyQuestionRequest;
import org.naho.question.dto.response.VocabulariesOfQuestionResponse;
import org.naho.question.port.in.CompleteVocabularyQuestionInputPort;
import org.naho.question.port.in.SearchVocabulariesOfQuestionInputPort;
import org.naho.question.result.VocabulariesOfQuestionResult;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.result.AccessTokenPayload;
import org.naho.user.type.RoleName;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vocabulary-questions")
@RequiredArgsConstructor
public class VocabularyQuestionController {

    private final RoleRepositoryPort roleRepositoryPort;
    private final SearchVocabulariesOfQuestionInputPort searchVocabulariesOfQuestionInputPort;
    private final VocabularyQuestionResponseMapper vocabularyQuestionResponseMapper;
    private final CompleteVocabularyQuestionInputPort completeVocabularyQuestionInputPort;
    private final org.naho.question.port.in.UpdateVocabularyQuestionInputPort updateVocabularyQuestionInputPort;

    private void verifyAdminOrManager(Long userId) {
        java.util.List<String> roleSet = roleRepositoryPort.findRoleNamesByUserId(userId);
        if (!roleSet.contains(RoleName.ADMIN.name()) && !roleSet.contains(RoleName.CONTENT_MANAGER.name())) {
            throw new org.springframework.security.access.AccessDeniedException("Access Denied");
        }
    }

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

    @PutMapping("/{id}")
    public ResponseEntity<org.naho.question.result.UpdateVocabularyQuestionResult> updateVocabularyQuestion(
            @PathVariable Long id,
            @AuthenticationPrincipal AccessTokenPayload payload,
            @RequestBody @jakarta.validation.Valid org.naho.question.dto.request.UpdateVocabularyQuestionRequest request
    ) {
        verifyAdminOrManager(payload.userId());
        java.util.List<org.naho.question.command.UpdateVocabularyQuestionCommand.NestedVocabularyCommand> vocabularies = new java.util.ArrayList<>();
        if (request.vocabularies() != null) {
            for (var v : request.vocabularies()) {
                vocabularies.add(new org.naho.question.command.UpdateVocabularyQuestionCommand.NestedVocabularyCommand(
                        v.id(), v.reading(), v.japanese(), v.vietnameseMeaningText(), v.englishMeaningText()
                ));
            }
        }
        var command = new org.naho.question.command.UpdateVocabularyQuestionCommand(id, vocabularies);
        var result = updateVocabularyQuestionInputPort.updateVocabularyQuestion(command);
        return ResponseEntity.ok(result);
    }
}
