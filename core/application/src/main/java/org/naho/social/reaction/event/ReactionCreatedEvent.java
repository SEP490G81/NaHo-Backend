package org.naho.social.reaction.event;

public record ReactionCreatedEvent(
        Long commentAuthorId,
        Long reactorId,
        Long commentId,
        Long questionId
) {
}
