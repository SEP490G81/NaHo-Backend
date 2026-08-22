package org.naho.speech.llm.conversation.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.llm.conversation.exception.LlmApplicationError;
import org.naho.speech.llm.conversation.mapper.SpeakingSessionResultMapper;
import org.naho.speech.llm.conversation.port.out.SpeakingSessionRepositoryPort;
import org.naho.speech.llm.conversation.result.SpeakingSessionListItemResult;
import org.naho.speech.llm.model.conversation.SpeakingSession;
import org.naho.speech.llm.type.SpeakingSessionStatus;
import org.naho.user.exception.UserErrorCode;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrudSpeakingSessionTest {

    @Mock
    private SpeakingSessionRepositoryPort speakingSessionRepositoryPort;
    @Mock
    private SpeakingSessionResultMapper speakingSessionResultMapper;

    @InjectMocks
    private CrudSpeakingSessionUseCase crudSpeakingSessionUseCase;

    @Test
    @DisplayName("UTCID01 - Tìm danh sách session theo userId và status thành công")
    void UTCID01_FindAllByUserIdAndStatus_Success() {
        SpeakingSession session = mock(SpeakingSession.class);
        SpeakingSessionListItemResult itemResult = mock(SpeakingSessionListItemResult.class);

        when(speakingSessionRepositoryPort.findAllByUserIdAndSpeakingSessionStatus(1L, SpeakingSessionStatus.IN_PROGRESS))
                .thenReturn(List.of(session));
        when(speakingSessionResultMapper.domainToListItemResult(any())).thenReturn(itemResult);

        List<SpeakingSessionListItemResult> results = crudSpeakingSessionUseCase
                .findAllByUserIdAndSpeakingSessionStatus(1L, SpeakingSessionStatus.IN_PROGRESS);

        assertNotNull(results);
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("UTCID02 - Thất bại khi userId null")
    void UTCID02_UserIdNull() {
        ApplicationException ex = assertThrows(ApplicationException.class, () ->
                crudSpeakingSessionUseCase.findAllByUserIdAndSpeakingSessionStatus(null, SpeakingSessionStatus.IN_PROGRESS));
        assertEquals(UserErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("UTCID03 - Thất bại khi status null")
    void UTCID03_StatusNull() {
        ApplicationException ex = assertThrows(ApplicationException.class, () ->
                crudSpeakingSessionUseCase.findAllByUserIdAndSpeakingSessionStatus(1L, null));
        assertEquals(LlmApplicationError.LLM_SESSION_STATUS_INVALID, ex.getErrorCode());
    }
}
