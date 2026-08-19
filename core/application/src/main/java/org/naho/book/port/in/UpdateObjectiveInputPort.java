package org.naho.book.port.in;

import org.naho.book.command.UpdateObjectiveCommand;
import org.naho.book.result.ObjectiveListItemResult;

public interface UpdateObjectiveInputPort {
    ObjectiveListItemResult updateObjective(UpdateObjectiveCommand command);
}
