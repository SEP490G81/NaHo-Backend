package org.naho.point.port.out;

import org.naho.point.command.PointHistoryQueryCommand;
import org.naho.point.model.PointHistory;

import java.util.List;

public interface PointHistoryRepositoryPort {
    PointHistory save(PointHistory pointHistory);

    List<PointHistory> findAllByUserId(PointHistoryQueryCommand command, Long userId);
}
