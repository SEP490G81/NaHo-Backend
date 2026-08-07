package org.naho.speech.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AiNaturalnessFeedback {
    private final Long id;
    private final Long aiFeedbackId;
    private final Integer score;
    private final String comment;
    private final List<AiBetterExpression> betterExpressions;

    private AiNaturalnessFeedback(Builder builder) {
        this.id = builder.id;
        this.aiFeedbackId = builder.aiFeedbackId;
        this.score = builder.score;
        this.comment = builder.comment;
        this.betterExpressions = builder.betterExpressions != null
                ? Collections.unmodifiableList(new ArrayList<>(builder.betterExpressions))
                : Collections.emptyList();
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public Long getAiFeedbackId() {
        return aiFeedbackId;
    }

    public Integer getScore() {
        return score;
    }

    public String getComment() {
        return comment;
    }

    public List<AiBetterExpression> getBetterExpressions() {
        return betterExpressions;
    }

    public static class Builder {
        private Long id;
        private Long aiFeedbackId;
        private Integer score;
        private String comment;
        private List<AiBetterExpression> betterExpressions;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder aiFeedbackId(Long aiFeedbackId) {
            this.aiFeedbackId = aiFeedbackId;
            return this;
        }

        public Builder score(Integer score) {
            this.score = score;
            return this;
        }

        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public Builder betterExpressions(List<AiBetterExpression> betterExpressions) {
            this.betterExpressions = betterExpressions;
            return this;
        }

        public AiNaturalnessFeedback build() {
            return new AiNaturalnessFeedback(this);
        }
    }
}
