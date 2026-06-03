package org.naho.furigana.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.furigana.command.AnalyzeFuriganaCommand;
import org.naho.furigana.dto.request.AnalyzeFuriganaRequest;

@Mapper(componentModel = "spring")
public interface FuriganaRequestMapper {
    AnalyzeFuriganaCommand requestToCommand(AnalyzeFuriganaRequest request);
}
