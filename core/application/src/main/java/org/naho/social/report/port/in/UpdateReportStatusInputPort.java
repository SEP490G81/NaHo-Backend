package org.naho.social.report.port.in;

import org.naho.social.report.command.UpdateReportStatusCommand;
import org.naho.social.report.result.ReportResult;

public interface UpdateReportStatusInputPort {
    ReportResult updateStatus(UpdateReportStatusCommand command);
}
