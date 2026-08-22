package org.naho.grammar.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.naho.grammar.command.SearchGrammarCommand;
import org.naho.grammar.dto.request.CreateGrammarRequest;
import org.naho.grammar.dto.request.UpdateGrammarRequest;
import org.naho.grammar.dto.response.GrammarResponse;
import org.naho.grammar.mapper.GrammarAdminMapper;
import org.naho.grammar.port.in.*;
import org.naho.grammar.result.GrammarResult;
import org.naho.pagination.PageData;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.result.AccessTokenPayload;
import org.naho.user.type.RoleName;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/grammars")
@RequiredArgsConstructor
public class GrammarAdminController {

    private final CreateGrammarInputPort createGrammarInputPort;
    private final UpdateGrammarInputPort updateGrammarInputPort;
    private final DeleteGrammarInputPort deleteGrammarInputPort;
    private final GetGrammarDetailInputPort getGrammarDetailInputPort;
    private final SearchGrammarInputPort searchGrammarInputPort;
    private final GrammarAdminMapper grammarAdminMapper;
    private final RoleRepositoryPort roleRepositoryPort;

    private void verifyAdminOrManager(AccessTokenPayload payload) {
        if (payload == null) {
            throw new org.springframework.security.access.AccessDeniedException("Access Denied");
        }
        java.util.List<String> roleSet = roleRepositoryPort.findRoleNamesByUserId(payload.userId());
        if (!roleSet.contains(RoleName.ADMIN.name()) && !roleSet.contains(RoleName.CONTENT_MANAGER.name())) {
            throw new org.springframework.security.access.AccessDeniedException("Access Denied");
        }
    }

    @PostMapping
    public ResponseEntity<GrammarResponse> createGrammar(
            @Valid @RequestBody CreateGrammarRequest request,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        verifyAdminOrManager(payload);
        GrammarResult result = createGrammarInputPort.createGrammar(grammarAdminMapper.toCommand(request));
        return ResponseEntity.ok(grammarAdminMapper.resultToResponse(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GrammarResponse> updateGrammar(
            @PathVariable Long id,
            @Valid @RequestBody UpdateGrammarRequest request,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        verifyAdminOrManager(payload);
        request.setId(id);
        GrammarResult result = updateGrammarInputPort.updateGrammar(grammarAdminMapper.toCommand(request));
        return ResponseEntity.ok(grammarAdminMapper.resultToResponse(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrammar(
            @PathVariable Long id,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        verifyAdminOrManager(payload);
        deleteGrammarInputPort.deleteGrammar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrammarResponse> getGrammarDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        verifyAdminOrManager(payload);
        GrammarResult result = getGrammarDetailInputPort.getGrammarDetail(id);
        return ResponseEntity.ok(grammarAdminMapper.resultToResponse(result));
    }

    @GetMapping("/search")
    public ResponseEntity<PageData<GrammarResponse>> searchGrammars(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        verifyAdminOrManager(payload);

        PageData<GrammarResult> result = searchGrammarInputPort.searchGrammars(new SearchGrammarCommand(keyword, page, size));

        PageData<GrammarResponse> response = new PageData<>(
                result.getData().stream().map(grammarAdminMapper::resultToResponse).toList(),
                result.getPageMeta()
        );

        return ResponseEntity.ok(response);
    }
}
