package org.naho.speech.llm.conversation.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.file.port.in.UploadFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.persona.model.Persona;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.speech.llm.conversation.command.SendTextMessageCommand;
import org.naho.speech.llm.conversation.command.StartSpeakingConversationCommand;
import org.naho.speech.llm.conversation.exception.LlmApplicationError;
import org.naho.speech.llm.conversation.helper.SpeakingSessionHelper;
import org.naho.speech.llm.conversation.internal.ParsedAiReply;
import org.naho.speech.llm.conversation.mapper.SpeakingSessionMessageResultMapper;
import org.naho.speech.llm.conversation.mapper.SpeakingSessionResultMapper;
import org.naho.speech.llm.conversation.port.out.AiChatPort;
import org.naho.speech.llm.conversation.port.out.SpeakingSessionMessageRepositoryPort;
import org.naho.speech.llm.conversation.port.out.SpeakingSessionRepositoryPort;
import org.naho.speech.llm.conversation.port.out.SpeechToTextPort;
import org.naho.speech.llm.conversation.result.ChatResult;
import org.naho.speech.llm.conversation.result.SpeakingSessionMessageResult;
import org.naho.speech.llm.conversation.validator.SpeakingSessionValidator;
import org.naho.speech.llm.model.conversation.SpeakingSession;
import org.naho.speech.llm.model.conversation.SpeakingSessionMessage;
import org.naho.speech.llm.type.SpeakingSessionStatus;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpeakingSessionTest {

    @Mock
    private AiChatPort aiChatPort;
    @Mock
    private SpeakingSessionMessageRepositoryPort speakingSessionMessageRepositoryPort;
    @Mock
    private SpeechToTextPort speechToTextPort;
    @Mock
    private PersonaRepositoryPort personaRepositoryPort;
    @Mock
    private SpeakingSessionRepositoryPort speakingSessionRepositoryPort;
    @Mock
    private FileRepositoryPort fileRepositoryPort;
    @Mock
    private UploadFileInputPort uploadFileInputPort;
    @Mock
    private SpeakingSessionValidator speakingSessionValidator;
    @Mock
    private SpeakingSessionHelper speakingSessionHelper;
    @Mock
    private SpeakingSessionResultMapper speakingSessionResultMapper;
    @Mock
    private TransactionPort transactionPort;
    @Mock
    private SpeakingSessionMessageResultMapper speakingSessionMessageResultMapper;

    @InjectMocks
    private SpeakingSessionUseCase speakingSessionUseCase;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        lenient().when(transactionPort.execute(any(Supplier.class))).thenAnswer(inv -> {
            Supplier<?> s = inv.getArgument(0);
            return s.get();
        });
    }

    @Test
    @DisplayName("UTCID01 - Bắt đầu hội thoại nói thành công")
    void UTCID01_StartConversation_Success() {
        StartSpeakingConversationCommand command = new StartSpeakingConversationCommand(1L, 10L, FormalityLevel.FORMAL, MarugotoLevel.STARTER_A1);
        Persona persona = mock(Persona.class);
        SpeakingSession session = mock(SpeakingSession.class);
        ParsedAiReply parsedReply = ParsedAiReply.builder()
                .reply("Konnichiwa")
                .replyTranslation("Xin chao")
                .build();

        when(personaRepositoryPort.findById(10L)).thenReturn(Optional.of(persona));
        when(persona.getName()).thenReturn("Tanaka");
        when(persona.getVoiceName()).thenReturn("ja-JP");
        when(speakingSessionRepositoryPort.initSpeakingSession(any(), eq(1L), eq(10L), any(), eq("ja-JP"), eq(FormalityLevel.FORMAL), eq(MarugotoLevel.STARTER_A1)))
                .thenReturn(session);
        when(session.getId()).thenReturn(100L);
        when(speakingSessionHelper.buildSystemPromptContent(persona, FormalityLevel.FORMAL, MarugotoLevel.STARTER_A1)).thenReturn("Prompt");
        when(aiChatPort.chatWithContext(any())).thenReturn("rawResponse");
        when(speakingSessionHelper.parseAiResponse("rawResponse")).thenReturn(parsedReply);

        String sessionCode = speakingSessionUseCase.startConversation(command);

        assertNotNull(sessionCode);
        verify(speakingSessionValidator, times(1)).validateSessionStartLimit(1L);
        verify(speakingSessionValidator, times(1)).validateMaxInProgressSession(1L);
        verify(speakingSessionRepositoryPort, times(1)).saveSpeakingSessionMessage(any());
    }

    @Test
    @DisplayName("UTCID02 - Gửi tin nhắn văn bản thành công")
    void UTCID02_SendMessage_Success() {
        SendTextMessageCommand command = new SendTextMessageCommand("CODE-1", "Konnichiwa");
        SpeakingSession session = mock(SpeakingSession.class);
        Persona persona = mock(Persona.class);
        SpeakingSessionMessage userMsg = mock(SpeakingSessionMessage.class);
        SpeakingSessionMessage aiMsg = mock(SpeakingSessionMessage.class);
        SpeakingSessionMessageResult userMsgResult = mock(SpeakingSessionMessageResult.class);
        SpeakingSessionMessageResult aiMsgResult = mock(SpeakingSessionMessageResult.class);
        ParsedAiReply parsedReply = ParsedAiReply.builder()
                .reply("Ogenki desu ka?")
                .build();

        when(session.getId()).thenReturn(100L);
        when(session.getUserId()).thenReturn(1L);
        when(session.getPersonaId()).thenReturn(10L);
        when(session.getStatus()).thenReturn(SpeakingSessionStatus.IN_PROGRESS);
        when(session.getTotalTurns()).thenReturn(1);

        when(speakingSessionRepositoryPort.findBySessionCode("CODE-1")).thenReturn(session);
        when(personaRepositoryPort.findById(10L)).thenReturn(Optional.of(persona));
        when(speakingSessionMessageRepositoryPort.findAllBySessionId(100L)).thenReturn(List.of());
        when(speakingSessionHelper.getSlidingWindowMessages(any(), any(), any(), any())).thenReturn(List.of());
        when(aiChatPort.chatWithContext(any())).thenReturn("rawReply");
        when(speakingSessionHelper.parseAiResponse("rawReply")).thenReturn(parsedReply);
        when(speakingSessionRepositoryPort.saveSpeakingSessionMessage(any())).thenReturn(userMsg).thenReturn(aiMsg);
        when(speakingSessionMessageResultMapper.domainToResult(userMsg)).thenReturn(userMsgResult);
        when(speakingSessionMessageResultMapper.domainToResult(aiMsg)).thenReturn(aiMsgResult);

        ChatResult result = speakingSessionUseCase.sendMessage(command);

        assertNotNull(result);
        assertEquals(userMsgResult, result.userMessage());
        assertEquals(aiMsgResult, result.aiMessage());
    }

    @Test
    @DisplayName("UTCID03 - Gửi tin nhắn thất bại do session đã hoàn thành")
    void UTCID03_SendMessage_InvalidSessionStatus() {
        SendTextMessageCommand command = new SendTextMessageCommand("CODE-1", "Konnichiwa");
        SpeakingSession session = mock(SpeakingSession.class);

        when(session.getStatus()).thenReturn(SpeakingSessionStatus.COMPLETED);
        when(speakingSessionRepositoryPort.findBySessionCode("CODE-1")).thenReturn(session);

        ApplicationException ex = assertThrows(ApplicationException.class, () ->
                speakingSessionUseCase.sendMessage(command));

        assertEquals(LlmApplicationError.LLM_SESSION_STATUS_INVALID, ex.getErrorCode());
    }
}


