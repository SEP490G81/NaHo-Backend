package org.naho.social.report.port.in;

import org.naho.social.report.result.ReportResult;

import java.util.List;

public interface GetListReportByContentManagerInputPort {
    List<ReportResult> getReportsByContentManager();
}
