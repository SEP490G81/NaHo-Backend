package org.naho.social.report.dto.mapper;

import org.naho.social.report.command.CreateReportCommand;
import org.naho.social.report.dto.request.CreateReportRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReportRequestMapper {

    @SuppressWarnings("unchecked")
    public CreateReportCommand requestToCommand(CreateReportRequest request, Long userId, List<?> imageFiles) {
        return new CreateReportCommand(
                userId,
                request.getQuestionId(),
                request.getCommentId(),
                request.getTitle(),
                request.getDescription(),
                request.getReportType(),
                imageFiles != null ? (List<Object>) imageFiles : List.of()
        );
    }
}
