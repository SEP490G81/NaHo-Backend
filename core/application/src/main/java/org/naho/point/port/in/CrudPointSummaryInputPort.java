package org.naho.point.port.in;

import org.naho.point.command.PointSummaryCommand;
import org.naho.point.result.PointSummaryResult;

public interface CrudPointSummaryInputPort {
    PointSummaryResult updateTotalPoint(PointSummaryCommand command);

    PointSummaryResult addPoint(PointSummaryCommand command);

    PointSummaryResult findPointSummaryByUserId(Long userId);
}
