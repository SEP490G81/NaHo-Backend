package org.naho.speech.azure.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.speech.azure.command.TextToSpeechCommand;
import org.naho.speech.azure.dto.request.TextToSpeechRequest;

@Mapper(componentModel = "spring")
public interface TextToSpeechRequestMapper {
    TextToSpeechCommand requestToCommand(TextToSpeechRequest request);
}
