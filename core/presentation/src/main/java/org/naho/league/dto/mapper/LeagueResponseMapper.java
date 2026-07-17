package org.naho.league.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.file.dto.mapper.FileResponseMapper;
import org.naho.league.dto.response.LeagueResponse;
import org.naho.league.result.LeagueResult;

@Mapper(
        componentModel = "spring",
        uses = {FileResponseMapper.class}
)
public interface LeagueResponseMapper {
    LeagueResponse resultToResponse(LeagueResult result);
}
