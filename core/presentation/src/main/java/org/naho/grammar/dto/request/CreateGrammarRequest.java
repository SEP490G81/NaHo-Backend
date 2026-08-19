package org.naho.grammar.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateGrammarRequest {
    @NotBlank(message = "reading.must.not.be.blank")
    private String reading;
    
    @NotBlank(message = "japanese.must.not.be.blank")
    private String japanese;
    
    @NotBlank(message = "vietnameseMeaningText.must.not.be.blank")
    private String vietnameseMeaningText;
    
    private String englishMeaningText;
}
