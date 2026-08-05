package org.naho.social.report.command;

public record UpdateReportStatusCommand(Long reportId, boolean isResolved) {
}
