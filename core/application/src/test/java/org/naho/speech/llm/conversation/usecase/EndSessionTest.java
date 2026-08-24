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
import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.speech.llm.conversation.exception.LlmApplicationError;
import org.naho.speech.llm.conversation.helper.SpeakingSessionHelper;
import org.naho.speech.llm.conversation.mapper.SpeakingSessionAssessmentResultMapper;
import org.naho.speech.llm.conversation.port.out.*;
import org.naho.speech.llm.conversation.result.SpeakingSessionAssessmentResult;
import org.naho.speech.llm.model.conversation.SpeakingSession;
import org.naho.speech.llm.model.conversation.SpeakingSessionAssessment;
import org.naho.speech.llm.model.conversation.SpeakingSessionMessage;
import org.naho.speech.llm.type.SenderType;

import java.util.List;
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
    private SpeakingSessionAssessmentResultMapper speakingSessionAssessmentResultMapper;
    @Mock
    private TransactionPort transactionPort;
    @Mock
    private SpeakingSessionMessageRepositoryPort speakingSessionMessageRepositoryPort;
    @Mock
    private AiChatPort aiChatPort;
    @Mock
    private SpeakingSessionAssessmentRepositoryPort speakingSessionAssessmentRepositoryPort;
    @Mock
    private CrudUserDailyMissionInputPort crudUserDailyMissionInputPort;
    @Mock
    private UserLearningProgressRepositoryPort userLearningProgressRepositoryPort;
    @Mock
    private UserLearningStreakInputPort userLearningStreakInputPort;

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
        SpeakingSessionMessage msg1 = mock(SpeakingSessionMessage.class);
        SpeakingSessionMessage msg2 = mock(SpeakingSessionMessage.class);
        SpeakingSessionAssessment assessment = mock(SpeakingSessionAssessment.class);
        SpeakingSessionAssessment savedAssessment = mock(SpeakingSessionAssessment.class);
        UserLearningProgress progress = mock(UserLearningProgress.class);
        SpeakingSessionAssessmentResult assessmentResult = mock(SpeakingSessionAssessmentResult.class);

        when(session.getId()).thenReturn(100L);
        when(session.getUserId()).thenReturn(1L);
        when(session.getPersonaId()).thenReturn(10L);
        when(session.getFormalityLevel()).thenReturn(FormalityLevel.FORMAL);
        when(session.getMarugotoLevel()).thenReturn(MarugotoLevel.STARTER_A1);

        when(speakingSessionRepositoryPort.findBySessionCode("SESS-1")).thenReturn(session);
        when(speakingSessionMessageRepositoryPort.findAllBySessionId(100L)).thenReturn(List.of(msg1, msg2));

        when(msg1.getSenderType()).thenReturn(SenderType.ASSISTANT);
        when(msg1.getContent()).thenReturn("Konnichiwa");
        when(msg2.getSenderType()).thenReturn(SenderType.USER);
        when(msg2.getContent()).thenReturn("Hajimemashite");

        when(personaRepositoryPort.findById(10L)).thenReturn(Optional.of(persona));
        when(persona.getName()).thenReturn("Tanaka");
        when(speakingSessionHelper.buildSystemPromptContent(persona, FormalityLevel.FORMAL, MarugotoLevel.STARTER_A1)).thenReturn("Prompt");
        when(aiChatPort.buildMessagesRequestBody(any())).thenReturn("JsonMessages");

        when(aiScoringPort.score("SESS-1", "Conversation with Tanaka", "Prompt", "JsonMessages")).thenReturn(assessment);
        when(speakingSessionAssessmentRepositoryPort.save(assessment)).thenReturn(savedAssessment);
        when(userLearningProgressRepositoryPort.findByUserId(1L)).thenReturn(Optional.of(progress));
        when(userLearningStreakInputPort.updateUserLearningStreak(any())).thenReturn(progress);
        when(speakingSessionAssessmentResultMapper.domainToResult(savedAssessment)).thenReturn(assessmentResult);

        SpeakingSessionAssessmentResult result = endSessionUseCase.endSession(1L, "SESS-1");

        assertNotNull(result);
        verify(crudUserDailyMissionInputPort, times(1)).completeMission(any());
        verify(speakingSessionRepositoryPort, times(1)).save(session);
    }

    @Test
    @DisplayName("UTCID02 - Thất bại khi userId null")
    void UTCID02_EndSession_UserIdNull() {
        SpeakingSession session = mock(SpeakingSession.class);
        when(session.getUserId()).thenReturn(1L);
        when(speakingSessionRepositoryPort.findBySessionCode("SESS-1")).thenReturn(session);

        ApplicationException ex = assertThrows(ApplicationException.class, () ->
                endSessionUseCase.endSession(null, "SESS-1"));

        assertEquals(LlmApplicationError.LLM_SESSION_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("UTCID03 - Thất bại khi session chưa có tin nhắn nào từ người dùng")
    void UTCID03_EndSession_TranscriptBlank() {
        SpeakingSession session = mock(SpeakingSession.class);
        when(session.getId()).thenReturn(100L);
        when(session.getUserId()).thenReturn(1L);
        when(speakingSessionRepositoryPort.findBySessionCode("SESS-1")).thenReturn(session);
        when(speakingSessionMessageRepositoryPort.findAllBySessionId(100L)).thenReturn(List.of());

        ApplicationException ex = assertThrows(ApplicationException.class, () ->
                endSessionUseCase.endSession(1L, "SESS-1"));

        assertEquals(LlmApplicationError.LLM_TRANSCRIPT_BLANK, ex.getErrorCode());
    }
}


