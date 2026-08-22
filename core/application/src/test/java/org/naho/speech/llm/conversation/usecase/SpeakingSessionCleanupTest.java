package org.naho.speech.llm.conversation.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.speech.llm.conversation.port.out.SpeakingSessionRepositoryPort;
import org.naho.speech.llm.conversation.validator.SpeakingSessionValidator;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpeakingSessionCleanupTest {

    @Mock
    private SpeakingSessionRepositoryPort speakingSessionRepositoryPort;
    @Mock
    private SpeakingSessionValidator speakingSessionValidator;

    @InjectMocks
    private SpeakingSessionCleanupUseCase speakingSessionCleanupUseCase;

    @Test
    @DisplayName("UTCID01 - Xóa session thành công")
    void UTCID01_DeleteSession_Success() {
        speakingSessionCleanupUseCase.deleteSession("SESS-100", 1L);

        verify(speakingSessionValidator, times(1)).validateSessionIsBelongToUser("SESS-100", 1L);
        verify(speakingSessionRepositoryPort, times(1)).deleteSessionBySessionCode("SESS-100");
    }
}
