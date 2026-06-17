package org.naho.social.report.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.social.report.dto.response.ReportResponse;
import org.naho.social.report.result.ReportResult;

@Mapper(componentModel = "spring")
public interface ReportResponseMapper {
    ReportResponse resultToResponse(ReportResult result);
}
