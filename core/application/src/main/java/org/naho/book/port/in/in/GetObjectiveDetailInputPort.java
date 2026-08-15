package org.naho.book.port.in.in;

import org.naho.book.command.GetObjectiveDetailCommand;
import org.naho.book.result.ObjectiveDetailResult;

public interface GetObjectiveDetailInputPort {
    ObjectiveDetailResult getObjectiveDetail(GetObjectiveDetailCommand command);
}
