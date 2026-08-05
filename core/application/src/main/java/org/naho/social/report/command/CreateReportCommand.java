package org.naho.social.report.command;

import org.naho.file.result.StoredFile;
import org.naho.social.report.type.ReportType;

import java.util.List;

public record CreateReportCommand(
        Long userId,
        Long questionId,
        Long commentId,
        String title,
        String description,
        ReportType reportType,
        List<StoredFile> imageFiles
) {
}
