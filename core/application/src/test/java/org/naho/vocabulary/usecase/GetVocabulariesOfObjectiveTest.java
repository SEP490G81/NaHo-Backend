package org.naho.vocabulary.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.question.model.Vocabulary;
import org.naho.question.port.out.VocabulariesQuestionPort;
import org.naho.vocabulary.mapper.VocabularyResultMapper;
import org.naho.vocabulary.result.VocabulariesOfObjectiveResult;
import org.naho.vocabulary.result.VocabularyResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetVocabulariesOfObjectiveTest {

    @Mock
    private VocabulariesQuestionPort vocabulariesQuestionPort;
    @Mock
    private VocabularyResultMapper vocabularyResultMapper;

    @InjectMocks
    private GetVocabulariesOfObjectiveUseCase getVocabulariesOfObjectiveUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy danh sách từ vựng theo objectiveId thành công")
    void UTCID01_GetVocabularyListOfObjective_Success() {
        Vocabulary v = mock(Vocabulary.class);
        VocabularyResult vr = mock(VocabularyResult.class);

        when(vocabulariesQuestionPort.findVocabularyListOfObjective(10L)).thenReturn(List.of(v));
        when(vocabularyResultMapper.domainToResult(any())).thenReturn(vr);

        VocabulariesOfObjectiveResult result = getVocabulariesOfObjectiveUseCase.getVocabularyListOfObjective(10L);

        assertNotNull(result);
        assertEquals(10L, result.objectiveId());
        assertEquals(1, result.vocabularies().size());
    }
}
