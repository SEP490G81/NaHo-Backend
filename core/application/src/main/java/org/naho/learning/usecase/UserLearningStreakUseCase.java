package org.naho.learning.usecase;

import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.learning.command.UpdateUserStreakCommand;
import org.naho.learning.model.UserLearningProgress;
import org.naho.learning.port.in.UserLearningStreakInputPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.user.exception.UserErrorCode;

import java.time.Instant;
import java.time.LocalDate;

public class UserLearningStreakUseCase implements UserLearningStreakInputPort {

    @Override
    public UserLearningProgress updateUserLearningStreak(UpdateUserStreakCommand command) {
        if (command.userId() == null) {
            throw new ApplicationException(
                    UserErrorCode.USER_NOT_FOUND,
                    UserDetailMessageKey.USER_ID_NULL
            );
        }

        UserLearningProgress userLearningProgress = command.userLearningProgress();

        Instant lastLearningAt = userLearningProgress.getLastLearningAt();

        // nếu người dùng học lần đầu tiên
        if (lastLearningAt == null) {
            userLearningProgress.setCurrentStreak(1);
            userLearningProgress.setLongestStreak(1);
            userLearningProgress.setLastLearningAt(command.now());
            return userLearningProgress;
        }

        LocalDate today = command.now().atZone(command.zoneId()).toLocalDate();
        LocalDate lastLearningDate = lastLearningAt.atZone(command.zoneId()).toLocalDate();

        Integer longestStreak = userLearningProgress.getLongestStreak();
        Integer currentStreak = userLearningProgress.getCurrentStreak();

        // nếu hôm nay đã học
        if (lastLearningDate.isEqual(today)) {
            userLearningProgress.setLastLearningAt(command.now());
            return userLearningProgress;
        }

        // nếu học liên tiếp
        if (lastLearningDate.plusDays(1).equals(today)) {
            currentStreak += 1;
        } else {
            // nếu đứt chuỗi
            currentStreak = 0;
        }

        userLearningProgress.setCurrentStreak(currentStreak);
        userLearningProgress.setLongestStreak(Math.max(longestStreak, currentStreak));
        userLearningProgress.setLastLearningAt(command.now());

        return userLearningProgress;
    }
}
