package org.naho.persona.port.in;

import org.naho.persona.result.ConversationStyleResult;

public interface GetConversationStyleInputPort {
    ConversationStyleResult findById(Long id);
}
