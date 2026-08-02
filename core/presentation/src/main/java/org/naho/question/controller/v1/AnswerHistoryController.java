package org.naho.question.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.question.port.in.CrudAnswerHistoryInputPort;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/answer-histories")
@RequiredArgsConstructor
public class AnswerHistoryController {
    private final CrudAnswerHistoryInputPort crudAnswerHistoryInputPort;

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
}
