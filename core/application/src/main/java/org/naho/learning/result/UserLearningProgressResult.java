package org.naho.learning.result;

import org.naho.user.result.LeaderboardUserResult;

import java.time.Instant;

public class UserLearningProgressResult {
    private Long id;
    private Long farthestAvailableNodeId;
    private Long lastLearningNodeId;
    private Instant lastLearningAt;
    private Integer currentStreak;
    private Integer longestStreak;
    private Double totalPoint;
    private LeaderboardUserResult leaderboardUser;

    public UserLearningProgressResult() {
    }

    public UserLearningProgressResult(
            Long id,
            Long farthestAvailableNodeId,
            Long lastLearningNodeId,
            Instant lastLearningAt,
            Integer currentStreak,
            Integer longestStreak,
            Double totalPoint,
            LeaderboardUserResult leaderboardUser
    ) {
        this.id = id;
        this.farthestAvailableNodeId = farthestAvailableNodeId;
        this.lastLearningNodeId = lastLearningNodeId;
        this.lastLearningAt = lastLearningAt;
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
        this.totalPoint = totalPoint;
        this.leaderboardUser = leaderboardUser;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFarthestAvailableNodeId() {
        return farthestAvailableNodeId;
    }

    public void setFarthestAvailableNodeId(Long farthestAvailableNodeId) {
        this.farthestAvailableNodeId = farthestAvailableNodeId;
    }

    public Long getLastLearningNodeId() {
        return lastLearningNodeId;
    }

    public void setLastLearningNodeId(Long lastLearningNodeId) {
        this.lastLearningNodeId = lastLearningNodeId;
    }

    public Instant getLastLearningAt() {
        return lastLearningAt;
    }

    public void setLastLearningAt(Instant lastLearningAt) {
        this.lastLearningAt = lastLearningAt;
    }

    public Integer getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(Integer currentStreak) {
        this.currentStreak = currentStreak;
    }

    public Integer getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(Integer longestStreak) {
        this.longestStreak = longestStreak;
    }

    public Double getTotalPoint() {
        return totalPoint;
    }

    public void setTotalPoint(Double totalPoint) {
        this.totalPoint = totalPoint;
    }

    public LeaderboardUserResult getLeaderboardUser() {
        return leaderboardUser;
    }

    public void setLeaderboardUser(LeaderboardUserResult leaderboardUser) {
        this.leaderboardUser = leaderboardUser;
    }

    public static class Builder {
        private Long id;
        private Long farthestAvailableNodeId;
        private Long lastLearningNodeId;
        private Instant lastLearningAt;
        private Integer currentStreak;
        private Integer longestStreak;
        private Double totalPoint;
        private LeaderboardUserResult leaderboardUser;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder farthestAvailableNodeId(Long farthestAvailableNodeId) {
            this.farthestAvailableNodeId = farthestAvailableNodeId;
            return this;
        }

        public Builder lastLearningNodeId(Long lastLearningNodeId) {
            this.lastLearningNodeId = lastLearningNodeId;
            return this;
        }

        public Builder lastLearningAt(Instant lastLearningAt) {
            this.lastLearningAt = lastLearningAt;
            return this;
        }

        public Builder currentStreak(Integer currentStreak) {
            this.currentStreak = currentStreak;
            return this;
        }

        public Builder longestStreak(Integer longestStreak) {
            this.longestStreak = longestStreak;
            return this;
        }

        public Builder totalPoint(Double totalPoint) {
            this.totalPoint = totalPoint;
            return this;
        }

        public Builder leaderboardUser(LeaderboardUserResult leaderboardUser) {
            this.leaderboardUser = leaderboardUser;
            return this;
        }

        public UserLearningProgressResult build() {
            return new UserLearningProgressResult(
                    id,
                    farthestAvailableNodeId,
                    lastLearningNodeId,
                    lastLearningAt,
                    currentStreak,
                    longestStreak,
                    totalPoint,
                    leaderboardUser
            );
        }
    }
}
