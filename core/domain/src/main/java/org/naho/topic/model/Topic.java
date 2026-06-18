package org.naho.topic.model;


import org.naho.i18n.message.speech.TopicDetailMessageKey;
import org.naho.shared.exception.DomainException;
import org.naho.topic.exception.TopicDomainErrorCode;
import org.naho.topic.type.TopicStatus;
import org.naho.user.type.JLPTLevel;

public class Topic {
    private final Long id;
    private final Long userId;
    private Long categoryId;
    private Long coverImageFileId;
    private String japaneseName;
    private String japaneseDescription;
    private String japaneseNameMarkup;
    private String japaneseDescriptionMarkup;
    private TopicStatus status;
    private JLPTLevel jlptLevel;
    private Double orderIndex;

    // Private constructor dùng cho Builder
    private Topic(Builder builder) {
        this.id = builder.id;
        this.coverImageFileId = builder.coverImageFileId;
        this.userId = builder.userId;
        this.categoryId = builder.categoryId;
        this.japaneseName = builder.japaneseName;
        this.japaneseDescription = builder.japaneseDescription;
        this.japaneseNameMarkup = builder.japaneseNameMarkup;
        this.japaneseDescriptionMarkup = builder.japaneseDescriptionMarkup;
        this.status = builder.status;
        this.jlptLevel = builder.jlptLevel;
        this.orderIndex = builder.orderIndex;
    }

    public static Builder builder() {
        return new Builder();
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
                       JLPTLevel jlptLevel,
                       Double orderIndex,
                       Long coverImageFileId,
                       Long categoryId
    ) {
        validateJapaneseName(japaneseName);
        validateJapaneseDescription(japaneseDescription);

        this.japaneseName = japaneseName;
        this.japaneseDescription = japaneseDescription;
        this.japaneseNameMarkup = japaneseNameMarkup;
        this.japaneseDescriptionMarkup = japaneseDescriptionMarkup;
        this.status = status;
        this.jlptLevel = jlptLevel;
        this.orderIndex = orderIndex;
        this.coverImageFileId = coverImageFileId;
        this.categoryId = categoryId;
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

    public Long getCategoryId() {
        return categoryId;
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

    public JLPTLevel getJlptLevel() {
        return jlptLevel;
    }

    public Double getOrderIndex() {
        return orderIndex;
    }

    // Builder
    public static class Builder {
        private Long id;
        private Long coverImageFileId;
        private Long userId;
        private Long categoryId;
        private String japaneseName;
        private String japaneseDescription;
        private String japaneseNameMarkup;
        private String japaneseDescriptionMarkup;
        private TopicStatus status;
        private JLPTLevel jlptLevel;
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

        public Builder categoryId(Long categoryId) {
            this.categoryId = categoryId;
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

        public Builder jlptLevel(JLPTLevel jlptLevel) {
            this.jlptLevel = jlptLevel;
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
            return topic;
        }
    }
}