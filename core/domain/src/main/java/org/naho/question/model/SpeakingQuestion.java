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
    private Long speakingQuestionAudioFileId;
    private String japaneseName;
    private String japaneseNameMarkup;
    private String vietnameseName;
    private String description;
    private String descriptionMarkup;
    private String japaneseSampleAnswer;
    private String japaneseSampleAnswerMarkup;
    private String vietnameseSampleAnswer;
    private String englishSampleAnswer;
    private QuestionStatus status;

    // Private constructor dùng cho Builder
    private SpeakingQuestion(Builder builder) {
        this.id = builder.id;
        this.speakingQuestionAudioFileId = builder.speakingQuestionAudioFileId;
        this.userId = builder.userId;
        this.japaneseName = builder.japaneseName;
        this.japaneseNameMarkup = builder.japaneseNameMarkup;
        this.vietnameseName = builder.vietnameseName;
        this.description = builder.description;
        this.descriptionMarkup = builder.descriptionMarkup;
        this.japaneseSampleAnswer = builder.japaneseSampleAnswer;
        this.japaneseSampleAnswerMarkup = builder.japaneseSampleAnswerMarkup;
        this.vietnameseSampleAnswer = builder.vietnameseSampleAnswer;
        this.englishSampleAnswer = builder.englishSampleAnswer;
        this.status = builder.status;
        this.grammars = builder.grammars;
        this.vocabularies = builder.vocabularies;
    }

    public static Builder builder() {
        return new Builder();
    }

    private void validateJapaneseName(String japaneseName) {
        if (japaneseName == null || japaneseName.isBlank()) {
            throw new DomainException(
                    SpeakingQuestionDomainErrorCode.SPEAKING_QUESTION_TITLE_EMPTY,
                    SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_TITLE_EMPTY);
        }
    }

    private void validateDescription(String description) {
        // if (description == null || description.isBlank()) {
        // throw new DomainException(
        // SpeakingQuestionDomainErrorCode.SPEAKING_QUESTION_DESCRIPTION_EMPTY,
        // SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_DESCRIPTION_EMPTY
        // );
        // }
    }

    public void update(
            String japaneseName,
            String japaneseNameMarkup,
            String vietnameseName,
            String description,
            String descriptionMarkup,
            String japaneseSampleAnswer,
            String japaneseSampleAnswerMarkup,
            String vietnameseSampleAnswer,
            String englishSampleAnswer,
            Long speakingQuestionAudioFileId,
            List<Vocabulary> vocabularies,
            List<Grammar> grammars
    ) {
        validateJapaneseName(japaneseName);
        validateDescription(description);

        this.japaneseName = japaneseName;
        this.japaneseNameMarkup = japaneseNameMarkup;
        this.vietnameseName = vietnameseName;
        this.description = description;
        this.descriptionMarkup = descriptionMarkup;
        this.japaneseSampleAnswer = japaneseSampleAnswer;
        this.japaneseSampleAnswerMarkup = japaneseSampleAnswerMarkup;
        this.vietnameseSampleAnswer = vietnameseSampleAnswer;
        this.englishSampleAnswer = englishSampleAnswer;
        this.speakingQuestionAudioFileId = speakingQuestionAudioFileId;

        if (vocabularies != null) {
            this.vocabularies.clear();
            this.vocabularies.addAll(vocabularies);
        }

        if (grammars != null) {
            this.grammars.clear();
            this.grammars.addAll(grammars);
        }
    }

    public void changeStatus(QuestionStatus status) {
        this.status = status;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getSpeakingQuestionAudioFileId() {
        return speakingQuestionAudioFileId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getJapaneseName() {
        return japaneseName;
    }

    public String getJapaneseNameMarkup() {
        return japaneseNameMarkup;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }

    public String getDescription() {
        return description;
    }

    public String getDescriptionMarkup() {
        return descriptionMarkup;
    }

    public String getJapaneseSampleAnswer() {
        return japaneseSampleAnswer;
    }

    public String getJapaneseSampleAnswerMarkup() {
        return japaneseSampleAnswerMarkup;
    }

    public String getVietnameseSampleAnswer() {
        return vietnameseSampleAnswer;
    }

    public String getEnglishSampleAnswer() {
        return englishSampleAnswer;
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
        private Long speakingQuestionAudioFileId;
        private Long userId;
        private String japaneseName;
        private String japaneseNameMarkup;
        private String vietnameseName;
        private String description;
        private String descriptionMarkup;
        private String japaneseSampleAnswer;
        private String japaneseSampleAnswerMarkup;
        private String vietnameseSampleAnswer;
        private String englishSampleAnswer;
        private QuestionStatus status;
        private List<Grammar> grammars;
        private List<Vocabulary> vocabularies;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder speakingQuestionAudioFileId(Long speakingQuestionAudioFileId) {
            this.speakingQuestionAudioFileId = speakingQuestionAudioFileId;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder japaneseName(String japaneseName) {
            this.japaneseName = japaneseName;
            return this;
        }

        public Builder japaneseNameMarkup(String japaneseNameMarkup) {
            this.japaneseNameMarkup = japaneseNameMarkup;
            return this;
        }

        public Builder vietnameseName(String vietnameseName) {
            this.vietnameseName = vietnameseName;
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

        public Builder japaneseSampleAnswer(String japaneseSampleAnswer) {
            this.japaneseSampleAnswer = japaneseSampleAnswer;
            return this;
        }

        public Builder japaneseSampleAnswerMarkup(String japaneseSampleAnswerMarkup) {
            this.japaneseSampleAnswerMarkup = japaneseSampleAnswerMarkup;
            return this;
        }

        public Builder vietnameseSampleAnswer(String vietnameseSampleAnswer) {
            this.vietnameseSampleAnswer = vietnameseSampleAnswer;
            return this;
        }

        public Builder englishSampleAnswer(String englishSampleAnswer) {
            this.englishSampleAnswer = englishSampleAnswer;
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

        public SpeakingQuestion build() {
            SpeakingQuestion speakingQuestion = new SpeakingQuestion(this);
            speakingQuestion.validateJapaneseName(speakingQuestion.getJapaneseName());
            speakingQuestion.validateDescription(speakingQuestion.getDescription());
            return speakingQuestion;
        }
    }
}
