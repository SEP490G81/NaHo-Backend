package org.naho.speech.llm.conversation.command;

import org.naho.file.model.File;
import org.naho.speech.llm.type.MessageType;
import org.naho.speech.llm.type.SenderType;

import java.util.List;

public record SpeakingSessionMessageCommand(
        Long sessionId,
        Integer turnIndex,
        SenderType senderType,
        MessageType messageType,
        String content,
        String contentTranslation,
        String correctedText,
        String correctionExplanation,
        String grammarNote,
        String hintForLearner,
        Double pronunciationScore,
        File audioFile,
        List<String> suggestedReplies
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Long sessionId;
        private Integer turnIndex;
        private SenderType senderType;
        private MessageType messageType;
        private String content;
        private String contentTranslation;
        private String correctedText;
        private String correctionExplanation;
        private String grammarNote;
        private String hintForLearner;
        private Double pronunciationScore;
        private File audioFile;
        private List<String> suggestedReplies;

        public Builder sessionId(Long sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder turnIndex(Integer turnIndex) {
            this.turnIndex = turnIndex;
            return this;
        }

        public Builder senderType(SenderType senderType) {
            this.senderType = senderType;
            return this;
        }

        public Builder messageType(MessageType messageType) {
            this.messageType = messageType;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder contentTranslation(String contentTranslation) {
            this.contentTranslation = contentTranslation;
            return this;
        }

        public Builder correctedText(String correctedText) {
            this.correctedText = correctedText;
            return this;
        }

        public Builder correctionExplanation(String correctionExplanation) {
            this.correctionExplanation = correctionExplanation;
            return this;
        }

        public Builder grammarNote(String grammarNote) {
            this.grammarNote = grammarNote;
            return this;
        }

        public Builder hintForLearner(String hintForLearner) {
            this.hintForLearner = hintForLearner;
            return this;
        }

        public Builder pronunciationScore(Double pronunciationScore) {
            this.pronunciationScore = pronunciationScore;
            return this;
        }

        public Builder audioFile(File audioFile) {
            this.audioFile = audioFile;
            return this;
        }

        public Builder suggestedReplies(List<String> suggestedReplies) {
            this.suggestedReplies = suggestedReplies;
            return this;
        }

        public SpeakingSessionMessageCommand build() {
            return new SpeakingSessionMessageCommand(
                    sessionId,
                    turnIndex,
                    senderType,
                    messageType,
                    content,
                    contentTranslation,
                    correctedText,
                    correctionExplanation,
                    grammarNote,
                    hintForLearner,
                    pronunciationScore,
                    audioFile,
                    suggestedReplies
            );
        }
    }
}