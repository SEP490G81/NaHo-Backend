package org.naho.speech.llm.conversation.usecase;

import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.llm.conversation.mapper.SpeakingSessionResultMapper;
import org.naho.speech.llm.conversation.port.in.CrudSpeakingSessionInputPort;
import org.naho.speech.llm.conversation.port.out.SpeakingSessionRepositoryPort;
import org.naho.speech.llm.conversation.result.SpeakingSessionListItemResult;
import org.naho.speech.llm.model.conversation.SpeakingSession;
import org.naho.user.exception.UserErrorCode;

import java.util.List;

public class CrudSpeakingSessionUseCase implements CrudSpeakingSessionInputPort {
    private final SpeakingSessionRepositoryPort speakingSessionRepositoryPort;
    private final SpeakingSessionResultMapper speakingSessionResultMapper;

    public CrudSpeakingSessionUseCase(
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort,
            SpeakingSessionResultMapper speakingSessionResultMapper
    ) {
        this.speakingSessionRepositoryPort = speakingSessionRepositoryPort;
        this.speakingSessionResultMapper = speakingSessionResultMapper;
    }

    @Override
    public List<SpeakingSessionListItemResult> findAllInProgressSessionsByUserId(Long userId) {
        if (userId == null) {
            throw new ApplicationException(
                    UserErrorCode.USER_NOT_FOUND,
                    UserDetailMessageKey.USER_ID_NULL
            );
        }

        List<SpeakingSession> inProgressSessions = speakingSessionRepositoryPort
                .findAllInProgressSessionsByUserId(userId);

        return inProgressSessions.stream()
                .map(speakingSessionResultMapper::domainToListItemResult)
                .toList();
    }
}
