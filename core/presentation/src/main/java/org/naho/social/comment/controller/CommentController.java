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
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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

    @MessageMapping("/comments/create")
    @SendTo("/topic/comments")
    public CommentResponse createComment(
            @Valid CreateCommentRequest createCommentRequest,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        Long userId = payload != null ? payload.userId() : null;
        CommentCreateCommand createCommand = commentCommandMapper.requestToCommand(createCommentRequest, userId);
        CommentResonseResult commentResonseResult = commentCrudInputPort.createComment(createCommand);
        return commentResponseMapper.resultToResponse(commentResonseResult);
    }

    @MessageMapping("/comments/update")
    @SendTo("/topic/comments")
    public CommentResponse fixComment(
            @Valid UpdateCommentRequest updateCommentRequest,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        Long userId = payload != null ? payload.userId() : null;
        CommentUpdateCommand command = commentCommandMapper.requestToUpdateCommand(updateCommentRequest, userId);
        CommentResonseResult commentResonseResult = commentCrudInputPort.updateComment(command);
        return commentResponseMapper.resultToResponse(commentResonseResult);
    }

    @MessageMapping("/comments/delete")
    @SendTo("/topic/comments")
    public CommentResponse deleteComment(
            @Valid DeleteCommandRequest deleteCommandRequest,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        Long userId = payload != null ? payload.userId() : null;
        CommentDeleteCommand command = commentCommandMapper.requestToDeleteCommand(deleteCommandRequest, userId);
        CommentResonseResult commentResonseResult = commentCrudInputPort.deleteComment(command);
        return commentResponseMapper.resultToResponse(commentResonseResult);
    }
}
