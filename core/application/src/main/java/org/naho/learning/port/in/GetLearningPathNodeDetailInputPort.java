package org.naho.learning.port.in;

import org.naho.learning.command.GetLearningPathNodeDetailCommand;
import org.naho.learning.result.LearningPathNodeDetailResult;

public interface GetLearningPathNodeDetailInputPort {
    LearningPathNodeDetailResult getLearningPathNodeDetail(GetLearningPathNodeDetailCommand command);
}
