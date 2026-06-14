package org.naho.topic.model;

import org.naho.i18n.message.speech.TopicDetailMessageKey;
import org.naho.shared.exception.DomainException;
import org.naho.topic.exception.TopicDomainErrorCode;
import org.naho.topic.type.TopicStatus;
import org.naho.user.type.JLPTLevel;

public class Topic {
    private final Long id;
    private final Long userId;
    private Long coverImageFileId;
    private String japaneseName;
    private String description;
    private String japaneseNameTokens;
    private String japaneseDescriptionTokens;
    private TopicStatus status;
    private JLPTLevel jlptLevel;
    private Double orderIndex;


    // Private constructor dùng cho Builder
    private Topic(Builder builder) {
        this.id = builder.id;
        this.coverImageFileId = builder.coverImageFileId;
        this.userId = builder.userId;
        this.japaneseName = builder.japaneseName;
        this.description = builder.description;
        this.japaneseNameTokens = builder.japaneseNameTokens;
        this.japaneseDescriptionTokens = builder.japaneseDescriptionTokens;
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

    private void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new DomainException(
                    TopicDomainErrorCode.TOPIC_DESCRIPTION_EMPTY,
                    TopicDetailMessageKey.TOPIC_DESCRIPTION_EMPTY
            );
        }
    }

    public void update(String japaneseName, String description, String japaneseNameTokens, String japaneseDescriptionTokens, TopicStatus status, JLPTLevel jlptLevel, Double orderIndex, Long coverImageFileId) {
        validateJapaneseName(japaneseName);
        validateDescription(description);

        this.japaneseName = japaneseName;
        this.description = description;
        this.japaneseNameTokens = japaneseNameTokens;
        this.japaneseDescriptionTokens = japaneseDescriptionTokens;
        this.status = status;
        this.jlptLevel = jlptLevel;
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

    public String getDescription() {
        return description;
    }

    public String getJapaneseNameTokens() {
        return japaneseNameTokens;
    }

    public String getJapaneseDescriptionTokens() {
        return japaneseDescriptionTokens;
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
        private String japaneseName;
        private String description;
        private String japaneseNameTokens;
        private String japaneseDescriptionTokens;
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

        public Builder japaneseName(String japaneseName) {
            this.japaneseName = japaneseName;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder japaneseNameTokens(String japaneseNameTokens) {
            this.japaneseNameTokens = japaneseNameTokens;
            return this;
        }

        public Builder japaneseDescriptionTokens(String japaneseDescriptionTokens) {
            this.japaneseDescriptionTokens = japaneseDescriptionTokens;
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
            topic.validateDescription(topic.getDescription());
            return topic;
        }
    }
}