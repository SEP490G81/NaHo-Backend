package org.naho.vocabulary.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VocabularyResponse {
    private Long id;
    private String reading;
    private String japanese;
    private String vietnameseMeaningText;
    private String englishMeaningText;
}
