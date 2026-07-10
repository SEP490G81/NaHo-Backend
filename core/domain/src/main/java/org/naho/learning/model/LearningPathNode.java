package org.naho.learning.model;

import org.naho.i18n.message.learning.LearningPathNodeDetailMessageKey;
import org.naho.learning.exception.LearningPathNodeDomainErrorCode;
import org.naho.learning.type.NodeType;
import org.naho.shared.exception.DomainException;

public class LearningPathNode {

    private final Long id;
    private final Long objectiveId;
    private final Long speakingQuestionId;
    private final Long vocabularyQuestionId;
    private final Long chestId;

    private final Double globalOrderIndex;
    private final Double orderIndex;
    private NodeType nodeType;

    private LearningPathNode(Builder builder) {
        this.id = builder.id;
        this.objectiveId = builder.objectiveId;
        this.speakingQuestionId = builder.speakingQuestionId;
        this.vocabularyQuestionId = builder.vocabularyQuestionId;
        this.chestId = builder.chestId;
        this.globalOrderIndex = builder.globalOrderIndex;
        this.orderIndex = builder.orderIndex;
        this.nodeType = builder.nodeType;
    }

    public Long getId() {
        return id;
    }

    public Long getObjectiveId() {
        return objectiveId;
    }

    public Long getSpeakingQuestionId() {
        return speakingQuestionId;
    }

    public Long getVocabularyQuestionId() {
        return vocabularyQuestionId;
    }

    public Long getChestId() {
        return chestId;
    }

    public Double getGlobalOrderIndex() {
        return globalOrderIndex;
    }

    public Double getOrderIndex() {
        return orderIndex;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Long id;
        private Long objectiveId;
        private Long speakingQuestionId;
        private Long vocabularyQuestionId;
        private Long chestId;
        private Double globalOrderIndex;
        private Double orderIndex;
        private NodeType nodeType;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder objectiveId(Long objectiveId) {
            this.objectiveId = objectiveId;
            return this;
        }

        public Builder speakingQuestionId(Long speakingQuestionId) {
            this.speakingQuestionId = speakingQuestionId;
            return this;
        }

        public Builder vocabularyQuestionId(Long vocabularyQuestionId) {
            this.vocabularyQuestionId = vocabularyQuestionId;
            return this;
        }

        public Builder chestId(Long chestId) {
            this.chestId = chestId;
            return this;
        }

        public Builder globalOrderIndex(Double globalOrderIndex) {
            this.globalOrderIndex = globalOrderIndex;
            return this;
        }

        public Builder orderIndex(Double orderIndex) {
            this.orderIndex = orderIndex;
            return this;
        }

        public Builder nodeType(NodeType nodeType) {
            this.nodeType = nodeType;
            return this;
        }

        public LearningPathNode build() {
            if (objectiveId == null) {
                throw new DomainException(
                        LearningPathNodeDomainErrorCode.LEARNING_PATH_NODE_OBJECTIVE_EMPTY,
                        LearningPathNodeDetailMessageKey.LEARNING_PATH_NODE_OBJECTIVE_EMPTY
                );
            }

            if (globalOrderIndex == null) {
                throw new DomainException(
                        LearningPathNodeDomainErrorCode.LEARNING_PATH_NODE_GLOBAL_ORDER_INDEX_EMPTY,
                        LearningPathNodeDetailMessageKey.LEARNING_PATH_NODE_GLOBAL_ORDER_INDEX_EMPTY
                );
            }

            if (orderIndex == null) {
                throw new DomainException(
                        LearningPathNodeDomainErrorCode.LEARNING_PATH_NODE_ORDER_INDEX_EMPTY,
                        LearningPathNodeDetailMessageKey.LEARNING_PATH_NODE_ORDER_INDEX_EMPTY
                );
            }

            if (nodeType == null) {
                throw new DomainException(
                        LearningPathNodeDomainErrorCode.LEARNING_PATH_NODE_TYPE_EMPTY,
                        LearningPathNodeDetailMessageKey.LEARNING_PATH_NODE_TYPE_EMPTY
                );
            }

            if (speakingQuestionId == null && nodeType.equals(NodeType.SPEAKING_QUESTION)) {
                throw new DomainException(
                        LearningPathNodeDomainErrorCode.LEARNING_PATH_NODE_SPEAKING_QUESTION_EMPTY,
                        LearningPathNodeDetailMessageKey.LEARNING_PATH_NODE_SPEAKING_QUESTION_EMPTY
                );
            }

            if (vocabularyQuestionId == null && nodeType.equals(NodeType.VOCABULARY_QUESTION)) {
                throw new DomainException(
                        LearningPathNodeDomainErrorCode.LEARNING_PATH_NODE_VOCABULARY_QUESTION_EMPTY,
                        LearningPathNodeDetailMessageKey.LEARNING_PATH_NODE_VOCABULARY_QUESTION_EMPTY
                );
            }

            if (chestId == null && nodeType.equals(NodeType.CHEST)) {
                throw new DomainException(
                        LearningPathNodeDomainErrorCode.LEARNING_PATH_NODE_CHEST_EMPTY,
                        LearningPathNodeDetailMessageKey.LEARNING_PATH_NODE_CHEST_EMPTY
                );
            }
            return new LearningPathNode(this);
        }
    }
}