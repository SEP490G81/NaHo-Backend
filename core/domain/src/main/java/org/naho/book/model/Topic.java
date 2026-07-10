package org.naho.book.model;

import org.naho.book.exception.TopicDomainErrorCode;
import org.naho.book.type.TopicStatus;
import org.naho.i18n.message.book.TopicDetailMessageKey;
import org.naho.shared.exception.DomainException;

public class Topic {
    private final Long id;
    private final Long userId;
    private Long coverImageFileId;
    private final Long bookId;

    private String japaneseName;
    private String japaneseDescription;
    private String japaneseNameMarkup;
    private String japaneseDescriptionMarkup;
    private TopicStatus status;
    private Double orderIndex;

    // Private constructor dùng cho Builder
    private Topic(Builder builder) {
        this.id = builder.id;
        this.coverImageFileId = builder.coverImageFileId;
        this.userId = builder.userId;
        this.bookId = builder.bookId;
        this.japaneseName = builder.japaneseName;
        this.japaneseDescription = builder.japaneseDescription;
        this.japaneseNameMarkup = builder.japaneseNameMarkup;
        this.japaneseDescriptionMarkup = builder.japaneseDescriptionMarkup;
        this.status = builder.status;
        this.orderIndex = builder.orderIndex;
    }

    public static Builder builder() {
        return new Builder();
    }

    private void validateBookId(Long bookId){
        if(bookId == null){
            throw new DomainException(
                    TopicDomainErrorCode.BOOK_ID_EMPTY,
                    TopicDetailMessageKey.BOOK_ID_EMPTY
            );
        }
    }

    private void validateOrderIndex(Double orderIndex) {
        if (orderIndex == null) {
            throw new DomainException(
                    TopicDomainErrorCode.TOPIC_ORDER_INDEX_EMPTY,
                    TopicDetailMessageKey.TOPIC_ORDER_INDEX_EMPTY
            );
        }
    }

    private void validateJapaneseName(String japaneseName) {
        if (japaneseName == null || japaneseName.isBlank()) {
            throw new DomainException(
                    TopicDomainErrorCode.TOPIC_NAME_EMPTY,
                    TopicDetailMessageKey.TOPIC_NAME_EMPTY
            );
        }
    }

    private void validateJapaneseDescription(String japaneseDescription) {
        if (japaneseDescription == null || japaneseDescription.isBlank()) {
            throw new DomainException(
                    TopicDomainErrorCode.TOPIC_DESCRIPTION_EMPTY,
                    TopicDetailMessageKey.TOPIC_DESCRIPTION_EMPTY
            );
        }
    }

    public void update(String japaneseName,
                       String japaneseDescription,
                       String japaneseNameMarkup,
                       String japaneseDescriptionMarkup,
                       TopicStatus status,
                       Double orderIndex,
                       Long coverImageFileId
    ) {
        validateJapaneseName(japaneseName);
        validateJapaneseDescription(japaneseDescription);
        validateBookId(this.bookId);
        validateOrderIndex(orderIndex);

        this.japaneseName = japaneseName;
        this.japaneseDescription = japaneseDescription;
        this.japaneseNameMarkup = japaneseNameMarkup;
        this.japaneseDescriptionMarkup = japaneseDescriptionMarkup;
        this.status = status;
        this.orderIndex = orderIndex;
        this.coverImageFileId = coverImageFileId;
    }

    // Getter
    public Long getId() {
        return id;
    }

    public Long getCoverImageFileId() {
        return coverImageFileId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getJapaneseName() {
        return japaneseName;
    }

    public String getJapaneseDescription() {
        return japaneseDescription;
    }

    public String getJapaneseNameMarkup() {
        return japaneseNameMarkup;
    }

    public String getJapaneseDescriptionMarkup() {
        return japaneseDescriptionMarkup;
    }

    public TopicStatus getStatus() {
        return status;
    }

    public Double getOrderIndex() {
        return orderIndex;
    }

    public Long getBookId() {
        return bookId;
    }

    // Builder
    public static class Builder {
        private Long id;
        private Long coverImageFileId;
        private Long userId;
        private Long bookId;
        private String japaneseName;
        private String japaneseDescription;
        private String japaneseNameMarkup;
        private String japaneseDescriptionMarkup;
        private TopicStatus status;
        private Double orderIndex;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder coverImageFileId(Long coverImageFileId) {
            this.coverImageFileId = coverImageFileId;
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

        public Builder japaneseName(String japaneseName) {
            this.japaneseName = japaneseName;
            return this;
        }

        public Builder japaneseDescription(String japaneseDescription) {
            this.japaneseDescription = japaneseDescription;
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

        public Topic build() {
            Topic topic = new Topic(this);
            topic.validateJapaneseName(topic.getJapaneseName());
            topic.validateJapaneseDescription(topic.getJapaneseDescription());
            topic.validateBookId(topic.getBookId());
            topic.validateOrderIndex(topic.getOrderIndex());
            return topic;
        }
    }
}