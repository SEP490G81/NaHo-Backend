package org.naho.learning.port.in;

import org.naho.learning.command.UpdateUserStreakCommand;
import org.naho.learning.model.UserLearningProgress;

public interface UserLearningStreakInputPort {
    UserLearningProgress updateUserLearningStreak(UpdateUserStreakCommand command);
}
