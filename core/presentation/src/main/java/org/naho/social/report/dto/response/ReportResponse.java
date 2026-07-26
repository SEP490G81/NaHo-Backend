package org.naho.social.report.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.naho.social.report.type.ReportType;

@Getter
@Setter
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ReportResponse {
    Long id;
    Long userId;
    String title;
    String description;
    ReportType reportType;
    Boolean isResolved;
    Long questionId;
    Long commentId;
}
