package org.naho.learning.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.learning.LearningPathNodeDetailMessageKey;
import org.naho.learning.command.GetLearningPathNodeDetailCommand;
import org.naho.learning.dto.mapper.LearningPathNodeResponseMapper;
import org.naho.learning.dto.response.LearningPathNodeDetailResponse;
import org.naho.learning.port.in.GetLearningPathNodeDetailInputPort;
import org.naho.shared.annotation.ApiResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/learning-path-nodes")
@RequiredArgsConstructor
public class LearningPathNodeController {

    private final GetLearningPathNodeDetailInputPort getLearningPathNodeDetailInputPort;
    private final LearningPathNodeResponseMapper learningPathNodeResponseMapper;

    @GetMapping("/{id}")
    @ApiResponseMessage(message = LearningPathNodeDetailMessageKey.LEARNING_PATH_NODE_GET_DETAIL_SUCCESS)
    public ResponseEntity<LearningPathNodeDetailResponse> getLearningPathNodeDetail(
            @PathVariable Long id
    ) {
        var command = new GetLearningPathNodeDetailCommand(id);
        var result = getLearningPathNodeDetailInputPort.getLearningPathNodeDetail(command);
        var response = learningPathNodeResponseMapper.detailResultToResponse(result);
        return ResponseEntity.ok(response);
    }
}
