package org.naho.social.report.event;

public record ReportStatusUpdatedEvent(
        Long reporterId,
        Long reportId,
        String reportTitle,
        String adminNote,
        String reportType
) {
}
