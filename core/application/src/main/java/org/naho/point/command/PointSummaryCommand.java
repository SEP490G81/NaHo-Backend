package org.naho.point.command;

import org.naho.i18n.message.point.PointSummaryDetailMessageKey;
import org.naho.point.exception.PointSummaryErrorCode;
import org.naho.shared.exception.ApplicationException;

public record PointSummaryCommand(
        Long id,
        Double point
) {
    public PointSummaryCommand {
        if (point == null) {
            throw new ApplicationException(
                    PointSummaryErrorCode.POINT_SUMMARY_POINT_INVALID,
                    PointSummaryDetailMessageKey.POINT_SUMMARY_TOTAL_POINT_BLANK
            );
        }
    }
}
