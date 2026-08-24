package org.naho.book.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.naho.book.command.GetLessonDetailCommand;
import org.naho.book.dto.mapper.LessonRequestMapper;
import org.naho.book.dto.mapper.LessonResponseMapper;
import org.naho.book.dto.request.UpdateLessonRequest;
import org.naho.book.dto.response.LessonDetailResponse;
import org.naho.book.dto.response.LessonResponse;
import org.naho.book.port.in.GetLessonDetailInputPort;
import org.naho.book.port.in.UpdateLessonInputPort;
import org.naho.i18n.message.book.LessonDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.result.AccessTokenPayload;
import org.naho.user.type.RoleName;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final GetLessonDetailInputPort getLessonDetailInputPort;
    private final LessonResponseMapper lessonResponseMapper;

    private final RoleRepositoryPort roleRepositoryPort;
    private final UpdateLessonInputPort updateLessonInputPort;
    private final LessonRequestMapper lessonRequestMapper;

    // ROLE: ADMIN, CONTENT_MANAGER, LEANER
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTENT_MANAGER', 'LEARNER')")
    @GetMapping("/{id}")
    @ApiResponseMessage(message = LessonDetailMessageKey.LESSON_GET_DETAIL_SUCCESS)
    public ResponseEntity<LessonDetailResponse> getLessonDetail(
            @PathVariable("id") Long id) {
        var command = new GetLessonDetailCommand(id);
        var result = getLessonDetailInputPort.getLessonDetail(command);
        var response = lessonResponseMapper.detailResultToResponse(result);
        return ResponseEntity.ok(response);
    }

    // ROLE: CONTENT_MANAGER
    @PreAuthorize("hasAnyRole('CONTENT_MANAGER', 'ADMIN')")
    @PutMapping("/{id}")
    @ApiResponseMessage(message = LessonDetailMessageKey.LESSON_UPDATE_SUCCESS)
    public ResponseEntity<LessonResponse> updateLesson(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateLessonRequest request,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        List<String> roleSet = roleRepositoryPort.findRoleNamesByUserId(payload.userId());
        boolean isAdminOrManager =
                roleSet.contains(RoleName.ADMIN.name()) ||
                        roleSet.contains(RoleName.CONTENT_MANAGER.name());

        var command = lessonRequestMapper.toUpdateCommand(request, id, payload.userId(), isAdminOrManager);
        var result = updateLessonInputPort.updateLesson(command);
        var response = lessonResponseMapper.listItemResultToResponse(result);
        return ResponseEntity.ok(response);
    }
}
