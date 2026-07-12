package org.naho.vocabulary.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.question.VocabularyQuestionDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.vocabulary.dto.mapper.VocabularyObjectiveResponseMapper;
import org.naho.vocabulary.dto.response.VocabulariesOfObjectiveResponse;
import org.naho.vocabulary.port.in.GetVocabulariesOfObjectiveInputPort;
import org.naho.vocabulary.result.VocabulariesOfObjectiveResult;
import org.naho.vocabulary.usecase.ImportVocabularyUseCase;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/vocabularies")
@RequiredArgsConstructor
public class VocabularyController {

    private final ImportVocabularyUseCase importVocabularyUseCase;
    private final GetVocabulariesOfObjectiveInputPort getVocabulariesOfObjectiveInputPort;
    private final VocabularyObjectiveResponseMapper vocabularyObjectiveResponseMapper;

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> importVocabulary(@RequestPart("file") MultipartFile file) {
        try {
            importVocabularyUseCase.importVocabulary(file.getInputStream());
            return ResponseEntity.ok("Vocabulary imported successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/objective")
    @ApiResponseMessage(message = VocabularyQuestionDetailMessageKey.VOCABULARY_OBJECTIVE_GET_SUCCESS)
    public ResponseEntity<VocabulariesOfObjectiveResponse> getVocabulariesOfObjective(
            @RequestParam("objective_id") int objectiveId
    ) {
        VocabulariesOfObjectiveResult result = getVocabulariesOfObjectiveInputPort.getVocabularyListOfObjective(objectiveId);
        VocabulariesOfObjectiveResponse response = vocabularyObjectiveResponseMapper.toResponse(result);
        return ResponseEntity.ok(response);
    }
}
