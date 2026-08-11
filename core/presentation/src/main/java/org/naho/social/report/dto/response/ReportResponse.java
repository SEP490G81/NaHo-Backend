package org.naho.social.report.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.naho.file.dto.response.FileResponse;
import org.naho.social.report.type.ReportType;

import java.util.List;

@Getter
@Setter
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ReportResponse {
    Long id;
    Long userId;
    String fullName;
    String title;
    String description;
    ReportType reportType;
    Boolean isResolved;
    String adminReply;
    Long questionId;
    Long commentId;
    List<FileResponse> files;
}
