package org.naho.social.report.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class UpdateReportStatusRequest {
    @NotNull(message = "report.status.blank")
    Boolean isResolved;

    String adminReply;
}
