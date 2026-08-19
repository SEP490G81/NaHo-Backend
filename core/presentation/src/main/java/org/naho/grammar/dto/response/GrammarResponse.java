package org.naho.grammar.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GrammarResponse {
    private Long id;
    private String reading;
    private String japanese;
    private String vietnameseMeaningText;
    private String englishMeaningText;
}
