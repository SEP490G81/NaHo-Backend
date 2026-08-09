package org.naho.book.result;

import org.naho.book.type.TopicStatus;

public record TopicResult(
        Long id,
        Long userId,
        Long bookId,
        Long coverImageFileId,
        String japaneseName,
        String japaneseDescription,
        String vietnameseDescription,
        String englishDescription,
        String japaneseNameMarkup,
        String japaneseDescriptionMarkup,
        TopicStatus status,
        Double orderIndex,
        Double firstNodeGlobalOrderIndex,
        Double lastNodeGlobalOrderIndex
) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private Long userId;
        private Long bookId;
        private Long coverImageFileId;
        private String japaneseName;
        private String japaneseDescription;
        private String vietnameseDescription;
        private String englishDescription;
        private String japaneseNameMarkup;
        private String japaneseDescriptionMarkup;
        private TopicStatus status;
        private Double orderIndex;
        private Double firstNodeGlobalOrderIndex;
        private Double lastNodeGlobalOrderIndex;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder bookId(Long bookId) {
            this.bookId = bookId;
            return this;
        }

        public Builder coverImageFileId(Long coverImageFileId) {
            this.coverImageFileId = coverImageFileId;
            return this;
        }

        public Builder japaneseName(String japaneseName) {
            this.japaneseName = japaneseName;
            return this;
        }

        public Builder japaneseDescription(String japaneseDescription) {
            this.japaneseDescription = japaneseDescription;
            return this;
        }

        public Builder vietnameseDescription(String vietnameseDescription) {
            this.vietnameseDescription = vietnameseDescription;
            return this;
        }

        public Builder englishDescription(String englishDescription) {
            this.englishDescription = englishDescription;
            return this;
        }

        public Builder japaneseNameMarkup(String japaneseNameMarkup) {
            this.japaneseNameMarkup = japaneseNameMarkup;
            return this;
        }

        public Builder japaneseDescriptionMarkup(String japaneseDescriptionMarkup) {
            this.japaneseDescriptionMarkup = japaneseDescriptionMarkup;
            return this;
        }

        public Builder status(TopicStatus status) {
            this.status = status;
            return this;
        }

        public Builder orderIndex(Double orderIndex) {
            this.orderIndex = orderIndex;
            return this;
        }

        public Builder firstNodeGlobalOrderIndex(Double firstNodeGlobalOrderIndex) {
            this.firstNodeGlobalOrderIndex = firstNodeGlobalOrderIndex;
            return this;
        }

        public Builder lastNodeGlobalOrderIndex(Double lastNodeGlobalOrderIndex) {
            this.lastNodeGlobalOrderIndex = lastNodeGlobalOrderIndex;
            return this;
        }

        public TopicResult build() {
            return new TopicResult(
                    id,
                    userId,
                    bookId,
                    coverImageFileId,
                    japaneseName,
                    japaneseDescription,
                    vietnameseDescription,
                    englishDescription,
                    japaneseNameMarkup,
                    japaneseDescriptionMarkup,
                    status,
                    orderIndex,
                    firstNodeGlobalOrderIndex,
                    lastNodeGlobalOrderIndex
            );
        }
    }
}