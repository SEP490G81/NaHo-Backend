package org.naho.social.report.dto.mapper;

import org.naho.file.result.StoredFile;
import org.naho.social.report.command.CreateReportCommand;
import org.naho.social.report.dto.request.CreateReportRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReportRequestMapper {

    public CreateReportCommand requestToCommand(CreateReportRequest request, Long userId, List<StoredFile> imageFiles) {
        return new CreateReportCommand(
                userId,
                request.getQuestionId(),
                request.getCommentId(),
                request.getTitle(),
                request.getDescription(),
                request.getReportType(),
                imageFiles
        );
    }
}
