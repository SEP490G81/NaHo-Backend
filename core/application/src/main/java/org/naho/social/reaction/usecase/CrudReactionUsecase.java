package org.naho.social.reaction.usecase;

import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.social.reaction.command.ReactionActionCommand;
import org.naho.social.reaction.mapper.ReactionActionCommandMapper;
import org.naho.social.reaction.mapper.ReactionResultResponseMapper;
import org.naho.social.reaction.model.Reaction;
import org.naho.social.reaction.port.in.CrudReactionTypeInputPort;
import org.naho.social.reaction.port.out.ReactionRepositoryPort;
import org.naho.social.reaction.result.ReactionDetailResult;
import org.naho.social.reaction.result.ReactionResult;
import org.naho.social.reaction.type.ReactionType;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.port.out.UserRepositoryPort;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CrudReactionUsecase implements CrudReactionTypeInputPort {
    private final ReactionRepositoryPort reactionRepositoryPort;
    private final ReactionActionCommandMapper reactionActionCommandMapper;
    private final ReactionResultResponseMapper reactionResultResponseMapper;
    private final UserRepositoryPort userRepositoryPort;

    public CrudReactionUsecase(ReactionRepositoryPort reactionRepositoryPort,
                                ReactionActionCommandMapper reactionActionCommandMapper,
                                ReactionResultResponseMapper reactionResultResponseMapper,
                                UserRepositoryPort userRepositoryPort) {
        this.reactionRepositoryPort = reactionRepositoryPort;
        this.reactionActionCommandMapper = reactionActionCommandMapper;
        this.reactionResultResponseMapper = reactionResultResponseMapper;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public ReactionResult chooseReaction(ReactionActionCommand reactionActionCommand) {
        String username = userRepositoryPort.findById(reactionActionCommand.userId())
                .orElseThrow(() -> new ApplicationException(
                        UserErrorCode.USER_NOT_FOUND,
                        UserDetailMessageKey.USER_ID_NOT_FOUND
                ))
                .getUsername()
                .getValue();

        Reaction existingReaction = reactionRepositoryPort.findByUserAndTarget(
                reactionActionCommand.userId(),
                reactionActionCommand.comment_id()
        );

        if (existingReaction == null) {
            Reaction newReaction = reactionActionCommandMapper.commandToDomain(reactionActionCommand);
            reactionRepositoryPort.save(newReaction);
            return reactionResultResponseMapper.domainToResult(newReaction, username);
        }

        if (existingReaction.getReactionType() == reactionActionCommand.reactionType()) {
            reactionRepositoryPort.delete(existingReaction.getId());
            return new ReactionResult(null, username);
        }

        Reaction updatedReaction = existingReaction.toBuilder()
                .reactionType(reactionActionCommand.reactionType())
                .build();
        reactionRepositoryPort.save(updatedReaction);
        return reactionResultResponseMapper.domainToResult(updatedReaction, username);
    }

    @Override
    public ReactionDetailResult getReactionsByComment(Long commentId) {
        List<Reaction> reactions = reactionRepositoryPort.findByCommentId(commentId);

        Map<ReactionType, Long> counts = reactions.stream()
                .collect(Collectors.groupingBy(Reaction::getReactionType, Collectors.counting()));

        Map<ReactionType, List<ReactionDetailResult.ReactionUserItem>> users = reactions.stream()
                .collect(Collectors.groupingBy(
                        Reaction::getReactionType,
                        Collectors.mapping(
                                r -> new ReactionDetailResult.ReactionUserItem(r.getUserId(), null),
                                Collectors.toList()
                        )
                ));

        return new ReactionDetailResult(commentId, reactions.size(), counts, users);
    }
}
