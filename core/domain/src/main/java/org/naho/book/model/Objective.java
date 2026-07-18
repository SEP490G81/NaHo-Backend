package org.naho.book.model;

import org.naho.book.exception.ObjectiveDomainErrorCode;
import org.naho.book.type.TopicStatus;
import org.naho.i18n.message.book.ObjectiveDetailMessageKey;
import org.naho.shared.exception.DomainException;

public class Objective {
    private final Long id;
    private final Long lessonId;
    private final Double firstNodeGlobalOrderIndex;
    private final Double lastNodeGlobalOrderIndex;
    private String japaneseName;
    private String japaneseDescription;
    private String japaneseNameMarkup;
    private String japaneseDescriptionMarkup;
    private TopicStatus status;
    private Double orderIndex;

    private Objective(Builder builder) {
        this.id = builder.id;
        this.lessonId = builder.lessonId;
        this.japaneseName = builder.japaneseName;
        this.japaneseDescription = builder.japaneseDescription;
        this.japaneseNameMarkup = builder.japaneseNameMarkup;
        this.japaneseDescriptionMarkup = builder.japaneseDescriptionMarkup;
        this.status = builder.status;
        this.orderIndex = builder.orderIndex;
        this.firstNodeGlobalOrderIndex = builder.firstNodeGlobalOrderIndex;
        this.lastNodeGlobalOrderIndex = builder.lastNodeGlobalOrderIndex;
    }

    public static Builder builder() {
        return new Builder();
    }

    private void validateOrderIndex(Double orderIndex) {
        if (orderIndex == null) {
            throw new DomainException(
                    ObjectiveDomainErrorCode.OBJECTIVE_ORDER_INDEX_EMPTY,
                    ObjectiveDetailMessageKey.OBJECTIVE_ORDER_INDEX_EMPTY
            );
        }
    }

    public void update(String japaneseName,
                       String japaneseDescription,
                       String japaneseNameMarkup,
                       String japaneseDescriptionMarkup,
                       TopicStatus status,
                       Double orderIndex) {
        validateOrderIndex(orderIndex);
        this.japaneseName = japaneseName;
        this.japaneseDescription = japaneseDescription;
        this.japaneseNameMarkup = japaneseNameMarkup;
        this.japaneseDescriptionMarkup = japaneseDescriptionMarkup;
        this.status = status;
        this.orderIndex = orderIndex;
    }

    public Long getId() {
        return id;
    }

    public Long getLessonId() {
        return lessonId;
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

    public Double getFirstNodeGlobalOrderIndex() {
        return firstNodeGlobalOrderIndex;
    }

    public Double getLastNodeGlobalOrderIndex() {
        return lastNodeGlobalOrderIndex;
    }

    public static class Builder {
        private Long id;
        private Long lessonId;
        private String japaneseName;
        private String japaneseDescription;
        private String japaneseNameMarkup;
        private String japaneseDescriptionMarkup;
        private TopicStatus status;
        private Double orderIndex;
        private Double firstNodeGlobalOrderIndex;
        private Double lastNodeGlobalOrderIndex;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder lessonId(Long lessonId) {
            this.lessonId = lessonId;
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

        public Builder firstNodeGlobalOrderIndex(Double firstNodeGlobalOrderIndex) {
            this.firstNodeGlobalOrderIndex = firstNodeGlobalOrderIndex;
            return this;
        }

        public Builder lastNodeGlobalOrderIndex(Double lastNodeGlobalOrderIndex) {
            this.lastNodeGlobalOrderIndex = lastNodeGlobalOrderIndex;
            return this;
        }

        public Objective build() {
            Objective objective = new Objective(this);
            objective.validateOrderIndex(objective.getOrderIndex());
            return objective;
        }
    }
}
