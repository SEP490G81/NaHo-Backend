package org.naho.social.report.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.file.dto.mapper.FileResponseMapper;
import org.naho.social.report.dto.response.ReportResponse;
import org.naho.social.report.result.ReportResult;

@Mapper(componentModel = "spring", uses = {FileResponseMapper.class})
public interface ReportResponseMapper {
    ReportResponse resultToResponse(ReportResult result);
}
