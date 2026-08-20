package org.naho.grammar.mapper;

import org.mapstruct.Mapper;
import org.naho.grammar.command.CreateGrammarCommand;
import org.naho.grammar.command.UpdateGrammarCommand;
import org.naho.grammar.dto.request.CreateGrammarRequest;
import org.naho.grammar.dto.request.UpdateGrammarRequest;
import org.naho.grammar.dto.response.GrammarResponse;
import org.naho.grammar.result.GrammarResult;

@Mapper(componentModel = "spring")
public interface GrammarAdminMapper {
    CreateGrammarCommand toCommand(CreateGrammarRequest request);

    UpdateGrammarCommand toCommand(UpdateGrammarRequest request);

    GrammarResponse toResponse(GrammarResult result);
}
