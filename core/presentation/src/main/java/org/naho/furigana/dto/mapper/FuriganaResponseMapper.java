package org.naho.furigana.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.furigana.dto.response.FuriganaResponse;
import org.naho.furigana.result.FuriganaResult;

@Mapper(componentModel = "spring")
public interface FuriganaResponseMapper {
    FuriganaResponse resultToResponse(FuriganaResult result);
}
