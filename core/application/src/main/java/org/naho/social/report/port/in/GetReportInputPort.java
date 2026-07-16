package org.naho.social.report.port.in;

import org.naho.social.report.command.GetReportCommand;
import org.naho.social.report.result.ReportResult;

import java.util.List;

public interface GetReportInputPort {
    ReportResult getReport(GetReportCommand command);

    List<ReportResult> getReportsByContentManager();

    List<ReportResult> getReportsByAdmin();
}
