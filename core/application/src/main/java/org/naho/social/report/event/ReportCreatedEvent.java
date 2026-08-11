package org.naho.social.report.event;

public record ReportCreatedEvent(
        Long reporterId,
        Long reportId,
        String reportType
) {
}
