package org.naho.point.port.in;

import org.naho.point.command.PointHistoryCommand;
import org.naho.point.command.PointHistoryQueryCommand;
import org.naho.point.result.PointHistoryResult;

import java.util.List;

public interface CrudPointHistoryInputPort {
    PointHistoryResult createPointHistory(PointHistoryCommand command);

    List<PointHistoryResult> findAllByUserId(PointHistoryQueryCommand command, Long userId);
}
