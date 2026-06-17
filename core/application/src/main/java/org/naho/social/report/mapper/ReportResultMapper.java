package org.naho.social.report.mapper;

import org.naho.social.model.Report;
import org.naho.social.report.result.ReportResult;

public class ReportResultMapper {
    public ReportResult domainToResult(Report domain) {
        if (domain == null) {
            return null;
        }
        return new ReportResult(
                domain.getId(),
                domain.getUserId(),
                domain.getTitle(),
                domain.getDescription(),
                domain.getReportType(),
                domain.isResolved(),
                domain.getQuestionId(),
                domain.getCommentId()
        );
    }
}
