package org.naho.point.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.file.dto.mapper.FileResponseMapper;
import org.naho.point.dto.response.LeagueResponse;
import org.naho.point.result.LeagueResult;

@Mapper(
        componentModel = "spring",
        uses = {FileResponseMapper.class}
)
public interface LeagueResponseMapper {
    LeagueResponse resultToResponse(LeagueResult result);
}
