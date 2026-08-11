package org.naho.social.report.event;

public record ReportStatusUpdatedEvent(
        Long reporterId,
        Long reportId,
        String newStatus,
        String reportTitle,
        String adminNote
) {
}
