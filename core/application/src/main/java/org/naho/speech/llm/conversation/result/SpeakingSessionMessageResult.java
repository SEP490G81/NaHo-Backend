package org.naho.speech.llm.conversation.result;

import org.naho.speech.llm.type.MessageType;
import org.naho.speech.llm.type.SenderType;

import java.util.List;

public record SpeakingSessionMessageResult(
        Long id,
        Long sessionId,
        Long audioFileId,

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
        String aiReplyAudio,
        String userRecordAudio,
        List<String> suggestedReplies
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Long id;
        private Long sessionId;
        private Long audioFileId;

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
        private String aiReplyAudio;
        private String userRecordAudio;
        private List<String> suggestedReplies;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder sessionId(Long sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder audioFileId(Long audioFileId) {
            this.audioFileId = audioFileId;
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

        public Builder aiReplyAudio(String aiReplyAudio) {
            this.aiReplyAudio = aiReplyAudio;
            return this;
        }

        public Builder userRecordAudio(String userRecordAudio) {
            this.userRecordAudio = userRecordAudio;
            return this;
        }

        public Builder suggestedReplies(List<String> suggestedReplies) {
            this.suggestedReplies = suggestedReplies;
            return this;
        }

        public SpeakingSessionMessageResult build() {
            return new SpeakingSessionMessageResult(
                    id,
                    sessionId,
                    audioFileId,
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
                    aiReplyAudio,
                    userRecordAudio,
                    suggestedReplies
            );
        }
    }
}
