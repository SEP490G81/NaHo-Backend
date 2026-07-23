package org.naho.daily.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.chest.dto.mapper.ChestResponseMapper;
import org.naho.daily.dto.response.DailyRewardResponse;
import org.naho.daily.result.DailyRewardResult;

@Mapper(
        componentModel = "spring",
        uses = {ChestResponseMapper.class}
)
public interface DailyRewardResponseMapper {
    DailyRewardResponse resultToResponse(DailyRewardResult result);
}
