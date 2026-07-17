package org.naho.season.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.file.dto.mapper.FileResponseMapper;
import org.naho.season.dto.response.LeagueResponse;
import org.naho.season.result.LeagueResult;

@Mapper(
        componentModel = "spring",
        uses = {FileResponseMapper.class}
)
public interface LeagueResponseMapper {
    LeagueResponse resultToResponse(LeagueResult result);
}
