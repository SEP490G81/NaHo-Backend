package org.naho.persona.model;

import org.naho.i18n.message.persona.ConversationStyleDetailMessageKey;
import org.naho.persona.exception.ConversationStyleDomainErrorCode;
import org.naho.persona.type.FormalityLevel;
import org.naho.shared.exception.DomainException;

public class ConversationStyle {

    private final Long id;
    private final String description;
    private final String prompt;
    private final FormalityLevel formalityLevel;

    private ConversationStyle(Builder builder) {
        this.id = builder.id;
        this.description = builder.description;
        this.prompt = builder.prompt;
        this.formalityLevel = builder.formalityLevel;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getPrompt() {
        return prompt;
    }

    public FormalityLevel getFormalityLevel() {
        return formalityLevel;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Long id;
        private String description;
        private String prompt;
        private FormalityLevel formalityLevel;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder prompt(String prompt) {
            this.prompt = prompt;
            return this;
        }

        public Builder formalityLevel(FormalityLevel formalityLevel) {
            this.formalityLevel = formalityLevel;
            return this;
        }

        public ConversationStyle build() {
            if (prompt == null || prompt.isBlank()) {
                throw new DomainException(
                        ConversationStyleDomainErrorCode.CONVERSATION_STYLE_PROMPT_NOT_VALID,
                        ConversationStyleDetailMessageKey.CONVERSATION_STYLE_PROMPT_BLANK
                );
            }
            
            if (formalityLevel == null) {
                throw new DomainException(
                        ConversationStyleDomainErrorCode.CONVERSATION_STYLE_FORMALITY_LEVEL_NOT_VALID,
                        ConversationStyleDetailMessageKey.CONVERSATION_STYLE_FORMALITY_LEVEL_BLANK
                );
            }

            return new ConversationStyle(this);
        }
    }
}