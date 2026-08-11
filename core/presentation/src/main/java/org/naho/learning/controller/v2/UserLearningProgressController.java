package org.naho.learning.controller.v2;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.learning.UserLearningProgressDetailMessageKey;
import org.naho.learning.dto.mapper.UserLearningProgressResponseMapper;
import org.naho.learning.dto.response.UserLearningProgressResponse;
import org.naho.learning.port.in.CrudUserLearningProgressInputPort;
import org.naho.learning.result.UserLearningProgressResult;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("userLearningProgressControllerV2")
@RequestMapping("/api/v2/user-learning-progresses")
@RequiredArgsConstructor
public class UserLearningProgressController {
    private final CrudUserLearningProgressInputPort crudUserLearningProgressInputPort;
    private final UserLearningProgressResponseMapper userLearningProgressResponseMapper;

    @ApiResponseMessage(message = UserLearningProgressDetailMessageKey.USER_LEARNING_PROGRESS_GET_SUCCESS)
    @GetMapping("/me")
    public ResponseEntity<UserLearningProgressResponse> findMyUserLearningProgress(
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        UserLearningProgressResult result = crudUserLearningProgressInputPort
                .findUserLearningProgressByUserId(payload.userId());

        UserLearningProgressResponse response = userLearningProgressResponseMapper
                .resultToResponse(result);

        return ResponseEntity.ok(response);
    }
}

