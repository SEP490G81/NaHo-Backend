package org.naho.persona.usecase;

import org.naho.i18n.message.persona.ConversationStyleDetailMessageKey;
import org.naho.i18n.message.persona.PersonaDetailMessageKey;
import org.naho.persona.exception.PersonaErrorCode;
import org.naho.persona.mapper.ConversationStyleMapper;
import org.naho.persona.model.ConversationStyle;
import org.naho.persona.port.in.GetConversationStyleInputPort;
import org.naho.persona.port.out.ConversationStyleRepositoryPort;
import org.naho.persona.result.ConversationStyleResult;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.exception.CommonErrorCode;

public class GetConversationStyleUseCase implements GetConversationStyleInputPort {
    private final ConversationStyleRepositoryPort conversationStyleRepositoryPort;
    private final ConversationStyleMapper conversationStyleMapper;

    public GetConversationStyleUseCase(
            ConversationStyleRepositoryPort conversationStyleRepositoryPort,
            ConversationStyleMapper conversationStyleMapper
    ) {
        this.conversationStyleRepositoryPort = conversationStyleRepositoryPort;
        this.conversationStyleMapper = conversationStyleMapper;
    }

    @Override
    public ConversationStyleResult findById(Long id) {
        if (id == null) {
            throw new ApplicationException(
                    CommonErrorCode.COMMON_INVALID_REQUEST,
                    ConversationStyleDetailMessageKey.CONVERSATION_STYLE_ID_NULL
            );
        }

        ConversationStyle conversationStyle = conversationStyleRepositoryPort.findById(id)
                .orElseThrow(() -> new ApplicationException(
                        PersonaErrorCode.PERSONA_NOT_FOUND,
                        PersonaDetailMessageKey.PERSONA_NOT_FOUND,
                        id
                ));

        return conversationStyleMapper.domainToResult(conversationStyle);
    }
}
