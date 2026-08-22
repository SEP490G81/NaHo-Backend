package org.naho.persona.result;

import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;

public record ConversationStyleResult(
        Long id,
        String description,
        String prompt,
        FormalityLevel formalityLevel,
        MarugotoLevel marugotoLevel
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Long id;
        private String description;
        private String prompt;
        private FormalityLevel formalityLevel;
        private MarugotoLevel marugotoLevel;

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

        public Builder marugotoLevel(MarugotoLevel marugotoLevel) {
            this.marugotoLevel = marugotoLevel;
            return this;
        }

        public ConversationStyleResult build() {
            return new ConversationStyleResult(
                    id,
                    description,
                    prompt,
                    formalityLevel,
                    marugotoLevel
            );
        }
    }
}
