package org.naho.social.report.mapper;

import org.naho.file.port.out.FileResultMapperPort;
import org.naho.file.result.FileResult;
import org.naho.social.report.command.CreateReportCommand;
import org.naho.social.report.model.Report;
import org.naho.social.report.result.ReportResult;

import java.util.List;

public class ReportResultMapper {

    private final FileResultMapperPort fileResultMapperPort;

    public ReportResultMapper(FileResultMapperPort fileResultMapperPort) {
        this.fileResultMapperPort = fileResultMapperPort;
    }

    public Report commandToDomain(CreateReportCommand command) {
        if (command == null) {
            return null;
        }
        return Report.builder()
                .userId(command.userId())
                .questionId(command.questionId())
                .commentId(command.commentId())
                .title(command.title())
                .description(command.description())
                .reportType(command.reportType())
                .isResolved(false)
                .build();
    }

    public ReportResult domainToResult(Report domain) {
        if (domain == null) {
            return null;
        }

        List<FileResult> fileResults = domain.getFiles().stream().map(fileResultMapperPort::domainToResult).toList();

        return ReportResult.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .fullName(domain.getFullName())
                .title(domain.getTitle())
                .description(domain.getDescription())
                .reportType(domain.getReportType())
                .isResolved(domain.isResolved())
                .adminReply(domain.getAdminReply())
                .questionId(domain.getQuestionId())
                .commentId(domain.getCommentId())
                .files(fileResults)
                .build();
    }
}
