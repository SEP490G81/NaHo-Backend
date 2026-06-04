package org.naho.speech.topic.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.speech.TopicDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.speech.topic.dto.mapper.TopicRequestMapper;
import org.naho.speech.topic.dto.mapper.TopicResponseMapper;
import org.naho.speech.topic.dto.request.CreateTopicRequest;
import org.naho.speech.topic.dto.response.TopicResponse;
import org.naho.speech.topic.port.in.CreateTopicInputPort;
import org.naho.speech.topic.result.TopicResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/topics")
@RequiredArgsConstructor
public class TopicController {

    private final CreateTopicInputPort createTopicInputPort;
    private final TopicRequestMapper topicRequestMapper;
    private final TopicResponseMapper topicResponseMapper;

    @PostMapping
    @ApiResponseMessage(message = TopicDetailMessageKey.TOPIC_CREATION_SUCCESS)
    public ResponseEntity<TopicResponse> createTopic(@RequestBody CreateTopicRequest request) {
//         TODO: Lay ID nguoi dung dang dang nhap tu token (tam thoi gan bang 1L)
        Long userId = 1L;

        var command = topicRequestMapper.toCommand(request, userId);
        TopicResult result = createTopicInputPort.createTopic(command);
        TopicResponse response = topicResponseMapper.resultToResponse(result);


        return ResponseEntity.ok(response);
    }
}
