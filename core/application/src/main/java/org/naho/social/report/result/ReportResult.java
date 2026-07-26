package org.naho.social.report.result;


import org.naho.social.reaction.type.ReportType;

public record ReportResult(
        Long id,
        Long userId,
        String title,
        String description,
        ReportType reportType,
        Boolean isResolved,
        Long questionId,
        Long commentId
) {
}