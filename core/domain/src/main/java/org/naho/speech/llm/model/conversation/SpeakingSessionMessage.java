package org.naho.speech.llm.model.conversation;

import org.naho.speech.llm.type.MessageType;
import org.naho.speech.llm.type.SenderType;

public class SpeakingSessionMessage {

    private Long id;
    private Long sessionId;
    private Long audioFileId;

    private int turnIndex;
    private SenderType senderType;
    private MessageType messageType;
    private String content;
    private String contentTranslation;
    private String correctedText;
    private String correctionExplanation;
    private String grammarNote;
    private String hintForLearner;
    private Double pronunciationScore;

    private SpeakingSessionMessage(Builder builder) {
        this.id = builder.id;
        this.sessionId = builder.sessionId;
        this.audioFileId = builder.audioFileId;

        this.turnIndex = builder.turnIndex;
        this.senderType = builder.senderType;
        this.messageType = builder.messageType;
        this.content = builder.content;
        this.contentTranslation = builder.contentTranslation;
        this.correctedText = builder.correctedText;
        this.correctionExplanation = builder.correctionExplanation;
        this.grammarNote = builder.grammarNote;
        this.hintForLearner = builder.hintForLearner;
        this.pronunciationScore = builder.pronunciationScore;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public Long getAudioFileId() {
        return audioFileId;
    }

    public int getTurnIndex() {
        return turnIndex;
    }

    public SenderType getSenderType() {
        return senderType;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getContent() {
        return content;
    }

    public String getContentTranslation() {
        return contentTranslation;
    }

    public String getCorrectedText() {
        return correctedText;
    }

    public String getCorrectionExplanation() {
        return correctionExplanation;
    }

    public String getGrammarNote() {
        return grammarNote;
    }

    public String getHintForLearner() {
        return hintForLearner;
    }

    public Double getPronunciationScore() {
        return pronunciationScore;
    }

    public static class Builder {

        private Long id;
        private Long sessionId;
        private Long audioFileId;

        private int turnIndex;
        private SenderType senderType;
        private MessageType messageType;
        private String content;
        private String contentTranslation;
        private String correctedText;
        private String correctionExplanation;
        private String grammarNote;
        private String hintForLearner;
        private Double pronunciationScore;

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

        public Builder turnIndex(int turnIndex) {
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

        public SpeakingSessionMessage build() {
            return new SpeakingSessionMessage(this);
        }
    }
}
