package org.naho.persona.port.out;

import org.naho.persona.model.ConversationStyle;

import java.util.Optional;

public interface ConversationStyleRepositoryPort {
    Optional<ConversationStyle> findById(Long id);

    ConversationStyle save(ConversationStyle conversationStyle);
}
