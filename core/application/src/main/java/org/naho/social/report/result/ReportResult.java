package org.naho.social.report.result;


import org.naho.file.result.FileResult;
import org.naho.social.report.type.ReportType;

import java.util.List;

public record ReportResult(
        Long id,
        Long userId,
        String title,
        String description,
        ReportType reportType,
        Boolean isResolved,
        Long questionId,
        Long commentId,
        List<FileResult> files
) {
}