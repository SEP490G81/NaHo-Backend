package org.naho.social.reaction.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.social.ReactionDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.social.reaction.command.ReactionActionCommand;
import org.naho.social.reaction.dto.mapper.ReactionRequestMapper;
import org.naho.social.reaction.dto.mapper.ReactionResponseMapper;
import org.naho.social.reaction.dto.request.ReactionRequest;
import org.naho.social.reaction.dto.response.ReactionDetailResponse;
import org.naho.social.reaction.dto.response.ReactionResponse;
import org.naho.social.reaction.port.in.CrudReactionTypeInputPort;
import org.naho.social.reaction.result.ReactionDetailResult;
import org.naho.social.reaction.result.ReactionResult;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reactions")
@RequiredArgsConstructor
public class ReactionController {

    private final CrudReactionTypeInputPort crudReactionTypeInputPort;
    private final ReactionResponseMapper reactionResponseMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final ReactionRequestMapper reactionRequestMapper;

    @GetMapping
    @ApiResponseMessage(message = ReactionDetailMessageKey.REACTION_GET_DETAIL_SUCCESS)
    public ResponseEntity<ReactionDetailResponse> getReactionsByComment(
            @RequestParam("commentId") Long commentId) {
        ReactionDetailResult result = crudReactionTypeInputPort.getReactionsByComment(commentId);
        return ResponseEntity.ok(reactionResponseMapper.resultToDetailResponse(result));
    }

    @PostMapping("/toggle")
    @ApiResponseMessage(message = ReactionDetailMessageKey.REACTION_TOGGLE_SUCCESS)
    public ResponseEntity<ReactionResponse> toggleReaction(
            @Valid @RequestBody ReactionRequest request,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        ReactionActionCommand command = reactionRequestMapper.toCommand(request, payload.userId());
        ReactionResult result = crudReactionTypeInputPort.chooseReaction(command);
        ReactionResponse response = reactionResponseMapper.resultToResponse(result);
        messagingTemplate.convertAndSend("/topic/reaction/" + request.commentId(), response);
        return ResponseEntity.ok(response);
    }

    @MessageMapping("/reaction/toggle")
    @SendTo("/topic/reaction")
    public ReactionResponse actionReact(
            ReactionRequest request,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        ReactionActionCommand command = reactionRequestMapper.toCommand(request, payload.userId());
        ReactionResult result = crudReactionTypeInputPort.chooseReaction(command);
        return reactionResponseMapper.resultToResponse(result);
    }
}
