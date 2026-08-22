package org.naho.speech.llm.conversation.usecase;

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
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.llm.conversation.command.StartSpeakingConversationCommand;
import org.naho.speech.llm.conversation.helper.SpeakingSessionHelper;
import org.naho.speech.llm.conversation.helper.SpeakingSessionHelper.ParsedAiReply;
import org.naho.speech.llm.conversation.mapper.SpeakingSessionResultMapper;
import org.naho.speech.llm.conversation.port.out.AiChatPort;
import org.naho.speech.llm.conversation.port.out.SpeakingSessionMessageRepositoryPort;
import org.naho.speech.llm.conversation.port.out.SpeakingSessionRepositoryPort;
import org.naho.speech.llm.conversation.port.out.SpeechToTextPort;
import org.naho.speech.llm.conversation.result.StartConversationResult;
import org.naho.speech.llm.conversation.validator.SpeakingSessionValidator;
import org.naho.speech.llm.model.conversation.SpeakingSession;
import org.naho.speech.llm.type.SpeakingSessionStatus;
import org.naho.user.exception.UserErrorCode;

import java.util.Optional;

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

    @InjectMocks
    private SpeakingSessionUseCase speakingSessionUseCase;

    @Test
    @DisplayName("UTCID01 - Bắt đầu hội thoại nói thành công")
    void UTCID01_StartConversation_Success() {
        StartSpeakingConversationCommand command = new StartSpeakingConversationCommand(1L, 10L, null, null);
        Persona persona = mock(Persona.class);
        when(persona.getName()).thenReturn("Tanaka");
        when(persona.getVoiceName()).thenReturn("ja-JP");

        when(personaRepositoryPort.findById(10L)).thenReturn(Optional.of(persona));

        String sessionCode = speakingSessionUseCase.startConversation(command);

        assertNotNull(sessionCode);
        verify(speakingSessionValidator, times(1)).validateSessionStartLimit(1L);
        verify(speakingSessionValidator, times(1)).validateMaxInProgressSession(1L);
        verify(speakingSessionRepositoryPort, times(1)).initSpeakingSession(any(), eq(1L), eq(10L), any(), any(), any(), any());
    }

    @Test
    @DisplayName("UTCID02 - Khởi tạo câu chào đầu tiên thành công")
    void UTCID02_InitFirstGreeting_Success() {
        SpeakingSession session = mock(SpeakingSession.class);
        Persona persona = mock(Persona.class);
        ParsedAiReply parsedReply = new ParsedAiReply("Konnichiwa", "Xin chao", "Grammar", "Correction", "Explanation", "Hint", null);

        when(session.getStatus()).thenReturn(SpeakingSessionStatus.INIT);
        when(session.getPersonaId()).thenReturn(10L);
        when(speakingSessionRepositoryPort.findBySessionCode("CODE-1")).thenReturn(session);
        when(personaRepositoryPort.findById(10L)).thenReturn(Optional.of(persona));
        when(speakingSessionHelper.buildCustomInstruction(any(), any(), any())).thenReturn("Instruction");
        when(aiChatPort.chatWithContext(any())).thenReturn("AI Reply");
        when(speakingSessionHelper.parseAiResponse("AI Reply")).thenReturn(parsedReply);
        when(speakingSessionHelper.toAudioBase64(any(), any())).thenReturn("audio-base64");

        StartConversationResult result = speakingSessionUseCase.initFirstGreeting("CODE-1", 1L);

        assertNotNull(result);
        assertEquals("Konnichiwa", result.content());
        assertEquals("audio-base64", result.audioBase64());
    }

    @Test
    @DisplayName("UTCID03 - Lấy chi tiết session đang thực hiện thất bại do userId null")
    void UTCID03_GetInProgressSessionDetails_UserIdNull() {
        ApplicationException ex = assertThrows(ApplicationException.class, () ->
                speakingSessionUseCase.getInProgressSessionDetails("CODE-1", null));

        assertEquals(UserErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }
}
