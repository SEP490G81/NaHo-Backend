package org.naho.persona.mapper;

import org.naho.persona.model.ConversationStyle;
import org.naho.persona.result.ConversationStyleResult;

public class ConversationStyleMapper {
    public ConversationStyleResult domainToResult(ConversationStyle domain) {
        if (domain == null) {
            return null;
        }

        return ConversationStyleResult.builder()
                .id(domain.getId())
                .description(domain.getDescription())
                .prompt(domain.getPrompt())
                .formalityLevel(domain.getFormalityLevel())
                .marugotoLevel(domain.getMarugotoLevel())
                .build();
    }
}
