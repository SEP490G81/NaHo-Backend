package org.naho.social.report.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.naho.social.report.type.ReportType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class CreateReportRequest {
    @NotBlank(message = "report.title.blank")
    String title;

    @NotBlank(message = "report.description.blank")
    String description;

    @NotNull(message = "report.type.blank")
    ReportType reportType;

    Long questionId;
    Long commentId;

    List<MultipartFile> files;
}
