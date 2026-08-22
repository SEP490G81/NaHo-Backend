package org.naho.speech.llm.conversation.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.daily.port.in.CrudUserDailyMissionInputPort;
import org.naho.learning.model.UserLearningProgress;
import org.naho.learning.port.in.UserLearningStreakInputPort;
import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.naho.persona.model.Persona;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.speech.llm.conversation.exception.LlmApplicationError;
import org.naho.speech.llm.conversation.helper.SpeakingSessionHelper;
import org.naho.speech.llm.conversation.mapper.SpeakingSessionResultMapper;
import org.naho.speech.llm.conversation.port.out.AiScoringPort;
import org.naho.speech.llm.conversation.port.out.SpeakingSessionRepositoryPort;
import org.naho.speech.llm.conversation.result.SpeakingSessionAssessmentResult;
import org.naho.speech.llm.conversation.result.SpeakingSessionResult;
import org.naho.speech.llm.model.conversation.SpeakingSession;

import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EndSessionTest {

    @Mock
    private AiScoringPort aiScoringPort;
    @Mock
    private SpeakingSessionRepositoryPort speakingSessionRepositoryPort;
    @Mock
    private PersonaRepositoryPort personaRepositoryPort;
    @Mock
    private SpeakingSessionHelper speakingSessionHelper;
    @Mock
    private SpeakingSessionResultMapper speakingSessionResultMapper;
    @Mock
    private CrudUserDailyMissionInputPort crudUserDailyMissionInputPort;
    @Mock
    private UserLearningStreakInputPort userLearningStreakInputPort;
    @Mock
    private UserLearningProgressRepositoryPort userLearningProgressRepositoryPort;
    @Mock
    private TransactionPort transactionPort;

    @InjectMocks
    private EndSessionUseCase endSessionUseCase;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        lenient().when(transactionPort.execute(any(Supplier.class))).thenAnswer(inv -> {
            Supplier<?> s = inv.getArgument(0);
            return s.get();
        });
    }

    @Test
    @DisplayName("UTCID01 - Kết thúc session nói thành công và nhận đánh giá")
    void UTCID01_EndSession_Success() {
        SpeakingSession session = mock(SpeakingSession.class);
        Persona persona = mock(Persona.class);
        SpeakingSessionAssessmentResult assessmentResult = mock(SpeakingSessionAssessmentResult.class);
        SpeakingSession savedSession = mock(SpeakingSession.class);
        UserLearningProgress progress = mock(UserLearningProgress.class);
        SpeakingSessionResult sessionResult = mock(SpeakingSessionResult.class);

        when(session.getUserId()).thenReturn(1L);
        when(session.getPersonaId()).thenReturn(10L);
        when(speakingSessionRepositoryPort.findBySessionCode("SESS-1")).thenReturn(session);
        when(personaRepositoryPort.findById(10L)).thenReturn(Optional.of(persona));
        when(aiScoringPort.score(any(), any(), any(), any(), any(), any())).thenReturn(assessmentResult);
        when(speakingSessionRepositoryPort.saveSpeakingSession(any(), any(), any(), any(), any(), any(), any(), anyInt(), any(), any(), any()))
                .thenReturn(savedSession);
        when(userLearningProgressRepositoryPort.findByUserId(1L)).thenReturn(Optional.of(progress));
        when(userLearningStreakInputPort.updateUserLearningStreak(any())).thenReturn(progress);
        when(speakingSessionResultMapper.domainToResult(savedSession)).thenReturn(sessionResult);

        SpeakingSessionResult result = endSessionUseCase.endSession(1L, "SESS-1", "Topic", "meta", "80.5");

        assertNotNull(result);
        verify(crudUserDailyMissionInputPort, times(1)).completeMission(any());
    }

    @Test
    @DisplayName("UTCID02 - Thất bại khi userId null")
    void UTCID02_EndSession_UserIdNull() {
        SpeakingSession session = mock(SpeakingSession.class);
        when(session.getUserId()).thenReturn(null);
        when(speakingSessionRepositoryPort.findBySessionCode("SESS-1")).thenReturn(session);

        ApplicationException ex = assertThrows(ApplicationException.class, () ->
                endSessionUseCase.endSession(null, "SESS-1", "Topic", "meta", "80.5"));

        assertEquals(LlmApplicationError.LLM_SESSION_NOT_FOUND, ex.getErrorCode());
    }
}
