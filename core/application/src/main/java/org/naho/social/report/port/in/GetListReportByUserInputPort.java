package org.naho.social.report.port.in;

import org.naho.social.report.command.GetReportsByUserCommand;
import org.naho.social.report.result.ReportResult;

import java.util.List;

public interface GetListReportByUserInputPort {
    List<ReportResult> getReportsByUser(GetReportsByUserCommand command);
}
