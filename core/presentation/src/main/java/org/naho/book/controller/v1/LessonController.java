package org.naho.book.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.book.command.GetLessonDetailCommand;
import org.naho.book.dto.mapper.LessonResponseMapper;
import org.naho.book.dto.response.LessonDetailResponse;
import org.naho.book.port.in.GetLessonDetailInputPort;
import org.naho.i18n.message.book.LessonDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final GetLessonDetailInputPort getLessonDetailInputPort;
    private final LessonResponseMapper lessonResponseMapper;

    @GetMapping("/{id}")
    @ApiResponseMessage(message = LessonDetailMessageKey.LESSON_GET_DETAIL_SUCCESS)
    public ResponseEntity<LessonDetailResponse> getLessonDetail(
            @PathVariable("id") Long id) {
        var command = new GetLessonDetailCommand(id);
        var result = getLessonDetailInputPort.getLessonDetail(command);
        var response = lessonResponseMapper.detailResultToResponse(result);
        return ResponseEntity.ok(response);
    }
}
