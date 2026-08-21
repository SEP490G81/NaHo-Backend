package org.naho.persona.model;

import org.naho.i18n.message.persona.PersonaDetailMessageKey;
import org.naho.persona.exception.PersonaDomainErrorCode;
import org.naho.persona.type.PersonaStatus;
import org.naho.shared.exception.DomainException;
import org.naho.user.type.Gender;

public class Persona {

    private final Long id;
    private final Long avatarFileId;
    private final Long suggestedConversationStyleId;
    private final String name;
    private final String prompt;
    private final PersonaStatus status;
    private final String voiceName;
    private final Gender gender;

    private Persona(Builder builder) {
        this.id = builder.id;
        this.avatarFileId = builder.avatarFileId;
        this.suggestedConversationStyleId = builder.suggestedConversationStyleId;
        this.name = builder.name;
        this.prompt = builder.prompt;
        this.status = builder.status != null ? builder.status : PersonaStatus.ACTIVE;
        this.voiceName = builder.voiceName;
        this.gender = builder.gender;
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

    public String getName() {
        return name;
    }

    public String getPrompt() {
        return prompt;
    }

    public PersonaStatus getStatus() {
        return status;
    }

    public String getVoiceName() {
        return voiceName;
    }

    public Gender getGender() {
        return gender;
    }

    public static class Builder {

        private Long id;
        private Long avatarFileId;
        private Long suggestedConversationStyleId;
        private String name;
        private String prompt;
        private PersonaStatus status;
        private String voiceName;
        private Gender gender;

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

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder prompt(String prompt) {
            this.prompt = prompt;
            return this;
        }

        public Builder status(PersonaStatus status) {
            this.status = status;
            return this;
        }

        public Builder voiceName(String voiceName) {
            this.voiceName = voiceName;
            return this;
        }

        public Builder gender(Gender gender) {
            this.gender = gender;
            return this;
        }

        public Persona build() {

            if (name == null || name.isBlank()) {
                throw new DomainException(
                        PersonaDomainErrorCode.PERSONA_NAME_NOT_VALID,
                        PersonaDetailMessageKey.PERSONA_NAME_BLANK);
            }

            if (prompt == null || prompt.isBlank()) {
                throw new DomainException(
                        PersonaDomainErrorCode.PERSONA_PROMPT_NOT_VALID,
                        PersonaDetailMessageKey.PERSONA_PROMPT_BLANK);
            }

            return new Persona(this);
        }
    }
}