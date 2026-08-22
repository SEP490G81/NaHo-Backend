package org.naho.question.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.question.command.FindSpeakingQuestionCommand;
import org.naho.question.exception.SpeakingQuestionErrorCode;
import org.naho.question.mapper.SpeakingQuestionResultMapper;
import org.naho.question.model.SpeakingQuestion;
import org.naho.question.port.out.SpeakingQuestionRepositoryPort;
import org.naho.question.result.SpeakingQuestionListItemResult;
import org.naho.question.result.SpeakingQuestionResult;
import org.naho.shared.exception.ApplicationException;
import org.naho.subscription.port.in.GetActiveSubscriptionInputPort;
import org.naho.subscription.result.SubscriptionPlanResult;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetSpeakingQuestionTest {

    @Mock
    private SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort;
    @Mock
    private SpeakingQuestionResultMapper speakingQuestionResultMapper;
    @Mock
    private GetActiveSubscriptionInputPort getActiveSubscriptionInputPort;

    @InjectMocks
    private GetSpeakingQuestionUseCase getSpeakingQuestionUseCase;

    @Test
    @DisplayName("UTCID01 - Tìm SpeakingQuestion theo ID thành công")
    void UTCID01_FindById_Success() {
        FindSpeakingQuestionCommand command = new FindSpeakingQuestionCommand(10L, 1L);
        SpeakingQuestion question = mock(SpeakingQuestion.class);
        SubscriptionPlanResult planResult = mock(SubscriptionPlanResult.class);
        SpeakingQuestionResult questionResult = mock(SpeakingQuestionResult.class);

        when(planResult.sampleAnswerEnabled()).thenReturn(true);
        when(speakingQuestionRepositoryPort.findById(10L)).thenReturn(Optional.of(question));
        when(getActiveSubscriptionInputPort.getUserActiveSubscriptionPlan(1L)).thenReturn(planResult);
        when(speakingQuestionResultMapper.domainToResult(question, true)).thenReturn(questionResult);

        SpeakingQuestionResult result = getSpeakingQuestionUseCase.findById(command);

        assertNotNull(result);
        verify(speakingQuestionRepositoryPort, times(1)).findById(10L);
    }

    @Test
    @DisplayName("UTCID02 - Thất bại khi speakingQuestionId null")
    void UTCID02_FindById_QuestionIdNull() {
        FindSpeakingQuestionCommand command = new FindSpeakingQuestionCommand(null, 1L);

        ApplicationException ex = assertThrows(ApplicationException.class, () -> getSpeakingQuestionUseCase.findById(command));
        assertEquals(SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("UTCID03 - Lấy listItemResult thành công")
    void UTCID03_FindSpeakingQuestionListItem_Success() {
        FindSpeakingQuestionCommand command = new FindSpeakingQuestionCommand(10L, 1L);
        SpeakingQuestion question = mock(SpeakingQuestion.class);
        SubscriptionPlanResult planResult = mock(SubscriptionPlanResult.class);
        SpeakingQuestionListItemResult listItemResult = mock(SpeakingQuestionListItemResult.class);

        when(planResult.sampleAnswerEnabled()).thenReturn(false);
        when(speakingQuestionRepositoryPort.findById(10L)).thenReturn(Optional.of(question));
        when(getActiveSubscriptionInputPort.getUserActiveSubscriptionPlan(1L)).thenReturn(planResult);
        when(speakingQuestionResultMapper.domainToListItemResult(question, false)).thenReturn(listItemResult);

        SpeakingQuestionListItemResult result = getSpeakingQuestionUseCase.findSpeakingQuestionListItem(command);

        assertNotNull(result);
    }
}
