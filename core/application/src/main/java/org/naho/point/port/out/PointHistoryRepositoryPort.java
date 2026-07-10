package org.naho.point.port.out;

import org.naho.pagination.PageData;
import org.naho.point.command.PointHistoryQueryCommand;
import org.naho.point.model.PointHistory;

public interface PointHistoryRepositoryPort {
    PointHistory save(PointHistory pointHistory);

    PageData<PointHistory> findAllByUserId(PointHistoryQueryCommand command, Long userId);
}
