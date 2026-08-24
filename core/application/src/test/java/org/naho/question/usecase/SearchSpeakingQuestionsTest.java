package org.naho.question.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.question.command.SearchSpeakingQuestionsCommand;
import org.naho.question.port.out.SpeakingQuestionListRepositoryPort;
import org.naho.question.result.SearchSpeakingQuestionsResult;
import org.naho.question.result.SpeakingQuestionListItemResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchSpeakingQuestionsTest {

    @Mock
    private SpeakingQuestionListRepositoryPort speakingQuestionListRepositoryPort;

    @InjectMocks
    private SearchSpeakingQuestionsUseCase searchSpeakingQuestionsUseCase;

    @Test
    @DisplayName("UTCID01 - Tìm kiếm SpeakingQuestions có kết quả thành công")
    void UTCID01_SearchSpeakingQuestions_HasResults() {
        SearchSpeakingQuestionsCommand command = new SearchSpeakingQuestionsCommand(1, 10, null, null, null, null, null, null);
        SpeakingQuestionListItemResult item = mock(SpeakingQuestionListItemResult.class);

        when(speakingQuestionListRepositoryPort.countSpeakingQuestions(command)).thenReturn(15L);
        when(speakingQuestionListRepositoryPort.findSpeakingQuestions(command)).thenReturn(List.of(item));

        SearchSpeakingQuestionsResult result = searchSpeakingQuestionsUseCase.searchSpeakingQuestions(command);

        assertNotNull(result);
        assertEquals(15L, result.totalElements());
        assertEquals(2, result.totalPages());
    }

    @Test
    @DisplayName("UTCID02 - Tìm kiếm SpeakingQuestions kết quả rỗng")
    void UTCID02_SearchSpeakingQuestions_Empty() {
        SearchSpeakingQuestionsCommand command = new SearchSpeakingQuestionsCommand(1, 10, null, null, null, null, null, null);

        when(speakingQuestionListRepositoryPort.countSpeakingQuestions(command)).thenReturn(0L);

        SearchSpeakingQuestionsResult result = searchSpeakingQuestionsUseCase.searchSpeakingQuestions(command);

        assertNotNull(result);
        assertEquals(0L, result.totalElements());
        assertTrue(result.items().isEmpty());
    }
}
