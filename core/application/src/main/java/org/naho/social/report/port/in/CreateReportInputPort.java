package org.naho.social.report.port.in;

import org.naho.social.report.command.CreateReportCommand;
import org.naho.social.report.result.ReportResult;

public interface CreateReportInputPort {
    ReportResult createReport(CreateReportCommand command);
}
