package org.naho.learning.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.learning.dto.mapper.UserNodeProgressResponseMapper;
import org.naho.learning.dto.response.UserNodeProgressResponse;
import org.naho.learning.port.in.CrudUserNodeProgressPort;
import org.naho.learning.result.UserNodeProgressResult;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user-node-progresses")
@RequiredArgsConstructor
public class UserNodeProgressController {
    private final CrudUserNodeProgressPort crudUserNodeProgressPort;
    private final UserNodeProgressResponseMapper userNodeProgressResponseMapper;

    /**
     * Lấy thông tin về 1 node mà người dùng học như:
     * - Điểm cao nhất từng đạt được
     * - Điểm gần nhất
     * - Số lần học
     * - Học lần cuối lúc
     * - Trạng thái FAILED/PASSED
     *
     * @param payload            chứa user id
     * @param learningPathNodeId node mà muốn xem chi tiết
     * @return UserNodeProgressResponse
     */
    // ROLE: LEARNER
    @PreAuthorize("hasRole('LEARNER')")
    @ApiResponseMessage
    @GetMapping("/learning-path-nodes/{learningPathNodeId}")
    public ResponseEntity<UserNodeProgressResponse> findByLearningPathNodeIdAndUserId(
            @AuthenticationPrincipal AccessTokenPayload payload,
            @PathVariable Long learningPathNodeId
    ) {
        UserNodeProgressResult result = crudUserNodeProgressPort
                .findByLearningPathNodeIdAndUserId(learningPathNodeId, payload.userId());

        UserNodeProgressResponse response = userNodeProgressResponseMapper.resultToResponse(result);
        return ResponseEntity.ok(response);
    }
}
