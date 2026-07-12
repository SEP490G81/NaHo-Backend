package org.naho.question.model;

import org.naho.i18n.message.question.SpeakingQuestionDetailMessageKey;
import org.naho.question.exception.SpeakingQuestionDomainErrorCode;
import org.naho.question.type.QuestionStatus;
import org.naho.shared.exception.DomainException;

import java.util.List;

public class SpeakingQuestion {
    private final Long id;
    private final Long userId;
    private final List<Grammar> grammars;
    private final List<Vocabulary> vocabularies;
    private Long questionAudioFileId;
    private String title;
    private String titleMarkup;
    private String description;
    private String descriptionMarkup;
    private QuestionStatus status;
    private Double orderIndex;
    private Long objectiveId;

    // Private constructor dùng cho Builder
    private SpeakingQuestion(Builder builder) {
        this.id = builder.id;
        this.questionAudioFileId = builder.questionAudioFileId;
        this.userId = builder.userId;
        this.title = builder.title;
        this.titleMarkup = builder.titleMarkup;
        this.description = builder.description;
        this.descriptionMarkup = builder.descriptionMarkup;
        this.status = builder.status;
        this.grammars = builder.grammars;
        this.vocabularies = builder.vocabularies;
        this.orderIndex = builder.orderIndex;
        this.objectiveId = builder.objectiveId;
    }

    public static Builder builder() {
        return new Builder();
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new DomainException(
                    SpeakingQuestionDomainErrorCode.SPEAKING_QUESTION_TITLE_EMPTY,
                    SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_TITLE_EMPTY
            );
        }
    }

    private void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new DomainException(
                    SpeakingQuestionDomainErrorCode.SPEAKING_QUESTION_DESCRIPTION_EMPTY,
                    SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_DESCRIPTION_EMPTY
            );
        }
    }

    public void update(String title,
                       String description,
                       String titleMarkup,
                       String descriptionMarkup,
                       Long questionAudioFileId) {
        validateTitle(title);
        validateDescription(description);

        this.title = title;
        this.description = description;
        this.titleMarkup = titleMarkup;
        this.descriptionMarkup = descriptionMarkup;
        this.questionAudioFileId = questionAudioFileId;
    }

    public void changeStatus(QuestionStatus newStatus) {
        this.status = newStatus;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getQuestionAudioFileId() {
        return questionAudioFileId;
    }



    public Long getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getTitleMarkup() {
        return titleMarkup;
    }

    public String getDescription() {
        return description;
    }

    public String getDescriptionMarkup() {
        return descriptionMarkup;
    }
    public Double getOrderIndex() {
        return orderIndex;
    }

    public Long getObjectiveId() {
        return objectiveId;
    }

    public QuestionStatus getStatus() {
        return status;
    }

    public List<Grammar> getGrammars() {
        return grammars;
    }

    public List<Vocabulary> getVocabularies() {
        return vocabularies;
    }

    // Builder
    public static class Builder {
        private Long id;
        private Long questionAudioFileId;
        private Long userId;
        private String title;
        private String titleMarkup;
        private String description;
        private String descriptionMarkup;
        private QuestionStatus status;
        private List<Grammar> grammars;
        private List<Vocabulary> vocabularies;
        private Double orderIndex;
        private Long objectiveId;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder questionAudioFileId(Long questionAudioFileId) {
            this.questionAudioFileId = questionAudioFileId;
            return this;
        }



        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder titleMarkup(String titleMarkup) {
            this.titleMarkup = titleMarkup;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder descriptionMarkup(String descriptionMarkup) {
            this.descriptionMarkup = descriptionMarkup;
            return this;
        }



        public Builder status(QuestionStatus status) {
            this.status = status;
            return this;
        }

        public Builder grammars(List<Grammar> grammars) {
            this.grammars = grammars;
            return this;
        }

        public Builder vocabularies(List<Vocabulary> vocabularies) {
            this.vocabularies = vocabularies;
            return this;
        }

        public Builder orderIndex(Double orderIndex) {
            this.orderIndex = orderIndex;
            return this;
        }

        public Builder objectiveId(Long objectiveId) {
            this.objectiveId = objectiveId;
            return this;
        }

        public SpeakingQuestion build() {
            SpeakingQuestion speakingQuestion = new SpeakingQuestion(this);
            speakingQuestion.validateTitle(speakingQuestion.getTitle());
            speakingQuestion.validateDescription(speakingQuestion.getDescription());
            return speakingQuestion;
        }
    }
}