package org.naho.vocabulary.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VocabularyQuizResponse {
    private Long vocabularyId;
    private String japanese;
    private String reading;
    private List<VocabularyQuizOptionResponse> options;
    private String correctOptionId;
}
