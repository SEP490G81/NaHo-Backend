package org.naho.vocabulary.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.question.VocabularyQuestionDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.shared.exception.BaseException;
import org.naho.shared.exception.PresentationException;
import org.naho.vocabulary.dto.mapper.VocabularyObjectiveResponseMapper;
import org.naho.vocabulary.dto.response.VocabulariesOfObjectiveResponse;
import org.naho.vocabulary.exception.VocabularyErrorCode;
import org.naho.vocabulary.port.in.ExportVocabularyInputPort;
import org.naho.vocabulary.port.in.GetVocabulariesOfObjectiveInputPort;
import org.naho.vocabulary.port.in.ImportVocabularyPort;
import org.naho.vocabulary.result.VocabulariesOfObjectiveResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/api/v1/vocabularies")
@RequiredArgsConstructor
public class VocabularyController {

    private final ImportVocabularyPort importVocabularyPort;
    private final GetVocabulariesOfObjectiveInputPort getVocabulariesOfObjectiveInputPort;
    private final VocabularyObjectiveResponseMapper vocabularyObjectiveResponseMapper;
    private final ExportVocabularyInputPort exportVocabularyInputPort;

    //validate file excel
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponseMessage(message = VocabularyQuestionDetailMessageKey.VOCABULARY_IMPORT_SUCCESS)
    public ResponseEntity<Void> importVocabulary(@RequestPart("file") MultipartFile file) {
        try {
            importVocabularyPort.importVocabulary(file.getInputStream());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new PresentationException(
                    VocabularyErrorCode.VOCABULARY_IMPORT_INVALID_FILE,
                    VocabularyQuestionDetailMessageKey.VOCABULARY_IMPORT_INVALID_FILE,
                    e.getMessage()
            );
        }
    }

    @GetMapping("/objective/{objectiveId}")
    @ApiResponseMessage(message = VocabularyQuestionDetailMessageKey.VOCABULARY_OBJECTIVE_GET_SUCCESS)
    public ResponseEntity<VocabulariesOfObjectiveResponse> getVocabulariesOfObjective(
            @PathVariable("objectiveId") int objectiveId
    ) {
        VocabulariesOfObjectiveResult result = getVocabulariesOfObjectiveInputPort.getVocabularyListOfObjective(objectiveId);
        VocabulariesOfObjectiveResponse response = vocabularyObjectiveResponseMapper.toResponse(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/export/question/{questionId}")
    public ResponseEntity<byte[]> exportByQuestion(@PathVariable("questionId") int questionId) {
        try {
            ByteArrayInputStream in = exportVocabularyInputPort.exportByQuestion(questionId);
            byte[] data = in.readAllBytes();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=vocabularies_question_" + questionId + ".xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(data);
        } catch (Exception e) {
            if (e instanceof BaseException) {
                throw (BaseException) e;
            }
            throw new RuntimeException("Failed to export question vocabularies: " + e.getMessage(), e);
        }
    }

    @GetMapping("/export/objective/{objectiveId}")
    public ResponseEntity<byte[]> exportByObjective(@PathVariable("objectiveId") int objectiveId) {
        try {
            ByteArrayInputStream in = exportVocabularyInputPort.exportByObjective(objectiveId);
            byte[] data = in.readAllBytes();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=vocabularies_objective_" + objectiveId + ".xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(data);
        } catch (Exception e) {
            if (e instanceof BaseException) {
                throw (BaseException) e;
            }
            throw new RuntimeException("Failed to export objective vocabularies: " + e.getMessage(), e);
        }
    }
}
