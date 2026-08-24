package org.naho.grammar.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.grammar.exception.GrammarErrorCode;
import org.naho.grammar.port.in.ImportGrammarPort;
import org.naho.i18n.message.question.GrammarDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.shared.exception.PresentationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/grammars")
@RequiredArgsConstructor
public class GrammarController {

    private final ImportGrammarPort importGrammarPort;

    // ROLE: CONTENT_MANAGER
    @PreAuthorize("hasRole('CONTENT_MANAGER')")
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponseMessage(message = GrammarDetailMessageKey.GRAMMAR_IMPORT_SUCCESS)
    public ResponseEntity<Void> importGrammar(@RequestPart("file") MultipartFile file) {
        try {
            importGrammarPort.importGrammar(file.getInputStream());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new PresentationException(
                    GrammarErrorCode.GRAMMAR_IMPORT_INVALID_FILE,
                    GrammarDetailMessageKey.GRAMMAR_IMPORT_INVALID_FILE,
                    e.getMessage()
            );
        }
    }
}
