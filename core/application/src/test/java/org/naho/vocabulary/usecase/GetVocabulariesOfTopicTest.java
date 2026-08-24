package org.naho.vocabulary.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.question.model.Vocabulary;
import org.naho.question.port.out.VocabulariesQuestionPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.vocabulary.exception.VocabularyErrorCode;
import org.naho.vocabulary.result.VocabulariesOfTopicResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetVocabulariesOfTopicTest {

    @Mock
    private VocabulariesQuestionPort vocabulariesQuestionPort;

    @InjectMocks
    private GetVocabulariesOfTopicUseCase getVocabulariesOfTopicUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy danh sách từ vựng theo topicId thành công")
    void UTCID01_GetVocabularyListOfTopic_Success() {
        Vocabulary vocab = mock(Vocabulary.class);
        when(vocabulariesQuestionPort.findVocabularyListOfTopic(100L)).thenReturn(List.of(vocab));

        VocabulariesOfTopicResult result = getVocabulariesOfTopicUseCase.getVocabularyListOfTopic(100L);

        assertNotNull(result);
        assertEquals(100L, result.topicId());
        assertEquals(1, result.vocabularies().size());
    }

    @Test
    @DisplayName("UTCID02 - Lấy thất bại do không có từ vựng nào trong topic")
    void UTCID02_GetVocabularyListOfTopic_Empty() {
        when(vocabulariesQuestionPort.findVocabularyListOfTopic(99L)).thenReturn(List.of());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> getVocabulariesOfTopicUseCase.getVocabularyListOfTopic(99L));
        assertEquals(VocabularyErrorCode.VOCABULARY_NOT_FOUND, ex.getErrorCode());
    }
}
