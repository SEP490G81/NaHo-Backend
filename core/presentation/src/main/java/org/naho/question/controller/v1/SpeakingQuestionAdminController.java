package org.naho.question.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.question.SpeakingQuestionDetailMessageKey;
import org.naho.question.command.UpdateSpeakingQuestionCommand;
import org.naho.question.dto.request.UpdateSpeakingQuestionRequest;
import org.naho.question.port.in.UpdateSpeakingQuestionInputPort;
import org.naho.question.result.UpdateSpeakingQuestionResult;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.result.AccessTokenPayload;
import org.naho.user.type.RoleName;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/speaking-questions")
@RequiredArgsConstructor
public class SpeakingQuestionAdminController {

    private final UpdateSpeakingQuestionInputPort updateSpeakingQuestionInputPort;
    private final RoleRepositoryPort roleRepositoryPort;

    @PutMapping("/{id}")
    @ApiResponseMessage(message = SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_UPDATE_SUCCESS)
    public ResponseEntity<UpdateSpeakingQuestionResult> updateSpeakingQuestion(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateSpeakingQuestionRequest request,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        Long currentUserId = payload.userId();
        List<String> roleSet = roleRepositoryPort.findRoleNamesByUserId(currentUserId);
        boolean isContentManager = roleSet.contains(RoleName.ADMIN.name()) || roleSet.contains(RoleName.CONTENT_MANAGER.name());

        if (!isContentManager) {
            throw new org.springframework.security.access.AccessDeniedException("Access Denied");
        }

        List<UpdateSpeakingQuestionCommand.NestedVocabularyCommand> vocabularies = new ArrayList<>();
        if (request.vocabularies() != null) {
            for (var v : request.vocabularies()) {
                vocabularies.add(new UpdateSpeakingQuestionCommand.NestedVocabularyCommand(
                        v.id(), v.reading(), v.japanese(), v.vietnameseMeaningText(), v.englishMeaningText()
                ));
            }
        }

        List<UpdateSpeakingQuestionCommand.NestedGrammarCommand> grammars = new ArrayList<>();
        if (request.grammars() != null) {
            for (var g : request.grammars()) {
                grammars.add(new UpdateSpeakingQuestionCommand.NestedGrammarCommand(
                        g.id(), g.reading(), g.japanese(), g.vietnameseMeaningText(), g.englishMeaningText()
                ));
            }
        }

        UpdateSpeakingQuestionCommand command = new UpdateSpeakingQuestionCommand(
                id,
                currentUserId,
                request.japaneseName(),
                request.vietnameseName(),
                request.description(),
                request.japaneseSampleAnswer(),
                request.vietnameseSampleAnswer(),
                request.englishSampleAnswer(),
                isContentManager,
                vocabularies,
                grammars
        );

        UpdateSpeakingQuestionResult result = updateSpeakingQuestionInputPort.updateSpeakingQuestion(command);
        return ResponseEntity.ok(result);
    }
}
