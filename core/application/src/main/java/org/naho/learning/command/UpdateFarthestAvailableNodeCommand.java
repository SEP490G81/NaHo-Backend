package org.naho.learning.command;

import org.naho.i18n.message.learning.LearningPathNodeDetailMessageKey;
import org.naho.i18n.message.learning.UserLearningProgressDetailMessageKey;
import org.naho.learning.exception.LearningPathNodeErrorCode;
import org.naho.learning.exception.UserLearningProgressErrorCode;
import org.naho.learning.model.LearningPathNode;
import org.naho.learning.model.UserLearningProgress;
import org.naho.shared.exception.ApplicationException;

public record UpdateFarthestAvailableNodeCommand(
        UserLearningProgress userLearningProgress,
        LearningPathNode currentLearningPathNode
) {
    public UpdateFarthestAvailableNodeCommand {
        if (userLearningProgress == null) {
            throw new ApplicationException(
                    UserLearningProgressErrorCode.USER_LEARNING_PROGRESS_NOT_FOUND,
                    UserLearningProgressDetailMessageKey.USER_LEARNING_PROGRESS_USER_EMPTY
            );
        }
        if (currentLearningPathNode == null) {
            throw new ApplicationException(
                    LearningPathNodeErrorCode.LEARNING_PATH_NODE_NOT_FOUND,
                    LearningPathNodeDetailMessageKey.LEARNING_PATH_NODE_ID_NOT_FOUND
            );
        }
    }
}
