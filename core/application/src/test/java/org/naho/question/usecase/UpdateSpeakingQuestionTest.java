package org.naho.question.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.furigana.port.out.FuriganaGenerationPort;
import org.naho.grammar.port.out.GrammarRepositoryPort;
import org.naho.question.command.UpdateSpeakingQuestionCommand;
import org.naho.question.exception.SpeakingQuestionErrorCode;
import org.naho.question.model.SpeakingQuestion;
import org.naho.question.port.out.SpeakingQuestionRepositoryPort;
import org.naho.question.result.UpdateSpeakingQuestionResult;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.vocabulary.port.out.VocabularyRepositoryPort;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateSpeakingQuestionTest {

    @Mock
    private SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort;
    @Mock
    private VocabularyRepositoryPort vocabularyRepositoryPort;
    @Mock
    private GrammarRepositoryPort grammarRepositoryPort;
    @Mock
    private TransactionPort transactionPort;
    @Mock
    private FuriganaGenerationPort furiganaGenerationPort;

    @InjectMocks
    private UpdateSpeakingQuestionUseCase updateSpeakingQuestionUseCase;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        lenient().when(transactionPort.execute(any(Supplier.class))).thenAnswer(inv -> {
            Supplier<?> s = inv.getArgument(0);
            return s.get();
        });
    }

    @Test
    @DisplayName("UTCID01 - Cập nhật SpeakingQuestion thành công")
    void UTCID01_UpdateSpeakingQuestion_Success() {
        UpdateSpeakingQuestionCommand command = new UpdateSpeakingQuestionCommand(1L, 1L, "New Name", "Vi Name", "Desc", "Answer", "Vi Answer", "En Answer", true, null, null);

        SpeakingQuestion question = SpeakingQuestion.builder()
                .id(1L)
                .japaneseName("Old Name")
                .vocabularies(new ArrayList<>())
                .grammars(new ArrayList<>())
                .build();

        when(speakingQuestionRepositoryPort.findById(1L)).thenReturn(Optional.of(question));
        when(speakingQuestionRepositoryPort.hasSpeakingQuestionBeenAnswered(1L)).thenReturn(false);
        when(furiganaGenerationPort.generateFuriganaMarkup(any())).thenReturn("Markup");
        when(speakingQuestionRepositoryPort.save(any(SpeakingQuestion.class))).thenReturn(question);

        UpdateSpeakingQuestionResult result = updateSpeakingQuestionUseCase.updateSpeakingQuestion(command);

        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(speakingQuestionRepositoryPort, times(1)).save(any(SpeakingQuestion.class));
    }

    @Test
    @DisplayName("UTCID02 - Thất bại do không tìm thấy SpeakingQuestion")
    void UTCID02_UpdateSpeakingQuestion_NotFound() {
        UpdateSpeakingQuestionCommand command = new UpdateSpeakingQuestionCommand(99L, 1L, "Name", "Vi", "Desc", "Ans", "ViAns", "EnAns", true, null, null);
        when(speakingQuestionRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> updateSpeakingQuestionUseCase.updateSpeakingQuestion(command));
        assertEquals(SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("UTCID03 - Thất bại do câu hỏi đã từng được trả lời")
    void UTCID03_UpdateSpeakingQuestion_AlreadyAnswered() {
        UpdateSpeakingQuestionCommand command = new UpdateSpeakingQuestionCommand(1L, 1L, "Name", "Vi", "Desc", "Ans", "ViAns", "EnAns", true, null, null);
        SpeakingQuestion question = SpeakingQuestion.builder()
                .id(1L)
                .japaneseName("Name")
                .vocabularies(new ArrayList<>())
                .grammars(new ArrayList<>())
                .build();

        when(speakingQuestionRepositoryPort.findById(1L)).thenReturn(Optional.of(question));
        when(speakingQuestionRepositoryPort.hasSpeakingQuestionBeenAnswered(1L)).thenReturn(true);

        ApplicationException ex = assertThrows(ApplicationException.class, () -> updateSpeakingQuestionUseCase.updateSpeakingQuestion(command));
        assertEquals(SpeakingQuestionErrorCode.SPEAKING_QUESTION_UPDATE_FORBIDDEN, ex.getErrorCode());
    }
}
