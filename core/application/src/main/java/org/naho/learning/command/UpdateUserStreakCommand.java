package org.naho.learning.command;

import org.naho.learning.model.UserLearningProgress;

import java.time.Instant;
import java.time.ZoneId;

public record UpdateUserStreakCommand(
        UserLearningProgress userLearningProgress,
        Long userId,
        Instant now,
        ZoneId zoneId
) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UserLearningProgress userLearningProgress;
        private Long userId;
        private Instant now;
        private ZoneId zoneId;

        public Builder userLearningProgress(UserLearningProgress userLearningProgress) {
            this.userLearningProgress = userLearningProgress;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder now(Instant now) {
            this.now = now;
            return this;
        }

        public Builder zoneId(ZoneId zoneId) {
            this.zoneId = zoneId;
            return this;
        }

        public UpdateUserStreakCommand build() {
            return new UpdateUserStreakCommand(
                    userLearningProgress,
                    userId,
                    now,
                    zoneId
            );
        }
    }
}
