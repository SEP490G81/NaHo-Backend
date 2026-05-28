package org.naho.speech.model;

import org.naho.speech.type.TopicStatus;
import org.naho.user.type.JLPTLevel;

public class Topic {
    private Long id;
    private Long coverImageFileId;
    private String name;
    private String description;
    private TopicStatus status;
    private JLPTLevel jlptLevel;
    private Integer orderIndex;

    // Private constructor dùng cho Builder
    private Topic(Builder builder) {
        this.id = builder.id;
        this.coverImageFileId = builder.coverImageFileId;
        this.name = builder.name;
        this.description = builder.description;
        this.status = builder.status;
        this.jlptLevel = builder.jlptLevel;
        this.orderIndex = builder.orderIndex;
    }

    // Getter
    public Long getId() {
        return id;
    }

    public Long getCoverImageFileId() {
        return coverImageFileId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TopicStatus getStatus() {
        return status;
    }

    public JLPTLevel getJlptLevel() {
        return jlptLevel;
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
        private Long coverImageFileId;
        private String name;
        private String description;
        private TopicStatus status;
        private JLPTLevel jlptLevel;
        private Integer orderIndex;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder coverImageFileId(Long coverImageFileId) {
            this.coverImageFileId = coverImageFileId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
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

        public Builder orderIndex(Integer orderIndex) {
            this.orderIndex = orderIndex;
            return this;
        }

        public Topic build() {
            return new Topic(this);
        }
    }
}