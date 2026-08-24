package org.naho.speech.llm.conversation.internal;

import java.util.List;

public record ParsedAiReply(
        String reply,
        String replyTranslation,
        String grammarNote,
        String correctedUserText,
        String correctionExplanation,
        String hintForLearner,
        List<String> suggestedReplies
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String reply;
        private String replyTranslation;
        private String grammarNote;
        private String correctedUserText;
        private String correctionExplanation;
        private String hintForLearner;
        private List<String> suggestedReplies;

        public Builder reply(String reply) {
            this.reply = reply;
            return this;
        }

        public Builder replyTranslation(String replyTranslation) {
            this.replyTranslation = replyTranslation;
            return this;
        }

        public Builder grammarNote(String grammarNote) {
            this.grammarNote = grammarNote;
            return this;
        }

        public Builder correctedUserText(String correctedUserText) {
            this.correctedUserText = correctedUserText;
            return this;
        }

        public Builder correctionExplanation(String correctionExplanation) {
            this.correctionExplanation = correctionExplanation;
            return this;
        }

        public Builder hintForLearner(String hintForLearner) {
            this.hintForLearner = hintForLearner;
            return this;
        }

        public Builder suggestedReplies(List<String> suggestedReplies) {
            this.suggestedReplies = suggestedReplies;
            return this;
        }

        public ParsedAiReply build() {
            return new ParsedAiReply(
                    reply,
                    replyTranslation,
                    grammarNote,
                    correctedUserText,
                    correctionExplanation,
                    hintForLearner,
                    suggestedReplies
            );
        }
    }
}
