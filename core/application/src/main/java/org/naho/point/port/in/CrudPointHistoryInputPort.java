package org.naho.point.port.in;

import org.naho.pagination.PageData;
import org.naho.point.command.PointHistoryCommand;
import org.naho.point.command.PointHistoryQueryCommand;
import org.naho.point.result.PointHistoryResult;

public interface CrudPointHistoryInputPort {
    PointHistoryResult createPointHistory(PointHistoryCommand command);

    PageData<PointHistoryResult> findAllByUserId(PointHistoryQueryCommand command, Long userId);
}
