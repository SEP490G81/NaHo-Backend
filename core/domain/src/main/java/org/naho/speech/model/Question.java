package org.naho.speech.model;

import org.naho.speech.type.QuestionStatus;

public class Question {
    private final Long id;
    private final Long questionAudioFileId;
    private final Long topicId;
    private final String questionText;
    private final String contextualHint;
    private final Integer orderIndex;
    private final QuestionStatus status;

    // Private constructor dùng cho Builder
    private Question(Builder builder) {
        this.id = builder.id;
        this.questionAudioFileId = builder.questionAudioFileId;
        this.topicId = builder.topicId;
        this.questionText = builder.questionText;
        this.contextualHint = builder.contextualHint;
        this.orderIndex = builder.orderIndex;
        this.status = builder.status;
    }

    public static Builder builder() {
        return new Builder();
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

    public QuestionStatus getStatus() {
        return status;
    }

    // Builder
    public static class Builder {
        private Long id;
        private Long questionAudioFileId;
        private Long topicId;
        private String questionText;
        private String contextualHint;
        private Integer orderIndex;
        private QuestionStatus status;

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

        public Builder status(QuestionStatus status) {
            this.status = status;
            return this;
        }

        public Question build() {
            return new Question(this);
        }
    }
}