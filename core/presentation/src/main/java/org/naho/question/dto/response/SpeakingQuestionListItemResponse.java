package org.naho.question.dto.response;

import org.naho.question.type.QuestionStatus;

public record SpeakingQuestionListItemResponse(
        Long id,
        Long userId,
        Long speakingQuestionAudioFileId,
        String japaneseName,
        String japaneseNameMarkup,
        String vietnameseName,
        String description,
        String descriptionMarkup,
        String japaneseSampleAnswer,
        String japaneseSampleAnswerMarkup,
        String vietnameseSampleAnswer,
        String englishSampleAnswer,
        QuestionStatus status
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Long id;
        private Long userId;
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

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder speakingQuestionAudioFileId(Long speakingQuestionAudioFileId) {
            this.speakingQuestionAudioFileId = speakingQuestionAudioFileId;
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

        public SpeakingQuestionListItemResponse build() {
            return new SpeakingQuestionListItemResponse(
                    id,
                    userId,
                    speakingQuestionAudioFileId,
                    japaneseName,
                    japaneseNameMarkup,
                    vietnameseName,
                    description,
                    descriptionMarkup,
                    japaneseSampleAnswer,
                    japaneseSampleAnswerMarkup,
                    vietnameseSampleAnswer,
                    englishSampleAnswer,
                    status
            );
        }
    }
}
