package org.naho.furigana.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.furigana.command.GenerateFuriganaCommand;
import org.naho.furigana.dto.request.GenerateFuriganaRequest;

@Mapper(componentModel = "spring")
public interface FuriganaRequestMapper {
    GenerateFuriganaCommand requestToCommand(GenerateFuriganaRequest request);
}
