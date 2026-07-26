package org.naho.learning.port.in;

import org.naho.learning.command.UpdateFarthestAvailableNodeCommand;
import org.naho.learning.model.UserLearningProgress;
import org.naho.learning.result.UserLearningProgressResult;

public interface CrudUserLearningProgressInputPort {
    UserLearningProgressResult findUserLearningProgressByUserId(Long userId);

    void initUserLearningProgress(Long userId);

    UserLearningProgress updateFarthestAvailableNodeWhenCompletedANode(UpdateFarthestAvailableNodeCommand command);
}
