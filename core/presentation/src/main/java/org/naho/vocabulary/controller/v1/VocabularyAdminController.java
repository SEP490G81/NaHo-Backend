package org.naho.vocabulary.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.question.VocabularyDetailMessageKey;
import org.naho.pagination.PageData;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.result.AccessTokenPayload;
import org.naho.user.type.RoleName;
import org.naho.vocabulary.command.SearchVocabularyCommand;
import org.naho.vocabulary.dto.request.CreateVocabularyRequest;
import org.naho.vocabulary.dto.request.UpdateVocabularyRequest;
import org.naho.vocabulary.dto.response.VocabularyResponse;
import org.naho.vocabulary.mapper.VocabularyAdminMapper;
import org.naho.vocabulary.port.in.*;
import org.naho.vocabulary.result.VocabularyResult;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vocabularies")
@RequiredArgsConstructor
public class VocabularyAdminController {

    private final CreateVocabularyInputPort createVocabularyInputPort;
    private final UpdateVocabularyInputPort updateVocabularyInputPort;
    private final DeleteVocabularyInputPort deleteVocabularyInputPort;
    private final GetVocabularyDetailInputPort getVocabularyDetailInputPort;
    private final SearchVocabularyInputPort searchVocabularyInputPort;
    private final VocabularyAdminMapper vocabularyAdminMapper;
    private final RoleRepositoryPort roleRepositoryPort;

    private void verifyAdminOrManager(AccessTokenPayload payload) {
        if (payload == null) {
            throw new AccessDeniedException("Access Denied");
        }
        java.util.List<String> roleSet = roleRepositoryPort.findRoleNamesByUserId(payload.userId());
        if (!roleSet.contains(RoleName.ADMIN.name()) && !roleSet.contains(RoleName.CONTENT_MANAGER.name())) {
            throw new AccessDeniedException("Access Denied");
        }
    }

    // ROLE: CONTENT_MANAGER
    @PreAuthorize("hasAnyRole('CONTENT_MANAGER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<VocabularyResponse> createVocabulary(
            @Valid @RequestBody CreateVocabularyRequest request,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        verifyAdminOrManager(payload);
        VocabularyResult result = createVocabularyInputPort.createVocabulary(vocabularyAdminMapper.toCommand(request));
        return ResponseEntity.ok(vocabularyAdminMapper.toResponse(result));
    }

    // ROLE: CONTENT_MANAGER
    @PreAuthorize("hasAnyRole('CONTENT_MANAGER', 'ADMIN')")
    @PutMapping("/{id}")
    @ApiResponseMessage(message = VocabularyDetailMessageKey.VOCABULARY_UPDATE_SUCCESS)
    public ResponseEntity<VocabularyResponse> updateVocabulary(
            @PathVariable Long id,
            @Valid @RequestBody UpdateVocabularyRequest request,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        verifyAdminOrManager(payload);
        request.setId(id);
        VocabularyResult result = updateVocabularyInputPort.updateVocabulary(vocabularyAdminMapper.toCommand(request));
        return ResponseEntity.ok(vocabularyAdminMapper.toResponse(result));
    }

    // ROLE: CONTENT_MANAGER
    @PreAuthorize("hasAnyRole('CONTENT_MANAGER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVocabulary(
            @PathVariable Long id,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        verifyAdminOrManager(payload);
        deleteVocabularyInputPort.deleteVocabulary(id);
        return ResponseEntity.noContent().build();
    }

    // ROLE: CONTENT_MANAGER
    @PreAuthorize("hasAnyRole('CONTENT_MANAGER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<VocabularyResponse> getVocabularyDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        verifyAdminOrManager(payload);
        VocabularyResult result = getVocabularyDetailInputPort.getVocabularyDetail(id);
        return ResponseEntity.ok(vocabularyAdminMapper.toResponse(result));
    }

    // ROLE: CONTENT_MANAGER
    @PreAuthorize("hasAnyRole('CONTENT_MANAGER', 'ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<PageData<VocabularyResponse>> searchVocabularies(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        verifyAdminOrManager(payload);

        PageData<VocabularyResult> result = searchVocabularyInputPort.searchVocabularies(new SearchVocabularyCommand(keyword, page, size));

        PageData<VocabularyResponse> response = new PageData<>(
                result.getData().stream().map(vocabularyAdminMapper::toResponse).toList(),
                result.getPageMeta()
        );

        return ResponseEntity.ok(response);
    }
}
