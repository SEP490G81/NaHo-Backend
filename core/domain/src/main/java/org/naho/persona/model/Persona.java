package org.naho.persona.model;

import org.naho.i18n.message.persona.PersonaDetailMessageKey;
import org.naho.persona.exception.PersonaDomainErrorCode;
import org.naho.shared.exception.DomainException;

public class Persona {

    private final Long id;
    private final Long avatarFileId;
    private final Long suggestedConversationStyleId;
    private final ConversationStyle conversationStyle;
    private final String name;
    private final String prompt;

    private Persona(Builder builder) {
        this.id = builder.id;
        this.avatarFileId = builder.avatarFileId;
        this.suggestedConversationStyleId = builder.suggestedConversationStyleId;
        this.conversationStyle = builder.conversationStyle;
        this.name = builder.name;
        this.prompt = builder.prompt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public Long getAvatarFileId() {
        return avatarFileId;
    }

    public Long getSuggestedConversationStyleId() {
        return suggestedConversationStyleId;
    }

    public ConversationStyle getConversationStyle() {
        return conversationStyle;
    }

    public String getName() {
        return name;
    }

    public String getPrompt() {
        return prompt;
    }

    public static class Builder {

        private Long id;
        private Long avatarFileId;
        private Long suggestedConversationStyleId;
        private ConversationStyle conversationStyle;
        private String name;
        private String prompt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder avatarFileId(Long avatarFileId) {
            this.avatarFileId = avatarFileId;
            return this;
        }

        public Builder suggestedConversationStyleId(Long suggestedConversationStyleId) {
            this.suggestedConversationStyleId = suggestedConversationStyleId;
            return this;
        }

        public Builder conversationStyle(ConversationStyle conversationStyle) {
            this.conversationStyle = conversationStyle;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder prompt(String prompt) {
            this.prompt = prompt;
            return this;
        }

        public Persona build() {

            if (name == null || name.isBlank()) {
                throw new DomainException(
                        PersonaDomainErrorCode.PERSONA_NAME_NOT_VALID,
                        PersonaDetailMessageKey.PERSONA_NAME_BLANK
                );
            }

            if (prompt == null || prompt.isBlank()) {
                throw new DomainException(
                        PersonaDomainErrorCode.PERSONA_PROMPT_NOT_VALID,
                        PersonaDetailMessageKey.PERSONA_PROMPT_BLANK
                );
            }

            return new Persona(this);
        }
    }
}