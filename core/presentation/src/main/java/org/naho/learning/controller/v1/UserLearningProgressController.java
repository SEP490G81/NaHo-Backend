package org.naho.learning.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.learning.UserLearningProgressDetailMessageKey;
import org.naho.learning.dto.mapper.UserLearningProgressResponseMapper;
import org.naho.learning.dto.response.UserLearningProgressResponse;
import org.naho.learning.port.in.CrudUserLearningProgressInputPort;
import org.naho.learning.result.UserLearningProgressResult;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("userLearningProgressControllerV1")
@RequestMapping("/api/v1/user-learning-progresses")
@RequiredArgsConstructor
public class UserLearningProgressController {
    private final CrudUserLearningProgressInputPort crudUserLearningProgressInputPort;
    private final UserLearningProgressResponseMapper userLearningProgressResponseMapper;

    // ROLE: LEARNER

    /**
     * @param payload chứa user id của người đang đăng nhập
     * @return UserLearningProgressResponse
     * @deprecated Lấy user learning progress của người đang đăng nhập
     */
    @PreAuthorize("hasRole('LEARNER')")
    @Deprecated(forRemoval = true)
    @ApiResponseMessage(message = UserLearningProgressDetailMessageKey.USER_LEARNING_PROGRESS_GET_SUCCESS)
    @GetMapping
    public ResponseEntity<UserLearningProgressResponse> findMyUserLearningProgress(
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        UserLearningProgressResult result = crudUserLearningProgressInputPort
                .findUserLearningProgressByUserId(payload.userId());

        UserLearningProgressResponse response = userLearningProgressResponseMapper
                .resultToResponse(result);

        return ResponseEntity.ok(response);
    }

    // ROLE: LEARNER

    /**
     * Lấy user learning progress theo id của người dùng
     *
     * @param userId user id
     * @return UserLearningProgressResponse
     */
    @PreAuthorize("hasAnyRole('LEARNER', 'ADMIN', 'CONTENT_MANAGER')")
    @ApiResponseMessage(message = UserLearningProgressDetailMessageKey.USER_LEARNING_PROGRESS_GET_SUCCESS)
    @GetMapping("/{userId}")
    public ResponseEntity<UserLearningProgressResponse> findUserLearningProgressByUserId(
            @PathVariable Long userId
    ) {
        UserLearningProgressResult result = crudUserLearningProgressInputPort
                .findUserLearningProgressByUserId(userId);

        UserLearningProgressResponse response = userLearningProgressResponseMapper
                .resultToResponse(result);

        return ResponseEntity.ok(response);
    }
}
