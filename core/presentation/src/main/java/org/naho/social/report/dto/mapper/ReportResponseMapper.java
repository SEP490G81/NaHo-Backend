package org.naho.social.report.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.file.dto.mapper.FileResponseMapper;
import org.naho.social.report.dto.response.ReportResponse;
import org.naho.social.report.result.ReportResult;

@Mapper(componentModel = "spring", uses = {FileResponseMapper.class})
public interface ReportResponseMapper {
    @Mapping(target = "files", source = "files")
    ReportResponse resultToResponse(ReportResult result);
}
