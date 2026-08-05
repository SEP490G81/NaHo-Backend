package org.naho.social.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.social.CommentDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.social.comment.command.CommentCreateCommand;
import org.naho.social.comment.command.CommentDeleteCommand;
import org.naho.social.comment.command.CommentReadCommand;
import org.naho.social.comment.command.CommentUpdateCommand;
import org.naho.social.comment.dto.mapper.CommentCommandMapper;
import org.naho.social.comment.dto.mapper.CommentResponseMapper;
import org.naho.social.comment.dto.request.CreateCommentRequest;
import org.naho.social.comment.dto.request.DeleteCommandRequest;
import org.naho.social.comment.dto.request.UpdateCommentRequest;
import org.naho.social.comment.dto.response.CommentResponse;
import org.naho.social.comment.port.in.CommentCrudInputPort;
import org.naho.social.comment.result.CommentListResponseResult;
import org.naho.social.comment.result.CommentResonseResult;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentCrudInputPort commentCrudInputPort;
    private final CommentCommandMapper commentCommandMapper;
    private final CommentResponseMapper commentResponseMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping
    @ApiResponseMessage(message = CommentDetailMessageKey.COMMENT_GET_LIST_SUCCESS)
    public ResponseEntity<CommentListResponseResult> getComments(
            @RequestParam("speakingQuestionId") Long speakingQuestionId,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        Long currentUserId = payload != null ? payload.userId() : null;
        CommentReadCommand command = new CommentReadCommand(speakingQuestionId);
        CommentListResponseResult result = commentCrudInputPort.getListCommentOfQuestion(command, currentUserId);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<CommentResponse> createCommentRest(
            @Valid @RequestBody CreateCommentRequest createCommentRequest,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        Long userId = payload != null ? payload.userId() : createCommentRequest.userId();
        CommentCreateCommand createCommand = commentCommandMapper.requestToCommand(createCommentRequest, userId);
        CommentResonseResult commentResonseResult = commentCrudInputPort.createComment(createCommand);
        CommentResponse response = commentResponseMapper.resultToResponse(commentResonseResult);
        messagingTemplate.convertAndSend("/topic/comments", response);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<CommentResponse> updateCommentRest(
            @Valid @RequestBody UpdateCommentRequest updateCommentRequest,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        Long userId = payload != null ? payload.userId() : updateCommentRequest.userId();
        CommentUpdateCommand command = commentCommandMapper.requestToUpdateCommand(updateCommentRequest, userId);
        CommentResonseResult commentResonseResult = commentCrudInputPort.updateComment(command);
        CommentResponse response = commentResponseMapper.resultToResponse(commentResonseResult);
        messagingTemplate.convertAndSend("/topic/comments", response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<CommentResponse> deleteCommentRest(
            @Valid @RequestBody DeleteCommandRequest deleteCommandRequest,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        Long userId = payload != null ? payload.userId() : null;
        CommentDeleteCommand command = commentCommandMapper.requestToDeleteCommand(deleteCommandRequest, userId);
        CommentResonseResult commentResonseResult = commentCrudInputPort.deleteComment(command);
        CommentResponse response = commentResponseMapper.resultToResponse(commentResonseResult);
        messagingTemplate.convertAndSend("/topic/comments", response);
        return ResponseEntity.ok(response);
    }

    @MessageMapping("/comments/create")
    @SendTo("/topic/comments")
    public CommentResponse createComment(
            @Valid @Payload CreateCommentRequest createCommentRequest,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        Long userId = payload != null ? payload.userId() : createCommentRequest.userId();
        CommentCreateCommand createCommand = commentCommandMapper.requestToCommand(createCommentRequest, userId);
        CommentResonseResult commentResonseResult = commentCrudInputPort.createComment(createCommand);
        return commentResponseMapper.resultToResponse(commentResonseResult);
    }

    @MessageMapping("/comments/update")
    @SendTo("/topic/comments")
    public CommentResponse fixComment(
            @Valid @Payload UpdateCommentRequest updateCommentRequest,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        Long userId = payload != null ? payload.userId() : updateCommentRequest.userId();
        CommentUpdateCommand command = commentCommandMapper.requestToUpdateCommand(updateCommentRequest, userId);
        CommentResonseResult commentResonseResult = commentCrudInputPort.updateComment(command);
        return commentResponseMapper.resultToResponse(commentResonseResult);
    }

    @MessageMapping("/comments/delete")
    @SendTo("/topic/comments")
    public CommentResponse deleteComment(
            @Valid @Payload DeleteCommandRequest deleteCommandRequest,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        Long userId = payload != null ? payload.userId() : null;
        CommentDeleteCommand command = commentCommandMapper.requestToDeleteCommand(deleteCommandRequest, userId);
        CommentResonseResult commentResonseResult = commentCrudInputPort.deleteComment(command);
        return commentResponseMapper.resultToResponse(commentResonseResult);
    }
}
