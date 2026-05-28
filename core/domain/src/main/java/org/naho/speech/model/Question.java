package org.naho.speech.model;

public class Question {
    private Long id;
    private Long questionAudioFileId;
    private Long topicId;
    private String questionText;
    private String contextualHint;
    private Integer orderIndex;

    // Private constructor dùng cho Builder
    private Question(Builder builder) {
        this.id = builder.id;
        this.questionAudioFileId = builder.questionAudioFileId;
        this.topicId = builder.topicId;
        this.questionText = builder.questionText;
        this.contextualHint = builder.contextualHint;
        this.orderIndex = builder.orderIndex;
    }

    // Getter
    public Long getId() {
        return id;
    }

    public Long getQuestionAudioFileId() {
        return questionAudioFileId;
    }

    public Long getTopicId() {
        return topicId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getContextualHint() {
        return contextualHint;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Builder
    public static class Builder {
        private Long id;
        private Long questionAudioFileId;
        private Long topicId;
        private String questionText;
        private String contextualHint;
        private Integer orderIndex;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder questionAudioFileId(Long questionAudioFileId) {
            this.questionAudioFileId = questionAudioFileId;
            return this;
        }

        public Builder topicId(Long topicId) {
            this.topicId = topicId;
            return this;
        }

        public Builder questionText(String questionText) {
            this.questionText = questionText;
            return this;
        }

        public Builder contextualHint(String contextualHint) {
            this.contextualHint = contextualHint;
            return this;
        }

        public Builder orderIndex(Integer orderIndex) {
            this.orderIndex = orderIndex;
            return this;
        }

        public Question build() {
            return new Question(this);
        }
    }
}